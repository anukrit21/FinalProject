package com.demoApp.mess.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.demoApp.mess.dto.DeliveryPersonDTO;
import com.demoApp.mess.entity.DeliveryPerson;
import com.demoApp.mess.entity.User;
import com.demoApp.mess.exception.BadRequestException;
import com.demoApp.mess.exception.ResourceNotFoundException;
import com.demoApp.mess.repository.DeliveryPersonRepository;
import com.demoApp.mess.repository.UserRepository;
import com.demoApp.mess.security.UserSecurity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeliveryPersonService {

    private final DeliveryPersonRepository deliveryPersonRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final FileStorageService fileStorageService;
    private final UserSecurity userSecurity;

    /**
     * Get all delivery persons (admin only)
     */
    public List<DeliveryPersonDTO> getAllDeliveryPersons() {
        return deliveryPersonRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get delivery persons with pagination and search
     */
    public Map<String, Object> getDeliveryPersonsPaged(int page, int size, String search) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        Page<DeliveryPerson> deliveryPersonsPage;
        
        if (search != null && !search.isEmpty()) {
            deliveryPersonsPage = deliveryPersonRepository.searchDeliveryPersons(search, pageable);
        } else {
            deliveryPersonsPage = deliveryPersonRepository.findAll(pageable);
        }
        
        List<DeliveryPersonDTO> deliveryPersonDTOs = deliveryPersonsPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        Map<String, Object> response = new HashMap<>();
        response.put("deliveryPersons", deliveryPersonDTOs);
        response.put("currentPage", deliveryPersonsPage.getNumber());
        response.put("totalItems", deliveryPersonsPage.getTotalElements());
        response.put("totalPages", deliveryPersonsPage.getTotalPages());
        
        return response;
    }

    /**
     * Get delivery persons for a specific mess
     */
    public List<DeliveryPersonDTO> getDeliveryPersonsByMess(Long messId) {
        User mess = userRepository.findById(messId)
                .orElseThrow(() -> new ResourceNotFoundException("Mess not found with id: " + messId));
        
        return deliveryPersonRepository.findByMess(mess).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get delivery persons for current mess
     */
    public List<DeliveryPersonDTO> getDeliveryPersonsForCurrentMess() {
        User currentUser = userSecurity.getCurrentUser();
        if (currentUser == null) {
            throw new BadRequestException("No authenticated user found");
        }
        return deliveryPersonRepository.findByMess(currentUser).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get delivery persons for a specific mess with pagination and search
     */
    public Map<String, Object> getDeliveryPersonsByMessPaged(Long messId, int page, int size, String search) {
        User mess = userRepository.findById(messId)
                .orElseThrow(() -> new ResourceNotFoundException("Mess not found with id: " + messId));
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        Page<DeliveryPerson> deliveryPersonsPage;
        
        if (search != null && !search.isEmpty()) {
            deliveryPersonsPage = deliveryPersonRepository.searchDeliveryPersonsByMess(mess, search, pageable);
        } else {
            deliveryPersonsPage = deliveryPersonRepository.findByMess(mess, pageable);
        }
        
        List<DeliveryPersonDTO> deliveryPersonDTOs = deliveryPersonsPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        Map<String, Object> response = new HashMap<>();
        response.put("deliveryPersons", deliveryPersonDTOs);
        response.put("currentPage", deliveryPersonsPage.getNumber());
        response.put("totalItems", deliveryPersonsPage.getTotalElements());
        response.put("totalPages", deliveryPersonsPage.getTotalPages());
        
        return response;
    }

    /**
     * Get delivery person by ID
     */
    public DeliveryPersonDTO getDeliveryPersonById(Long id) {
        DeliveryPerson deliveryPerson = deliveryPersonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery person not found with id: " + id));
        return convertToDTO(deliveryPerson);
    }

    /**
     * Create a new delivery person
     */
    @Transactional
    public DeliveryPersonDTO createDeliveryPerson(DeliveryPersonDTO deliveryPersonDTO) {
        if (deliveryPersonRepository.existsByEmail(deliveryPersonDTO.getEmail())) {
            throw new BadRequestException("Email already in use");
        }
        if (deliveryPersonRepository.existsByPhone(deliveryPersonDTO.getPhone())) {
            throw new BadRequestException("Phone number already in use");
        }
        
        User mess;
        if (deliveryPersonDTO.getMessId() != null) {
            mess = userRepository.findById(deliveryPersonDTO.getMessId())
                    .orElseThrow(() -> new ResourceNotFoundException("Mess not found with id: " + deliveryPersonDTO.getMessId()));
        } else {
            mess = userSecurity.getCurrentUser();
            if (mess == null) {
                throw new BadRequestException("No authenticated user found");
            }
        }
        
        DeliveryPerson deliveryPerson = modelMapper.map(deliveryPersonDTO, DeliveryPerson.class);
        deliveryPerson.setId(null); // New entity
        deliveryPerson.setMess(mess);
        deliveryPerson.setActive(true);
        deliveryPerson.setAverageRating(0.0);
        deliveryPerson.setTotalRatings(0);
        deliveryPerson.setCreatedBy(userSecurity.getCurrentUserId());
        
        DeliveryPerson savedDeliveryPerson = deliveryPersonRepository.save(deliveryPerson);
        return convertToDTO(savedDeliveryPerson);
    }

    /**
     * Update delivery person details
     */
    @Transactional
    public DeliveryPersonDTO updateDeliveryPerson(Long id, DeliveryPersonDTO deliveryPersonDTO) {
        DeliveryPerson deliveryPerson = deliveryPersonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery person not found with id: " + id));
        
        if (!deliveryPerson.getEmail().equals(deliveryPersonDTO.getEmail()) &&
                deliveryPersonRepository.existsByEmail(deliveryPersonDTO.getEmail())) {
            throw new BadRequestException("Email already in use");
        }
        if (!deliveryPerson.getPhone().equals(deliveryPersonDTO.getPhone()) &&
                deliveryPersonRepository.existsByPhone(deliveryPersonDTO.getPhone())) {
            throw new BadRequestException("Phone number already in use");
        }
        
        if (deliveryPersonDTO.getName() != null) {
            deliveryPerson.setName(deliveryPersonDTO.getName());
        }
        if (deliveryPersonDTO.getEmail() != null) {
            deliveryPerson.setEmail(deliveryPersonDTO.getEmail());
        }
        if (deliveryPersonDTO.getPhone() != null) {
            deliveryPerson.setPhone(deliveryPersonDTO.getPhone());
        }
        if (deliveryPersonDTO.getAddress() != null) {
            deliveryPerson.setAddress(deliveryPersonDTO.getAddress());
        }
        if (deliveryPersonDTO.getVehicleType() != null) {
            deliveryPerson.setVehicleType(deliveryPersonDTO.getVehicleType());
        }
        if (deliveryPersonDTO.getVehicleNumber() != null) {
            deliveryPerson.setVehicleNumber(deliveryPersonDTO.getVehicleNumber());
        }
        if (deliveryPersonDTO.getIdType() != null) {
            deliveryPerson.setIdType(deliveryPersonDTO.getIdType());
        }
        if (deliveryPersonDTO.getIdNumber() != null) {
            deliveryPerson.setIdNumber(deliveryPersonDTO.getIdNumber());
        }
        if (deliveryPersonDTO.getMessId() != null && userSecurity.isAdmin()) {
            User mess = userRepository.findById(deliveryPersonDTO.getMessId())
                    .orElseThrow(() -> new ResourceNotFoundException("Mess not found with id: " + deliveryPersonDTO.getMessId()));
            deliveryPerson.setMess(mess);
        }
        
        deliveryPerson.setUpdatedBy(userSecurity.getCurrentUserId());
        DeliveryPerson updatedDeliveryPerson = deliveryPersonRepository.save(deliveryPerson);
        return convertToDTO(updatedDeliveryPerson);
    }

    /**
     * Upload profile image for delivery person
     */
    @Transactional
    public DeliveryPersonDTO uploadProfileImage(Long id, MultipartFile image) {
        DeliveryPerson deliveryPerson = deliveryPersonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery person not found with id: " + id));
        
        if (deliveryPerson.getProfileImageUrl() != null) {
            fileStorageService.deleteFile(deliveryPerson.getProfileImageUrl());
        }
        
        String imageUrl = fileStorageService.uploadFile(image, "delivery-person-images");
        deliveryPerson.setProfileImageUrl(imageUrl);
        deliveryPerson.setUpdatedBy(userSecurity.getCurrentUserId());
        
        DeliveryPerson updatedDeliveryPerson = deliveryPersonRepository.save(deliveryPerson);
        return convertToDTO(updatedDeliveryPerson);
    }

    /**
     * Upload ID proof image for delivery person
     */
    @Transactional
    public DeliveryPersonDTO uploadIdProofImage(Long id, MultipartFile image) {
        DeliveryPerson deliveryPerson = deliveryPersonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery person not found with id: " + id));
        
        if (deliveryPerson.getIdProofImageUrl() != null) {
            fileStorageService.deleteFile(deliveryPerson.getIdProofImageUrl());
        }
        
        String imageUrl = fileStorageService.uploadFile(image, "delivery-person-id-proofs");
        deliveryPerson.setIdProofImageUrl(imageUrl);
        deliveryPerson.setUpdatedBy(userSecurity.getCurrentUserId());
        
        DeliveryPerson updatedDeliveryPerson = deliveryPersonRepository.save(deliveryPerson);
        return convertToDTO(updatedDeliveryPerson);
    }

    /**
     * Activate a delivery person
     */
    @Transactional
    public DeliveryPersonDTO activateDeliveryPerson(Long id) {
        DeliveryPerson deliveryPerson = deliveryPersonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery person not found with id: " + id));
        
        deliveryPerson.setActive(true);
        deliveryPerson.setUpdatedBy(userSecurity.getCurrentUserId());
        
        DeliveryPerson updatedDeliveryPerson = deliveryPersonRepository.save(deliveryPerson);
        return convertToDTO(updatedDeliveryPerson);
    }

    /**
     * Deactivate a delivery person
     */
    @Transactional
    public DeliveryPersonDTO deactivateDeliveryPerson(Long id) {
        DeliveryPerson deliveryPerson = deliveryPersonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery person not found with id: " + id));
        
        deliveryPerson.setActive(false);
        deliveryPerson.setUpdatedBy(userSecurity.getCurrentUserId());
        
        DeliveryPerson updatedDeliveryPerson = deliveryPersonRepository.save(deliveryPerson);
        return convertToDTO(updatedDeliveryPerson);
    }

    /**
     * Delete a delivery person
     */
    @Transactional
    public void deleteDeliveryPerson(Long id) {
        DeliveryPerson deliveryPerson = deliveryPersonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery person not found with id: " + id));
        
        if (deliveryPerson.getProfileImageUrl() != null) {
            fileStorageService.deleteFile(deliveryPerson.getProfileImageUrl());
        }
        if (deliveryPerson.getIdProofImageUrl() != null) {
            fileStorageService.deleteFile(deliveryPerson.getIdProofImageUrl());
        }
        
        deliveryPersonRepository.delete(deliveryPerson);
    }

    /**
     * Update delivery person rating
     */
    @Transactional
    public DeliveryPersonDTO updateRating(Long id, Integer rating) {
        if (rating < 1 || rating > 5) {
            throw new BadRequestException("Rating must be between 1 and 5");
        }
        
        DeliveryPerson deliveryPerson = deliveryPersonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery person not found with id: " + id));
        
        Double currentAverage = deliveryPerson.getAverageRating() != null ? deliveryPerson.getAverageRating() : 0.0;
        Integer totalRatings = deliveryPerson.getTotalRatings() != null ? deliveryPerson.getTotalRatings() : 0;
        
        Double newAverage = ((currentAverage * totalRatings) + rating) / (totalRatings + 1);
        
        deliveryPerson.setAverageRating(newAverage);
        deliveryPerson.setTotalRatings(totalRatings + 1);
        deliveryPerson.setUpdatedBy(userSecurity.getCurrentUserId());
        
        DeliveryPerson updatedDeliveryPerson = deliveryPersonRepository.save(deliveryPerson);
        return convertToDTO(updatedDeliveryPerson);
    }

    /**
     * Get active delivery persons by mess
     */
    public List<DeliveryPersonDTO> getActiveDeliveryPersonsByMess(Long messId) {
        User mess;
        if (messId != null) {
            mess = userRepository.findById(messId)
                    .orElseThrow(() -> new ResourceNotFoundException("Mess not found with id: " + messId));
        } else {
            mess = userSecurity.getCurrentUser();
            if (mess == null) {
                throw new BadRequestException("No authenticated user found");
            }
        }
        
        return deliveryPersonRepository.findByMessAndActiveTrue(mess).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Convert entity to DTO
     */
    private DeliveryPersonDTO convertToDTO(DeliveryPerson deliveryPerson) {
        DeliveryPersonDTO dto = modelMapper.map(deliveryPerson, DeliveryPersonDTO.class);
        if (deliveryPerson.getMess() != null) {
            dto.setMessId(deliveryPerson.getMess().getId());
            dto.setMessName(deliveryPerson.getMess().getUsername());
        }
        return dto;
    }

    public Object uploadIdProof(Long id, MultipartFile file) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'uploadIdProof'");
    }

    public Object updateDeliveryPersonRating(Long id, Double rating) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateDeliveryPersonRating'");
    }
}
