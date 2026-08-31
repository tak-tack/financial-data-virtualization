package com.bnk.platform.virtualization.service;

import com.bnk.platform.virtualization.config.AffiliateProperties;
import org.springframework.stereotype.Component;

@Component
public class SourceSqlBuilder {
    /** 조건을 원천으로 전달하는 Query Pushdown SQL. 값은 Adapter에서 PreparedStatement로 바인딩해야 한다. */
    public String customerById(AffiliateProperties.Affiliate source) {
        String table = source.getCustomerTable();
        if (!table.matches("[A-Za-z0-9_$.]+")) throw new IllegalArgumentException("허용되지 않은 테이블명");
        return "SELECT CUSTOMER_ID, CUSTOMER_NAME, CUSTOMER_TYPE, STATUS_CODE, JOINED_DATE " +
               "FROM " + table + " WHERE CUSTOMER_ID = ?";
    }
}
