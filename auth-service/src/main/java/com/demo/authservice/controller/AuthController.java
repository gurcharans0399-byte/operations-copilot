package com.demo.authservice.controller;

import com.demo.authservice.dto.AuthTokenResponse;
import com.demo.authservice.dto.LoginRequest;
import com.demo.authservice.exception.AuthCredentialExceptions;
import com.demo.authservice.exception.InvalidAPIParameterException;
import com.demo.authservice.model.User;
import com.demo.authservice.service.JwtService;
import com.demo.authservice.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwtService jwtService;
    private final UserService userService;

    public AuthController(JwtService jwtService, UserService userService) {
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthTokenResponse> login(@RequestBody LoginRequest loginRequest) {
        String email = loginRequest.getEmail();
        String password = loginRequest.getPassword();

        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            throw new InvalidAPIParameterException("email and password are required");
        }

        Optional<User> authenticatedUser = userService.authenticateUser(email, password);
        if (authenticatedUser.isEmpty()) {
            throw new AuthCredentialExceptions("Invalid email or password");
        }

        User user = authenticatedUser.get();
        String token = jwtService.generateToken(user.getEmail(), user.getName());
        return ResponseEntity.ok(new AuthTokenResponse(token, "Bearer", jwtService.getExpirationMs()));
    }
}
