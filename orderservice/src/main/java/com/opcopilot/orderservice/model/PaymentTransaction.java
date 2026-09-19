package com.opcopilot.orderservice.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class PaymentTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String  userId;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    private PaymentStatus status;

    private String error;

    @CreationTimestamp
    private LocalDateTime timestamp;
}
