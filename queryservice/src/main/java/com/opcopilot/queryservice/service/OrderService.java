package com.opcopilot.queryservice.service;

import com.opcopilot.queryservice.dto.DeliveryResponse;
import com.opcopilot.queryservice.dto.PaymentResponse;
import com.opcopilot.queryservice.model.*;
import com.opcopilot.queryservice.repository.ProposedActionRepository;
import com.opcopilot.queryservice.repository.ProposedRefundUpdateRepository;
import com.opcopilot.queryservice.restclient.OrderServiceClient;
import com.opcopilot.queryservice.dto.OrderMetadataResponse;
import com.opcopilot.queryservice.dto.OrderStatusResponse;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OrderService {

    private final OrderServiceClient orderServiceClient;
    private final ProposedRefundUpdateRepository proposedRefundUpdateRepository;
    private final ProposedActionRepository proposedActionRepository;

    OrderService(OrderServiceClient orderServiceClient,
                 ProposedRefundUpdateRepository proposedRefundUpdateRepository,
                 ProposedActionRepository proposedActionRepository) {
        this.orderServiceClient = orderServiceClient;
        this.proposedRefundUpdateRepository = proposedRefundUpdateRepository;
        this.proposedActionRepository = proposedActionRepository;
    }

    @Tool(description = "fetch the status and last updated date of an order")
    OrderStatusResponse getOrderStatus(@ToolParam(description = "order ID") String orderId) {
        return orderServiceClient.getOrderStatus(orderId);
    }

    @Tool(description = "Fetch the user details and order amount for an order ID")
    OrderMetadataResponse getOrderMetadata(@ToolParam(description = "order ID") String orderId) {
        return orderServiceClient.getOrderMetadata(orderId);
    }

    @Tool(description = "Fetch all the payment transactions made corresponding to an order ID or any specific payment" +
            " made via a payment ID, " +
            "results are in the form of a list of PaymentResponse.class")
    List<PaymentResponse> getPayment(@ToolParam(description = "order ID", required = false) String orderId,
                                     @ToolParam(description = "payment Id", required = false) String paymentId) {
        List<PaymentResponse> response = new ArrayList<>();
        if(paymentId != null) {
            response = orderServiceClient.getPayment(paymentId);
        }
        else if(orderId != null) {
            response = orderServiceClient.getPaymentByOrder(orderId);
        }
        return response;
    }

    @Tool(description = "Fetch details of delivery status as a list of delivery logs representing delivery state change of " +
            "bought items. A normal delivery state change is, ORDER_RECEIVED -> PREPARING_SHIPMENT -> SHIPPED -> OUT_FOR_DELIVERY -> DELIVERED")
    List<DeliveryResponse> getDeliveryLogs(@ToolParam(description = "order ID", required = false) String orderId) {
        return orderServiceClient.getDeliveryLog(orderId);
    }

    // Update SystemMessage
    @Tool(description = "Initiate a refund for an order and create the corresponding ProposedAction to capture the " +
            "consent workflow. Only use this tool if the user has explicitly requested a refund")
    public Map<String, String> proposeRefund(@ToolParam(description = "order ID") String orderId,
                               @ToolParam(description = "refund state") RefundState refundState,
                               @ToolParam(description = "reason for refund") String reason,
                               @ToolParam(description = "created timestamp") LocalDateTime createdDate,
                               @ToolParam(description = "updated timestamp") LocalDateTime updateDate,
                               @ToolParam(description = "user ID of the loggedIn User") String initiator) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime refundCreatedDate = createdDate != null ? createdDate : now;
        LocalDateTime refundUpdatedDate = updateDate != null ? updateDate : now;

        ProposedRefundUpdate proposedRefund = new ProposedRefundUpdate(orderId, refundState, reason, refundCreatedDate, refundUpdatedDate);
        proposedRefund.setCreateAt(refundCreatedDate);
        proposedRefund.setUpdateAt(refundUpdatedDate);
        ProposedRefundUpdate savedRefund = proposedRefundUpdateRepository.save(proposedRefund);

        ProposedAction proposedAction = new ProposedAction(
                ActionType.INITIATE_REFUND,
                String.valueOf(savedRefund.getId()),
                ActionState.REQUIRE_CONSENT,
                false,
                initiator,
                null,
                null,
//                null,
                ConsentType.CHAT
        );
        proposedAction.setCreateAt(now);
        proposedAction.setUpdateAt(now);

        ProposedAction savedAction = proposedActionRepository.save(proposedAction);
        Map<String, String> response = new HashMap<>();
        response.put("viewProposedActionUrl", "/user/proposed-action/{proposedActionId}");
        response.put("proposedActionId", String.valueOf(savedAction.getId()));
        return response;
    }

}
