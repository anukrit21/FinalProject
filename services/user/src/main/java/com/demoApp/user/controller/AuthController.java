package com.demoApp.user.controller;

import com.demoApp.user.dto.ApiResponse;
import com.demoApp.user.dto.auth.LoginRequest;
import com.demoApp.user.dto.auth.RegistrationRequest;
import com.demoApp.user.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegistrationRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
    
    @GetMapping("/verify")
    public ResponseEntity<ApiResponse> verifyEmail(@RequestParam String token) {
        boolean verified = authService.verifyEmail(token);
        if (verified) {
            return ResponseEntity.ok(new ApiResponse("Email verified successfully", true));
        } else {
            return ResponseEntity.badRequest().body(new ApiResponse("Invalid or expired token", false));
        }
    }
    
    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse> forgotPassword(@RequestParam String email) {
        authService.forgotPassword(email);
        return ResponseEntity.ok(new ApiResponse("Password reset email sent if email exists", true));
    }
    
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse> resetPassword(
            @RequestParam String token,
            @RequestParam String newPassword) {
        boolean reset = authService.resetPassword(token, newPassword);
        if (reset) {
            return ResponseEntity.ok(new ApiResponse("Password reset successfully", true));
        } else {
            return ResponseEntity.badRequest().body(new ApiResponse("Invalid or expired token", false));
        }
    }
} 