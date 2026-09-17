package com.demo.authservice.service;

import com.demo.authservice.model.User;
import com.demo.authservice.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public User createUser(String email, String name, String password) {
        String hashedPassword = passwordEncoder.encode(password);
        User user = new User(email, name, hashedPassword);
        return userRepository.save(user);
    }

    public Optional<User> authenticateUser(String email, String password) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent() && passwordEncoder.matches(password, user.get().getPasswordHash())) {
            return user;
        }
        return Optional.empty();
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
