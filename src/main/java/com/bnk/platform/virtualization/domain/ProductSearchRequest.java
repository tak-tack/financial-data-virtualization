package com.bnk.platform.virtualization.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProductSearchRequest(@NotBlank @Size(max = 200) String itemName) { }
