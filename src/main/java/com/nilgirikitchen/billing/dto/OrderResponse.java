// OrderResponse.java
package com.nilgirikitchen.billing.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.nilgirikitchen.billing.enums.OrderMode;
import com.nilgirikitchen.billing.enums.PaymentStatus;
import com.nilgirikitchen.billing.enums.PaymentType;

public record OrderResponse(String billNo, BigDecimal totalAmount, OrderMode orderMode,
                             PaymentStatus paymentStatus, PaymentType paymentType,
                             LocalDateTime orderDate, Boolean isDelivered,
                             List<OrderItemResponse> items) {}