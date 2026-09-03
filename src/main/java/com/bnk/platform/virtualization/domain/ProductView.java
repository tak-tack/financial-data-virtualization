package com.bnk.platform.virtualization.domain;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/** 각 계열사의 원천 상품 정보를 표준화해 제공하는 논리 뷰 모델입니다. */
public record ProductView(String affiliateCode, String itemCode, String itemName, String itemType,
                          BigDecimal annualRate, String status, OffsetDateTime lastChangedAt) { }
