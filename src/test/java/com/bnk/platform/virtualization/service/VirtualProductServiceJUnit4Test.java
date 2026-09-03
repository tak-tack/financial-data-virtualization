package com.bnk.platform.virtualization.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.bnk.platform.virtualization.adapter.SourceAdapter;
import com.bnk.platform.virtualization.adapter.SourceQuery;
import com.bnk.platform.virtualization.config.AffiliateProperties;
import com.bnk.platform.virtualization.domain.ProductView;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class VirtualProductServiceJUnit4Test {
    @Mock
    private SourceAdapter sourceAdapter;

    private AffiliateProperties properties;
    private VirtualProductService service;

    @Before
    public void setUp() {
        properties = new AffiliateProperties();
        properties.getAffiliates().put("affiliate-a", enabledAffiliate());
        service = new VirtualProductService(properties, sourceAdapter);
    }

    @Test
    public void injectedMockAdapterResultIsReturned() {
        String itemName = "테스트 적금";
        ProductView expected = new ProductView(
                "affiliate-a", "P-001", itemName, "SAVINGS",
                new BigDecimal("3.25"), "ACTIVE",
                OffsetDateTime.parse("2026-01-01T00:00:00+09:00"));
        ArgumentCaptor<SourceQuery> queryCaptor = ArgumentCaptor.forClass(SourceQuery.class);

        when(sourceAdapter.execute(any(SourceQuery.class), eq(itemName)))
                .thenReturn(List.of(expected));

        List<ProductView> result = service.findByItemName(itemName);

        assertThat(result).containsExactly(expected);
        verify(sourceAdapter).execute(queryCaptor.capture(), eq(itemName));
        assertThat(queryCaptor.getValue().affiliateCode()).isEqualTo("affiliate-a");
    }

    private AffiliateProperties.Affiliate enabledAffiliate() {
        AffiliateProperties.Affiliate affiliate = new AffiliateProperties.Affiliate();
        affiliate.setEnabled(true);
        affiliate.setDbms(AffiliateProperties.Dbms.ORACLE);
        affiliate.setProductTable("TB_FIN_PRODUCT");
        return affiliate;
    }
}
