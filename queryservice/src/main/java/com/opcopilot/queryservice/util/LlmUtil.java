package com.opcopilot.queryservice.util;

import com.opcopilot.queryservice.model.Conversation;
import com.opcopilot.queryservice.repository.ConversationRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class LlmUtil {
    private final ConversationRepository conversationRepository;

    public LlmUtil(ConversationRepository conversationRepository) {
        this.conversationRepository = conversationRepository;
    }

    public Conversation updateOrCreateConversationAndTokens(String conversationId, Integer usedTokens) {
        Conversation conversation =
                conversationRepository.findById(UUID.fromString(conversationId))
                        .orElseGet(() -> new Conversation(UUID.fromString(conversationId), 0,
                                AuthContextUtil.getLoggedInUserEmail()));
        conversation.setTokens(conversation.getTokens() + usedTokens);
        return conversationRepository.save(conversation);
    }
}
