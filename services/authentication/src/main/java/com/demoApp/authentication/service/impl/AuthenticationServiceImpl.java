package com.demoApp.authentication.service.impl;

import com.demoApp.authentication.dto.*;
import com.demoApp.authentication.entity.Authentication;
import com.demoApp.authentication.entity.Authentication.Role;
import com.demoApp.authentication.exception.AuthenticationException;
import com.demoApp.authentication.repository.AuthenticationRepository;
import com.demoApp.authentication.security.JwtTokenUtil;
import com.demoApp.authentication.service.AuthenticationService;
import com.demoApp.authentication.service.EmailService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import dev.samstevens.totp.code.CodeGenerator;
import dev.samstevens.totp.code.CodeVerifier;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.qr.QrGenerator;
import dev.samstevens.totp.secret.SecretGenerator;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationRepository authRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;
    private final EmailService emailService;
    private final SecretGenerator secretGenerator;
    private final CodeVerifier codeVerifier;
    private final QrGenerator qrGenerator;
    
    @Value("${app.mfa.issuer:DemoApp}")
    private String mfaIssuer;
    
    @Value("${app.account.max-failed-attempts:5}")
    private int maxFailedAttempts;
    
    @Value("${app.account.lock-time-minutes:30}")
    private int lockTimeInMinutes;

    @Override
    @Transactional
    public AuthResponse login(AuthRequest authRequest) {
        log.info("Attempting login for user: {}", authRequest.getUsername());
        
        Optional<Authentication> authOpt = authRepository.findByUsername(authRequest.getUsername());
        
        if (authOpt.isEmpty()) {
            log.warn("Login failed: User not found - {}", authRequest.getUsername());
            return AuthResponse.builder()
                    .status("FAILED")
                    .message("Invalid username or password")
                    .build();
        }
        
        Authentication auth = authOpt.get();
        
        // Check if account is locked
        if (auth.isAccountLocked()) {
            log.warn("Login failed: Account locked - {}", authRequest.getUsername());
            return AuthResponse.builder()
                    .status("FAILED")
                    .message("Your account is locked due to too many failed attempts. Please try again later or reset your password.")
                    .build();
        }
        
        if (!passwordEncoder.matches(authRequest.getPassword(), auth.getPassword())) {
            // Increment failed attempts
            auth.incrementFailedAttempts();
            
            // Lock account if max attempts reached
            if (auth.getFailedAttempts() >= maxFailedAttempts) {
                auth.lock(lockTimeInMinutes);
                log.warn("Account locked for user: {} due to {} failed attempts", 
                        auth.getUsername(), auth.getFailedAttempts());
            }
            
            authRepository.save(auth);
            
            log.warn("Login failed: Incorrect password for user - {}", authRequest.getUsername());
            return AuthResponse.builder()
                    .status("FAILED")
                    .message("Invalid username or password")
                    .build();
        }
        
        if (!auth.isEnabled()) {
            log.warn("Login failed: Account disabled - {}", authRequest.getUsername());
            return AuthResponse.builder()
                    .status("FAILED")
                    .message("Your account is disabled")
                    .build();
        }
        
        // Check if MFA is enabled
        if (auth.isMfaEnabled()) {
            return AuthResponse.builder()
                    .userId(auth.getUserId())
                    .username(auth.getUsername())
                    .status("MFA_REQUIRED")
                    .message("MFA verification required")
                    .build();
        }
        
        // Reset failed attempts on successful login
        auth.resetFailedAttempts();
        
        // Get all roles for this user
        Set<String> roles = auth.getRoles();
        String rolesString = roles != null && !roles.isEmpty() 
                ? String.join(",", roles) 
                : Role.USER.toString();
        
        // Generate JWT token
        String token = jwtTokenUtil.generateToken(auth.getUsername(), auth.getUserId(), rolesString);
        
        // Update last login and token
        auth.setLastLogin(LocalDateTime.now());
        auth.setToken(token);
        authRepository.save(auth);
        
        log.info("User logged in successfully: {}", auth.getUsername());
        
        return AuthResponse.builder()
                .userId(auth.getUserId())
                .username(auth.getUsername())
                .token(token)
                .role(rolesString)
                .expiresAt(LocalDateTime.now().plusSeconds(jwtTokenUtil.getExpirationDateFromToken(token).getTime() / 1000))
                .status("SUCCESS")
                .message("Login successful")
                .build();
    }

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest registerRequest) {
        log.info("Attempting to register new user: {}", registerRequest.getUsername());
        
        if (authRepository.existsByUsername(registerRequest.getUsername())) {
            log.warn("Registration failed: Username already exists - {}", registerRequest.getUsername());
            return AuthResponse.builder()
                    .status("FAILED")
                    .message("Username already exists")
                    .build();
        }
        
        // Create new authentication entity
        Authentication auth = new Authentication();
        auth.setUsername(registerRequest.getUsername());
        auth.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        auth.setEnabled(true);
        auth.setCreatedAt(LocalDateTime.now());
        
        // For a new user, we would typically create an associated user profile
        // and get the user ID from there. For simplicity, we're using a placeholder.
        Long userId = null; // This should be set after creating a user profile
        auth.setUserId(userId);
        
        // Set initial role as USER
        Role role = registerRequest.getRole() != null ? registerRequest.getRole() : Role.USER;
        Set<String> roles = new HashSet<>();
        roles.add(role.toString());
        auth.setRoles(roles);
        
        // Save the authentication record
        Authentication savedAuth = authRepository.save(auth);
        
        // Generate JWT token
        String rolesString = String.join(",", roles);
        String token = jwtTokenUtil.generateToken(savedAuth.getUsername(), savedAuth.getUserId(), rolesString);
        
        // Update token in database
        savedAuth.setToken(token);
        authRepository.save(savedAuth);
        
        log.info("User registered successfully: {}", savedAuth.getUsername());
        
        return AuthResponse.builder()
                .userId(savedAuth.getUserId())
                .username(savedAuth.getUsername())
                .token(token)
                .role(rolesString)
                .expiresAt(LocalDateTime.now().plusSeconds(jwtTokenUtil.getExpirationDateFromToken(token).getTime() / 1000))
                .status("SUCCESS")
                .message("Registration successful")
                .build();
    }

    @Override
    public boolean validateToken(String token) {
        if (!jwtTokenUtil.validateToken(token)) {
            log.warn("Token validation failed: Invalid token");
            return false;
        }
        
        if (jwtTokenUtil.isTokenExpired(token)) {
            log.warn("Token validation failed: Token expired");
            return false;
        }
        
        String username = jwtTokenUtil.getUsernameFromToken(token);
        Optional<Authentication> authOpt = authRepository.findByUsername(username);
        
        if (authOpt.isEmpty()) {
            log.warn("Token validation failed: User not found for token");
            return false;
        }
        
        Authentication auth = authOpt.get();
        if (!auth.isEnabled()) {
            log.warn("Token validation failed: User account disabled - {}", username);
            return false;
        }
        
        // Check if token matches the stored token (optional, for additional security)
        if (auth.getToken() == null || !auth.getToken().equals(token)) {
            log.warn("Token validation failed: Token does not match stored token for user - {}", username);
            return false;
        }
        
        log.info("Token validated successfully for user: {}", username);
        return true;
    }

    @Override
    @Transactional
    public AuthResponse refreshToken(String token) {
        if (!validateToken(token)) {
            return AuthResponse.builder()
                    .status("FAILED")
                    .message("Invalid or expired token")
                    .build();
        }
        
        String username = jwtTokenUtil.getUsernameFromToken(token);
        Long userId = jwtTokenUtil.getUserIdFromToken(token);
        String roles = jwtTokenUtil.getRoleFromToken(token);
        
        // Generate new token
        String newToken = jwtTokenUtil.generateToken(username, userId, roles);
        
        // Update token in database
        Optional<Authentication> authOpt = authRepository.findByUsername(username);
        if (authOpt.isPresent()) {
            Authentication auth = authOpt.get();
            auth.setToken(newToken);
            authRepository.save(auth);
        }
        
        log.info("Token refreshed successfully for user: {}", username);
        
        return AuthResponse.builder()
                .userId(userId)
                .username(username)
                .token(newToken)
                .role(roles)
                .expiresAt(LocalDateTime.now().plusSeconds(jwtTokenUtil.getExpirationDateFromToken(newToken).getTime() / 1000))
                .status("SUCCESS")
                .message("Token refreshed successfully")
                .build();
    }

    @Override
    @Transactional
    public void logout(String token) {
        if (token != null && validateToken(token)) {
            String username = jwtTokenUtil.getUsernameFromToken(token);
            
            Optional<Authentication> authOpt = authRepository.findByUsername(username);
            if (authOpt.isPresent()) {
                Authentication auth = authOpt.get();
                auth.setToken(null);  // Clear the token
                authRepository.save(auth);
                log.info("User logged out successfully: {}", username);
            }
        }
    }

    @Override
    @Transactional
    public boolean changePassword(Long userId, String oldPassword, String newPassword) {
        Optional<Authentication> authOpt = authRepository.findByUserId(userId);
        
        if (authOpt.isEmpty()) {
            log.warn("Password change failed: User not found with ID - {}", userId);
            return false;
        }
        
        Authentication auth = authOpt.get();
        
        if (!passwordEncoder.matches(oldPassword, auth.getPassword())) {
            log.warn("Password change failed: Incorrect old password for user - {}", auth.getUsername());
            return false;
        }
        
        // Set new password
        auth.setPassword(passwordEncoder.encode(newPassword));
        // Clear token to force re-login
        auth.setToken(null);
        authRepository.save(auth);
        
        log.info("Password changed successfully for user: {}", auth.getUsername());
        return true;
    }
    
    @Override
    @Transactional
    public boolean requestPasswordReset(PasswordResetRequest request) {
        log.info("Password reset requested for username: {}", request.getUsername());
        
        Optional<Authentication> authOpt = authRepository.findByUsername(request.getUsername());
        if (authOpt.isEmpty()) {
            log.warn("Password reset failed: User not found - {}", request.getUsername());
            // We don't want to reveal if the user exists or not for security reasons
            return false;
        }
        
        Authentication auth = authOpt.get();
        
        // Generate a secure random token
        String resetToken = UUID.randomUUID().toString();
        
        // Set token and expiry (typically 24 hours)
        auth.setResetToken(resetToken);
        auth.setResetTokenExpiry(LocalDateTime.now().plusHours(24));
        authRepository.save(auth);
        
        // In a real application, we would send an email with the reset link
        // We're using a placeholder email service here
        String resetLink = "https://yourdomain.com/reset-password?token=" + resetToken;
        emailService.sendPasswordResetEmail(request.getEmail(), resetLink);
        
        log.info("Password reset token generated for user: {}", request.getUsername());
        return true;
    }
    
    @Override
    @Transactional
    public boolean confirmPasswordReset(PasswordResetConfirmRequest request) {
        log.info("Confirming password reset with token");
        
        if (!StringUtils.hasText(request.getToken())) {
            log.warn("Password reset confirmation failed: Token is empty");
            return false;
        }
        
        // Validate new password matches confirmation
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            log.warn("Password reset confirmation failed: Passwords do not match");
            return false;
        }
        
        Optional<Authentication> authOpt = authRepository.findByResetToken(request.getToken());
        if (authOpt.isEmpty()) {
            log.warn("Password reset confirmation failed: Invalid token");
            return false;
        }
        
        Authentication auth = authOpt.get();
        
        // Check if token is expired
        if (auth.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            log.warn("Password reset confirmation failed: Token expired");
            return false;
        }
        
        // Set new password
        auth.setPassword(passwordEncoder.encode(request.getNewPassword()));
        
        // Clear reset token and expiry
        auth.setResetToken(null);
        auth.setResetTokenExpiry(null);
        
        // Reset failed attempts and unlock account
        auth.resetFailedAttempts();
        
        // Clear current token to force re-login
        auth.setToken(null);
        
        authRepository.save(auth);
        
        log.info("Password reset successfully for user: {}", auth.getUsername());
        return true;
    }
    
    @Override
    @Transactional
    public MfaSetupResponse setupMfa(String username) {
        log.info("Setting up MFA for user: {}", username);
        
        Optional<Authentication> authOpt = authRepository.findByUsername(username);
        if (authOpt.isEmpty()) {
            log.warn("MFA setup failed: User not found - {}", username);
            throw new AuthenticationException("User not found");
        }
        
        Authentication auth = authOpt.get();
        
        // Generate a new secret key using the injected secretGenerator
        String secretKey = secretGenerator.generate();
        
        // Store the secret key
        auth.setMfaSecret(secretKey);
        auth.setMfaEnabled(false); // Will be enabled after verification
        authRepository.save(auth);
        
        // Generate QR code for the secret
        QrData qrData = new QrData.Builder()
                .label(username)
                .secret(secretKey)
                .issuer(mfaIssuer)
                .algorithm(QrData.Algorithm.SHA1)
                .digits(6)
                .period(30)
                .build();
        
        String qrCodeUrl;
        try {
            // Use the injected qrGenerator
            byte[] qrCode = qrGenerator.generate(qrData);
            qrCodeUrl = "data:image/png;base64," + Base64.getEncoder().encodeToString(qrCode);
        } catch (Exception e) {
            log.error("Failed to generate QR code", e);
            throw new RuntimeException("Failed to generate QR code", e);
        }
        
        log.info("MFA setup initialized for user: {}", username);
        
        return MfaSetupResponse.builder()
                .secretKey(secretKey)
                .qrCodeUrl(qrCodeUrl)
                .mfaEnabled(false)
                .message("MFA setup initialized. Please verify with a code from your authenticator app.")
                .build();
    }
    
    @Override
    @Transactional
    public boolean verifyMfa(MfaVerifyRequest request) {
        log.info("Verifying MFA code for user: {}", request.getUsername());
        
        Optional<Authentication> authOpt = authRepository.findByUsername(request.getUsername());
        if (authOpt.isEmpty()) {
            log.warn("MFA verification failed: User not found - {}", request.getUsername());
            return false;
        }
        
        Authentication auth = authOpt.get();
        
        if (auth.getMfaSecret() == null) {
            log.warn("MFA verification failed: MFA not set up for user - {}", request.getUsername());
            return false;
        }
        
        // Verify the code using the injected codeVerifier
        boolean isValidCode = codeVerifier.isValidCode(auth.getMfaSecret(), request.getCode());
        
        if (isValidCode) {
            // If MFA was being set up, enable it now
            if (!auth.isMfaEnabled()) {
                auth.setMfaEnabled(true);
                authRepository.save(auth);
                log.info("MFA enabled for user: {}", request.getUsername());
            }
            
            log.info("MFA code verified successfully for user: {}", request.getUsername());
            return true;
        } else {
            log.warn("MFA verification failed: Invalid code for user - {}", request.getUsername());
            return false;
        }
    }
    
    @Override
    @Transactional
    public boolean disableMfa(String username) {
        log.info("Disabling MFA for user: {}", username);
        
        Optional<Authentication> authOpt = authRepository.findByUsername(username);
        if (authOpt.isEmpty()) {
            log.warn("MFA disable failed: User not found - {}", username);
            return false;
        }
        
        Authentication auth = authOpt.get();
        
        auth.setMfaEnabled(false);
        auth.setMfaSecret(null);
        authRepository.save(auth);
        
        log.info("MFA disabled for user: {}", username);
        return true;
    }
    
    @Override
    @Transactional
    public AuthResponse oauthLogin(OAuthLoginRequest request) {
        log.info("Processing OAuth login for provider: {}", request.getProvider());
        
        // In a real implementation, we would validate the access token with the provider
        // and retrieve user information. This is a simplified version.
        
        String provider = request.getProvider().toLowerCase();
        String accessToken = request.getAccessToken();
        
        // Validate token with provider and get user info
        Map<String, String> userInfo = validateOAuthToken(provider, accessToken);
        
        if (userInfo == null || !userInfo.containsKey("id") || !userInfo.containsKey("email")) {
            log.warn("OAuth login failed: Invalid token or missing user info");
            return AuthResponse.builder()
                    .status("FAILED")
                    .message("Invalid OAuth token")
                    .build();
        }
        
        String providerId = userInfo.get("id");
        String email = userInfo.get("email");
        String name = userInfo.getOrDefault("name", email);
        
        // Check if user already exists with this provider
        Optional<Authentication> existingAuth;
        try {
            existingAuth = authRepository.findByOauthProviderAndProviderId(provider, providerId);
        } catch (Exception e) {
            log.error("Error finding user by OAuth provider", e);
            // Fallback to finding by username (email)
            existingAuth = authRepository.findByUsername(email);
        }
        
        Authentication auth;
        
        if (existingAuth.isPresent()) {
            // User exists, update login time
            auth = existingAuth.get();
            auth.setLastLogin(LocalDateTime.now());
        } else {
            // Create new user
            auth = new Authentication();
            auth.setUsername(email); // Use email as username
            // Generate a random password since the user will login via OAuth
            auth.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
            auth.setEnabled(true);
            auth.setCreatedAt(LocalDateTime.now());
            
            // Set roles
            Set<String> roles = new HashSet<>();
            roles.add(Role.USER.toString());
            auth.setRoles(roles);
            
            // Add OAuth provider
            Set<Authentication.OAuthProvider> providers = new HashSet<>();
            Authentication.OAuthProvider oauthProvider = new Authentication.OAuthProvider(
                    provider, providerId);
            providers.add(oauthProvider);
            auth.setOauthProviders(providers);
            
            // In a real app, we would create a user profile here
            // For now, we're using a placeholder user ID
            auth.setUserId(null);
        }
        
        // Save the user
        auth = authRepository.save(auth);
        
        // Generate JWT token
        String rolesString = auth.getRoles() != null && !auth.getRoles().isEmpty() 
                ? String.join(",", auth.getRoles()) 
                : Role.USER.toString();
        
        String token = jwtTokenUtil.generateToken(auth.getUsername(), auth.getUserId(), rolesString);
        
        // Update token in database
        auth.setToken(token);
        authRepository.save(auth);
        
        log.info("OAuth login successful for provider: {} and user: {}", provider, auth.getUsername());
        
        return AuthResponse.builder()
                .userId(auth.getUserId())
                .username(auth.getUsername())
                .token(token)
                .role(rolesString)
                .expiresAt(LocalDateTime.now().plusSeconds(jwtTokenUtil.getExpirationDateFromToken(token).getTime() / 1000))
                .status("SUCCESS")
                .message("OAuth login successful")
                .build();
    }
    
    @Override
    @Transactional
    public boolean addRole(Long userId, String role) {
        log.info("Adding role {} to user with ID: {}", role, userId);
        
        Optional<Authentication> authOpt = authRepository.findByUserId(userId);
        if (authOpt.isEmpty()) {
            log.warn("Add role failed: User not found with ID - {}", userId);
            return false;
        }
        
        Authentication auth = authOpt.get();
        
        // Validate the role
        try {
            Role.valueOf(role);
        } catch (IllegalArgumentException e) {
            log.warn("Add role failed: Invalid role - {}", role);
            return false;
        }
        
        // Initialize roles set if null
        if (auth.getRoles() == null) {
            auth.setRoles(new HashSet<>());
        }
        
        // Add the role if not already present
        boolean added = auth.getRoles().add(role);
        
        if (added) {
            authRepository.save(auth);
            log.info("Role {} added to user: {}", role, auth.getUsername());
        } else {
            log.info("Role {} already assigned to user: {}", role, auth.getUsername());
        }
        
        return true;
    }
    
    @Override
    @Transactional
    public boolean removeRole(Long userId, String role) {
        log.info("Removing role {} from user with ID: {}", role, userId);
        
        Optional<Authentication> authOpt = authRepository.findByUserId(userId);
        if (authOpt.isEmpty()) {
            log.warn("Remove role failed: User not found with ID - {}", userId);
            return false;
        }
        
        Authentication auth = authOpt.get();
        
        // Remove the role if present
        if (auth.getRoles() != null && auth.getRoles().remove(role)) {
            // Ensure user has at least one role
            if (auth.getRoles().isEmpty()) {
                auth.getRoles().add(Role.USER.toString());
                log.info("Added default USER role after removing all roles");
            }
            
            authRepository.save(auth);
            log.info("Role {} removed from user: {}", role, auth.getUsername());
            return true;
        } else {
            log.info("Role {} not assigned to user: {}", role, auth.getUsername());
            return false;
        }
    }
    
    @Override
    public Set<String> getUserRoles(Long userId) {
        log.info("Getting roles for user with ID: {}", userId);
        
        Optional<Authentication> authOpt = authRepository.findByUserId(userId);
        if (authOpt.isEmpty()) {
            log.warn("Get roles failed: User not found with ID - {}", userId);
            return Collections.emptySet();
        }
        
        Authentication auth = authOpt.get();
        
        return auth.getRoles() != null 
                ? new HashSet<>(auth.getRoles()) 
                : Collections.singleton(Role.USER.toString());
    }
    
    @Override
    public boolean isAccountLocked(String username) {
        log.info("Checking if account is locked for user: {}", username);
        
        Optional<Authentication> authOpt = authRepository.findByUsername(username);
        if (authOpt.isEmpty()) {
            log.warn("Account lock check failed: User not found - {}", username);
            return false;
        }
        
        Authentication auth = authOpt.get();
        
        boolean locked = auth.isAccountLocked();
        log.info("Account lock status for user {}: {}", username, locked ? "Locked" : "Not locked");
        
        return locked;
    }
    
    @Override
    @Transactional
    public void unlockAccount(String username) {
        log.info("Unlocking account for user: {}", username);
        
        Optional<Authentication> authOpt = authRepository.findByUsername(username);
        if (authOpt.isPresent()) {
            Authentication auth = authOpt.get();
            auth.resetFailedAttempts();
            authRepository.save(auth);
            log.info("Account unlocked for user: {}", username);
        } else {
            log.warn("Account unlock failed: User not found - {}", username);
        }
    }
    
    // Helper method to validate OAuth token with provider
    private Map<String, String> validateOAuthToken(String provider, String accessToken) {
        // In a real implementation, this would make an API call to the provider
        // to validate the token and retrieve user information
        
        // Placeholder implementation
        Map<String, String> userInfo = new HashMap<>();
        userInfo.put("id", UUID.randomUUID().toString());
        userInfo.put("email", "user@example.com");
        userInfo.put("name", "OAuth User");
        
        return userInfo;
    }
} 