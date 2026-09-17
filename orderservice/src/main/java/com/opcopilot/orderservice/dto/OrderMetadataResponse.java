package com.opcopilot.orderservice.dto;

import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderMetadataResponse {
    private String userEmail;
    private String userFullName;
    private String userAddress;
    private String orderUpdateDate;
    private String orderAmount;
}
