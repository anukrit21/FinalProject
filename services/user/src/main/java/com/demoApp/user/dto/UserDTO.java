package com.demoApp.user.dto;

import com.demoApp.user.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;
    
    @NotBlank
    @Email
    private String email;
    
    @NotBlank
    @Size(min = 6)
    private String password;
    
    @NotBlank
    private String name;
    
    private String description;
    private String imagePath;
    private User.UserType memberType;
    private boolean isVerified;
    private String address;
    private String mobileNumber;
    private String role;
    private String category;
    private List<String> preferredCategory;
}