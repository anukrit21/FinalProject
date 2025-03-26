package com.demoApp.mess.dto.auth;

import com.demoApp.mess.entity.User;
import com.demoApp.mess.enums.RoleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String fullName;
    private String phoneNumber;
    private String profileImageUrl;
    private String address;
    private RoleType role;  // Correct RoleType for user roles
    private String token;

    // Map User.Role to RoleType
    private static RoleType mapRoleToRoleType(User.Role userRole) {
        switch (userRole) {
            case ADMIN:
                return RoleType.ADMIN;
            case MESS_OWNER:
                return RoleType.MESS_OWNER;
            case DELIVERY_PERSON:
                return RoleType.DELIVERY_PERSON;
            case CUSTOMER:
                return RoleType.CUSTOMER;
            default:
                throw new IllegalArgumentException("Unknown role: " + userRole);
        }
    }

    public static AuthResponse fromUser(User user, String token) {
        if (user == null) {
            return null;
        }

        return AuthResponse.builder()
                .id(user.getId())  // Set the id of the User entity
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .fullName(user.getFullName())
                .phoneNumber(user.getPhoneNumber())
                .profileImageUrl(user.getProfileImageUrl())
                .address(user.getAddress())
                .role(mapRoleToRoleType(user.getRole()))  // Convert User.Role to RoleType
                .token(token)
                .build();
    }
}
