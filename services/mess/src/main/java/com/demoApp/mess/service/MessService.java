package com.demoApp.mess.service;

import com.demoApp.mess.dto.MessDTO;
import com.demoApp.mess.entity.Mess;
import com.demoApp.mess.exception.DuplicateResourceException;
import com.demoApp.mess.exception.ResourceNotFoundException;
import com.demoApp.mess.repository.MessRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessService {

    private final MessRepository messRepository;
    private final PasswordEncoder passwordEncoder;

    public List<Mess> getAllMesses() {
        log.debug("Getting all messes");
        return messRepository.findAll();
    }

    public List<Mess> getApprovedMesses() {
        log.debug("Getting approved messes");
        return messRepository.findByApproved(true);
    }

    public List<Mess> getPendingMesses() {
        log.debug("Getting pending messes");
        return messRepository.findByApproved(false);
    }

    public Mess getMessById(Long id) {
        log.debug("Getting mess by ID: {}", id);
        return messRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mess not found with ID: " + id));
    }

    @Transactional
public Mess createMess(MessDTO messDTO, String role) {
    log.debug("Creating new mess with email: {}", messDTO.getEmail());

    if (messRepository.existsByEmail(messDTO.getEmail())) {
        throw new DuplicateResourceException("Email is already registered");
    }

    Mess mess = Mess.builder()
            .name(messDTO.getName()) 
            .email(messDTO.getEmail())
            .password(passwordEncoder.encode(messDTO.getPassword()))
            .contactNumber(messDTO.getContactNumber())
            .location(messDTO.getLocation())
            .approved(false)
            .role(Mess.Role.valueOf(role.toUpperCase()))  
            .build();

    return messRepository.save(mess);
}


    @Transactional
    public Mess updateMess(Long id, MessDTO messDTO) {
        log.debug("Updating mess with ID: {}", id);

        Mess mess = messRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mess not found with ID: " + id));

        if (!mess.getEmail().equals(messDTO.getEmail()) &&
                messRepository.existsByEmail(messDTO.getEmail())) {
            throw new ResourceNotFoundException("Email is already registered");
        }

        // Mapping messName from DTO to 'name' in entity
        mess.setName(messDTO.getName()); 
        mess.setEmail(messDTO.getEmail());
        mess.setContactNumber(messDTO.getContactNumber());
        mess.setLocation(messDTO.getLocation());

        if (messDTO.getPassword() != null && !messDTO.getPassword().isEmpty()) {
            mess.setPassword(passwordEncoder.encode(messDTO.getPassword()));
        }

        return messRepository.save(mess);
    }

    @Transactional
    public Mess approveMess(Long id) {
        log.debug("Approving mess with ID: {}", id);

        Mess mess = messRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mess not found with ID: " + id));

        mess.setApproved(true);
        return messRepository.save(mess);
    }

    @Transactional
    public void deleteMess(Long id) {
        log.debug("Deleting mess with ID: {}", id);

        Mess mess = messRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mess not found with ID: " + id));

        messRepository.delete(mess);
    }
}
