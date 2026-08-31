package com.bnk.platform.virtualization.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CustomerSearchRequest(@NotBlank @Size(max = 30) String customerId) { }
