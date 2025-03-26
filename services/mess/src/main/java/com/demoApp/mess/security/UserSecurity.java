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
     * Checks if the authenticated user is the same as the one being accessed.
     * 
     * @param userId The ID of the user being accessed
     * @return true if the authenticated user is the same as the user being accessed
     */
    public boolean isCurrentUser(Long userId) {
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
                .map((User user) -> user.getId().equals(userId))
                .orElse(false);
    }

    /**
     * Check if the current user is the owner of the mess or an admin.
     *
     * @param messId The ID of the mess to check ownership for
     * @return true if the current user is the owner of the mess or an admin
     */
    public boolean isMessOwnerOrAdmin(Long messId) {
        if (messId == null) {
            return false;
        }
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return false;
        }
        // Check if user is admin
        if (currentUser.getRole() != null && currentUser.getRole().equals(RoleType.ADMIN)) {
            return true;
        }
        // Check if user is mess owner (placeholder implementation)
        return false;
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

    /**
     * Checks if the current user has the specified role.
     *
     * @param role The role to check
     * @return true if the current user has the specified role, false otherwise
     */
    public boolean hasRole(RoleType role) {
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
                .map((User user) -> user.getRole() != null && user.getRole().equals(role))
                .orElse(false);
    }

    /**
     * Retrieves the current authenticated user.
     * 
     * @return The authenticated user, or null if not authenticated
     */
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

    /**
     * Retrieves the current authenticated user's ID.
     * 
     * @return The user ID, or null if not authenticated
     */
    public Long getCurrentUserId() {
        User currentUser = getCurrentUser();
        return currentUser != null ? currentUser.getId() : null;
    }
}
