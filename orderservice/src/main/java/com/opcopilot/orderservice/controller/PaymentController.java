package com.opcopilot.orderservice.controller;

import com.opcopilot.orderservice.dto.PaymentResponse;
import com.opcopilot.orderservice.exception.InvalidAPIRequestException;
import com.opcopilot.orderservice.model.PaymentTransaction;
import com.opcopilot.orderservice.repository.PaymentRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
public class PaymentController {

    PaymentRepository paymentRepository;

    PaymentController(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @GetMapping( value = {
            "/order/{orderId}/payment/{paymentId}",
            "/order/{orderId}/payment",
            "/order/payment/{paymentId}"
            }
    )
    public List<PaymentResponse> getPayment(@PathVariable(required = false) String orderId,
                                            @PathVariable(required = false) String paymentId) {
        if(orderId != null){
            List<PaymentTransaction> paymentsByOrderId = paymentRepository.findByOrder_orderId(UUID.fromString(orderId));
            List<PaymentResponse> paymentResponseList = paymentsByOrderId.stream()
                    .map(payment -> new PaymentResponse(payment.getOrder().getOrderId().toString(),
                            payment.getUserId().toString(),
                            payment.getStatus().toString(),
                            payment.getError(),
                            payment.getTimestamp().toString()))
                    .collect(Collectors.toList());
            return paymentResponseList;
        }
        else if(paymentId != null) {
            PaymentTransaction payment =
                    paymentRepository.findById(UUID.fromString(paymentId)).orElseThrow(() -> new InvalidAPIRequestException("Payment not found for paymentId: " + paymentId));
             return List.of(new PaymentResponse(payment.getOrder().getOrderId().toString(),
                    payment.getUserId().toString(),
                    payment.getStatus().toString(),
                    payment.getError(),
                    payment.getTimestamp().toString()));
        }
        throw new InvalidAPIRequestException("Invalid request: either orderId or paymentId must be provided");
    }
}
