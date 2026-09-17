package com.opcopilot.orderservice.dto;

import com.opcopilot.orderservice.model.RefundState;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RefundRequest {
    private String reason;
    private RefundState refundState;
}
