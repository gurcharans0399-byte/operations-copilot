package com.opcopilot.orderservice.controller;

import com.opcopilot.orderservice.dto.DeliveryResponse;
import com.opcopilot.orderservice.exception.InvalidAPIRequestException;
import com.opcopilot.orderservice.model.OrderDeliveryLog;
import com.opcopilot.orderservice.repository.DeliveryRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
public class DeliveryController {

    DeliveryRepository deliveryRepository;

    DeliveryController(DeliveryRepository deliveryRepository) {
        this.deliveryRepository = deliveryRepository;
    }

    @GetMapping("/order/{orderId}/delivery/logs")
    public List<DeliveryResponse> getDeliveryInfo(@PathVariable String orderId) {
        UUID parsedOrderId;
        try {
            parsedOrderId = UUID.fromString(orderId);
        } catch (IllegalArgumentException ex) {
            throw new InvalidAPIRequestException("Invalid orderId provided: " + orderId);
        }

        List<OrderDeliveryLog> deliveryLogs = deliveryRepository.findByOrder_orderId(parsedOrderId);
        List<DeliveryResponse> deliveryLogList = deliveryLogs.stream()
                .map(deliveryLog -> new DeliveryResponse(deliveryLog.getId().toString(),
                        deliveryLog.getOrder().getOrderId().toString(),
                        deliveryLog.getStatus().toString(),
                        deliveryLog.getCreatedAt().toString()))
                .collect(Collectors.toList());
        return deliveryLogList;
    }
}
