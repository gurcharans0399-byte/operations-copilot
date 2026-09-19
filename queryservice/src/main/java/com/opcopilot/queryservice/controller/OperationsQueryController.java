package com.opcopilot.queryservice.controller;

import com.opcopilot.queryservice.dto.LlmResponse;
import com.opcopilot.queryservice.model.Conversation;
import com.opcopilot.queryservice.repository.ConversationRepository;
import com.opcopilot.queryservice.repository.ProposedActionRepository;
import com.opcopilot.queryservice.restclient.OrderServiceClient;
import com.opcopilot.queryservice.dto.QueryRequest;
import com.opcopilot.queryservice.dto.QueryResponse;
import com.opcopilot.queryservice.service.OrderServiceTools;
import com.opcopilot.queryservice.util.LlmUtil;
import com.opcopilot.queryservice.utility.ChatMemoryUtility;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/opcopilot")
public class OperationsQueryController {

    private static final Logger logger = LoggerFactory.getLogger(OperationsQueryController.class);
    private final ChatClient aiChatClient;
    private final OrderServiceClient orderServiceClient;
    private final OrderServiceTools orderServiceTools;
    private final ProposedActionRepository proposedActionRepository;
    private final LlmUtil llmUtil;
    private String systemMessage;

    OperationsQueryController(@Qualifier("geminiChatClient") ChatClient chatClient,
                              OrderServiceClient orderServiceClient,
                              OrderServiceTools orderServiceTools,
                              ProposedActionRepository proposedActionRepository,
                              LlmUtil llmUtil,
                              @Value("${chat_client.config.system_message}")
                              String systemPrompt) {
        this.aiChatClient = chatClient;
        this.orderServiceClient = orderServiceClient;
        this.orderServiceTools = orderServiceTools;
        this.proposedActionRepository = proposedActionRepository;
        this.llmUtil = llmUtil;
        this.systemMessage = systemPrompt;
    }

    @PostMapping("/query")
    public QueryResponse queryOperationsDatabase(@RequestBody QueryRequest queryRequest,
                                                 @CookieValue(name = "conversation_id", required = false) String conversationId,
                                                 @RequestHeader(name = "no_call", defaultValue = "false") boolean noCall,
                                                 HttpServletResponse response) {
        if(conversationId == null || conversationId.isEmpty()){
            logger.info("Processing with system prompt: {}", systemMessage);
        }
        conversationId = ChatMemoryUtility.getOrsetConversationIdCookie(response, conversationId);
        MDC.put("conversation_id", conversationId);

        String finalConversationId = conversationId;
        org.springframework.ai.chat.client.ResponseEntity<ChatResponse, LlmResponse> chatResponse = aiChatClient.prompt()
                .advisors(advisor -> advisor.param(ChatMemory.CONVERSATION_ID, finalConversationId))
                .user(queryRequest.getQuery())
                .tools(orderServiceTools)
                .call()
                .responseEntity(LlmResponse.class, spec -> spec.validateSchema());

        Integer promptTokens = chatResponse.getResponse().getMetadata().getUsage().getPromptTokens();
        Integer responseTokens = chatResponse.getResponse().getMetadata().getUsage().getCompletionTokens();

        Conversation conversation = llmUtil.updateOrCreateConversationAndTokens(conversationId,
                promptTokens + responseTokens);

        return new QueryResponse(chatResponse.getEntity().getResponse(),
                chatResponse.getEntity().isManualActionRequired(),
                null, null, chatResponse.getEntity().getUserAction(),
                promptTokens, responseTokens, conversation.getTokens());
    }

    @PostMapping("/close")
    public ResponseEntity<String> closeChatSession(@CookieValue(name = "conversation_id", required = false) String conversationId,
                                                   HttpServletResponse response) {
        ChatMemoryUtility.removeConversationIdCookie(response);
        return new ResponseEntity<>("Chat Session Closed", HttpStatus.OK);
    }

}
