package com.nilgirikitchen.billing.dto;

import java.math.BigDecimal;

public record MenuItemVariantRequest(String variantName, BigDecimal price) {}
