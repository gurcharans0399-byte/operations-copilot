package com.opcopilot.orderservice.model;

public enum OrderStatus {
    IN_PROGRESS,
    PAYMENT_PENDING,
    PAYMENT_ERROR,
    CONFIRMED,
    SHIPPED,
    DELIVERED,
    REFUND_IN_PROGRESS,
    REFUND_INITIATED,
    REFUND_REJECTED
}
