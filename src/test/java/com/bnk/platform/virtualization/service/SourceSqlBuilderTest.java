package com.bnk.platform.virtualization.service;

import com.bnk.platform.virtualization.config.AffiliateProperties;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class SourceSqlBuilderTest {
    @Test void itemNameConditionIsPushedDown() {
        var source = new AffiliateProperties.Affiliate();
        source.setDbms(AffiliateProperties.Dbms.POSTGRESQL);
        source.setProductTable("PRODUCT_MASTER");
        assertThat(new SourceSqlBuilder().productByName(source)).contains("FROM PRODUCT_MASTER WHERE ITEM_NAME = ?");
    }

    @Test void mysqlColumnsAreMappedToTheStandardProductFields() {
        var source = new AffiliateProperties.Affiliate();
        source.setDbms(AffiliateProperties.Dbms.MYSQL);
        source.setProductTable("FINANCIAL_PRODUCTS");

        assertThat(new SourceSqlBuilder().productByName(source))
                .contains("PRODUCT_ID AS ITEM_CODE")
                .contains("PRODUCT_TITLE AS ITEM_NAME")
                .contains("CATEGORY AS ITEM_TYPE")
                .contains("BASE_RATE AS ANNUAL_RATE")
                .contains("ENABLED AS STATUS")
                .contains("MODIFIED_AT AS LAST_CHANGED_AT")
                .contains("FROM FINANCIAL_PRODUCTS WHERE PRODUCT_TITLE = ?");
    }
}
