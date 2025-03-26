package com.demoApp.authentication.repository;

import com.demoApp.authentication.entity.Authentication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthenticationRepository extends JpaRepository<Authentication, Long> {
    
    Optional<Authentication> findByUsername(String username);
    
    boolean existsByUsername(String username);
    
    Optional<Authentication> findByToken(String token);
    
    Optional<Authentication> findByUserId(Long userId);
    
    Optional<Authentication> findByResetToken(String resetToken);
    
    @Query("SELECT a FROM Authentication a JOIN a.oauthProviders p WHERE p.providerName = :provider AND p.providerUserId = :providerId")
    Optional<Authentication> findByOauthProviderAndProviderId(
            @Param("provider") String provider, 
            @Param("providerId") String providerId);
} 