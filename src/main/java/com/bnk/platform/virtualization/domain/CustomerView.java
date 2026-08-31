package com.bnk.platform.virtualization.domain;

import java.time.LocalDate;

/** 계열사 원천 컬럼을 표준화해 외부에 제공하는 논리 뷰 모델. */
public record CustomerView(String affiliateCode, String customerId, String customerName,
                           String customerType, String status, LocalDate joinedDate) { }
