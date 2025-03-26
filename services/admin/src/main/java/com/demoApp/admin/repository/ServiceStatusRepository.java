package com.demoApp.admin.repository;

import com.demoApp.admin.entity.ServiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ServiceStatusRepository extends JpaRepository<ServiceStatus, Long> {
    Optional<ServiceStatus> findByServiceName(String serviceName);
} 