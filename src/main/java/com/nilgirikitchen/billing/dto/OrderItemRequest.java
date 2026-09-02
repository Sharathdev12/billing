// OrderItemRequest.java
package com.nilgirikitchen.billing.dto;

public record OrderItemRequest(Long menuItemId, Long variantId, Integer quantity) {}