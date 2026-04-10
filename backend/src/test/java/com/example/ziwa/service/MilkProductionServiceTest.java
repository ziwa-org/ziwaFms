package com.example.ziwa.service;

import com.example.ziwa.exception.BusinessRuleException;
import com.example.ziwa.exception.DuplicateResourceException;
import com.example.ziwa.exception.ResourceNotFoundException;
import com.example.ziwa.model.Cow;
import com.example.ziwa.model.MilkProduction;
import com.example.ziwa.repository.CowRepository;
import com.example.ziwa.repository.MilkProductionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MilkProductionServiceTest {

    @Mock
    private MilkProductionRepository milkProductionRepository;

    @Mock
    private CowRepository cowRepository;

    @InjectMocks
    private MilkProductionService milkProductionService;

    private Cow testCow;
    private MilkProduction testProduction;

    @BeforeEach
    void setUp() {
        testCow = Cow.builder()
                .id(1L)
                .tagId("COW001")
                .breed("Holstein")
                .dateOfBirth(LocalDate.of(2020, 1, 1))
                .acquisitionDate(LocalDate.of(2020, 3, 1))
                .status(Cow.CowStatus.ACTIVE)
                .build();

        testProduction = MilkProduction.builder()
                .id(1L)
                .cow(testCow)
                .date(LocalDate.of(2024, 1, 15))
                .morningQuantity(10.5)
                .eveningQuantity(9.5)
                .totalQuantity(20.0)
                .notes("Normal production")
                .build();
    }

    @Test
    void recordProduction_Success() {
        // Arrange
        Long cowId = 1L;
        LocalDate date = LocalDate.of(2024, 1, 15);
        Double morningQuantity = 10.5;
        Double eveningQuantity = 9.5;
        String notes = "Normal production";

        when(cowRepository.findById(cowId)).thenReturn(Optional.of(testCow));
        when(milkProductionRepository.findByCowIdAndDate(cowId, date)).thenReturn(Optional.empty());
        when(milkProductionRepository.save(any(MilkProduction.class))).thenReturn(testProduction);

        // Act
        MilkProduction result = milkProductionService.recordProduction(cowId, date, morningQuantity, eveningQuantity, notes);

        // Assert
        assertNotNull(result);
        assertEquals(20.0, result.getTotalQuantity());
        verify(cowRepository).findById(cowId);
        verify(milkProductionRepository).findByCowIdAndDate(cowId, date);
        verify(milkProductionRepository).save(any(MilkProduction.class));
    }

    @Test
    void recordProduction_CowNotFound_ThrowsException() {
        // Arrange
        Long cowId = 999L;
        LocalDate date = LocalDate.of(2024, 1, 15);

        when(cowRepository.findById(cowId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> 
            milkProductionService.recordProduction(cowId, date, 10.0, 10.0, null)
        );
        verify(cowRepository).findById(cowId);
        verify(milkProductionRepository, never()).save(any());
    }

    @Test
    void recordProduction_NegativeMorningQuantity_ThrowsException() {
        // Arrange
        Long cowId = 1L;
        LocalDate date = LocalDate.of(2024, 1, 15);

        when(cowRepository.findById(cowId)).thenReturn(Optional.of(testCow));

        // Act & Assert
        assertThrows(BusinessRuleException.class, () -> 
            milkProductionService.recordProduction(cowId, date, -5.0, 10.0, null)
        );
        verify(milkProductionRepository, never()).save(any());
    }

    @Test
    void recordProduction_NegativeEveningQuantity_ThrowsException() {
        // Arrange
        Long cowId = 1L;
        LocalDate date = LocalDate.of(2024, 1, 15);

        when(cowRepository.findById(cowId)).thenReturn(Optional.of(testCow));

        // Act & Assert
        assertThrows(BusinessRuleException.class, () -> 
            milkProductionService.recordProduction(cowId, date, 10.0, -5.0, null)
        );
        verify(milkProductionRepository, never()).save(any());
    }

    @Test
    void recordProduction_DuplicateCowAndDate_ThrowsException() {
        // Arrange
        Long cowId = 1L;
        LocalDate date = LocalDate.of(2024, 1, 15);

        when(cowRepository.findById(cowId)).thenReturn(Optional.of(testCow));
        when(milkProductionRepository.findByCowIdAndDate(cowId, date)).thenReturn(Optional.of(testProduction));

        // Act & Assert
        assertThrows(DuplicateResourceException.class, () -> 
            milkProductionService.recordProduction(cowId, date, 10.0, 10.0, null)
        );
        verify(milkProductionRepository, never()).save(any());
    }

    @Test
    void recordProduction_ZeroQuantities_Success() {
        // Arrange
        Long cowId = 1L;
        LocalDate date = LocalDate.of(2024, 1, 15);
        MilkProduction zeroProduction = MilkProduction.builder()
                .id(2L)
                .cow(testCow)
                .date(date)
                .morningQuantity(0.0)
                .eveningQuantity(0.0)
                .totalQuantity(0.0)
                .build();

        when(cowRepository.findById(cowId)).thenReturn(Optional.of(testCow));
        when(milkProductionRepository.findByCowIdAndDate(cowId, date)).thenReturn(Optional.empty());
        when(milkProductionRepository.save(any(MilkProduction.class))).thenReturn(zeroProduction);

        // Act
        MilkProduction result = milkProductionService.recordProduction(cowId, date, 0.0, 0.0, null);

        // Assert
        assertNotNull(result);
        assertEquals(0.0, result.getTotalQuantity());
    }

    @Test
    void getProductionById_Success() {
        // Arrange
        Long id = 1L;
        when(milkProductionRepository.findById(id)).thenReturn(Optional.of(testProduction));

        // Act
        MilkProduction result = milkProductionService.getProductionById(id);

        // Assert
        assertNotNull(result);
        assertEquals(id, result.getId());
        verify(milkProductionRepository).findById(id);
    }

    @Test
    void getProductionById_NotFound_ThrowsException() {
        // Arrange
        Long id = 999L;
        when(milkProductionRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> 
            milkProductionService.getProductionById(id)
        );
    }

    @Test
    void listProduction_WithCowAndDateRange() {
        // Arrange
        Long cowId = 1L;
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);
        Pageable pageable = PageRequest.of(0, 10);
        List<MilkProduction> expectedList = Arrays.asList(testProduction);

        when(milkProductionRepository.findByCowIdAndDateBetween(cowId, startDate, endDate, pageable))
                .thenReturn(expectedList);

        // Act
        List<MilkProduction> result = milkProductionService.listProduction(cowId, startDate, endDate, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(milkProductionRepository).findByCowIdAndDateBetween(cowId, startDate, endDate, pageable);
    }

    @Test
    void updateProduction_Success() {
        // Arrange
        Long id = 1L;
        Double newMorning = 12.0;
        Double newEvening = 11.0;
        String newNotes = "Updated notes";

        when(milkProductionRepository.findById(id)).thenReturn(Optional.of(testProduction));
        when(milkProductionRepository.save(any(MilkProduction.class))).thenReturn(testProduction);

        // Act
        MilkProduction result = milkProductionService.updateProduction(id, newMorning, newEvening, newNotes);

        // Assert
        assertNotNull(result);
        assertEquals(23.0, result.getTotalQuantity()); // 12.0 + 11.0
        verify(milkProductionRepository).save(testProduction);
    }

    @Test
    void updateProduction_NegativeQuantity_ThrowsException() {
        // Arrange
        Long id = 1L;
        when(milkProductionRepository.findById(id)).thenReturn(Optional.of(testProduction));

        // Act & Assert
        assertThrows(BusinessRuleException.class, () -> 
            milkProductionService.updateProduction(id, -5.0, 10.0, null)
        );
        verify(milkProductionRepository, never()).save(any());
    }

    @Test
    void deleteProduction_Success() {
        // Arrange
        Long id = 1L;
        when(milkProductionRepository.findById(id)).thenReturn(Optional.of(testProduction));
        doNothing().when(milkProductionRepository).delete(testProduction);

        // Act
        milkProductionService.deleteProduction(id);

        // Assert
        verify(milkProductionRepository).findById(id);
        verify(milkProductionRepository).delete(testProduction);
    }

    @Test
    void deleteProduction_NotFound_ThrowsException() {
        // Arrange
        Long id = 999L;
        when(milkProductionRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> 
            milkProductionService.deleteProduction(id)
        );
        verify(milkProductionRepository, never()).delete(any());
    }

    @Test
    void getProductionTrends_Success() {
        // Arrange
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 1, 31);
        Object[] row1 = {LocalDate.of(2024, 1, 15), 100.0, 20.0, 5L};
        Object[] row2 = {LocalDate.of(2024, 1, 16), 110.0, 22.0, 5L};
        List<Object[]> mockResults = Arrays.asList(row1, row2);

        when(milkProductionRepository.getProductionTrends(startDate, endDate)).thenReturn(mockResults);

        // Act
        List<MilkProductionService.ProductionTrendDTO> result = 
            milkProductionService.getProductionTrends(startDate, endDate);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(100.0, result.get(0).getTotalProduction());
        assertEquals(110.0, result.get(1).getTotalProduction());
    }

    @Test
    void getCowProductivity_Success() {
        // Arrange
        Integer limit = 10;
        Object[] row1 = {1L, "COW001", 20.5};
        Object[] row2 = {2L, "COW002", 18.3};
        List<Object[]> mockResults = Arrays.asList(row1, row2);

        when(milkProductionRepository.getCowProductivity(any(Pageable.class))).thenReturn(mockResults);

        // Act
        List<MilkProductionService.CowProductivityDTO> result = 
            milkProductionService.getCowProductivity(limit);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("COW001", result.get(0).getCowTagId());
        assertEquals(20.5, result.get(0).getAverageProduction());
    }

    @Test
    void getTopProducers_Success() {
        // Arrange
        Integer limit = 5;
        Object[] row1 = {1L, "COW001", 500.0, 25L};
        Object[] row2 = {2L, "COW002", 450.0, 23L};
        List<Object[]> mockResults = Arrays.asList(row1, row2);

        when(milkProductionRepository.getTopProducers(any(Pageable.class))).thenReturn(mockResults);

        // Act
        List<MilkProductionService.TopProducerDTO> result = 
            milkProductionService.getTopProducers(limit);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1, result.get(0).getRank());
        assertEquals("COW001", result.get(0).getCowTagId());
        assertEquals(500.0, result.get(0).getTotalProduction());
        assertEquals(2, result.get(1).getRank());
    }

    @Test
    void getTopProducers_DefaultLimit() {
        // Arrange
        when(milkProductionRepository.getTopProducers(any(Pageable.class))).thenReturn(Arrays.asList());

        // Act
        List<MilkProductionService.TopProducerDTO> result = 
            milkProductionService.getTopProducers(null);

        // Assert
        assertNotNull(result);
        verify(milkProductionRepository).getTopProducers(PageRequest.of(0, 10));
    }
}
