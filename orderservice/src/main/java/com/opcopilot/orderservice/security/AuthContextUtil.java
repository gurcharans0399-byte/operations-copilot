package com.opcopilot.orderservice.security;

import com.opcopilot.orderservice.dto.UserDetailResponse;
import com.opcopilot.orderservice.exception.InvalidFeignCallException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class AuthContextUtil {

    private final Logger logger = LoggerFactory.getLogger(AuthContextUtil.class);

    @Value("${AUTH_SERVICE_HOST:http://localhost:8083}")
    private String AUTH_SERVICE_HOST;
    private String AUTH_SERVICE_URL = "/api/users/";

    RestTemplate restTemplate;

    public AuthContextUtil(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

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

    public UserDetailResponse getLoggedInUserDetailsFromAuthService() {
        UserDetailResponse userDetails;
        String userEmail = AuthContextUtil.getLoggedInUserEmail();
        try {
            userDetails = restTemplate.getForObject(
                    AUTH_SERVICE_HOST + AUTH_SERVICE_URL + userEmail,
                    UserDetailResponse.class);
        } catch (Exception e) {
            logger.error("Failed to fetch user details from auth-service for email: {}", userEmail, e);
            throw new InvalidFeignCallException("Failed to fetch user details");
        }
        return userDetails;
    }
    public UserDetailResponse getUserDetailsFromAuthService(String userEmail) {
        UserDetailResponse userDetails;
        try {
            userDetails = restTemplate.getForObject(
                    AUTH_SERVICE_HOST + AUTH_SERVICE_URL + userEmail,
                    UserDetailResponse.class);
        } catch (Exception e) {
            logger.error("Failed to fetch user details from auth-service for email: {}", userEmail, e);
            throw new InvalidFeignCallException("Failed to fetch user details");
        }
        return userDetails;
    }
}
