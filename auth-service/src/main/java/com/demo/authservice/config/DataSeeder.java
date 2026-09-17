package com.demo.authservice.config;

import com.demo.authservice.model.User;
import com.demo.authservice.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataSeeder {

    @Bean
    public CommandLineRunner seedAdminUser(UserRepository userRepository) {
        return args -> {
            String adminEmail = "admin@gmail.com";
            if (userRepository.findByEmail(adminEmail).isEmpty()) {
                PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
                String hashedPassword = passwordEncoder.encode("admin@password");
                User adminUser = new User(adminEmail, "Admin", hashedPassword);
                userRepository.save(adminUser);
                System.out.println("Admin user created successfully: " + adminEmail);
            } else {
                System.out.println("Admin user already exists: " + adminEmail);
            }
        };
    }
}
