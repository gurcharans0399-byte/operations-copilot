package com.opcopilot.queryservice.dto;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class OrderStatusResponse {
    private String orderStatus;
    private String orderUpdateDate;
    private String comments;
}
