package com.nilgirikitchen.billing.dto;

import java.math.BigDecimal;

public record MenuItemVariantResponse(Long id, String variantName, BigDecimal price) {}
