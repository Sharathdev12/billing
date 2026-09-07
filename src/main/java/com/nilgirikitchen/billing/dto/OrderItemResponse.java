// OrderItemResponse.java
package com.nilgirikitchen.billing.dto;

import com.nilgirikitchen.billing.enums.MenuCategory;
import java.math.BigDecimal;

public record OrderItemResponse(String itemName, String variantName, MenuCategory category,
                                 BigDecimal unitPrice, Integer quantity, BigDecimal lineTotal) {}
