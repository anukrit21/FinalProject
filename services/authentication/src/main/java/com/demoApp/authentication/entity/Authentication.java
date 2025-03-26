package com.demoApp.authentication.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "authentication")
public class Authentication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String username;
    
    @Column(nullable = false)
    private String password;
    
    @Column(name = "user_id")
    private Long userId;
    
    @Column(name = "token")
    private String token;
    
    @Column(name = "reset_token")
    private String resetToken;
    
    @Column(name = "reset_token_expiry")
    private LocalDateTime resetTokenExpiry;
    
    @Column(name = "mfa_enabled")
    private boolean mfaEnabled;
    
    @Column(name = "mfa_secret")
    private String mfaSecret;
    
    @Column(name = "failed_attempts")
    private int failedAttempts;
    
    @Column(name = "locked_until")
    private LocalDateTime lockedUntil;
    
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    private Set<String> roles;
    
    @ElementCollection
    @CollectionTable(name = "oauth_providers", joinColumns = @JoinColumn(name = "user_id"))
    private Set<OAuthProvider> oauthProviders;
    
    @Builder.Default
    private boolean enabled = true;
    
    @Builder.Default
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    private LocalDateTime lastLogin;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    public enum Role {
        USER, ADMIN, VENDOR, OWNER, MANAGER, SUPERVISOR
    }
    
    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OAuthProvider {
        @Column(name = "provider_name")
        private String providerName; // e.g., "google", "facebook", "github"
        
        @Column(name = "provider_user_id")
        private String providerUserId;
    }
    
    // Helper methods for account locking
    public boolean isAccountLocked() {
        return lockedUntil != null && lockedUntil.isAfter(LocalDateTime.now());
    }
    
    public void incrementFailedAttempts() {
        this.failedAttempts++;
    }
    
    public void resetFailedAttempts() {
        this.failedAttempts = 0;
        this.lockedUntil = null;
    }
    
    public void lock(int lockTimeInMinutes) {
        this.lockedUntil = LocalDateTime.now().plusMinutes(lockTimeInMinutes);
    }
} 