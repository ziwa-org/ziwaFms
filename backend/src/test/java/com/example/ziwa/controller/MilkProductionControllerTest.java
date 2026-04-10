package com.example.ziwa.controller;

import com.example.ziwa.dto.ProductionRecordRequest;
import com.example.ziwa.dto.ProductionRecordResponse;
import com.example.ziwa.model.Cow;
import com.example.ziwa.model.MilkProduction;
import com.example.ziwa.service.MilkProductionService;
import com.example.ziwa.service.MilkProductionService.CowProductivityDTO;
import com.example.ziwa.service.MilkProductionService.ProductionTrendDTO;
import com.example.ziwa.service.MilkProductionService.TopProducerDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MilkProductionControllerTest {

    @Mock
    private MilkProductionService milkProductionService;

    @InjectMocks
    private MilkProductionController milkProductionController;

    private Cow testCow;
    private MilkProduction testProduction;
    private ProductionRecordRequest testRequest;

    @BeforeEach
    void setUp() {
        testCow = Cow.builder()
                .id(1L)
                .tagId("COW001")
                .breed("Holstein")
                .dateOfBirth(LocalDate.of(2020, 1, 1))
                .acquisitionDate(LocalDate.of(2020, 3, 1))
                .status(Cow.CowStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        testProduction = MilkProduction.builder()
                .id(1L)
                .cow(testCow)
                .date(LocalDate.now())
                .morningQuantity(15.5)
                .eveningQuantity(14.3)
                .totalQuantity(29.8)
                .notes("Normal production")
                .createdAt(LocalDateTime.now())
                .build();

        testRequest = ProductionRecordRequest.builder()
                .cowId(1L)
                .date(LocalDate.now())
                .morningQuantity(15.5)
                .eveningQuantity(14.3)
                .notes("Normal production")
                .build();
    }

    @Test
    void recordProduction_ShouldReturnCreatedStatus() {
        // Arrange
        when(milkProductionService.recordProduction(
                anyLong(), any(LocalDate.class), anyDouble(), anyDouble(), anyString()))
                .thenReturn(testProduction);

        // Act
        ResponseEntity<ProductionRecordResponse> response = 
                milkProductionController.recordProduction(testRequest);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(testProduction.getId(), response.getBody().getId());
        assertEquals(testProduction.getCow().getId(), response.getBody().getCowId());
        assertEquals(testProduction.getCow().getTagId(), response.getBody().getCowTagId());
        assertEquals(testProduction.getTotalQuantity(), response.getBody().getTotalQuantity());
        
        verify(milkProductionService).recordProduction(
                testRequest.getCowId(),
                testRequest.getDate(),
                testRequest.getMorningQuantity(),
                testRequest.getEveningQuantity(),
                testRequest.getNotes()
        );
    }

    @Test
    void getProductionById_ShouldReturnProduction() {
        // Arrange
        when(milkProductionService.getProductionById(1L)).thenReturn(testProduction);

        // Act
        ResponseEntity<ProductionRecordResponse> response = 
                milkProductionController.getProductionById(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(testProduction.getId(), response.getBody().getId());
        assertEquals(testProduction.getTotalQuantity(), response.getBody().getTotalQuantity());
        
        verify(milkProductionService).getProductionById(1L);
    }

    @Test
    void listProduction_ShouldReturnFilteredList() {
        // Arrange
        List<MilkProduction> productions = Arrays.asList(testProduction);
        when(milkProductionService.listProduction(
                anyLong(), any(LocalDate.class), any(LocalDate.class), any(Pageable.class)))
                .thenReturn(productions);

        // Act
        ResponseEntity<com.example.ziwa.dto.PagedResponse<ProductionRecordResponse>> response = 
                milkProductionController.listProduction(
                        1L, LocalDate.now().minusDays(7), LocalDate.now(), 0, 20, null, null);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getContent().size());
        assertEquals(testProduction.getId(), response.getBody().getContent().get(0).getId());
        
        verify(milkProductionService).listProduction(
                eq(1L), any(LocalDate.class), any(LocalDate.class), any(Pageable.class));
    }

    @Test
    void updateProduction_ShouldReturnUpdatedProduction() {
        // Arrange
        MilkProduction updatedProduction = MilkProduction.builder()
                .id(1L)
                .cow(testCow)
                .date(LocalDate.now())
                .morningQuantity(16.0)
                .eveningQuantity(15.0)
                .totalQuantity(31.0)
                .notes("Updated production")
                .createdAt(LocalDateTime.now())
                .build();
        
        when(milkProductionService.updateProduction(
                anyLong(), anyDouble(), anyDouble(), anyString()))
                .thenReturn(updatedProduction);

        // Act
        ResponseEntity<ProductionRecordResponse> response = 
                milkProductionController.updateProduction(1L, testRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(31.0, response.getBody().getTotalQuantity());
        
        verify(milkProductionService).updateProduction(
                eq(1L),
                eq(testRequest.getMorningQuantity()),
                eq(testRequest.getEveningQuantity()),
                eq(testRequest.getNotes())
        );
    }

    @Test
    void deleteProduction_ShouldReturnNoContent() {
        // Arrange
        doNothing().when(milkProductionService).deleteProduction(1L);

        // Act
        ResponseEntity<Void> response = milkProductionController.deleteProduction(1L);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(milkProductionService).deleteProduction(1L);
    }

    @Test
    void getProductionTrends_ShouldReturnTrends() {
        // Arrange
        LocalDate startDate = LocalDate.now().minusDays(7);
        LocalDate endDate = LocalDate.now();
        List<ProductionTrendDTO> trends = Arrays.asList(
                new ProductionTrendDTO(LocalDate.now(), 100.0, 25.0, 4)
        );
        when(milkProductionService.getProductionTrends(startDate, endDate))
                .thenReturn(trends);

        // Act
        ResponseEntity<List<ProductionTrendDTO>> response = 
                milkProductionController.getProductionTrends(startDate, endDate);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(100.0, response.getBody().get(0).getTotalProduction());
        
        verify(milkProductionService).getProductionTrends(startDate, endDate);
    }

    @Test
    void getCowProductivity_ShouldReturnProductivityList() {
        // Arrange
        List<CowProductivityDTO> productivity = Arrays.asList(
                new CowProductivityDTO(1L, "COW001", 25.5)
        );
        when(milkProductionService.getCowProductivity(10)).thenReturn(productivity);

        // Act
        ResponseEntity<List<CowProductivityDTO>> response = 
                milkProductionController.getCowProductivity(10);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(25.5, response.getBody().get(0).getAverageProduction());
        
        verify(milkProductionService).getCowProductivity(10);
    }

    @Test
    void getTopProducers_ShouldReturnTopProducersList() {
        // Arrange
        List<TopProducerDTO> topProducers = Arrays.asList(
                new TopProducerDTO(1, 1L, "COW001", 500.0, 20)
        );
        when(milkProductionService.getTopProducers(10)).thenReturn(topProducers);

        // Act
        ResponseEntity<List<TopProducerDTO>> response = 
                milkProductionController.getTopProducers(10);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(1, response.getBody().get(0).getRank());
        assertEquals(500.0, response.getBody().get(0).getTotalProduction());
        
        verify(milkProductionService).getTopProducers(10);
    }

    @Test
    void getTopProducers_WithDefaultLimit_ShouldUseDefaultValue() {
        // Arrange
        List<TopProducerDTO> topProducers = Arrays.asList(
                new TopProducerDTO(1, 1L, "COW001", 500.0, 20)
        );
        // The controller passes the default value (10) to the service
        when(milkProductionService.getTopProducers(anyInt())).thenReturn(topProducers);

        // Act
        ResponseEntity<List<TopProducerDTO>> response = 
                milkProductionController.getTopProducers(null);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        
        verify(milkProductionService).getTopProducers(10);
    }

    @Test
    void listProduction_WithNoFilters_ShouldReturnAllProductions() {
        // Arrange
        List<MilkProduction> productions = Arrays.asList(testProduction);
        when(milkProductionService.listProduction(
                isNull(), isNull(), isNull(), any(Pageable.class)))
                .thenReturn(productions);

        // Act
        ResponseEntity<com.example.ziwa.dto.PagedResponse<ProductionRecordResponse>> response = 
                milkProductionController.listProduction(null, null, null, 0, 20, null, null);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getContent().size());
        
        verify(milkProductionService).listProduction(
                isNull(), isNull(), isNull(), any(Pageable.class));
    }
}
