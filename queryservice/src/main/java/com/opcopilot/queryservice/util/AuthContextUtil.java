package com.opcopilot.queryservice.util;

import com.opcopilot.queryservice.config.JwtUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthContextUtil {

    public static JwtUserDetails getLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getDetails() instanceof JwtUserDetails) {
            return (JwtUserDetails) authentication.getDetails();
        }
        return null;
    }

    public static String getLoggedInUserEmail() {
        JwtUserDetails user = getLoggedInUser();
        return user != null ? user.getEmail() : null;
    }

    public static String getLoggedInUserName() {
        JwtUserDetails user = getLoggedInUser();
        return user != null ? user.getName() : null;
    }
}
