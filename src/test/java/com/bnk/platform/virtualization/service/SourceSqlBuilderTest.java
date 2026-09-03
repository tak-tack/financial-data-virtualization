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
        source.setProductTable("AFF_B.FINANCIAL_PRODUCTS");

        assertThat(new SourceSqlBuilder().productByName(source))
                .contains("PRD_CD AS ITEM_CODE")
                .contains("USE_YN AS STATUS")
                .contains("WHERE PRD_NM = ?");
    }
}
