package com.opcopilot.orderservice.dto;

import lombok.*;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatusResponse {
    private String orderStatus;
    private String orderUpdateDate;
    private String comments;

    public OrderStatusResponse(String orderStatus, String orderUpdateDate) {
        this.orderStatus = orderStatus;
        this.orderUpdateDate = orderUpdateDate;
    }
}
