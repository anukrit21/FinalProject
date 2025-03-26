package com.demoApp.owner.security;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class OwnerAuthorizationService {

    public boolean isOwnerOrAdmin(Long ownerId, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        
        // Check if user has ADMIN role
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        
        if (isAdmin) {
            return true;
        }
        
        // Check if authenticated user is the owner
        if (authentication.getPrincipal() instanceof OwnerPrincipal) {
            OwnerPrincipal principal = (OwnerPrincipal) authentication.getPrincipal();
            return principal.getId().equals(ownerId);
        }
        
        return false;
    }
}