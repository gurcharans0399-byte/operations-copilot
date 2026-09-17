package com.opcopilot.orderservice.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class OrderDeliveryLog {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    private DeliveryStatus status;

   private String comments;

//    @CreationTimestamp
   private LocalDateTime createdAt;

//    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
