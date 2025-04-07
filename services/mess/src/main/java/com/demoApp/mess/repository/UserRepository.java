package com.demoApp.mess.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.demoApp.mess.entity.User;
import com.demoApp.mess.entity.User.Role;
import com.demoApp.mess.enums.RoleType; 

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByUsername(String username);
    Page<User> findEnabledUsersByRole(User.Role role, Pageable pageable);
    Optional<User> findByEmail(String email);
    
    Boolean existsByUsername(String username);
    
    Boolean existsByEmail(String email);
    
    Optional<User> findByUsernameOrEmail(String username, String email);
    
    // ✅ Remove duplicate and incorrect `findByRole` method
    List<User> findByRole(Role role);
    
    @Query("SELECT u FROM User u WHERE u.role = :role AND u.enabled = true")
    Page<User> findEnabledUsersByRole(RoleType role, Pageable pageable);
    
    @Query("SELECT u FROM User u WHERE " +
           "(LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "AND u.enabled = true")
    Page<User> searchUsers(String keyword, Pageable pageable);
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.createdAt >= :startDate")
    Long countNewUsersAfterDate(LocalDateTime startDate);

    Optional<User> findByResetToken(String token);
}
