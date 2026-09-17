package com.opcopilot.orderservice.model;

public enum DeliveryStatus {
    ORDER_RECEIVED,
    PREPARING_SHIPMENT,
    SHIPPED,
    OUT_FOR_DELIVERY,
    DELIVERED,
    REJECTED_AT_DELIVERY
}
