package com.opcopilot.queryservice.utility;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.ai.chat.messages.AssistantMessage;

import java.util.List;

@Getter
@AllArgsConstructor
public class ResponseResultLog {
    private String messageType;
    private String text;
    List<AssistantMessage.ToolCall> toolCall;
}
