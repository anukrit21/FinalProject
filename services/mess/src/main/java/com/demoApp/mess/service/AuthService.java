package com.demoApp.mess.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.demoApp.mess.dto.auth.AuthResponse;
import com.demoApp.mess.dto.auth.ChangePasswordRequest;
import com.demoApp.mess.dto.auth.ForgotPasswordRequest;
import com.demoApp.mess.dto.auth.LoginRequest;
import com.demoApp.mess.dto.auth.RegisterRequest;
import com.demoApp.mess.dto.auth.ResetPasswordRequest;
import com.demoApp.mess.entity.User;
import com.demoApp.mess.exception.BadRequestException;
import com.demoApp.mess.exception.ResourceNotFoundException;
import com.demoApp.mess.repository.UserRepository;
import com.demoApp.mess.security.JwtProvider;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final FileStorageService fileStorageService;
    private final EmailService emailService;

    public AuthResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsernameOrEmail(),
                        loginRequest.getPassword()
                )
        );
    
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Object principal = authentication.getPrincipal();
        String username;
    
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else if (principal instanceof User) {
            username = ((User) principal).getUsername();
        } else {
            throw new BadRequestException("Unexpected authentication principal type");
        }
    
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    
        String token = jwtProvider.generateToken(user.getUsername());
    
        return AuthResponse.fromUser(user, token);
    }
    

    @Transactional
public AuthResponse register(RegisterRequest registerRequest) {
    // Validate username and email
    if (userRepository.existsByUsername(registerRequest.getUsername())) {
        throw new BadRequestException("Username is already taken");
    }

    if (userRepository.existsByEmail(registerRequest.getEmail())) {
        throw new BadRequestException("Email is already registered");
    }

    // Create new user
    User user = User.builder()
        .username(registerRequest.getUsername())
        .email(registerRequest.getEmail())
        .password(passwordEncoder.encode(registerRequest.getPassword()))
        .firstName(registerRequest.getFirstName())
        .lastName(registerRequest.getLastName())
        .phoneNumber(registerRequest.getPhoneNumber())
        .address(registerRequest.getAddress())
        .role(registerRequest.getRole())
        .build();

    // Handle profile image if provided
    if (registerRequest.getProfileImage() != null && !registerRequest.getProfileImage().isEmpty()) {
        String imageUrl = fileStorageService.uploadUserImage(registerRequest.getProfileImage());
        user.setProfileImageUrl(imageUrl);
    }

    // Save user
    User savedUser = userRepository.save(user);

    // Generate JWT token
    String token = jwtProvider.generateToken(savedUser.getUsername());

    return AuthResponse.fromUser(savedUser, token);
}
    @Transactional
    public AuthResponse updateProfileImage(Long userId, MultipartFile image) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        if (user.getProfileImageUrl() != null && !user.getProfileImageUrl().isEmpty()) {
            fileStorageService.deleteFile(user.getProfileImageUrl());
        }

        String imageUrl = fileStorageService.uploadUserImage(image);
        user.setProfileImageUrl(imageUrl);
        User updatedUser = userRepository.save(user);

        String token = jwtProvider.generateToken(updatedUser.getUsername());

        return AuthResponse.fromUser(updatedUser, token);
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public AuthResponse refreshToken() {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            throw new BadRequestException("User not authenticated");
        }

        String token = jwtProvider.generateRefreshToken(currentUser.getUsername());

        return AuthResponse.fromUser(currentUser, token);
    }

    public boolean usernameExists(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    @Transactional
public void forgotPassword(ForgotPasswordRequest request) {
    User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new ResourceNotFoundException("No user found with this email"));

    String token = UUID.randomUUID().toString();
    LocalDateTime tokenExpiry = LocalDateTime.now().plusHours(1); // Token expires in 1 hour

    user.setResetToken(token);
    user.setResetTokenExpiry(tokenExpiry);

    userRepository.save(user);

    String resetLink = "http://localhost:3000/reset-password?token=" + token;
    emailService.sendPasswordResetEmail(user.getEmail(), user.getUsername(), resetLink);
}


@Transactional
public void resetPassword(ResetPasswordRequest request) {
    if (!request.getPassword().equals(request.getConfirmPassword())) {
        throw new BadRequestException("Passwords do not match");
    }

    User user = userRepository.findByResetToken(request.getToken())
            .orElseThrow(() -> new BadRequestException("Invalid or expired token"));

    if (user.getResetTokenExpiry() == null || user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
        throw new BadRequestException("Token has expired");
    }

    user.setPassword(passwordEncoder.encode(request.getPassword()));
    user.setResetToken(null);  // Clear token after reset
    user.setResetTokenExpiry(null);  // Clear expiry after reset
    userRepository.save(user);
}


    @Transactional
    public void changePassword(ChangePasswordRequest request) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            throw new BadRequestException("User not authenticated");
        }

        if (!passwordEncoder.matches(request.getCurrentPassword(), currentUser.getPassword())) {
            throw new BadRequestException("Current password is incorrect");
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("New passwords do not match");
        }

        currentUser.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(currentUser);
    }
}
