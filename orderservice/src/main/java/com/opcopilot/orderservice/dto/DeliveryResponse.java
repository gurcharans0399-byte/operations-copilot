package com.opcopilot.orderservice.dto;

public record DeliveryResponse(String deliveryId,
                               String orderId,
                               String status,
                               String createdAt) {}
