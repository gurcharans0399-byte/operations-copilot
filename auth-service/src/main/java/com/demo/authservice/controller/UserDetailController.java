package com.demo.authservice.controller;

import com.demo.authservice.dto.UserDetailResponse;
import com.demo.authservice.exception.InvalidAPIParameterException;
import com.demo.authservice.model.User;
import com.demo.authservice.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserDetailController {

    private final UserService userService;

    public UserDetailController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{email}")
    public ResponseEntity<UserDetailResponse> getUserByEmail(@PathVariable String email) {
        if (email == null || email.isBlank()) {
            throw new InvalidAPIParameterException("email is required");
        }

        Optional<User> user = userService.getUserByEmail(email);
        if (user.isEmpty()) {
            throw new InvalidAPIParameterException("User not found");
        }

        User foundUser = user.get();
        UserDetailResponse response = new UserDetailResponse(foundUser.getId(), foundUser.getEmail(), foundUser.getName());
        return ResponseEntity.ok(response);
    }
}
