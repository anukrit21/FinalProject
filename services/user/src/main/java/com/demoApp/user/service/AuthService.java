package com.demoApp.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.demoApp.user.dto.auth.AuthResponse;
import com.demoApp.user.dto.auth.LoginRequest;
import com.demoApp.user.dto.auth.RegistrationRequest;
import com.demoApp.user.exception.BadRequestException;
import com.demoApp.user.exception.DuplicateResourceException;
import com.demoApp.user.exception.ResourceNotFoundException;
import com.demoApp.user.exception.UnauthorizedException;
import com.demoApp.user.model.User;
import com.demoApp.user.repository.UserRepository;
import com.demoApp.user.security.JwtTokenProvider;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    
    // In a real application, this would be stored in a database
    private final Map<String, VerificationToken> tokens = new HashMap<>();
    
    @Transactional
    public AuthResponse register(RegistrationRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already registered");
        }
        
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .mobileNumber(request.getMobileNumber())
                .address(request.getAddress())
                .memberType(User.UserType.CUSTOMER)
                .isVerified(false)
                .createdAt(LocalDateTime.now())
                .build();
                
        User savedUser = userRepository.save(user);
        
        // Generate a verification token
        String token = UUID.randomUUID().toString();
        tokens.put(token, new VerificationToken(savedUser.getId(), LocalDateTime.now().plusHours(24)));
        
        // Send verification email
        emailService.sendVerificationEmail(savedUser);
        
        // Generate JWT token
        String jwt = jwtTokenProvider.generateToken(savedUser.getEmail(), "ROLE_CUSTOMER");
        
        return AuthResponse.builder()
                .token(jwt)
                .tokenType("Bearer")
                .userId(savedUser.getId())
                .email(savedUser.getEmail())
                .name(savedUser.getName())
                .role("CUSTOMER")
                .build();
    }
    
    public AuthResponse login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
            
            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));
            
            // Update last login time
            user.setLastLoginAt(LocalDateTime.now());
            userRepository.save(user);
            
            // Generate JWT token
            String jwt = jwtTokenProvider.generateToken(authentication);
            
            return AuthResponse.builder()
                    .token(jwt)
                    .tokenType("Bearer")
                    .userId(user.getId())
                    .email(user.getEmail())
                    .name(user.getName())
                    .role(user.getMemberType().name())
                    .build();
                    
        } catch (AuthenticationException e) {
            throw new UnauthorizedException("Invalid email or password");
        }
    }
    
    @Transactional
    public boolean verifyEmail(String token) {
        VerificationToken verificationToken = tokens.get(token);
        if (verificationToken == null || verificationToken.isExpired()) {
            return false;
        }
        
        User user = userRepository.findById(verificationToken.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
                
        user.setVerified(true);
        userRepository.save(user);
        
        // Remove the token
        tokens.remove(token);
        
        return true;
    }
    
    @Transactional
    public void forgotPassword(String email) {
        userRepository.findByEmail(email).ifPresent(user -> {
            String resetToken = UUID.randomUUID().toString();
            tokens.put(resetToken, new VerificationToken(user.getId(), LocalDateTime.now().plusHours(1)));
            emailService.sendPasswordResetEmail(user, resetToken);
        });
    }
    
    @Transactional
    public boolean resetPassword(String token, String newPassword) {
        if (newPassword == null || newPassword.length() < 6) {
            throw new BadRequestException("Password must be at least 6 characters long");
        }
        
        VerificationToken verificationToken = tokens.get(token);
        if (verificationToken == null || verificationToken.isExpired()) {
            return false;
        }
        
        User user = userRepository.findById(verificationToken.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
                
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        
        // Remove the token
        tokens.remove(token);
        
        return true;
    }
    
    // Inner class to represent a verification token
    private static class VerificationToken {
        private final Long userId;
        private final LocalDateTime expiryDate;
        
        public VerificationToken(Long userId, LocalDateTime expiryDate) {
            this.userId = userId;
            this.expiryDate = expiryDate;
        }
        
        public Long getUserId() {
            return userId;
        }
        
        public boolean isExpired() {
            return LocalDateTime.now().isAfter(expiryDate);
        }
    }
}
