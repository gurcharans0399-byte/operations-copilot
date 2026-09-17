package com.opcopilot.queryservice.utility;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

import java.util.UUID;

public class ChatMemoryUtility {
    public static String getOrsetConversationIdCookie(HttpServletResponse response,
                                                      String conversationId) {
        if(conversationId != null && !conversationId.isBlank()) {
            return conversationId;
        }
        conversationId = UUID.randomUUID().toString();
        Cookie cookie = new Cookie("conversation_id", conversationId);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(10*60);
        response.addCookie(cookie);
        return conversationId;
    }

    public static void removeConversationIdCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie("conversation_id", null);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}
