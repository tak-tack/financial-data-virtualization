# 금융 계열사 데이터 가상화 서비스 (로컬 PoC)

BNK부산은행, 경남은행, 캐피탈, 투자증권, 시스템처럼 서로 다른 DBMS를 가진 계열사 RDB를 하나의 **상품 논리 가상 View**로 제공하기 위한 Spring Boot 프로젝트다. `local` 프로필은 Mock 응답을, `db` 프로필은 실제 JDBC 조회를 사용한다.

## 현재 제공 범위

- 계열사별 Data Source 설정 구조 및 DBMS 유형(Oracle, PostgreSQL, MS-SQL)
- 계열사별 상품 테이블을 `ProductView` 공통 모델로 변환하는 경계
- `POST /api/v1/virtual-views/products/search` 조회 API
- `ITEM_NAME = ?` 조건을 원천 SQL에 적용하는 Query Pushdown 예시
- 향후 MCP의 `product_search` tool이 호출할 수 있는 REST 경계

## 실행

JDK 21과 Maven 3.9 이상이 필요하다.

```bash
cd financial-data-virtualization
mvn spring-boot:run
```

실제 DB 대신 Mock 응답으로 실행하려면 `local` 프로필을 활성화한다. 이때
`application.yml` 위에 `application-local.yml` 설정이 병합되고,
`MockSourceAdapter`가 주입된다.

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

```bash
curl -X POST http://localhost:8080/api/v1/virtual-views/products/search \
  -H 'Content-Type: application/json' \
  -d '{"itemName":"C 정기적금"}'
```

각 활성 계열사의 결과가 표준 컬럼(`affiliateCode`, `itemCode`, `itemName`, `itemType`, `annualRate`, `status`, `lastChangedAt`)으로 반환된다.

## Local 프로필과 Mock 테스트

`local` 프로필은 실제 금융 계열사 DB에 접속하지 않고 로컬에서 API 흐름을 확인하기 위한 실행 환경이다.
Spring Boot가 `local` 프로필로 시작되면 `application.yml`과 `application-local.yml`을 병합하고,
`MockSourceAdapter`를 주입한다. 반대로 `db` 프로필에서는 `JdbcSourceAdapter`를 사용한다.

IntelliJ에서는 `실행(Run) → 구성 편집(Edit Configurations) → VirtualizationApplication`으로 이동한 후
`활성 프로필(Active profiles)`에 `local`을 입력한다. 실행 콘솔에서 다음 로그가 보이면 Mock 모드가
활성화된 것이다.

```text
The following 1 profile is active: "local"
```

PowerShell에서 직접 실행할 때는 다음 명령을 사용한다.

```powershell
mvn spring-boot:run "-Dspring-boot.run.profiles=local"
```

실행 후 아래 요청으로 Mock API를 확인할 수 있다.

```powershell
Invoke-RestMethod `
  -Method Post `
  -Uri http://localhost:8080/api/v1/virtual-views/products/search `
  -ContentType "application/json" `
  -Body '{"itemName":"테스트 적금"}'
```

`application-local.yml`에는 부산은행, 캐피탈, 투자증권 세 계열사가 활성화되어 있으므로 정상적인 경우
Mock 상품이 3건 반환된다. 각 결과의 `affiliateCode`는 서로 다르고 `itemName`은 요청값과 동일하다.

JUnit 테스트 전체 실행:

```powershell
mvn test
```

IntelliJ에서는 `VirtualProductServiceJUnit4Test` 클래스 왼쪽의 실행 아이콘을 누른 후
`VirtualProductServiceJUnit4Test 실행(Run 'VirtualProductServiceJUnit4Test')`을 선택한다.
이 JUnit4 테스트는 Mockito로 `SourceAdapter`를 주입하고 다음 내용을 검증한다.

- 활성화된 계열사가 Adapter에 전달되는지
- 요청한 상품명이 Adapter에 전달되는지
- Mock Adapter가 반환한 `ProductView`가 서비스 결과에 포함되는지

프로필을 실제 DB 모드로 되돌리려면 `활성 프로필(Active profiles)`을 `db`로 변경한다.
프로필 값을 비워도 `application.yml`에 지정된 기본 프로필 `db`가 사용된다.

## 실제 DB Adapter 연결 순서

1. 해당 DBMS의 JDBC Driver를 `pom.xml`에 추가한다. Oracle 드라이버는 조직의 라이선스·저장소 정책에 따라 별도 관리한다.
2. `application.yml`의 계열사별 `jdbc-url`, `username`, `password`를 Secret/환경변수로 주입한다. 계정은 반드시 읽기 전용 계정이어야 한다.
3. `JdbcSourceAdapter`에서 계열사별 HikariCP DataSource를 독립 생성하고 `SourceSqlBuilder`가 만든 SQL을 `PreparedStatement`로 실행한다.
4. 원천 컬럼과 코드값 차이는 Adapter 또는 별도 Mapper에서 `ProductView`로 변환한다.
5. 계열사별 pool 크기, connection/query timeout, circuit breaker 및 조회 건수 제한을 추가한다.

`application.yml`의 `product-table`과 `ProductView`/`SourceSqlBuilder`가 최초 커스터마이징 지점이다. 실제 테이블·컬럼은 계열사별로 다를 수 있다.

## 실제 DB 검색 테스트

`application.yml`의 활성 계열사는 모두 조회 대상입니다. PostgreSQL `aff_c.product_master`는
`ITEM_CODE`, `ITEM_NAME`, `ITEM_TYPE`, `ANNUAL_RATE`, `STATUS`, `LAST_CHANGED_AT` 컬럼을 사용합니다.
MySQL `AFF_B.FINANCIAL_PRODUCTS`는 `PRD_CD`, `PRD_NM`, `PRD_TYPE_CD`, `INT_RATE`, `USE_YN`,
`UPD_DTM`을 표준 응답 필드로 변환합니다. 상태값은 통합 코드 테이블을 도입하기 전까지 원본값을 반환합니다.

```bash
mvn spring-boot:run

curl -X POST http://localhost:8080/api/v1/virtual-views/products/search \
  -H 'Content-Type: application/json' \
  -d '{"itemName":"C 정기적금"}'
```

실제 DB 조회는 `db` 프로필의 `JdbcSourceAdapter`가 수행하며, 연결 실패나 SQL 오류는 해당
계열사 코드와 함께 HTTP 500으로 반환됩니다. Oracle을 활성화하려면 조직에서 승인한 Oracle JDBC
드라이버도 Maven 의존성에 제공되어야 합니다.

## MCP 연계

MCP 포털의 전송 방식(Streamable HTTP/SSE), 인증, tool schema가 확정되면 이 REST API를 내부 서비스로 두고 `customer_search` MCP Tool을 추가한다. MCP는 포털과의 호출 규약이며, Data Source·가상 View·Query Pushdown은 이 서비스 내부 책임으로 유지한다.
