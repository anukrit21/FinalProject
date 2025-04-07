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

    public List<DeliveryPersonDTO> getAllDeliveryPersons() {
        return deliveryPersonRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

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

    public List<DeliveryPersonDTO> getDeliveryPersonsByMess(Long messId) {
        User mess = messId == null ? userSecurity.getCurrentUser() :
                userRepository.findById(messId).orElseThrow(() -> new ResourceNotFoundException("Mess not found with id: " + messId));

        return deliveryPersonRepository.findByMess(mess).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<DeliveryPersonDTO> getActiveDeliveryPersonsByMess(Long messId) {
        User mess = messId == null ? userSecurity.getCurrentUser() :
                userRepository.findById(messId).orElseThrow(() -> new ResourceNotFoundException("Mess not found with id: " + messId));

        return deliveryPersonRepository.findByMessAndActiveTrue(mess).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public DeliveryPersonDTO getDeliveryPersonById(Long id) {
        DeliveryPerson deliveryPerson = deliveryPersonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery person not found with id: " + id));
        return convertToDTO(deliveryPerson);
    }

    @Transactional
    public DeliveryPersonDTO createDeliveryPerson(DeliveryPersonDTO deliveryPersonDTO) {
        if (deliveryPersonRepository.existsByEmail(deliveryPersonDTO.getEmail())) {
            throw new BadRequestException("Email already in use");
        }
        if (deliveryPersonRepository.existsByPhone(deliveryPersonDTO.getPhone())) {
            throw new BadRequestException("Phone number already in use");
        }

        User mess = deliveryPersonDTO.getMessId() == null ? userSecurity.getCurrentUser() :
                userRepository.findById(deliveryPersonDTO.getMessId())
                        .orElseThrow(() -> new ResourceNotFoundException("Mess not found with id: " + deliveryPersonDTO.getMessId()));

        DeliveryPerson deliveryPerson = modelMapper.map(deliveryPersonDTO, DeliveryPerson.class);
        deliveryPerson.setMess(mess);
        deliveryPerson.setActive(true);
        deliveryPerson.setAverageRating(0.0);

        return convertToDTO(deliveryPersonRepository.save(deliveryPerson));
    }

    public DeliveryPersonDTO updateDeliveryPerson(Long id, DeliveryPersonDTO deliveryPersonDTO) {
        DeliveryPerson existing = deliveryPersonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery person not found with id: " + id));

        modelMapper.map(deliveryPersonDTO, existing);
        return convertToDTO(deliveryPersonRepository.save(existing));
    }

    public void deleteDeliveryPerson(Long id) {
        if (!deliveryPersonRepository.existsById(id)) {
            throw new ResourceNotFoundException("Delivery person not found with id: " + id);
        }
        deliveryPersonRepository.deleteById(id);
    }

    public DeliveryPersonDTO activateDeliveryPerson(Long id) {
        DeliveryPerson person = deliveryPersonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery person not found with id: " + id));
        person.setActive(true);
        return convertToDTO(deliveryPersonRepository.save(person));
    }

    public DeliveryPersonDTO deactivateDeliveryPerson(Long id) {
        DeliveryPerson person = deliveryPersonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery person not found with id: " + id));
        person.setActive(false);
        return convertToDTO(deliveryPersonRepository.save(person));
    }

    public DeliveryPersonDTO uploadProfileImage(Long id, MultipartFile file) {
        DeliveryPerson person = deliveryPersonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery person not found with id: " + id));

        String fileName = fileStorageService.storeFile(file);
        person.setProfileImageUrl(fileName);
        return convertToDTO(deliveryPersonRepository.save(person));
    }

    public DeliveryPersonDTO uploadIdProof(Long id, MultipartFile file) {
        DeliveryPerson person = deliveryPersonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery person not found with id: " + id));

        String fileName = fileStorageService.storeFile(file);
        person.setIdProofUrl(fileName);
        return convertToDTO(deliveryPersonRepository.save(person));
    }

    public DeliveryPersonDTO updateDeliveryPersonRating(Long id, Double rating) {
        DeliveryPerson person = deliveryPersonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery person not found with id: " + id));

        person.setAverageRating(rating);
        return convertToDTO(deliveryPersonRepository.save(person));
    }

    private DeliveryPersonDTO convertToDTO(DeliveryPerson deliveryPerson) {
        return modelMapper.map(deliveryPerson, DeliveryPersonDTO.class);
    }
}