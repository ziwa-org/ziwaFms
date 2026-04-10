package com.example.ziwa.service;

import com.example.ziwa.exception.BusinessRuleException;
import com.example.ziwa.exception.DuplicateResourceException;
import com.example.ziwa.exception.ResourceNotFoundException;
import com.example.ziwa.model.BreedingRecord;
import com.example.ziwa.model.Cow;
import com.example.ziwa.model.Cow.CowStatus;
import com.example.ziwa.repository.BreedingRecordRepository;
import com.example.ziwa.repository.CowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class CowService {
    private final CowRepository cowRepository;
    private final BreedingRecordRepository breedingRecordRepository;

    /**
     * Register a new cow with tag ID uniqueness validation
     * Requirements: 1.1, 1.2
     */
    public Cow registerCow(Cow cow) {
        // Validate tag ID uniqueness
        if (cowRepository.existsByTagId(cow.getTagId())) {
            throw new DuplicateResourceException("Cow with tag ID '" + cow.getTagId() + "' already exists");
        }
        
        return cowRepository.save(cow);
    }

    /**
     * Get cow by ID with not found handling
     * Requirements: 1.4
     */
    @Transactional(readOnly = true)
    public Cow getCowById(Long id) {
        return cowRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cow not found with id: " + id));
    }

    /**
     * List cows with filtering by status and breed
     * Requirements: 1.5
     */
    @Transactional(readOnly = true)
    public Page<Cow> listCows(CowStatus status, String breed, Pageable pageable) {
        if (status != null && breed != null && !breed.trim().isEmpty()) {
            return cowRepository.findByStatusAndBreed(status, breed, pageable);
        } else if (status != null) {
            return cowRepository.findByStatus(status, pageable);
        } else {
            return cowRepository.findAll(pageable);
        }
    }

    /**
     * Update cow with validation
     * Requirements: 1.7
     */
    public Cow updateCow(Long id, Cow updatedCow) {
        Cow existingCow = getCowById(id);
        
        // Validate tag ID uniqueness if it's being changed
        if (!existingCow.getTagId().equals(updatedCow.getTagId())) {
            if (cowRepository.existsByTagId(updatedCow.getTagId())) {
                throw new DuplicateResourceException("Cow with tag ID '" + updatedCow.getTagId() + "' already exists");
            }
        }
        
        // Update fields
        existingCow.setTagId(updatedCow.getTagId());
        existingCow.setBreed(updatedCow.getBreed());
        existingCow.setDateOfBirth(updatedCow.getDateOfBirth());
        existingCow.setAcquisitionDate(updatedCow.getAcquisitionDate());
        existingCow.setStatus(updatedCow.getStatus());
        
        return cowRepository.save(existingCow);
    }

    /**
     * Delete cow with dependency checking
     * Requirements: 1.6, 11.2
     */
    public void deleteCow(Long id) {
        Cow cow = getCowById(id);
        
        // Check for dependencies
        if (!cow.getProductionRecords().isEmpty()) {
            throw new BusinessRuleException("Cannot delete cow with existing production records");
        }
        
        if (!cow.getHealthRecords().isEmpty()) {
            throw new BusinessRuleException("Cannot delete cow with existing health records");
        }
        
        if (!cow.getBreedingRecords().isEmpty()) {
            throw new BusinessRuleException("Cannot delete cow with existing breeding records");
        }
        
        cowRepository.delete(cow);
    }

    /**
     * Update cow status
     * Requirements: 1.3
     */
    public Cow updateCowStatus(Long id, CowStatus status) {
        Cow cow = getCowById(id);
        cow.setStatus(status);
        return cowRepository.save(cow);
    }

    /**
     * Add breeding record with date validation
     * Requirements: 2.1, 2.2
     */
    public BreedingRecord addBreedingRecord(Long cowId, BreedingRecord breedingRecord) {
        Cow cow = getCowById(cowId);
        
        // Validate breeding date is not in the future
        if (breedingRecord.getBreedingDate().isAfter(LocalDate.now())) {
            throw new BusinessRuleException("Breeding date cannot be in the future");
        }
        
        breedingRecord.setCow(cow);
        return breedingRecordRepository.save(breedingRecord);
    }

    /**
     * Get breeding records for a cow
     * Requirements: 2.3
     */
    @Transactional(readOnly = true)
    public List<BreedingRecord> getBreedingRecords(Long cowId) {
        // Verify cow exists
        getCowById(cowId);
        return breedingRecordRepository.findByCowId(cowId);
    }

    // Backward compatibility methods for existing controller (will be replaced in task 2.5)
    @Deprecated
    public List<Cow> getAllCows() {
        return cowRepository.findAll();
    }

    @Deprecated
    public Optional<Cow> getCowByIdOptional(Long id) {
        return cowRepository.findById(id);
    }

    @Deprecated
    public Cow saveCow(Cow cow) {
        return cowRepository.save(cow);
    }
}
