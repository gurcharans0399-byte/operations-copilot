package com.opcopilot.queryservice.controller;

import com.opcopilot.queryservice.model.ProposedAction;
import com.opcopilot.queryservice.model.ActionState;
import com.opcopilot.queryservice.repository.ProposedActionRepository;
import com.opcopilot.queryservice.restclient.OrderServiceClient;
import com.opcopilot.queryservice.dto.OrderStatusResponse;
import com.opcopilot.queryservice.dto.QueryRequest;
import com.opcopilot.queryservice.dto.QueryResponse;
import com.opcopilot.queryservice.service.OrderService;
import com.opcopilot.queryservice.utility.CustomChatLoggerAdvisor;
import com.opcopilot.queryservice.utility.ChatMemoryUtility;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/opcopilot")
public class OperationsQueryController {

    private static final Logger logger = LoggerFactory.getLogger(OperationsQueryController.class);
    private final ChatClient aiChatClient;
    private ChatMemory chatMemory;
    private final OrderServiceClient orderServiceClient;
    private final OrderService orderService;
    private final ProposedActionRepository proposedActionRepository;
    private String systemMessage;

    OperationsQueryController(@Qualifier("geminiChatClient") ChatClient chatClient,
                              ChatMemory chatMemory,
                              OrderServiceClient orderServiceClient,
                              OrderService orderService,
                              ProposedActionRepository proposedActionRepository,
                              @Value("${chat_client.config.system_message}")
                              String systemPrompt) {
        this.aiChatClient = chatClient;
        this.orderServiceClient = orderServiceClient;
        this.orderService = orderService;
        this.proposedActionRepository = proposedActionRepository;
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
        if(!noCall) {
            String finalConversationId = conversationId;
            ChatClientResponse chatResponse = aiChatClient.prompt()
                    .advisors(advisor -> advisor.param(ChatMemory.CONVERSATION_ID, finalConversationId))
                    .user(queryRequest.getQuery())
                    .tools(orderService)
                    .call()
                    .chatClientResponse();

            QueryResponse queryResponse = new QueryResponse();
            chatResponse.chatResponse().getResults().forEach( result -> {
                result.getOutput().getToolCalls().forEach( toolCall -> {
                    if (toolCall.name() == "proposeRefund") {
                        queryResponse.setManualActionRequired(true);
                    }
                });
            });

            queryResponse.setQueryResponse(chatResponse.chatResponse().getResults().get(0).getOutput().getText());
            return queryResponse;
        }
        return null;
    }

    @PostMapping("/close")
    public ResponseEntity<String> closeChatSession(@CookieValue(name = "conversation_id", required = false) String conversationId,
                                                   HttpServletResponse response) {
        ChatMemoryUtility.removeConversationIdCookie(response);
        return new ResponseEntity<>("Chat Session Closed", HttpStatus.OK);
    }

}
