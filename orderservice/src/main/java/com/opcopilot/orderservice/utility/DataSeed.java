package com.opcopilot.orderservice.utility;

import com.opcopilot.orderservice.dto.UserDetailResponse;
import com.opcopilot.orderservice.model.*;
import com.opcopilot.orderservice.repository.DeliveryRepository;
import com.opcopilot.orderservice.repository.OrderRepository;
import com.opcopilot.orderservice.repository.PaymentRepository;
import com.opcopilot.orderservice.security.AuthContextUtil;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class DataSeed implements CommandLineRunner {
    private OrderRepository orderRepository;
    private PaymentRepository paymentRepository;
    private DeliveryRepository deliveryRepository;
    private RestTemplate restTemplate;
    private AuthContextUtil authContextUtil;

    DataSeed(OrderRepository orderRepository,
             PaymentRepository paymentRepository,
             DeliveryRepository deliveryRepository,
             RestTemplate restTemplate,
             AuthContextUtil authContextUtil) {
        this.orderRepository = orderRepository;
        this.paymentRepository = paymentRepository;
        this.deliveryRepository = deliveryRepository;
        this.restTemplate = restTemplate;
        this.authContextUtil = authContextUtil;
    }

    @Override
    public void run(String... args) throws Exception {
        UserDetailResponse userDetails = authContextUtil.getUserDetailsFromAuthService("admin@gmail.com");


        Order order1 = Order.builder().userId(userDetails.getId().toString()).status(OrderStatus.DELIVERED).amount(new BigDecimal("2001.5"))
                .creationDate(LocalDateTime.now())
                .updateDate(LocalDateTime.now())
                .build();

        Order order2 = Order.builder().userId(userDetails.getId().toString()).status(OrderStatus.PAYMENT_ERROR).amount(new BigDecimal("2002.5"))
                .creationDate(LocalDateTime.now())
                .updateDate(LocalDateTime.now())
                .build();

        Order order3 = Order.builder().userId(userDetails.getId().toString()).status(OrderStatus.SHIPPED).amount(new BigDecimal("2003.5"))
                .creationDate(LocalDateTime.of(2026, 9, 10, 10,10))
                .updateDate(LocalDateTime.of(2026, 9, 11, 10,10))
                .build();

        Order savedOrder1 = orderRepository.save(order1);
        Order savedOrder2 = orderRepository.save(order2);
        Order savedOrder3 = orderRepository.save(order3);

        PaymentTransaction payment1 = PaymentTransaction.builder().userId(userDetails.getId().toString()).order(order1).status(PaymentStatus.COMPLETED).build();
        PaymentTransaction payment2 = PaymentTransaction.builder().userId(userDetails.getId().toString()).order(order2).status(PaymentStatus.TIMED_OUT).error("Internal Server error").build();
        PaymentTransaction payment3 = PaymentTransaction.builder().userId(userDetails.getId().toString()).order(order2).status(PaymentStatus.GATEWAY_ERROR).error("Gateway down for maintenance").build();
        PaymentTransaction payment4 = PaymentTransaction.builder().userId(userDetails.getId().toString()).order(order3).status(PaymentStatus.COMPLETED).build();

        paymentRepository.saveAll(List.of(payment1, payment2, payment3, payment4));

        OrderDeliveryLog delivery1 = OrderDeliveryLog.builder()
                .order(order1)
                .status(DeliveryStatus.ORDER_RECEIVED)
                .comments("Order received and queued for processing")
                .createdAt(LocalDateTime.of(2026,9,14, 10, 10))
                .build();
        OrderDeliveryLog delivery2 = OrderDeliveryLog.builder()
                .order(order1)
                .status(DeliveryStatus.PREPARING_SHIPMENT)
                .comments("Items packed and prepared for dispatch")
                .createdAt(LocalDateTime.of(2026,9,14, 12, 0))
                .build();
        OrderDeliveryLog delivery3 = OrderDeliveryLog.builder()
                .order(order1)
                .status(DeliveryStatus.SHIPPED)
                .comments("Shipment handed over to courier")
                .createdAt(LocalDateTime.now())
                .build();
        OrderDeliveryLog delivery4 = OrderDeliveryLog.builder()
                .order(savedOrder3)
                .status(DeliveryStatus.ORDER_RECEIVED)
                .comments("Order placed successfully and awaiting fulfillment")
                .createdAt(LocalDateTime.of(2026, 9, 10, 10,10))
                .build();
        OrderDeliveryLog delivery5 = OrderDeliveryLog.builder()
                .order(savedOrder3)
                .status(DeliveryStatus.SHIPPED)
                .comments("Package shipped to the customer address")
                .createdAt(LocalDateTime.of(2026, 9, 11, 10,10))
                .build();
        OrderDeliveryLog delivery6 = OrderDeliveryLog.builder()
                .order(savedOrder3)
                .status(DeliveryStatus.PREPARING_SHIPMENT)
                .comments("Customer order is being packed for shipment")
                .createdAt(LocalDateTime.of(2026, 9, 12, 10,10))
                .build();

        deliveryRepository.saveAll(List.of(delivery1, delivery2, delivery3, delivery4, delivery5, delivery6));

        System.out.println("All Orders: ");
        List.of(savedOrder1, savedOrder2, savedOrder3).stream()
                        .forEach(o -> System.out.println("Id: " + o.getOrderId().toString()
                                + " status: " + o.getStatus().toString()));
    }
}
