// CreateOrderRequest.java
package com.nilgirikitchen.billing.dto;

import java.util.List;

import com.nilgirikitchen.billing.enums.OrderMode;

public record CreateOrderRequest(List<OrderItemRequest> items, OrderMode orderMode) {}