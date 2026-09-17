package com.opcopilot.orderservice.controller;

import com.opcopilot.orderservice.dto.OrderMetadataResponse;
import com.opcopilot.orderservice.dto.OrderStatusResponse;
import com.opcopilot.orderservice.dto.RefundRequest;
import com.opcopilot.orderservice.exception.InvalidAPIParameterException;
import com.opcopilot.orderservice.model.Order;
import com.opcopilot.orderservice.model.Refund;
import com.opcopilot.orderservice.model.User;
import com.opcopilot.orderservice.repository.OrderRepository;
import com.opcopilot.orderservice.repository.RefundRepository;
import com.opcopilot.orderservice.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
public class OrderController {

    OrderRepository orderRepository;
    UserRepository userRepository;
    RefundRepository refundRepository;

    OrderController(OrderRepository orderRepository, UserRepository userRepository, RefundRepository refundRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.refundRepository = refundRepository;
    }

    @GetMapping("/order/status/{orderId}")
    public OrderStatusResponse getOrderStatus(@PathVariable String orderId) {
        UUID parsedOrderId;
        try {
            parsedOrderId = UUID.fromString(orderId);
        } catch (IllegalArgumentException ex) {
            throw new InvalidAPIParameterException("Invalid orderId provided: " + orderId);
        }

        Order order = orderRepository.findById(parsedOrderId)
                .orElseThrow(() -> new RuntimeException("Order not found for orderId: " + orderId));
        OrderStatusResponse orderStatus = OrderStatusResponse.builder()
                .orderStatus(order.getStatus().toString())
                .orderUpdateDate(order.getUpdateDate().toString())
                .comments(order.getComments())
                .build();
        return orderStatus;
    }

    @GetMapping("/order/metadata/{orderId}")
    public OrderMetadataResponse getOrderMetadata(@PathVariable String orderId) {
        UUID parsedOrderId;
        try {
            parsedOrderId = UUID.fromString(orderId);
        } catch (IllegalArgumentException ex) {
            throw new InvalidAPIParameterException("Invalid orderId provided: " + orderId);
        }

        Order order = orderRepository.findById(parsedOrderId)
                .orElseThrow(() -> new InvalidAPIParameterException("Order not found for orderId: " + orderId));
        User user = userRepository.findById(order.getUser().getUserId()).orElseThrow(() -> new InvalidAPIParameterException(
                "User not found for order: " + orderId));

        OrderMetadataResponse orderMetadata = OrderMetadataResponse.builder()
                .userEmail(user.getEmail())
                .userFullName(user.getFullName())
                .userAddress(user.getAddress())
                .orderAmount(order.getAmount().toString())
                .orderUpdateDate(order.getUpdateDate().toString())
                .build();

        return orderMetadata;
    }

    @PostMapping("/order/{orderId}/refund")
    public ResponseEntity<Refund> createRefund(@PathVariable String orderId, @RequestBody RefundRequest refundRequest) {
        UUID parsedOrderId;
        try {
            parsedOrderId = UUID.fromString(orderId);
        } catch (IllegalArgumentException ex) {
            throw new InvalidAPIParameterException("Invalid orderId provided: " + orderId);
        }

        Order order = orderRepository.findById(parsedOrderId)
                .orElseThrow(() -> new InvalidAPIParameterException("Order not found for orderId: " + orderId));

        if (refundRequest.getReason() == null || refundRequest.getReason().isBlank()) {
            throw new InvalidAPIParameterException("Reason is required for refund");
        }

        if (refundRequest.getRefundState() == null) {
            throw new InvalidAPIParameterException("RefundState is required for refund");
        }

        // Check if refund already exists for this order
        var existingRefund = refundRepository.findByOrder(order);
        
        if (existingRefund.isPresent()) {
            // Update existing refund
            Refund refund = existingRefund.get();
            refund.setReason(refundRequest.getReason());
            refund.setRefundState(refundRequest.getRefundState());
            Refund updatedRefund = refundRepository.save(refund);
            return ResponseEntity.ok(updatedRefund);
        } else {
            // Create new refund
            Refund refund = Refund.builder()
                    .order(order)
                    .reason(refundRequest.getReason())
                    .refundState(refundRequest.getRefundState())
                    .build();

            Refund savedRefund = refundRepository.save(refund);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedRefund);
        }
    }
}
