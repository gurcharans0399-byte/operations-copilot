package com.opcopilot.orderservice.service;

import com.opcopilot.orderservice.dto.RefundRequest;
import com.opcopilot.orderservice.model.Order;
import com.opcopilot.orderservice.model.Refund;
import com.opcopilot.orderservice.repository.RefundRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    RefundRepository refundRepository;

    OrderService(RefundRepository refundRepository) {
        this.refundRepository = refundRepository;
    }

    public Refund updateOrCreateRefund(Order order, RefundRequest refundRequest){
        var existingRefund = refundRepository.findByOrder(order);
        Refund refund;
        if (existingRefund.isPresent()) {
            refund = existingRefund.get();
            refund.setReason(refundRequest.getReason());
            refund.setRefundState(refundRequest.getRefundState());
        } else {
            refund = Refund.builder()
                    .order(order)
                    .reason(refundRequest.getReason())
                    .refundState(refundRequest.getRefundState())
                    .build();
        }
        return refundRepository.save(refund);
    }
}
