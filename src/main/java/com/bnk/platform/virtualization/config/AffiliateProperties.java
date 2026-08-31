package com.bnk.platform.virtualization.config;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "virtualization")
public class AffiliateProperties {
    private final Map<String, Affiliate> affiliates = new LinkedHashMap<>();
    public Map<String, Affiliate> getAffiliates() { return affiliates; }

    public static class Affiliate {
        private String displayName;
        private Dbms dbms;
        private boolean enabled = false;
        private String jdbcUrl;
        private String username;
        private String password;
        private String customerTable = "CUSTOMER_MASTER";
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
        public String getCustomerTable() { return customerTable; }
        public void setCustomerTable(String v) { customerTable = v; }
    }
    public enum Dbms { ORACLE, POSTGRESQL, MSSQL, MOCK }
}
