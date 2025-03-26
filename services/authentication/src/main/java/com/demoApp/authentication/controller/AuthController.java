package com.demoApp.authentication.controller;

import com.demoApp.authentication.dto.*;
import com.demoApp.authentication.entity.Authentication;
import com.demoApp.authentication.entity.Authentication.Role;
import com.demoApp.authentication.exception.AuthenticationException;
import com.demoApp.authentication.repository.AuthenticationRepository;
import com.demoApp.authentication.security.JwtTokenUtil;
import com.demoApp.authentication.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthenticationService authService;
    private final AuthenticationRepository authRepository;
    private final JwtTokenUtil jwtTokenUtil;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest authRequest) {
        log.info("Login request received for username: {}", authRequest.getUsername());
        AuthResponse response = authService.login(authRequest);
        
        if ("SUCCESS".equals(response.getStatus())) {
            return ResponseEntity.ok(response);
        } else if ("MFA_REQUIRED".equals(response.getStatus())) {
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        log.info("Registration request received for username: {}", registerRequest.getUsername());
        AuthResponse response = authService.register(registerRequest);
        
        if ("SUCCESS".equals(response.getStatus())) {
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PostMapping("/validate")
    public ResponseEntity<Map<String, Boolean>> validateToken(@RequestHeader("Authorization") String authHeader) {
        log.info("Token validation request received");
        String token = authHeader.substring(7); // Remove "Bearer " prefix
        
        boolean isValid = authService.validateToken(token);
        Map<String, Boolean> response = new HashMap<>();
        response.put("valid", isValid);
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@RequestHeader("Authorization") String authHeader) {
        log.info("Token refresh request received");
        String token = authHeader.substring(7); // Remove "Bearer " prefix
        
        AuthResponse response = authService.refreshToken(token);
        
        if ("SUCCESS".equals(response.getStatus())) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String authHeader) {
        log.info("Logout request received");
        String token = authHeader.substring(7); // Remove "Bearer " prefix
        
        authService.logout(token);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/change-password")
    public ResponseEntity<Map<String, String>> changePassword(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam String oldPassword,
            @RequestParam String newPassword) {
        
        log.info("Change password request received");
        String token = authHeader.substring(7); // Remove "Bearer " prefix
        
        Long userId = null;
        try {
            // Extract the user ID from the token
            userId = extractUserIdFromToken(token);
        } catch (Exception e) {
            log.error("Failed to extract user ID from token", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Invalid token"));
        }
        
        boolean success = authService.changePassword(userId, oldPassword, newPassword);
        
        if (success) {
            return ResponseEntity.ok(Map.of("message", "Password changed successfully"));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Failed to change password. Please check your current password."));
        }
    }
    
    // Password Reset Endpoints
    
    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> requestPasswordReset(
            @Valid @RequestBody PasswordResetRequest request) {
        
        log.info("Password reset request received for username: {}", request.getUsername());
        
        boolean success = authService.requestPasswordReset(request);
        
        // Always return success to prevent user enumeration
        return ResponseEntity.ok(Map.of(
                "message", "If your username and email are valid, you will receive a password reset link."));
    }
    
    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> confirmPasswordReset(
            @Valid @RequestBody PasswordResetConfirmRequest request) {
        
        log.info("Password reset confirmation received");
        
        boolean success = authService.confirmPasswordReset(request);
        
        if (success) {
            return ResponseEntity.ok(Map.of("message", "Password has been reset successfully"));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Failed to reset password. The token may be invalid or expired."));
        }
    }
    
    // MFA Endpoints
    
    @PostMapping("/mfa/setup")
    public ResponseEntity<MfaSetupResponse> setupMfa(@RequestHeader("Authorization") String authHeader) {
        log.info("MFA setup request received");
        String token = authHeader.substring(7); // Remove "Bearer " prefix
        
        String username = extractUsernameFromToken(token);
        
        try {
            MfaSetupResponse response = authService.setupMfa(username);
            return ResponseEntity.ok(response);
        } catch (AuthenticationException e) {
            log.error("MFA setup failed", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(MfaSetupResponse.builder()
                            .mfaEnabled(false)
                            .message(e.getMessage())
                            .build());
        } catch (Exception e) {
            log.error("MFA setup failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(MfaSetupResponse.builder()
                            .mfaEnabled(false)
                            .message("An error occurred during MFA setup")
                            .build());
        }
    }
    
    @PostMapping("/mfa/verify")
    public ResponseEntity<AuthResponse> verifyMfa(@Valid @RequestBody MfaVerifyRequest request) {
        log.info("MFA verification request received for username: {}", request.getUsername());
        
        boolean isValid = authService.verifyMfa(request);
        
        if (isValid) {
            try {
                // If this is for login, generate a full login response with token
                Optional<Authentication> authOpt = authRepository.findByUsername(request.getUsername());
                if (authOpt.isPresent()) {
                    Authentication auth = authOpt.get();
                    
                    String rolesString = auth.getRoles() != null && !auth.getRoles().isEmpty() 
                            ? String.join(",", auth.getRoles()) 
                            : Role.USER.toString();
                    
                    String token = jwtTokenUtil.generateToken(auth.getUsername(), auth.getUserId(), rolesString);
                    
                    // Update last login and token
                    auth.setLastLogin(LocalDateTime.now());
                    auth.setToken(token);
                    auth.resetFailedAttempts();
                    authRepository.save(auth);
                    
                    AuthResponse response = AuthResponse.builder()
                            .userId(auth.getUserId())
                            .username(auth.getUsername())
                            .token(token)
                            .role(rolesString)
                            .expiresAt(LocalDateTime.now().plusSeconds(jwtTokenUtil.getExpirationDateFromToken(token).getTime() / 1000))
                            .status("SUCCESS")
                            .message("MFA verification successful")
                            .build();
                    
                    return ResponseEntity.ok(response);
                }
            } catch (Exception e) {
                log.error("Error generating token after MFA verification", e);
            }
            
            // If we can't generate a token, still return success
            return ResponseEntity.ok(AuthResponse.builder()
                    .status("SUCCESS")
                    .username(request.getUsername())
                    .message("MFA verification successful")
                    .build());
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(AuthResponse.builder()
                            .status("FAILED")
                            .message("Invalid MFA code")
                            .build());
        }
    }
    
    @PostMapping("/mfa/disable")
    public ResponseEntity<Map<String, String>> disableMfa(@RequestHeader("Authorization") String authHeader) {
        log.info("MFA disable request received");
        String token = authHeader.substring(7); // Remove "Bearer " prefix
        
        String username = extractUsernameFromToken(token);
        
        boolean success = authService.disableMfa(username);
        
        if (success) {
            return ResponseEntity.ok(Map.of("message", "MFA has been disabled"));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Failed to disable MFA"));
        }
    }
    
    // OAuth Login
    
    @PostMapping("/oauth/login")
    public ResponseEntity<AuthResponse> oauthLogin(@Valid @RequestBody OAuthLoginRequest request) {
        log.info("OAuth login request received for provider: {}", request.getProvider());
        
        AuthResponse response = authService.oauthLogin(request);
        
        if ("SUCCESS".equals(response.getStatus())) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }
    
    // Role Management Endpoints
    
    @PostMapping("/roles/{userId}/add")
    public ResponseEntity<Map<String, String>> addRole(
            @PathVariable Long userId,
            @RequestParam String role) {
        
        log.info("Add role request received for user ID: {} and role: {}", userId, role);
        
        boolean success = authService.addRole(userId, role);
        
        if (success) {
            return ResponseEntity.ok(Map.of("message", "Role added successfully"));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Failed to add role"));
        }
    }
    
    @PostMapping("/roles/{userId}/remove")
    public ResponseEntity<Map<String, String>> removeRole(
            @PathVariable Long userId,
            @RequestParam String role) {
        
        log.info("Remove role request received for user ID: {} and role: {}", userId, role);
        
        boolean success = authService.removeRole(userId, role);
        
        if (success) {
            return ResponseEntity.ok(Map.of("message", "Role removed successfully"));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Failed to remove role"));
        }
    }
    
    @GetMapping("/roles/{userId}")
    public ResponseEntity<Set<String>> getUserRoles(@PathVariable Long userId) {
        log.info("Get roles request received for user ID: {}", userId);
        
        Set<String> roles = authService.getUserRoles(userId);
        
        return ResponseEntity.ok(roles);
    }
    
    // Account Lock Management
    
    @GetMapping("/account/lock-status")
    public ResponseEntity<Map<String, Boolean>> isAccountLocked(@RequestParam String username) {
        log.info("Account lock status check for username: {}", username);
        
        boolean locked = authService.isAccountLocked(username);
        
        return ResponseEntity.ok(Map.of("locked", locked));
    }
    
    @PostMapping("/account/unlock")
    public ResponseEntity<Map<String, String>> unlockAccount(@RequestParam String username) {
        log.info("Account unlock request for username: {}", username);
        
        authService.unlockAccount(username);
        
        return ResponseEntity.ok(Map.of("message", "Account unlocked successfully"));
    }
    
    // Helper methods
    
    private Long extractUserIdFromToken(String token) {
        // This is a placeholder. In a real implementation, you would use JwtTokenUtil
        // to extract the user ID from the token.
        return 1L; // Dummy value for demonstration
    }
    
    private String extractUsernameFromToken(String token) {
        // This is a placeholder. In a real implementation, you would use JwtTokenUtil
        // to extract the username from the token.
        return "user"; // Dummy value for demonstration
    }
} 