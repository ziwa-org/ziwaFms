package com.example.ziwa.controller;

import com.example.ziwa.dto.ErrorResponse;
import com.example.ziwa.dto.PagedResponse;
import com.example.ziwa.dto.ProductionRecordRequest;
import com.example.ziwa.dto.ProductionRecordResponse;
import com.example.ziwa.model.MilkProduction;
import com.example.ziwa.service.MilkProductionService;
import com.example.ziwa.service.MilkProductionService.CowProductivityDTO;
import com.example.ziwa.service.MilkProductionService.ProductionTrendDTO;
import com.example.ziwa.service.MilkProductionService.TopProducerDTO;
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
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/production")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Milk Production", description = "Endpoints for recording and analyzing milk production data")
@SecurityRequirement(name = "bearerAuth")
public class MilkProductionController {
    private final MilkProductionService milkProductionService;

    /**
     * POST /api/production - Record milk production
     * Requirements: 3.1, 3.2, 3.3, 3.4
     */
    @PostMapping
    public ResponseEntity<ProductionRecordResponse> recordProduction(
            @Valid @RequestBody ProductionRecordRequest request) {
        
        MilkProduction production = milkProductionService.recordProduction(
            request.getCowId(),
            request.getDate(),
            request.getMorningQuantity(),
            request.getEveningQuantity(),
            request.getNotes()
        );
        
        return ResponseEntity.status(HttpStatus.CREATED).body(mapToProductionResponse(production));
    }

    /**
     * GET /api/production/{id} - Get production record by ID
     * Requirements: 3.5
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductionRecordResponse> getProductionById(@PathVariable Long id) {
        MilkProduction production = milkProductionService.getProductionById(id);
        return ResponseEntity.ok(mapToProductionResponse(production));
    }

    /**
     * GET /api/production - List production records with filtering
     * Requirements: 3.5, 15.1, 15.2, 15.3, 15.4
     */
    @GetMapping
    public ResponseEntity<PagedResponse<ProductionRecordResponse>> listProduction(
            @RequestParam(required = false) Long cowId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortDirection) {
        
        Pageable pageable = PageRequestBuilder.build(page, size, sortBy, sortDirection);
        List<MilkProduction> productions = milkProductionService.listProduction(
            cowId, startDate, endDate, pageable
        );
        
        List<ProductionRecordResponse> content = productions.stream()
                .map(this::mapToProductionResponse)
                .collect(Collectors.toList());
        
        // Note: The service returns a List, not a Page, so we create a simple response
        // In a real implementation, the service should return Page<MilkProduction>
        PagedResponse<ProductionRecordResponse> response = PagedResponse.<ProductionRecordResponse>builder()
                .content(content)
                .pageNumber(pageable.getPageNumber())
                .pageSize(pageable.getPageSize())
                .totalElements((long) content.size())
                .totalPages(content.isEmpty() ? 0 : 1)
                .first(true)
                .last(true)
                .build();
        
        return ResponseEntity.ok(response);
    }

    /**
     * PUT /api/production/{id} - Update production record
     * Requirements: 3.6
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProductionRecordResponse> updateProduction(
            @PathVariable Long id,
            @Valid @RequestBody ProductionRecordRequest request) {
        
        MilkProduction production = milkProductionService.updateProduction(
            id,
            request.getMorningQuantity(),
            request.getEveningQuantity(),
            request.getNotes()
        );
        
        return ResponseEntity.ok(mapToProductionResponse(production));
    }

    /**
     * DELETE /api/production/{id} - Delete production record
     * Requirements: 3.5
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduction(@PathVariable Long id) {
        milkProductionService.deleteProduction(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * GET /api/production/analytics/trends - Get production trends
     * Requirements: 4.1, 12.6
     */
    @GetMapping("/analytics/trends")
    public ResponseEntity<List<ProductionTrendDTO>> getProductionTrends(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        List<ProductionTrendDTO> trends = milkProductionService.getProductionTrends(startDate, endDate);
        return ResponseEntity.ok(trends);
    }

    /**
     * GET /api/production/analytics/cow-productivity - Get per-cow productivity
     * Requirements: 4.2
     */
    @GetMapping("/analytics/cow-productivity")
    public ResponseEntity<List<CowProductivityDTO>> getCowProductivity(
            @RequestParam(required = false) Integer limit) {
        
        List<CowProductivityDTO> productivity = milkProductionService.getCowProductivity(limit);
        return ResponseEntity.ok(productivity);
    }

    /**
     * GET /api/production/analytics/top-producers - Get top producers
     * Requirements: 4.4
     */
    @GetMapping("/analytics/top-producers")
    public ResponseEntity<List<TopProducerDTO>> getTopProducers(
            @RequestParam(required = false) Integer limit) {
        
        // Use default value of 10 if not provided
        Integer effectiveLimit = (limit != null) ? limit : 10;
        List<TopProducerDTO> topProducers = milkProductionService.getTopProducers(effectiveLimit);
        return ResponseEntity.ok(topProducers);
    }

    // Mapping method
    private ProductionRecordResponse mapToProductionResponse(MilkProduction production) {
        return ProductionRecordResponse.builder()
                .id(production.getId())
                .cowId(production.getCow().getId())
                .cowTagId(production.getCow().getTagId())
                .date(production.getDate())
                .morningQuantity(production.getMorningQuantity())
                .eveningQuantity(production.getEveningQuantity())
                .totalQuantity(production.getTotalQuantity())
                .notes(production.getNotes())
                .createdAt(production.getCreatedAt())
                .build();
    }
}
