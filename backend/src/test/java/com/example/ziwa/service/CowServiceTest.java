package com.example.ziwa.service;

import com.example.ziwa.exception.BusinessRuleException;
import com.example.ziwa.exception.DuplicateResourceException;
import com.example.ziwa.exception.ResourceNotFoundException;
import com.example.ziwa.model.BreedingRecord;
import com.example.ziwa.model.Cow;
import com.example.ziwa.model.Cow.CowStatus;
import com.example.ziwa.model.MilkProduction;
import com.example.ziwa.repository.BreedingRecordRepository;
import com.example.ziwa.repository.CowRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CowServiceTest {

    @Mock
    private CowRepository cowRepository;

    @Mock
    private BreedingRecordRepository breedingRecordRepository;

    @InjectMocks
    private CowService cowService;

    private Cow testCow;

    @BeforeEach
    void setUp() {
        testCow = Cow.builder()
                .id(1L)
                .tagId("COW001")
                .breed("Holstein")
                .dateOfBirth(LocalDate.of(2020, 1, 1))
                .acquisitionDate(LocalDate.of(2020, 6, 1))
                .status(CowStatus.ACTIVE)
                .productionRecords(new ArrayList<>())
                .healthRecords(new ArrayList<>())
                .breedingRecords(new ArrayList<>())
                .build();
    }

    @Test
    void registerCow_WithUniqueTagId_ShouldSucceed() {
        // Arrange
        when(cowRepository.existsByTagId(testCow.getTagId())).thenReturn(false);
        when(cowRepository.save(any(Cow.class))).thenReturn(testCow);

        // Act
        Cow result = cowService.registerCow(testCow);

        // Assert
        assertNotNull(result);
        assertEquals("COW001", result.getTagId());
        verify(cowRepository).existsByTagId("COW001");
        verify(cowRepository).save(testCow);
    }

    @Test
    void registerCow_WithDuplicateTagId_ShouldThrowException() {
        // Arrange
        when(cowRepository.existsByTagId(testCow.getTagId())).thenReturn(true);

        // Act & Assert
        assertThrows(DuplicateResourceException.class, () -> cowService.registerCow(testCow));
        verify(cowRepository).existsByTagId("COW001");
        verify(cowRepository, never()).save(any(Cow.class));
    }

    @Test
    void getCowById_WhenExists_ShouldReturnCow() {
        // Arrange
        when(cowRepository.findById(1L)).thenReturn(Optional.of(testCow));

        // Act
        Cow result = cowService.getCowById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("COW001", result.getTagId());
    }

    @Test
    void getCowById_WhenNotExists_ShouldThrowException() {
        // Arrange
        when(cowRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> cowService.getCowById(999L));
    }

    @Test
    void listCows_WithStatusAndBreed_ShouldFilterCorrectly() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Cow> expectedPage = new PageImpl<>(List.of(testCow));
        when(cowRepository.findByStatusAndBreed(CowStatus.ACTIVE, "Holstein", pageable))
                .thenReturn(expectedPage);

        // Act
        Page<Cow> result = cowService.listCows(CowStatus.ACTIVE, "Holstein", pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(cowRepository).findByStatusAndBreed(CowStatus.ACTIVE, "Holstein", pageable);
    }

    @Test
    void listCows_WithStatusOnly_ShouldFilterByStatus() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Cow> expectedPage = new PageImpl<>(List.of(testCow));
        when(cowRepository.findByStatus(CowStatus.ACTIVE, pageable)).thenReturn(expectedPage);

        // Act
        Page<Cow> result = cowService.listCows(CowStatus.ACTIVE, null, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(cowRepository).findByStatus(CowStatus.ACTIVE, pageable);
    }

    @Test
    void updateCow_WithValidData_ShouldSucceed() {
        // Arrange
        Cow updatedCow = Cow.builder()
                .tagId("COW001")
                .breed("Jersey")
                .dateOfBirth(LocalDate.of(2020, 1, 1))
                .acquisitionDate(LocalDate.of(2020, 6, 1))
                .status(CowStatus.ACTIVE)
                .build();

        when(cowRepository.findById(1L)).thenReturn(Optional.of(testCow));
        when(cowRepository.save(any(Cow.class))).thenReturn(testCow);

        // Act
        Cow result = cowService.updateCow(1L, updatedCow);

        // Assert
        assertNotNull(result);
        assertEquals("Jersey", result.getBreed());
        verify(cowRepository).save(testCow);
    }

    @Test
    void updateCow_WithDuplicateTagId_ShouldThrowException() {
        // Arrange
        Cow updatedCow = Cow.builder()
                .tagId("COW002")
                .breed("Jersey")
                .dateOfBirth(LocalDate.of(2020, 1, 1))
                .acquisitionDate(LocalDate.of(2020, 6, 1))
                .status(CowStatus.ACTIVE)
                .build();

        when(cowRepository.findById(1L)).thenReturn(Optional.of(testCow));
        when(cowRepository.existsByTagId("COW002")).thenReturn(true);

        // Act & Assert
        assertThrows(DuplicateResourceException.class, () -> cowService.updateCow(1L, updatedCow));
    }

    @Test
    void deleteCow_WithNoDependencies_ShouldSucceed() {
        // Arrange
        when(cowRepository.findById(1L)).thenReturn(Optional.of(testCow));

        // Act
        cowService.deleteCow(1L);

        // Assert
        verify(cowRepository).delete(testCow);
    }

    @Test
    void deleteCow_WithProductionRecords_ShouldThrowException() {
        // Arrange
        testCow.getProductionRecords().add(new MilkProduction());
        when(cowRepository.findById(1L)).thenReturn(Optional.of(testCow));

        // Act & Assert
        assertThrows(BusinessRuleException.class, () -> cowService.deleteCow(1L));
        verify(cowRepository, never()).delete(any(Cow.class));
    }

    @Test
    void updateCowStatus_ShouldUpdateStatus() {
        // Arrange
        when(cowRepository.findById(1L)).thenReturn(Optional.of(testCow));
        when(cowRepository.save(any(Cow.class))).thenReturn(testCow);

        // Act
        Cow result = cowService.updateCowStatus(1L, CowStatus.SOLD);

        // Assert
        assertNotNull(result);
        assertEquals(CowStatus.SOLD, result.getStatus());
        verify(cowRepository).save(testCow);
    }

    @Test
    void addBreedingRecord_WithValidDate_ShouldSucceed() {
        // Arrange
        BreedingRecord breedingRecord = BreedingRecord.builder()
                .breedingDate(LocalDate.now().minusDays(1))
                .bullId("BULL001")
                .build();

        when(cowRepository.findById(1L)).thenReturn(Optional.of(testCow));
        when(breedingRecordRepository.save(any(BreedingRecord.class))).thenReturn(breedingRecord);

        // Act
        BreedingRecord result = cowService.addBreedingRecord(1L, breedingRecord);

        // Assert
        assertNotNull(result);
        assertEquals(testCow, result.getCow());
        verify(breedingRecordRepository).save(breedingRecord);
    }

    @Test
    void addBreedingRecord_WithFutureDate_ShouldThrowException() {
        // Arrange
        BreedingRecord breedingRecord = BreedingRecord.builder()
                .breedingDate(LocalDate.now().plusDays(1))
                .bullId("BULL001")
                .build();

        when(cowRepository.findById(1L)).thenReturn(Optional.of(testCow));

        // Act & Assert
        assertThrows(BusinessRuleException.class, () -> cowService.addBreedingRecord(1L, breedingRecord));
        verify(breedingRecordRepository, never()).save(any(BreedingRecord.class));
    }

    @Test
    void getBreedingRecords_ShouldReturnRecords() {
        // Arrange
        List<BreedingRecord> records = List.of(
                BreedingRecord.builder().id(1L).breedingDate(LocalDate.now()).build()
        );
        when(cowRepository.findById(1L)).thenReturn(Optional.of(testCow));
        when(breedingRecordRepository.findByCowId(1L)).thenReturn(records);

        // Act
        List<BreedingRecord> result = cowService.getBreedingRecords(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(breedingRecordRepository).findByCowId(1L);
    }
}
