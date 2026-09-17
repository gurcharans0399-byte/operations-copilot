package com.opcopilot.queryservice.utility;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.util.JacksonUtils;
import org.springframework.util.Assert;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CustomChatLoggerAdvisor extends SimpleLoggerAdvisor {

    private static final Logger logger = LoggerFactory.getLogger(CustomChatLoggerAdvisor.class);

    @Override
    protected void logRequest(ChatClientRequest request) {
        //  logs on info level not debug
        if(logger.isInfoEnabled()){
            ObjectMapper mapper = new ObjectMapper();
            ChatClientRequest updatedRequest = ChatClientRequest.builder()
                            .prompt(request.prompt().augmentSystemMessage("**System Message logged at the start of conversation**"))
                            .context(request.context())
                            .build();
            logger.info("request: {}", super.DEFAULT_REQUEST_TO_STRING.apply(updatedRequest));
        }
    }

    @Override
    protected void logResponse(ChatClientResponse chatClientResponse) {
        if(logger.isInfoEnabled()){
            logger.info("response: {}", updatedResponseLoggingPattern.apply(chatClientResponse.chatResponse()));
        }
    }


    public static Function<ChatResponse, String> updatedResponseLoggingPattern =
            (request) -> {
                ObjectMapper mapper = new ObjectMapper();
                return JacksonUtils.getDefaultJsonMapper().writerWithDefaultPrettyPrinter()
                        .writeValueAsString(new ResponseLog(request.getMetadata().getModel(),
                                request.getMetadata().getUsage().getTotalTokens().toString(),
                                request.getMetadata().getUsage().getPromptTokens().toString(),
                                request.getMetadata().getUsage().getCompletionTokens().toString(),
                                request.getResults()
                                        .stream()
                                        .map((result) -> {
                                            return new ResponseResultLog(
                                                    result.getOutput().getMessageType().getValue(),
                                                    result.getOutput().getText(),
                                                    result.getOutput().getToolCalls());
                                        })
                                        .collect(Collectors.toList())
                        ));
            };
}
