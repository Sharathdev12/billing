package com.nilgirikitchen.billing.dto;

import com.nilgirikitchen.billing.enums.PaymentType;

public record CollectPaymentRequest(PaymentType paymentType) {}
