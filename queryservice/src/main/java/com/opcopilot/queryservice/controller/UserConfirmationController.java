package com.opcopilot.queryservice.controller;

import com.opcopilot.queryservice.dto.UserDetailResponse;
import com.opcopilot.queryservice.model.ActionState;
import com.opcopilot.queryservice.model.ProposedAction;
import com.opcopilot.queryservice.model.ProposedRefundUpdate;
import com.opcopilot.queryservice.repository.ProposedActionRepository;
import com.opcopilot.queryservice.repository.ProposedRefundUpdateRepository;
import com.opcopilot.queryservice.util.AuthContextUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/user")
public class UserConfirmationController {

    @Value("${auth-service.url:http://localhost:8083}")
    private String AUTH_SERVICE_HOST;

    private static final Logger logger = LoggerFactory.getLogger(UserConfirmationController.class);
    private static final String AUTH_SERVICE_URL = "/api/users/";
    
    private final ProposedActionRepository proposedActionRepository;
    private final ProposedRefundUpdateRepository proposedRefundUpdateRepository;
    private final RestTemplate restTemplate;

    UserConfirmationController(ProposedActionRepository proposedActionRepository,
                              ProposedRefundUpdateRepository proposedRefundUpdateRepository,
                              RestTemplate restTemplate) {
        this.proposedActionRepository = proposedActionRepository;
        this.proposedRefundUpdateRepository = proposedRefundUpdateRepository;
        this.restTemplate = restTemplate;
    }

    @GetMapping("/proposed-action/{proposedActionId}")
    public ResponseEntity<ProposedRefundUpdate> viewProposedAction(@PathVariable String proposedActionId) {
        ProposedAction proposedAction = proposedActionRepository.findById(UUID.fromString(proposedActionId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Proposed action not found"));
        
        ProposedRefundUpdate proposedRefund = fetchProposedRefundFromAction(proposedAction);
        if (proposedRefund == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Proposed refund not found");
        }
        return ResponseEntity.ok(proposedRefund);
    }

    @PutMapping("/proposed-action/{proposedActionId}/verify")
    public ResponseEntity<ProposedRefundUpdate> verifyProposedAction(@PathVariable String proposedActionId) {
        ProposedAction proposedAction = proposedActionRepository.findById(UUID.fromString(proposedActionId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Proposed action not found"));
        
        // Get logged-in user email from JWT token
        String userEmail = AuthContextUtil.getLoggedInUserEmail();
        if (userEmail == null || userEmail.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
        }
        
        // Fetch user details from auth-service
        try {
            UserDetailResponse userDetails = restTemplate.getForObject(
                    AUTH_SERVICE_HOST + AUTH_SERVICE_URL + userEmail,
                    UserDetailResponse.class
            );
            if (userDetails != null && userDetails.getId() != null) {
                proposedAction.setInitiator(userDetails.getId().toString());
            }
        } catch (Exception e) {
            logger.error("Failed to fetch user details from auth-service for email: {}", userEmail, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to fetch user details");
        }
        
        proposedAction.setManuallyVerified(true);
        proposedAction.setUpdateAt(LocalDateTime.now());
        proposedActionRepository.save(proposedAction);
        
        ProposedRefundUpdate proposedRefund = fetchProposedRefundFromAction(proposedAction);
        if (proposedRefund == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Proposed refund not found");
        }
        return ResponseEntity.ok(proposedRefund);
    }

    @PutMapping("/proposed-action/{proposedActionId}")
    public ResponseEntity<ProposedRefundUpdate> updateProposedAction(@PathVariable String proposedActionId,
                                                                    @RequestBody ProposedRefundUpdate updatedRefund) {
        ProposedAction proposedAction = proposedActionRepository.findById(UUID.fromString(proposedActionId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Proposed action not found"));

        ProposedRefundUpdate proposedRefund = fetchProposedRefundFromAction(proposedAction);
        if (proposedRefund == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Proposed refund not found");
        }

        if (updatedRefund.getOrderId() != null) {
            proposedRefund.setOrderId(updatedRefund.getOrderId());
        }
        if (updatedRefund.getRefundState() != null) {
            proposedRefund.setRefundState(updatedRefund.getRefundState());
        }
        if (updatedRefund.getReason() != null) {
            proposedRefund.setReason(updatedRefund.getReason());
        }
        proposedRefund.setUpdateAt(LocalDateTime.now());
        
        ProposedRefundUpdate savedRefund = proposedRefundUpdateRepository.save(proposedRefund);
        return ResponseEntity.ok(savedRefund);
    }

    @PostMapping("/proposed-action/{proposedActionId}/confirm")
    public ResponseEntity<ProposedAction> confirmProposedAction(@PathVariable String proposedActionId) {
        ProposedAction proposedAction = proposedActionRepository.findById(UUID.fromString(proposedActionId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Proposed action not found"));

        String userEmail = AuthContextUtil.getLoggedInUserEmail();
        String consenter = "SYSTEM";
        
        if (userEmail != null && !userEmail.isBlank()) {
            // Fetch user details from auth-service
            try {
                UserDetailResponse userDetails = restTemplate.getForObject(
                        AUTH_SERVICE_HOST + AUTH_SERVICE_URL + userEmail,
                        UserDetailResponse.class
                );
                if (userDetails != null && userDetails.getId() != null) {
                    consenter = userDetails.getId().toString();
                }
            } catch (Exception e) {
                logger.error("Failed to fetch user details from auth-service for email: {}", userEmail, e);
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to fetch user details");
            }
        }
        
        proposedAction.setActionState(ActionState.APPROVED);
        proposedAction.setConsentor(consenter);
        proposedAction.setConsentDate(LocalDateTime.now());
        proposedAction.setUpdateAt(LocalDateTime.now());
        return ResponseEntity.ok(proposedActionRepository.save(proposedAction));
    }

    private ProposedRefundUpdate fetchProposedRefundFromAction(ProposedAction proposedAction) {
        try {
            return proposedRefundUpdateRepository.findById(UUID.fromString(proposedAction.getActionId()))
                    .orElse(null);
        } catch (NumberFormatException e) {
            logger.warn("Could not parse actionId as Long: {}", proposedAction.getActionId());
            return null;
        }
    }

}
