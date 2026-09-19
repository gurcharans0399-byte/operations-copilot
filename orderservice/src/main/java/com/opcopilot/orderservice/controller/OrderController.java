package com.opcopilot.orderservice.controller;

import com.opcopilot.orderservice.dto.OrderResponse;
import com.opcopilot.orderservice.dto.RefundRequest;
import com.opcopilot.orderservice.dto.UserDetailResponse;
import com.opcopilot.orderservice.exception.InvalidAPIRequestException;
import com.opcopilot.orderservice.exception.InvalidFeignCallException;
import com.opcopilot.orderservice.model.Order;
import com.opcopilot.orderservice.model.Refund;
import com.opcopilot.orderservice.repository.OrderRepository;
import com.opcopilot.orderservice.repository.RefundRepository;
import com.opcopilot.orderservice.security.AuthContextUtil;
import com.opcopilot.orderservice.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@RestController
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    OrderRepository orderRepository;
    RefundRepository refundRepository;
    OrderService orderService;
    RestTemplate restTemplate;
    AuthContextUtil authContextUtil;

    OrderController(OrderRepository orderRepository,
                    RefundRepository refundRepository,
                    OrderService orderService,
                    RestTemplate restTemplate,
                    AuthContextUtil authContextUtil) {
        this.orderRepository = orderRepository;
        this.refundRepository = refundRepository;
        this.orderService = orderService;
        this.restTemplate = restTemplate;
        this.authContextUtil = authContextUtil;
    }

    @GetMapping("/order/{orderId}")
    public OrderResponse getOrderInfo(@PathVariable String orderId) {
        UUID parsedOrderId;
        try {
            parsedOrderId = UUID.fromString(orderId);
        } catch (IllegalArgumentException ex) {
            throw new InvalidAPIRequestException("Invalid orderId provided: " + orderId);
        }

        Order order = orderRepository.findById(parsedOrderId)
                .orElseThrow(() -> new InvalidAPIRequestException("Order not found for orderId: " + orderId));

        UserDetailResponse userDetails = authContextUtil.getLoggedInUserDetailsFromAuthService();

        OrderResponse orderMetadata = OrderResponse.builder()
                .userEmail(userDetails.getEmail())
                .userFullName(userDetails.getName())
                .orderAmount(order.getAmount().toString())
                .orderUpdateDate(order.getUpdateDate().toString())
                .orderStatus(order.getStatus().toString())
                .comments(order.getComments())
                .build();

        return orderMetadata;
    }

    @PostMapping("/order/{orderId}/refund")
    public ResponseEntity<Refund> createRefund(@PathVariable String orderId, @RequestBody RefundRequest refundRequest) {
        UUID parsedOrderId;
        try {
            parsedOrderId = UUID.fromString(orderId);
        } catch (IllegalArgumentException ex) {
            throw new InvalidAPIRequestException("Invalid orderId provided: " + orderId);
        }

        Order order = orderRepository.findById(parsedOrderId)
                .orElseThrow(() -> new InvalidAPIRequestException("Order not found for orderId: " + orderId));

        if (refundRequest.getReason() == null || refundRequest.getReason().isBlank()
                || refundRequest.getRefundState() == null) {
            throw new InvalidAPIRequestException("Reason and RefundState are required for refund");
        }

        Refund refund = orderService.updateOrCreateRefund(order, refundRequest);
        return ResponseEntity.ok(refund);
    }
}
