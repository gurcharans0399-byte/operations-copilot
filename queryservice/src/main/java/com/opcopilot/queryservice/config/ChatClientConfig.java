package com.opcopilot.queryservice.config;

import com.opcopilot.queryservice.utility.CustomChatLoggerAdvisor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
public class ChatClientConfig {

    @Value("${chat_client.config.system_message}")
    private String systemMessage;

    @Bean(name = "geminiChatClient")
    ChatClient geminiChatClient(ChatClient.Builder chatClientBuilder, ChatMemory chatMemory) {
        return chatClientBuilder
                .defaultSystem(systemMessage)
                .defaultAdvisors(
//                        new CustomChatLoggerAdvisor(),
                        new SimpleLoggerAdvisor(),
                        MessageChatMemoryAdvisor.builder(chatMemory).build()
                )
                .build();
    }

}
