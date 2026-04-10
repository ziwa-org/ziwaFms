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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HealthServiceTest {

    @Mock
    private HealthRecordRepository healthRecordRepository;

    @Mock
    private CowRepository cowRepository;

    @InjectMocks
    private HealthService healthService;

    private Cow testCow;
    private HealthRecord testHealthRecord;
    private HealthRecordRequest testRequest;

    @BeforeEach
    void setUp() {
        testCow = Cow.builder()
            .id(1L)
            .tagId("COW001")
            .breed("Holstein")
            .dateOfBirth(LocalDate.of(2020, 1, 1))
            .acquisitionDate(LocalDate.of(2020, 1, 15))
            .status(Cow.CowStatus.ACTIVE)
            .build();

        testHealthRecord = HealthRecord.builder()
            .id(1L)
            .cow(testCow)
            .date(LocalDate.now())
            .recordType(HealthRecordType.VACCINATION)
            .description("Annual vaccination")
            .veterinarianName("Dr. Smith")
            .medication("Vaccine A")
            .withdrawalPeriodDays(7)
            .cost(50.0)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

        testRequest = HealthRecordRequest.builder()
            .cowId(1L)
            .date(LocalDate.now())
            .recordType(HealthRecordType.VACCINATION)
            .description("Annual vaccination")
            .veterinarianName("Dr. Smith")
            .medication("Vaccine A")
            .withdrawalPeriodDays(7)
            .cost(50.0)
            .build();
    }

    @Test
    void testCreateHealthRecord_Success() {
        when(cowRepository.findById(1L)).thenReturn(Optional.of(testCow));
        when(healthRecordRepository.save(any(HealthRecord.class))).thenReturn(testHealthRecord);

        HealthRecordResponse response = healthService.createHealthRecord(testRequest);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(1L, response.getCowId());
        assertEquals("COW001", response.getCowTagId());
        assertEquals(HealthRecordType.VACCINATION, response.getRecordType());
        assertEquals("Annual vaccination", response.getDescription());
        assertEquals(7, response.getWithdrawalPeriodDays());
        assertNotNull(response.getWithdrawalEndDate());
        assertEquals(LocalDate.now().plusDays(7), response.getWithdrawalEndDate());

        verify(cowRepository).findById(1L);
        verify(healthRecordRepository).save(any(HealthRecord.class));
    }

    @Test
    void testCreateHealthRecord_CowNotFound() {
        when(cowRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            healthService.createHealthRecord(testRequest);
        });

        verify(cowRepository).findById(1L);
        verify(healthRecordRepository, never()).save(any(HealthRecord.class));
    }

    @Test
    void testGetHealthRecordById_Success() {
        when(healthRecordRepository.findById(1L)).thenReturn(Optional.of(testHealthRecord));

        HealthRecordResponse response = healthService.getHealthRecordById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("COW001", response.getCowTagId());

        verify(healthRecordRepository).findById(1L);
    }

    @Test
    void testGetHealthRecordById_NotFound() {
        when(healthRecordRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            healthService.getHealthRecordById(1L);
        });

        verify(healthRecordRepository).findById(1L);
    }

    @Test
    void testUpdateHealthRecord_Success() {
        HealthRecordRequest updateRequest = HealthRecordRequest.builder()
            .cowId(1L)
            .date(LocalDate.now())
            .recordType(HealthRecordType.TREATMENT)
            .description("Updated treatment")
            .veterinarianName("Dr. Jones")
            .medication("Medicine B")
            .withdrawalPeriodDays(14)
            .cost(100.0)
            .build();

        when(healthRecordRepository.findById(1L)).thenReturn(Optional.of(testHealthRecord));
        when(healthRecordRepository.save(any(HealthRecord.class))).thenReturn(testHealthRecord);

        HealthRecordResponse response = healthService.updateHealthRecord(1L, updateRequest);

        assertNotNull(response);
        verify(healthRecordRepository).findById(1L);
        verify(healthRecordRepository).save(any(HealthRecord.class));
    }

    @Test
    void testDeleteHealthRecord_Success() {
        when(healthRecordRepository.existsById(1L)).thenReturn(true);
        doNothing().when(healthRecordRepository).deleteById(1L);

        assertDoesNotThrow(() -> healthService.deleteHealthRecord(1L));

        verify(healthRecordRepository).existsById(1L);
        verify(healthRecordRepository).deleteById(1L);
    }

    @Test
    void testDeleteHealthRecord_NotFound() {
        when(healthRecordRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> {
            healthService.deleteHealthRecord(1L);
        });

        verify(healthRecordRepository).existsById(1L);
        verify(healthRecordRepository, never()).deleteById(1L);
    }

    @Test
    void testGetActiveWithdrawals() {
        HealthRecord activeWithdrawal = HealthRecord.builder()
            .id(2L)
            .cow(testCow)
            .date(LocalDate.now().minusDays(3))
            .recordType(HealthRecordType.TREATMENT)
            .description("Treatment with withdrawal")
            .medication("Antibiotic")
            .withdrawalPeriodDays(7)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

        when(healthRecordRepository.findActiveWithdrawals()).thenReturn(Arrays.asList(activeWithdrawal));

        List<WithdrawalResponse> withdrawals = healthService.getActiveWithdrawals();

        assertNotNull(withdrawals);
        assertEquals(1, withdrawals.size());
        
        WithdrawalResponse withdrawal = withdrawals.get(0);
        assertEquals(1L, withdrawal.getCowId());
        assertEquals("COW001", withdrawal.getCowTagId());
        assertEquals(2L, withdrawal.getHealthRecordId());
        assertEquals("Antibiotic", withdrawal.getMedication());
        assertNotNull(withdrawal.getWithdrawalEndDate());
        assertTrue(withdrawal.getDaysRemaining() >= 0);

        verify(healthRecordRepository).findActiveWithdrawals();
    }

    @Test
    void testListHealthRecords_ByCowId() {
        Pageable pageable = PageRequest.of(0, 20);
        when(healthRecordRepository.findByCowId(1L, pageable)).thenReturn(Arrays.asList(testHealthRecord));

        List<HealthRecordResponse> records = healthService.listHealthRecords(1L, null, null, null, pageable);

        assertNotNull(records);
        assertEquals(1, records.size());
        assertEquals(1L, records.get(0).getCowId());

        verify(healthRecordRepository).findByCowId(1L, pageable);
    }

    @Test
    void testWithdrawalEndDateCalculation_ZeroWithdrawalPeriod() {
        HealthRecord noWithdrawal = HealthRecord.builder()
            .id(3L)
            .cow(testCow)
            .date(LocalDate.now())
            .recordType(HealthRecordType.CHECKUP)
            .description("Regular checkup")
            .withdrawalPeriodDays(0)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

        when(healthRecordRepository.findById(3L)).thenReturn(Optional.of(noWithdrawal));

        HealthRecordResponse response = healthService.getHealthRecordById(3L);

        assertNotNull(response);
        assertNull(response.getWithdrawalEndDate());
        assertEquals(0, response.getWithdrawalPeriodDays());
    }
}
