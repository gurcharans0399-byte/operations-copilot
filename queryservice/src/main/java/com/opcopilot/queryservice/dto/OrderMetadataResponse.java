package com.opcopilot.queryservice.dto;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class OrderMetadataResponse {
    private String userEmail;
    private String userFullName;
    private String userAddress;
    private String orderUpdateDate;
    private String OrderAmount;
}
