package com.demoApp.user.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.demoApp.user.model.User;
import com.demoApp.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserAuthorizationService {
    
    private final UserRepository userRepository;
    
    public boolean isUserAuthorized(Long userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        
        // Admin has access to all users
        if (hasAdminRole()) {
            return true;
        }
        
        // User has access to their own data
        String email = getCurrentUserEmail();
        if (email != null) {
            return userRepository.findByEmail(email)
                    .map(user -> user.getId().equals(userId))
                    .orElse(false);
        }
        
        return false;
    }
    
    public boolean hasAdminRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && 
                authentication.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
    
    public String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return null;
        }
        
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else if (principal instanceof String) {
            return (String) principal;
        }
        
        return null;
    }
    
    public User getCurrentUser() {
        String email = getCurrentUserEmail();
        if (email == null) {
            return null;
        }
        
        return userRepository.findByEmail(email).orElse(null);
    }
} 