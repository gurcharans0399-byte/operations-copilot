package com.opcopilot.orderservice.repository;

import com.opcopilot.orderservice.model.OrderDeliveryLog;
import com.opcopilot.orderservice.model.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DeliveryRepository extends JpaRepository<OrderDeliveryLog, UUID> {
    List<OrderDeliveryLog> findByOrder_orderId(UUID orderId);
}
