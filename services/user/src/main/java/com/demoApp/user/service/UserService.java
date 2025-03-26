package com.demoApp.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.demoApp.user.dto.UserCategoryDTO;
import com.demoApp.user.dto.UserDTO;
import com.demoApp.user.exception.BadRequestException;
import com.demoApp.user.exception.DuplicateResourceException;
import com.demoApp.user.exception.ResourceNotFoundException;
import com.demoApp.user.model.User;
import com.demoApp.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    
    @Transactional
    public User registerUser(UserDTO userDTO) {
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new DuplicateResourceException("Email already registered");
        }
        
        User user = modelMapper.map(userDTO, User.class);
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setMemberType(User.UserType.CUSTOMER); // Default to CUSTOMER
        user.setVerified(false);
        user.setCreatedAt(LocalDateTime.now());
        
        User savedUser = userRepository.save(user);
        emailService.sendVerificationEmail(savedUser);
        
        return savedUser;
    }
    
    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return modelMapper.map(user, UserDTO.class);
    }
    
    @Transactional
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        
        // If trying to change email, check for duplicates
        if (!user.getEmail().equals(userDTO.getEmail()) && 
            userRepository.existsByEmail(userDTO.getEmail())) {
            throw new DuplicateResourceException("Email already in use");
        }
        
        // Don't update password through this method
        user.setName(userDTO.getName());
        user.setDescription(userDTO.getDescription());
        user.setAddress(userDTO.getAddress());
        user.setMobileNumber(userDTO.getMobileNumber());
        user.setUpdatedAt(LocalDateTime.now());
        
        if (userDTO.getPreferredCategory() != null) {
            user.setPreferredCategory(userDTO.getPreferredCategory());
        }
        
        User updatedUser = userRepository.save(user);
        log.info("User updated successfully: {}", updatedUser.getId());
        return modelMapper.map(updatedUser, UserDTO.class);
    }
    
    public List<UserDTO> getAllVendors() {
        List<User> vendors = userRepository.findAllVendors();
        return vendors.stream()
                .map(vendor -> modelMapper.map(vendor, UserDTO.class))
                .collect(Collectors.toList());
    }
    
    public List<UserDTO> getUsersByCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            throw new BadRequestException("Category cannot be empty");
        }
        
        List<User> users = userRepository.findByCategory(category);
        return users.stream()
                .map(user -> modelMapper.map(user, UserDTO.class))
                .collect(Collectors.toList());
    }
    
    public List<UserDTO> getUsersByPreferredCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            throw new BadRequestException("Category cannot be empty");
        }
        
        List<User> users = userRepository.findUsersByPreferredCategory(category);
        return users.stream()
                .map(user -> modelMapper.map(user, UserDTO.class))
                .collect(Collectors.toList());
    }
    
    @Transactional
    public void updateUserCategories(Long userId, UserCategoryDTO userCategoryDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        if (userCategoryDTO.getCategories() == null || userCategoryDTO.getCategories().isEmpty()) {
            throw new BadRequestException("Categories list cannot be empty");
        }
        
        user.setPreferredCategory(userCategoryDTO.getCategories());
        user.setUpdatedAt(LocalDateTime.now());
        
        userRepository.save(user);
        log.info("User categories updated for user: {}", userId);
    }
    
    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        
        userRepository.delete(user);
        log.info("User deleted: {}", id);
    }
}