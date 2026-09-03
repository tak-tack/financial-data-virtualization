package com.bnk.platform.virtualization.adapter;

import static org.assertj.core.api.Assertions.assertThat;

import com.bnk.platform.virtualization.config.AffiliateProperties;
import com.bnk.platform.virtualization.service.SourceSqlBuilder;
import org.junit.jupiter.api.Test;

class JdbcSourceAdapterTest {
    @Test
    void executesPreparedQueryAndMapsProductView() throws Exception {
        var affiliate = new AffiliateProperties.Affiliate();
        affiliate.setJdbcUrl("jdbc:h2:mem:affiliate;MODE=PostgreSQL;DB_CLOSE_DELAY=-1");
        affiliate.setUsername("sa");
        affiliate.setPassword("");

        try (var connection = java.sql.DriverManager.getConnection(affiliate.getJdbcUrl(), "sa", "");
             var statement = connection.createStatement()) {
            statement.execute("CREATE TABLE PRODUCT_MASTER (ITEM_CODE VARCHAR(30), ITEM_NAME VARCHAR(100), "
                    + "ITEM_TYPE VARCHAR(30), ANNUAL_RATE DECIMAL(10,2), STATUS VARCHAR(30), LAST_CHANGED_AT TIMESTAMP)");
            statement.execute("INSERT INTO PRODUCT_MASTER VALUES "
                    + "('C-SAVE-001', '테스트 적금', '02', 3.25, 'ACTIVE', TIMESTAMP '2024-01-15 12:00:00')");
        }

        var adapter = new JdbcSourceAdapter(new SourceSqlBuilder());
        try {
            affiliate.setDbms(AffiliateProperties.Dbms.POSTGRESQL);
            affiliate.setProductTable("PRODUCT_MASTER");
            var result = adapter.execute(new SourceQuery("affiliate-a", affiliate), "테스트 적금");

            assertThat(result).singleElement().satisfies(customer -> {
                assertThat(customer.affiliateCode()).isEqualTo("affiliate-a");
                assertThat(customer.itemCode()).isEqualTo("C-SAVE-001");
                assertThat(customer.annualRate()).isEqualByComparingTo("3.25");
            });
        } finally {
            adapter.destroy();
        }
    }
}
