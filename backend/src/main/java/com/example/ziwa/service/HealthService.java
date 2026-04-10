package com.example.ziwa.service;

import com.example.ziwa.dto.HealthRecordRequest;
import com.example.ziwa.dto.HealthRecordResponse;
import com.example.ziwa.dto.WithdrawalResponse;
import com.example.ziwa.exception.ResourceNotFoundException;
import com.example.ziwa.model.Cow;
import com.example.ziwa.model.HealthRecord;
import com.example.ziwa.model.HealthRecord.HealthRecordType;
import com.example.ziwa.repository.CowRepository;
import com.example.ziwa.repository.HealthRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HealthService {
    private final HealthRecordRepository healthRecordRepository;
    private final CowRepository cowRepository;

    @Transactional
    public HealthRecordResponse createHealthRecord(HealthRecordRequest request) {
        Cow cow = cowRepository.findById(request.getCowId())
            .orElseThrow(() -> new ResourceNotFoundException("Cow not found with id: " + request.getCowId()));

        HealthRecord healthRecord = HealthRecord.builder()
            .cow(cow)
            .date(request.getDate())
            .recordType(request.getRecordType())
            .description(request.getDescription())
            .veterinarianName(request.getVeterinarianName())
            .medication(request.getMedication())
            .withdrawalPeriodDays(request.getWithdrawalPeriodDays() != null ? request.getWithdrawalPeriodDays() : 0)
            .cost(request.getCost())
            .build();

        HealthRecord saved = healthRecordRepository.save(healthRecord);
        return toResponse(saved);
    }

    public HealthRecordResponse getHealthRecordById(Long id) {
        HealthRecord healthRecord = healthRecordRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Health record not found with id: " + id));
        return toResponse(healthRecord);
    }

    public List<HealthRecordResponse> listHealthRecords(Long cowId, HealthRecordType type, 
                                                         LocalDate startDate, LocalDate endDate, 
                                                         Pageable pageable) {
        List<HealthRecord> records;

        if (cowId != null && type != null) {
            records = healthRecordRepository.findByCowIdAndRecordType(cowId, type, pageable);
        } else if (cowId != null) {
            records = healthRecordRepository.findByCowId(cowId, pageable);
        } else if (startDate != null && endDate != null) {
            records = healthRecordRepository.findByDateBetween(startDate, endDate, pageable);
        } else {
            records = healthRecordRepository.findAll(pageable).getContent();
        }

        return records.stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    @Transactional
    public HealthRecordResponse updateHealthRecord(Long id, HealthRecordRequest request) {
        HealthRecord healthRecord = healthRecordRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Health record not found with id: " + id));

        // Update fields
        healthRecord.setDate(request.getDate());
        healthRecord.setRecordType(request.getRecordType());
        healthRecord.setDescription(request.getDescription());
        healthRecord.setVeterinarianName(request.getVeterinarianName());
        healthRecord.setMedication(request.getMedication());
        healthRecord.setWithdrawalPeriodDays(request.getWithdrawalPeriodDays() != null ? request.getWithdrawalPeriodDays() : 0);
        healthRecord.setCost(request.getCost());

        HealthRecord updated = healthRecordRepository.save(healthRecord);
        return toResponse(updated);
    }

    @Transactional
    public void deleteHealthRecord(Long id) {
        if (!healthRecordRepository.existsById(id)) {
            throw new ResourceNotFoundException("Health record not found with id: " + id);
        }
        healthRecordRepository.deleteById(id);
    }

    public List<WithdrawalResponse> getActiveWithdrawals() {
        List<HealthRecord> activeWithdrawals = healthRecordRepository.findActiveWithdrawals();
        
        return activeWithdrawals.stream()
            .map(this::toWithdrawalResponse)
            .collect(Collectors.toList());
    }

    private HealthRecordResponse toResponse(HealthRecord healthRecord) {
        LocalDate withdrawalEndDate = null;
        if (healthRecord.getWithdrawalPeriodDays() > 0) {
            withdrawalEndDate = healthRecord.getDate().plusDays(healthRecord.getWithdrawalPeriodDays());
        }

        return HealthRecordResponse.builder()
            .id(healthRecord.getId())
            .cowId(healthRecord.getCow().getId())
            .cowTagId(healthRecord.getCow().getTagId())
            .date(healthRecord.getDate())
            .recordType(healthRecord.getRecordType())
            .description(healthRecord.getDescription())
            .veterinarianName(healthRecord.getVeterinarianName())
            .medication(healthRecord.getMedication())
            .withdrawalPeriodDays(healthRecord.getWithdrawalPeriodDays())
            .withdrawalEndDate(withdrawalEndDate)
            .cost(healthRecord.getCost())
            .createdAt(healthRecord.getCreatedAt())
            .build();
    }

    private WithdrawalResponse toWithdrawalResponse(HealthRecord healthRecord) {
        LocalDate withdrawalEndDate = healthRecord.getDate().plusDays(healthRecord.getWithdrawalPeriodDays());
        long daysRemaining = ChronoUnit.DAYS.between(LocalDate.now(), withdrawalEndDate);

        return WithdrawalResponse.builder()
            .cowId(healthRecord.getCow().getId())
            .cowTagId(healthRecord.getCow().getTagId())
            .healthRecordId(healthRecord.getId())
            .withdrawalEndDate(withdrawalEndDate)
            .daysRemaining((int) daysRemaining)
            .medication(healthRecord.getMedication())
            .build();
    }
}
