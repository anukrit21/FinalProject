package com.demoApp.user.controller;

import com.demoApp.user.dto.UserCategoryDTO;
import com.demoApp.user.dto.UserDTO;
import com.demoApp.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final ModelMapper modelMapper;
    
    @PostMapping("/register")
    public ResponseEntity<UserDTO> registerUser(@Valid @RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(modelMapper.map(userService.registerUser(userDTO), UserDTO.class));
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("@userAuthorizationService.isUserAuthorized(#id)")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("@userAuthorizationService.isUserAuthorized(#id)")
    public ResponseEntity<UserDTO> updateUser(
            @PathVariable Long id, 
            @Valid @RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(userService.updateUser(id, userDTO));
    }
    
    @GetMapping("/vendors")
    public ResponseEntity<List<UserDTO>> getAllVendors() {
        return ResponseEntity.ok(userService.getAllVendors());
    }
    
    @GetMapping("/category/{category}")
    public ResponseEntity<List<UserDTO>> getUsersByCategory(@PathVariable String category) {
        return ResponseEntity.ok(userService.getUsersByCategory(category));
    }
    
    @GetMapping("/preferred-category/{category}")
    public ResponseEntity<List<UserDTO>> getUsersByPreferredCategory(@PathVariable String category) {
        return ResponseEntity.ok(userService.getUsersByPreferredCategory(category));
    }
    
    @PutMapping("/{id}/categories")
    @PreAuthorize("@userAuthorizationService.isUserAuthorized(#id)")
    public ResponseEntity<Void> updateUserCategories(
            @PathVariable Long id,
            @Valid @RequestBody UserCategoryDTO userCategoryDTO) {
        userService.updateUserCategories(id, userCategoryDTO);
        return ResponseEntity.ok().build();
    }
}
