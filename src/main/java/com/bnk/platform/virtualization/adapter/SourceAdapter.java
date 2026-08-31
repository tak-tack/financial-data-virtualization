package com.bnk.platform.virtualization.adapter;

import com.bnk.platform.virtualization.domain.CustomerView;
import java.util.List;

public interface SourceAdapter {
    List<CustomerView> execute(SourceQuery query, String customerId);
}
