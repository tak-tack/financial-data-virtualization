package com.bnk.platform.virtualization.service;

import com.bnk.platform.virtualization.adapter.SourceAdapter;
import com.bnk.platform.virtualization.adapter.SourceQuery;
import com.bnk.platform.virtualization.config.AffiliateProperties;
import com.bnk.platform.virtualization.domain.CustomerView;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class VirtualCustomerService {
    private final AffiliateProperties properties;
    private final SourceSqlBuilder sqlBuilder;
    private final SourceAdapter adapter;
    public VirtualCustomerService(AffiliateProperties p, SourceSqlBuilder s, SourceAdapter a) { properties=p; sqlBuilder=s; adapter=a; }
    public List<CustomerView> findByCustomerId(String customerId) {
        return properties.getAffiliates().entrySet().stream()
                .filter(e -> e.getValue().isEnabled())
                .flatMap(e -> adapter.execute(new SourceQuery(e.getKey(), e.getValue(), sqlBuilder.customerById(e.getValue())), customerId).stream())
                .toList();
    }
}
