package com.demoApp.mess.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.demoApp.mess.entity.DeliveryPerson;
import com.demoApp.mess.entity.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeliveryPersonRepository extends JpaRepository<DeliveryPerson, Long> {
    
    // Find by email
    Optional<DeliveryPerson> findByEmail(String email);
    
    // Find by phone
    Optional<DeliveryPerson> findByPhone(String phone);
    
    // Check if email exists
    boolean existsByEmail(String email);
    
    // Check if phone exists
    boolean existsByPhone(String phone);
    
    // Find all by mess
    List<DeliveryPerson> findByMess(User mess);
    
    // Find all by mess with pagination
    Page<DeliveryPerson> findByMess(User mess, Pageable pageable);
    
    // Find active delivery persons by mess
    List<DeliveryPerson> findByMessAndActiveTrue(User mess);
    
    // Find active delivery persons by mess with pagination
    Page<DeliveryPerson> findByMessAndActiveTrue(User mess, Pageable pageable);
    
    // Find delivery persons with good ratings (customize the rating threshold as needed)
    @Query("SELECT d FROM DeliveryPerson d WHERE d.averageRating >= :minRating AND d.active = true")
    List<DeliveryPerson> findDeliveryPersonsByMinimumRating(Double minRating);
    
    // Search delivery persons by name or email or phone
    @Query("SELECT d FROM DeliveryPerson d WHERE " +
           "(LOWER(d.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(d.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "d.phone LIKE CONCAT('%', :keyword, '%'))")
    Page<DeliveryPerson> searchDeliveryPersons(String keyword, Pageable pageable);
    
    // Search delivery persons by mess and keyword
    @Query("SELECT d FROM DeliveryPerson d WHERE d.mess = :mess AND " +
           "(LOWER(d.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(d.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "d.phone LIKE CONCAT('%', :keyword, '%'))")
    Page<DeliveryPerson> searchDeliveryPersonsByMess(User mess, String keyword, Pageable pageable);
} 