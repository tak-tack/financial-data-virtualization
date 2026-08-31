package com.bnk.platform.virtualization.service;

import com.bnk.platform.virtualization.config.AffiliateProperties;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class SourceSqlBuilderTest {
    @Test void customerIdConditionIsPushedDown() {
        var source = new AffiliateProperties.Affiliate(); source.setCustomerTable("CUST_MASTER");
        assertThat(new SourceSqlBuilder().customerById(source)).contains("FROM CUST_MASTER WHERE CUSTOMER_ID = ?");
    }
}
