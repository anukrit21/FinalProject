package com.demoApp.admin.service;

import com.demoApp.admin.dto.UserActivityDTO;
import com.demoApp.admin.entity.UserActivity;
import com.demoApp.admin.exception.ResourceNotFoundException;
import com.demoApp.admin.repository.UserActivityRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserActivityService {

    private final UserActivityRepository userActivityRepository;
    private final ModelMapper modelMapper;

    public Page<UserActivityDTO> getAllUserActivities(Pageable pageable) {
        Page<UserActivity> userActivities = userActivityRepository.findAll(pageable);
        return userActivities.map(this::convertToDTO);
    }

    public UserActivityDTO getUserActivityById(Long id) {
        UserActivity userActivity = userActivityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User activity not found with id: " + id));
        return convertToDTO(userActivity);
    }

    public Page<UserActivityDTO> getUserActivitiesByUserId(Long userId, Pageable pageable) {
        return userActivityRepository.findByUserIdOrderByTimestampDesc(userId, pageable)
                .map(this::convertToDTO);
    }

    public Page<UserActivityDTO> getUserActivitiesByType(String userAction, Pageable pageable) {
        return userActivityRepository.findByActivityTypeOrderByTimestampDesc(userAction, pageable)
                .map(this::convertToDTO);
    }

    public Page<UserActivityDTO> getUserActivitiesByDateRange(LocalDateTime start, LocalDateTime end, Pageable pageable) {
        return userActivityRepository.findByTimestampBetweenOrderByTimestampDesc(start, end, pageable)
                .map(this::convertToDTO);
    }

    @Transactional
    public UserActivityDTO createUserActivity(UserActivityDTO userActivityDTO) {
        UserActivity userActivity = modelMapper.map(userActivityDTO, UserActivity.class);
        UserActivity savedActivity = userActivityRepository.save(userActivity);
        return convertToDTO(savedActivity);
    }

    @Transactional
    public UserActivityDTO logUserActivity(Long userId, UserActivity.ActivityType activityType, String description) {
        UserActivity userActivity = new UserActivity();
        userActivity.setUserId(userId);
        userActivity.setActivityType(activityType);
        userActivity.setDescription(description);
        userActivity.setTimestamp(LocalDateTime.now());

        UserActivity savedActivity = userActivityRepository.save(userActivity);
        return convertToDTO(savedActivity);
    }

    private UserActivityDTO convertToDTO(UserActivity userActivity) {
        return modelMapper.map(userActivity, UserActivityDTO.class);
    }
}
