package com.demoApp.owner.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.demoApp.owner.dto.OwnerDTO;
import com.demoApp.owner.entity.Owner;
import com.demoApp.owner.exception.ResourceNotFoundException;
import com.demoApp.owner.repository.OwnerRepository;

@Service
@RequiredArgsConstructor
public class OwnerService {

    private final OwnerRepository ownerRepository;
    private final ModelMapper modelMapper;

    /**
     * Creates a new Owner with the given DTO and role.
     *
     * @param ownerDTO the owner data transfer object containing details for creation.
     * @param role the role to assign (e.g., "ROLE_OWNER")
     * @return the saved Owner entity.
     */
    @Transactional
    public Owner createOwner(OwnerDTO ownerDTO, String role) {
        Owner owner = modelMapper.map(ownerDTO, Owner.class);
        owner.setRole(role);
        return ownerRepository.save(owner);
    }

    public OwnerDTO getOwnerById(Long id) {
        Owner owner = ownerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found with id: " + id));
        return modelMapper.map(owner, OwnerDTO.class);
    }

    public OwnerDTO updateOwner(Long id, OwnerDTO ownerDTO) {
        Owner owner = ownerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found with id: " + id));
        modelMapper.map(ownerDTO, owner);
        Owner updatedOwner = ownerRepository.save(owner);
        return modelMapper.map(updatedOwner, OwnerDTO.class);
    }

    public void deleteOwner(Long id) {
        Owner owner = ownerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found with id: " + id));
        ownerRepository.delete(owner);
    }
}
