package com.demoApp.user.repository;

import com.demoApp.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    Optional<User> findByEmailAndIsVerifiedTrue(String email);
    
    @Query("SELECT u FROM User u WHERE u.memberType = 'VENDOR'")
    List<User> findAllVendors();
    
    List<User> findByCategory(String category);
    
    @Query("SELECT u FROM User u WHERE :category MEMBER OF u.preferredCategory")
    List<User> findUsersByPreferredCategory(String category);
}