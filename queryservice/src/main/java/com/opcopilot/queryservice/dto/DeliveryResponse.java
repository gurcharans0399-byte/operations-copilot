package com.opcopilot.queryservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryResponse {
    private String deliveryId;
    private String orderId;
    private String status;
    private String createdAt;
}
