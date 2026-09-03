package com.bnk.platform.virtualization.adapter;

import com.bnk.platform.virtualization.config.AffiliateProperties;

public record SourceQuery(String affiliateCode, AffiliateProperties.Affiliate affiliate) { }
