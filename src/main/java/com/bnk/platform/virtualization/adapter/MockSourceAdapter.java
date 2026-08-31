package com.bnk.platform.virtualization.adapter;

import com.bnk.platform.virtualization.domain.CustomerView;
import java.time.LocalDate;
import java.util.List;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/** 로컬 개발 전용. 외부 망 또는 실제 금융 데이터에 접속하지 않는다. */
@Component
@Profile("local")
public class MockSourceAdapter implements SourceAdapter {
    @Override public List<CustomerView> execute(SourceQuery q, String customerId) {
        return List.of(new CustomerView(q.affiliateCode(), customerId,
                "테스트고객-" + q.affiliate().getDisplayName(), "INDIVIDUAL", "ACTIVE", LocalDate.of(2024, 1, 15)));

    }
}
