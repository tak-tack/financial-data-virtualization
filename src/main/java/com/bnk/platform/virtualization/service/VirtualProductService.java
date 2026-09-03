package com.bnk.platform.virtualization.service;

import com.bnk.platform.virtualization.adapter.SourceAdapter;
import com.bnk.platform.virtualization.adapter.SourceQuery;
import com.bnk.platform.virtualization.config.AffiliateProperties;
import com.bnk.platform.virtualization.domain.ProductView;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class VirtualProductService {
    private final AffiliateProperties properties;
    private final SourceAdapter adapter;

    public VirtualProductService(AffiliateProperties properties, SourceAdapter adapter) {
        this.properties = properties;
        this.adapter = adapter;
    }

    public List<ProductView> findByItemName(String itemName) {
        return properties.getAffiliates().entrySet().stream()
                .filter(entry -> entry.getValue().isEnabled())
                .flatMap(entry -> adapter.execute(
                        new SourceQuery(entry.getKey(), entry.getValue()), itemName).stream())
                .toList();
    }
}
