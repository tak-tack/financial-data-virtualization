package com.bnk.platform.virtualization.config;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * application.yml의 {@code virtualization.affiliates} 설정을 자바 객체로 읽어 오는 클래스입니다.
 *
 * <p>예를 들어 {@code virtualization.affiliates.local-db} 설정은 {@code affiliates} Map의
 * {@code "local-db"} 항목으로 저장됩니다. 서비스는 이 설정을 보고 어떤 DB를 조회할지,
 * 접속 정보와 고객 테이블 이름이 무엇인지를 판단합니다.</p>
 */
@ConfigurationProperties(prefix = "virtualization")
public class AffiliateProperties {
    /** Data Source 식별자(local-db, docker-db 등)와 접속 설정의 목록입니다. */
    private final Map<String, Affiliate> affiliates = new LinkedHashMap<>();
    public Map<String, Affiliate> getAffiliates() { return affiliates; }

    /** 한 개 Data Source(계열사 또는 DB 연결)의 설정값입니다. */
    public static class Affiliate {
        /** 로그나 응답에서 보여 줄 사람이 읽기 쉬운 이름입니다. */
        private String displayName;
        /** 연결할 DBMS 종류입니다. DB별 JDBC 연결 방식을 고를 때 사용합니다. */
        private Dbms dbms;
        /** true이면 고객 조회 시 이 Data Source를 호출하고, false이면 건너뜁니다. */
        private boolean enabled = false;
        /** JDBC 접속 주소입니다. 예: jdbc:postgresql://localhost:5432/customer */
        private String jdbcUrl;
        /** DB 읽기 전용 접속 계정입니다. */
        private String username;
        /** DB 접속 비밀번호입니다. 운영 환경에서는 환경변수 또는 Secret으로 주입합니다. */
        private String password;
        /** 상품 정보를 조회할 원천 테이블 이름입니다. */
        private String productTable = "PRODUCT_MASTER";
        public String getDisplayName() { return displayName; }
        public void setDisplayName(String v) { displayName = v; }
        public Dbms getDbms() { return dbms; }
        public void setDbms(Dbms v) { dbms = v; }
        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean v) { enabled = v; }
        public String getJdbcUrl() { return jdbcUrl; }
        public void setJdbcUrl(String v) { jdbcUrl = v; }
        public String getUsername() { return username; }
        public void setUsername(String v) { username = v; }
        public String getPassword() { return password; }
        public void setPassword(String v) { password = v; }
        public String getProductTable() { return productTable; }
        public void setProductTable(String v) { productTable = v; }
    }
    /** 지원하는 DBMS 구분값입니다. 새 DBMS를 쓰려면 여기에 값을 추가합니다. */
    public enum Dbms { ORACLE, POSTGRESQL, MSSQL, MOCK, MYSQL }
}
