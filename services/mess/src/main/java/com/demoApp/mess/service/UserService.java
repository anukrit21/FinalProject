package com.demoApp.mess.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.demoApp.mess.dto.UserDTO;
import com.demoApp.mess.entity.User;
import com.demoApp.mess.enums.RoleType;
import com.demoApp.mess.exception.BadRequestException;
import com.demoApp.mess.exception.ResourceNotFoundException;
import com.demoApp.mess.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;
    private final AuthService authService;

    // Helper method to convert between RoleType and User.Role
    private User.Role convertToUserRole(RoleType roleType) {
        return User.Role.valueOf(roleType.name());
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserDTO::fromUser)
                .collect(Collectors.toList());
    }

    public Map<String, Object> getUsersPaged(int page, int size, String search) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        
        Page<User> usersPage;
        if (search != null && !search.isEmpty()) {
            usersPage = userRepository.searchUsers(search, pageable);
        } else {
            usersPage = userRepository.findAll(pageable);
        }
        
        List<UserDTO> userDTOs = usersPage.getContent()
                .stream()
                .map(UserDTO::fromUser)
                .collect(Collectors.toList());
        
        Map<String, Object> response = new HashMap<>();
        response.put("users", userDTOs);
        response.put("currentPage", usersPage.getNumber());
        response.put("totalItems", usersPage.getTotalElements());
        response.put("totalPages", usersPage.getTotalPages());
        
        return response;
    }

    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return UserDTO.fromUser(user);
    }

    public UserDTO getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
        return UserDTO.fromUser(user);
    }

    public UserDTO getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        return UserDTO.fromUser(user);
    }

    @Transactional
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        if (userDTO.getUsername() != null && !userDTO.getUsername().equals(user.getUsername()) &&
                userRepository.existsByUsername(userDTO.getUsername())) {
            throw new BadRequestException("Username is already taken");
        }

        if (userDTO.getEmail() != null && !userDTO.getEmail().equals(user.getEmail()) &&
                userRepository.existsByEmail(userDTO.getEmail())) {
            throw new BadRequestException("Email is already registered");
        }

        if (userDTO.getUsername() != null) {
            user.setUsername(userDTO.getUsername());
        }
        if (userDTO.getEmail() != null) {
            user.setEmail(userDTO.getEmail());
        }
        if (userDTO.getFirstName() != null) {
            user.setFirstName(userDTO.getFirstName()); // Fixed: was setting username instead of firstName
        }
        if (userDTO.getLastName() != null) {
            user.setLastName(userDTO.getLastName());
        }
        if (userDTO.getPhoneNumber() != null) {
            user.setPhoneNumber(userDTO.getPhoneNumber());
        }
        if (userDTO.getAddress() != null) {
            user.setAddress(userDTO.getAddress());
        }
        if (userDTO.getRole() != null) {
            user.setRole(convertToUserRole(userDTO.getRole())); // Fixed: proper role conversion
        }
        if (userDTO.isActive() != user.isActive()) {
            user.setActive(userDTO.isActive());
        }
        if (userDTO.isEnabled() != user.isEnabled()) {
            user.setEnabled(userDTO.isEnabled());
        }

        User updatedUser = userRepository.save(user);
        return UserDTO.fromUser(updatedUser);
    }

    @Transactional
    public UserDTO updateProfileImage(Long id, MultipartFile image) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        if (user.getProfileImageUrl() != null && !user.getProfileImageUrl().isEmpty()) {
            fileStorageService.deleteFile(user.getProfileImageUrl());
        }

        String imageUrl = fileStorageService.uploadUserImage(image);
        user.setProfileImageUrl(imageUrl);

        User updatedUser = userRepository.save(user);
        return UserDTO.fromUser(updatedUser);
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        if (user.getProfileImageUrl() != null && !user.getProfileImageUrl().isEmpty()) {
            fileStorageService.deleteFile(user.getProfileImageUrl());
        }

        userRepository.delete(user);
    }

    @Transactional
    public UserDTO activateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        user.setEnabled(true);
        user.setActive(true);
        User updatedUser = userRepository.save(user);
        return UserDTO.fromUser(updatedUser);
    }

    @Transactional
    public UserDTO deactivateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        user.setEnabled(false);
        user.setActive(false);
        User updatedUser = userRepository.save(user);
        return UserDTO.fromUser(updatedUser);
    }

    public UserDTO getCurrentUser() {
        User currentUser = authService.getCurrentUser();
        if (currentUser == null) {
            return null;
        }
        return UserDTO.fromUser(currentUser);
    }

    public List<UserDTO> getUsersByRole(RoleType roleType) { // Fixed: changed parameter type
        return userRepository.findByRole(convertToUserRole(roleType)).stream() // Fixed: proper method call
                .map(UserDTO::fromUser)
                .collect(Collectors.toList());
    }

    public Map<String, Object> getUsersByRolePaged(RoleType roleType, int page, int size) { // Fixed: changed parameter type
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<User> usersPage = userRepository.findEnabledUsersByRole(convertToUserRole(roleType), pageable); // Fixed: proper role conversion
        
        List<UserDTO> userDTOs = usersPage.getContent()
                .stream()
                .map(UserDTO::fromUser)
                .collect(Collectors.toList());
        
        Map<String, Object> response = new HashMap<>();
        response.put("users", userDTOs);
        response.put("currentPage", usersPage.getNumber());
        response.put("totalItems", usersPage.getTotalElements());
        response.put("totalPages", usersPage.getTotalPages());
        
        return response;
    }

    public Long countNewUsersInLast30Days() {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        return userRepository.countNewUsersAfterDate(thirtyDaysAgo);
    }
}