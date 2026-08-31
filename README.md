# 금융 계열사 데이터 가상화 서비스 (로컬 PoC)

BNK부산은행, 경남은행, 캐피탈, 투자증권, 시스템처럼 서로 다른 DBMS를 가진 계열사 RDB를 하나의 **논리적 가상 View**로 제공하기 위한 Spring Boot 프로젝트 골격이다. 기본 프로필은 `local`이며 모든 결과를 Mock으로 반환한다. 따라서 실제 운영 서버·계정·금융 데이터에 접근하지 않는다.

## 현재 제공 범위

- 계열사별 Data Source 설정 구조 및 DBMS 유형(Oracle, PostgreSQL, MS-SQL)
- `CUSTOMER_MASTER` 예시 원천 테이블을 `CustomerView` 공통 모델로 변환하는 경계
- `POST /api/v1/virtual-views/customers/search` 조회 API
- `CUSTOMER_ID = ?` 조건을 원천 SQL에 적용하는 Query Pushdown 예시
- 향후 MCP의 `customer_search` tool이 호출할 수 있는 REST 경계

## 실행

JDK 21과 Maven 3.9 이상이 필요하다.

```bash
cd financial-data-virtualization
mvn spring-boot:run
```

```bash
curl -X POST http://localhost:8080/api/v1/virtual-views/customers/search \
  -H 'Content-Type: application/json' \
  -d '{"customerId":"CUST-0001"}'
```

각 활성 계열사의 Mock 결과가 표준 컬럼(`affiliateCode`, `customerId`, `customerName`, `customerType`, `status`, `joinedDate`)으로 반환된다.

## 실제 DB Adapter 연결 순서

1. 해당 DBMS의 JDBC Driver를 `pom.xml`에 추가한다. Oracle 드라이버는 조직의 라이선스·저장소 정책에 따라 별도 관리한다.
2. `application.yml`의 계열사별 `jdbc-url`, `username`, `password`를 Secret/환경변수로 주입한다. 계정은 반드시 읽기 전용 계정이어야 한다.
3. `SourceAdapter` 구현체에서 계열사별 HikariCP DataSource를 독립 생성하고 `PreparedStatement`로 `SourceQuery.sql()`을 실행한다.
4. 원천 컬럼과 코드값 차이는 Adapter 또는 별도 Mapper에서 `CustomerView`로 변환한다.
5. 계열사별 pool 크기, connection/query timeout, circuit breaker 및 조회 건수 제한을 추가한다.

`application.yml`의 `customer-table`과 `CustomerView`/`SourceSqlBuilder`가 최초 커스터마이징 지점이다. 실제 테이블·컬럼은 확정 전까지 예시 값이며, 개인정보 컬럼은 기본 모델에 포함하지 않았다.

## MCP 연계

MCP 포털의 전송 방식(Streamable HTTP/SSE), 인증, tool schema가 확정되면 이 REST API를 내부 서비스로 두고 `customer_search` MCP Tool을 추가한다. MCP는 포털과의 호출 규약이며, Data Source·가상 View·Query Pushdown은 이 서비스 내부 책임으로 유지한다.
