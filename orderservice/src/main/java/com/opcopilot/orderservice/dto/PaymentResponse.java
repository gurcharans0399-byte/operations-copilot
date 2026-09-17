package com.opcopilot.orderservice.dto;

public record PaymentResponse(String orderId,
                              String userId,
                              String paymentStatus,
                              String paymentError,
                              String paymentTimestamp){}
