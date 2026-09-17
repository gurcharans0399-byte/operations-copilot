package com.opcopilot.queryservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "proposed_action")
@NoArgsConstructor
@Getter
@Setter
public class ProposedAction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActionType actionType;

    @Column(nullable = false)
    private String actionId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActionState actionState = ActionState.REQUIRE_CONSENT;

    @Column(nullable = false)
    private boolean manuallyVerified;

    @Column(nullable = false)
    private String initiator;

    @Column
    private String consentor;

    @Column
    private LocalDateTime consentDate;

//    @Column
//    private String conversationId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ConsentType requiredConsentType;

    @Column(nullable = false)
    @CreationTimestamp
    private LocalDateTime createAt;

    @Column(nullable = false)
    @UpdateTimestamp
    private LocalDateTime updateAt;

    public ProposedAction(ActionType actionType,
                         String actionId,
                         ActionState actionState,
                         boolean manuallyVerified,
                         String initiator,
                         String consentor,
                         LocalDateTime consentDate,
//                         String conversationId,
                         ConsentType requiredConsentType) {
        this.actionType = actionType;
        this.actionId = actionId;
        this.actionState = actionState;
        this.manuallyVerified = manuallyVerified;
        this.initiator = initiator;
        this.consentor = consentor;
        this.consentDate = consentDate;
//        this.conversationId = conversationId;
        this.requiredConsentType = requiredConsentType;
        this.createAt = LocalDateTime.now();
        this.updateAt = LocalDateTime.now();
    }

}
