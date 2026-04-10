package com.example.ziwa.controller;

import com.example.ziwa.dto.HealthRecordRequest;
import com.example.ziwa.dto.HealthRecordResponse;
import com.example.ziwa.dto.WithdrawalResponse;
import com.example.ziwa.model.HealthRecord.HealthRecordType;
import com.example.ziwa.service.HealthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HealthController.class)
@WithMockUser
class HealthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private HealthService healthService;

    private HealthRecordRequest testRequest;
    private HealthRecordResponse testResponse;

    @BeforeEach
    void setUp() {
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

        testResponse = HealthRecordResponse.builder()
            .id(1L)
            .cowId(1L)
            .cowTagId("COW001")
            .date(LocalDate.now())
            .recordType(HealthRecordType.VACCINATION)
            .description("Annual vaccination")
            .veterinarianName("Dr. Smith")
            .medication("Vaccine A")
            .withdrawalPeriodDays(7)
            .withdrawalEndDate(LocalDate.now().plusDays(7))
            .cost(50.0)
            .createdAt(LocalDateTime.now())
            .build();
    }

    @Test
    void testCreateHealthRecord() throws Exception {
        when(healthService.createHealthRecord(any(HealthRecordRequest.class))).thenReturn(testResponse);

        mockMvc.perform(post("/api/health")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.cowId").value(1))
                .andExpect(jsonPath("$.cowTagId").value("COW001"))
                .andExpect(jsonPath("$.recordType").value("VACCINATION"))
                .andExpect(jsonPath("$.description").value("Annual vaccination"))
                .andExpect(jsonPath("$.withdrawalPeriodDays").value(7));

        verify(healthService).createHealthRecord(any(HealthRecordRequest.class));
    }

    @Test
    void testGetHealthRecordById() throws Exception {
        when(healthService.getHealthRecordById(1L)).thenReturn(testResponse);

        mockMvc.perform(get("/api/health/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.cowTagId").value("COW001"));

        verify(healthService).getHealthRecordById(1L);
    }

    @Test
    void testListHealthRecords() throws Exception {
        List<HealthRecordResponse> records = Arrays.asList(testResponse);
        when(healthService.listHealthRecords(any(), any(), any(), any(), any(Pageable.class)))
            .thenReturn(records);

        mockMvc.perform(get("/api/health")
                .param("cowId", "1")
                .param("page", "0")
                .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].cowId").value(1));

        verify(healthService).listHealthRecords(any(), any(), any(), any(), any(Pageable.class));
    }

    @Test
    void testUpdateHealthRecord() throws Exception {
        when(healthService.updateHealthRecord(eq(1L), any(HealthRecordRequest.class)))
            .thenReturn(testResponse);

        mockMvc.perform(put("/api/health/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(healthService).updateHealthRecord(eq(1L), any(HealthRecordRequest.class));
    }

    @Test
    void testDeleteHealthRecord() throws Exception {
        doNothing().when(healthService).deleteHealthRecord(1L);

        mockMvc.perform(delete("/api/health/1")
                .with(csrf()))
                .andExpect(status().isNoContent());

        verify(healthService).deleteHealthRecord(1L);
    }

    @Test
    void testGetActiveWithdrawals() throws Exception {
        WithdrawalResponse withdrawal = WithdrawalResponse.builder()
            .cowId(1L)
            .cowTagId("COW001")
            .healthRecordId(1L)
            .withdrawalEndDate(LocalDate.now().plusDays(4))
            .daysRemaining(4)
            .medication("Antibiotic")
            .build();

        when(healthService.getActiveWithdrawals()).thenReturn(Arrays.asList(withdrawal));

        mockMvc.perform(get("/api/health/withdrawals/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].cowId").value(1))
                .andExpect(jsonPath("$[0].cowTagId").value("COW001"))
                .andExpect(jsonPath("$[0].daysRemaining").value(4))
                .andExpect(jsonPath("$[0].medication").value("Antibiotic"));

        verify(healthService).getActiveWithdrawals();
    }

    @Test
    void testCreateHealthRecord_ValidationError() throws Exception {
        HealthRecordRequest invalidRequest = HealthRecordRequest.builder()
            .cowId(null) // Missing required field
            .date(LocalDate.now())
            .recordType(HealthRecordType.VACCINATION)
            .description("Test")
            .build();

        mockMvc.perform(post("/api/health")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(healthService, never()).createHealthRecord(any(HealthRecordRequest.class));
    }
}
