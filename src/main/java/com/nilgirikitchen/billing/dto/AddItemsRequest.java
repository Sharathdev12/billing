package com.nilgirikitchen.billing.dto;

import java.util.List;

public record AddItemsRequest(List<OrderItemRequest> items) {}
