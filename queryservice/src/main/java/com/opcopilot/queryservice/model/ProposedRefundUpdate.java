package com.opcopilot.queryservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class ProposedRefundUpdate {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String orderId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RefundState refundState;

    @Column(length = 2000)
    private String reason;

    @Column(nullable = false)
    private LocalDateTime createdDate;

    @Column(nullable = false)
    private LocalDateTime updateDate;

    @Column(nullable = false)
    @CreationTimestamp
    private LocalDateTime createAt;

    @Column(nullable = false)
    @UpdateTimestamp
    private LocalDateTime updateAt;

    public ProposedRefundUpdate(String orderId, RefundState refundState, String reason, LocalDateTime createdDate, LocalDateTime updateDate) {
        this.orderId = orderId;
        this.refundState = refundState;
        this.reason = reason;
        this.createdDate = createdDate;
        this.updateDate = updateDate;
        this.createAt = createdDate;
        this.updateAt = updateDate;
    }

}
