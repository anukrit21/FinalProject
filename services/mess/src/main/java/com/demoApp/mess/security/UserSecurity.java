package com.demoApp.mess.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.demoApp.mess.entity.User;
import com.demoApp.mess.enums.RoleType;
import com.demoApp.mess.repository.UserRepository;

@Component
public class UserSecurity {
    private final UserRepository userRepository;

    public UserSecurity(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Maps User.Role to RoleType
     */
    private RoleType mapUserRoleToRoleType(User.Role userRole) {
        if (userRole == null) return null;
        try {
            return RoleType.valueOf(userRole.name());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public boolean isMessOwnerOrAdmin(Long messId) {
        if (messId == null) {
            return false;
        }
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return false;
        }
        // Check if user is admin
        RoleType userRoleType = mapUserRoleToRoleType(currentUser.getRole());
        if (userRoleType != null && userRoleType == RoleType.ADMIN) {
            return true;
        }
        // Check if user is mess owner
        return userRoleType == RoleType.MESS_OWNER;
    }

    public boolean isMess() {
        return hasRole(RoleType.MESS);
    }

    public boolean isAdmin() {
        return hasRole(RoleType.ADMIN);
    }

    public boolean isUser() {
        return hasRole(RoleType.USER);
    }

    public boolean isMessOrAdmin() {
        return hasRole(RoleType.MESS) || hasRole(RoleType.ADMIN);
    }

    public boolean hasRole(RoleType roleType) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof UserDetails)) {
            return false;
        }
        String username = ((UserDetails) principal).getUsername();
        return userRepository.findByUsername(username)
                .map(user -> {
                    RoleType userRoleType = mapUserRoleToRoleType(user.getRole());
                    return userRoleType != null && userRoleType == roleType;
                })
                .orElse(false);
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof UserDetails)) {
            return null;
        }
        String username = ((UserDetails) principal).getUsername();
        return userRepository.findByUsername(username).orElse(null);
    }

    public Long getCurrentUserId() {
        User currentUser = getCurrentUser();
        return currentUser != null ? currentUser.getId() : null;
    }
}