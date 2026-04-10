package com.example.ziwa.controller;

import com.example.ziwa.dto.ErrorResponse;
import com.example.ziwa.dto.HealthRecordRequest;
import com.example.ziwa.dto.HealthRecordResponse;
import com.example.ziwa.dto.PagedResponse;
import com.example.ziwa.dto.WithdrawalResponse;
import com.example.ziwa.model.HealthRecord.HealthRecordType;
import com.example.ziwa.service.HealthService;
import com.example.ziwa.util.PageRequestBuilder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/health")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Health Management", description = "Endpoints for managing cow health records, treatments, and withdrawal periods")
@SecurityRequirement(name = "bearerAuth")
public class HealthController {
    private final HealthService healthService;

    /**
     * POST /api/health - Create health record
     * Requirements: 5.1, 5.2
     */
    @PostMapping
    public ResponseEntity<HealthRecordResponse> createHealthRecord(@Valid @RequestBody HealthRecordRequest request) {
        HealthRecordResponse response = healthService.createHealthRecord(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET /api/health - List health records with filtering
     * Requirements: 5.3, 15.1, 15.2, 15.3, 15.4
     */
    @GetMapping
    public ResponseEntity<PagedResponse<HealthRecordResponse>> listHealthRecords(
            @RequestParam(required = false) Long cowId,
            @RequestParam(required = false) HealthRecordType type,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortDirection) {
        
        // Default sort by date ascending if not specified
        String effectiveSortBy = (sortBy != null) ? sortBy : "date";
        String effectiveSortDirection = (sortDirection != null) ? sortDirection : "ASC";
        
        Pageable pageable = PageRequestBuilder.build(page, size, effectiveSortBy, effectiveSortDirection);
        
        List<HealthRecordResponse> records = healthService.listHealthRecords(cowId, type, startDate, endDate, pageable);
        
        // Note: The service returns a List, not a Page, so we create a simple response
        PagedResponse<HealthRecordResponse> response = PagedResponse.<HealthRecordResponse>builder()
                .content(records)
                .pageNumber(pageable.getPageNumber())
                .pageSize(pageable.getPageSize())
                .totalElements((long) records.size())
                .totalPages(records.isEmpty() ? 0 : 1)
                .first(true)
                .last(true)
                .build();
        
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/health/{id} - Get health record by ID
     * Requirements: 5.1, 5.2
     */
    @GetMapping("/{id}")
    public ResponseEntity<HealthRecordResponse> getHealthRecordById(@PathVariable Long id) {
        HealthRecordResponse response = healthService.getHealthRecordById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * PUT /api/health/{id} - Update health record
     * Requirements: 5.1, 5.2, 5.4
     */
    @PutMapping("/{id}")
    public ResponseEntity<HealthRecordResponse> updateHealthRecord(
            @PathVariable Long id,
            @Valid @RequestBody HealthRecordRequest request) {
        HealthRecordResponse response = healthService.updateHealthRecord(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * DELETE /api/health/{id} - Delete health record
     * Requirements: 5.1
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHealthRecord(@PathVariable Long id) {
        healthService.deleteHealthRecord(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * GET /api/health/withdrawals/active - Get cows in withdrawal period
     * Requirements: 5.5
     */
    @GetMapping("/withdrawals/active")
    public ResponseEntity<List<WithdrawalResponse>> getActiveWithdrawals() {
        List<WithdrawalResponse> withdrawals = healthService.getActiveWithdrawals();
        return ResponseEntity.ok(withdrawals);
    }
}
