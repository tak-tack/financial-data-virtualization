package com.bnk.platform.virtualization.service;

import com.bnk.platform.virtualization.config.AffiliateProperties;
import org.springframework.stereotype.Component;

@Component
public class SourceSqlBuilder {
    /** 상품명을 원천으로 전달하는 Query Pushdown SQL. 값은 Adapter에서 PreparedStatement로 바인딩해야 한다. */
    public String productByName(AffiliateProperties.Affiliate source) {
        String table = source.getProductTable();
        if (!table.matches("[A-Za-z0-9_$.]+")) throw new IllegalArgumentException("허용되지 않은 테이블명");
        return switch (source.getDbms()) {
            case POSTGRESQL -> "SELECT ITEM_CODE, ITEM_NAME, ITEM_TYPE, ANNUAL_RATE, STATUS, LAST_CHANGED_AT "
                    + "FROM " + table + " WHERE ITEM_NAME = ?";
            case MYSQL -> "SELECT PRODUCT_ID AS ITEM_CODE, PRODUCT_TITLE AS ITEM_NAME, CATEGORY AS ITEM_TYPE, "
                    + "BASE_RATE AS ANNUAL_RATE, ENABLED AS STATUS, MODIFIED_AT AS LAST_CHANGED_AT "
                    + "FROM " + table + " WHERE PRODUCT_TITLE = ?";
            default -> throw new IllegalArgumentException("상품 조회 SQL이 아직 정의되지 않은 DBMS: " + source.getDbms());
        };
    }
}
