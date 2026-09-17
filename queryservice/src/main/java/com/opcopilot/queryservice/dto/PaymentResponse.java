package com.opcopilot.queryservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse{
    private String orderId;
    private String userId;
    private String paymentStatus;
    private String paymentError;
    private String paymentTimestamp;
}