package com.demoApp.mess.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.demoApp.mess.entity.User;
import com.demoApp.mess.entity.User.Role; 
import com.demoApp.mess.repository.UserRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Create admin user if not exists
        if (!userRepository.existsByUsername("admin")) {
            User adminUser = User.builder()
                    .username("admin")
                    .email("admin@example.com")
                    .password(passwordEncoder.encode("admin123"))
                    .role(Role.ADMIN)  // Changed from RoleType to Role
                    .active(true)
                    .enabled(true)
                    .build();
            
            userRepository.save(adminUser);
            System.out.println("Admin user created successfully");
        }

        // Create mess owner user if not exists
        if (!userRepository.existsByUsername("mess")) {
            User messUser = User.builder()
                    .username("mess")
                    .email("mess@example.com")
                    .password(passwordEncoder.encode("mess123"))
                    .role(Role.MESS_OWNER)  // Changed from RoleType to Role
                    .active(true)
                    .enabled(true)
                    .build();
            
            userRepository.save(messUser);
            System.out.println("Mess user created successfully");
        }

        // Create regular user if not exists
        if (!userRepository.existsByUsername("user")) {
            User regularUser = User.builder()
                    .username("user")
                    .email("user@example.com")
                    .password(passwordEncoder.encode("user123"))
                    .role(Role.CUSTOMER)  // Changed from RoleType to Role
                    .active(true)
                    .enabled(true)
                    .build();
            
            userRepository.save(regularUser);
            System.out.println("Regular user created successfully");
        }
    }
}