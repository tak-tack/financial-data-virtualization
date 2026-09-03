package com.bnk.platform.virtualization.adapter;

import com.bnk.platform.virtualization.domain.ProductView;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/** 로컬 개발 전용. 외부 망 또는 실제 금융 데이터에 접속하지 않는다. */
@Component
@Profile("local")
public class MockSourceAdapter implements SourceAdapter {
    @Override public List<ProductView> execute(SourceQuery q, String itemName) {
        return List.of(new ProductView(q.affiliateCode(), "MOCK-001", itemName,
                "MOCK", BigDecimal.ZERO, "ACTIVE", OffsetDateTime.parse("2024-01-15T00:00:00+09:00")));

    }
}
