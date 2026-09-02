// OrderItemResponse.java
package com.nilgirikitchen.billing.dto;

import java.math.BigDecimal;

public record OrderItemResponse(String itemName, String variantName, BigDecimal unitPrice,
                                 Integer quantity, BigDecimal lineTotal) {}