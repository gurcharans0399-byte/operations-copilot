package com.opcopilot.queryservice.restclient;

import com.opcopilot.queryservice.dto.DeliveryResponse;
import com.opcopilot.queryservice.dto.OrderMetadataResponse;
import com.opcopilot.queryservice.dto.OrderStatusResponse;
import com.opcopilot.queryservice.dto.PaymentResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(
        name = "order-service",
        url = "${order-service.url:http://localhost:8081}"
)
public interface OrderServiceClient {
    @GetMapping("/order/{orderId}")
    public OrderStatusResponse getOrderStatus(@PathVariable String orderId);

    @GetMapping("/order/{orderId}/payment")
    public List<PaymentResponse> getPaymentByOrder(@PathVariable String orderId);

    @GetMapping("/order/payment/{paymentId}")
    public List<PaymentResponse> getPayment(@PathVariable String paymentId);

    @GetMapping("/order/{orderId}/delivery/logs")
    public List<DeliveryResponse> getDeliveryLog(@PathVariable String orderId);
}
