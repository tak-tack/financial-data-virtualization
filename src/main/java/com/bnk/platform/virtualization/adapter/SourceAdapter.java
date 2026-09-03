package com.bnk.platform.virtualization.adapter;

import com.bnk.platform.virtualization.domain.ProductView;
import java.util.List;

public interface SourceAdapter {
    List<ProductView> execute(SourceQuery query, String itemName);
}
