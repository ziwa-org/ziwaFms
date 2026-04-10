package com.example.ziwa.controller;

import com.example.ziwa.dto.*;
import com.example.ziwa.model.BreedingRecord;
import com.example.ziwa.model.Cow;
import com.example.ziwa.model.Cow.CowStatus;
import com.example.ziwa.service.CowService;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cows")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Livestock Management", description = "Endpoints for managing cow registration, lifecycle, and breeding records")
@SecurityRequirement(name = "bearerAuth")
public class CowController {
    private final CowService cowService;

    /**
     * POST /api/cows - Register new cow
     * Requirements: 1.1, 1.2
     */
    @PostMapping
    @Operation(summary = "Register a new cow", description = "Creates a new cow record with tag ID, breed, dates, and status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cow registered successfully",
                    content = @Content(schema = @Schema(implementation = CowResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input or duplicate tag ID",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<CowResponse> registerCow(@Valid @RequestBody CowRegistrationRequest request) {
        Cow cow = mapToCowEntity(request);
        Cow savedCow = cowService.registerCow(cow);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapToCowResponse(savedCow));
    }

    /**
     * GET /api/cows/{id} - Get cow by ID
     * Requirements: 1.4
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get cow by ID", description = "Retrieves a cow record by its unique identifier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cow found",
                    content = @Content(schema = @Schema(implementation = CowResponse.class))),
            @ApiResponse(responseCode = "404", description = "Cow not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<CowResponse> getCowById(
            @Parameter(description = "Cow ID", required = true) @PathVariable Long id) {
        Cow cow = cowService.getCowById(id);
        return ResponseEntity.ok(mapToCowResponse(cow));
    }

    /**
     * GET /api/cows - List cows with filtering and pagination
     * Requirements: 1.5, 15.1, 15.2, 15.4
     */
    @GetMapping
    @Operation(summary = "List cows", description = "Retrieves a paginated list of cows with optional filtering by status and breed")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cows retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<PagedResponse<CowResponse>> listCows(
            @Parameter(description = "Filter by cow status") @RequestParam(required = false) CowStatus status,
            @Parameter(description = "Filter by breed") @RequestParam(required = false) String breed,
            @Parameter(description = "Page number (0-indexed)") @RequestParam(required = false) Integer page,
            @Parameter(description = "Page size (default: 20)") @RequestParam(required = false) Integer size,
            @Parameter(description = "Sort field") @RequestParam(required = false) String sortBy,
            @Parameter(description = "Sort direction (asc/desc)") @RequestParam(required = false) String sortDirection) {
        
        Pageable pageable = PageRequestBuilder.build(page, size, sortBy, sortDirection);
        Page<Cow> cows = cowService.listCows(status, breed, pageable);
        Page<CowResponse> cowResponses = cows.map(this::mapToCowResponse);
        return ResponseEntity.ok(PagedResponse.of(cowResponses));
    }

    /**
     * PUT /api/cows/{id} - Update cow
     * Requirements: 1.7
     */
    @PutMapping("/{id}")
    public ResponseEntity<CowResponse> updateCow(
            @PathVariable Long id,
            @Valid @RequestBody CowUpdateRequest request) {
        
        Cow updatedCow = mapToCowEntityFromUpdate(request);
        Cow savedCow = cowService.updateCow(id, updatedCow);
        return ResponseEntity.ok(mapToCowResponse(savedCow));
    }

    /**
     * DELETE /api/cows/{id} - Delete cow
     * Requirements: 1.6
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCow(@PathVariable Long id) {
        cowService.deleteCow(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * PATCH /api/cows/{id}/status - Update cow status
     * Requirements: 1.3
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<CowResponse> updateCowStatus(
            @PathVariable Long id,
            @RequestParam CowStatus status) {
        
        Cow updatedCow = cowService.updateCowStatus(id, status);
        return ResponseEntity.ok(mapToCowResponse(updatedCow));
    }

    /**
     * POST /api/cows/{id}/breeding - Add breeding record
     * Requirements: 2.1, 2.2
     */
    @PostMapping("/{id}/breeding")
    public ResponseEntity<BreedingRecordResponse> addBreedingRecord(
            @PathVariable Long id,
            @Valid @RequestBody BreedingRecordRequest request) {
        
        BreedingRecord breedingRecord = mapToBreedingRecordEntity(request);
        BreedingRecord savedRecord = cowService.addBreedingRecord(id, breedingRecord);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapToBreedingRecordResponse(savedRecord));
    }

    /**
     * GET /api/cows/{id}/breeding - Get breeding records
     * Requirements: 2.3
     */
    @GetMapping("/{id}/breeding")
    public ResponseEntity<List<BreedingRecordResponse>> getBreedingRecords(@PathVariable Long id) {
        List<BreedingRecord> records = cowService.getBreedingRecords(id);
        List<BreedingRecordResponse> response = records.stream()
                .map(this::mapToBreedingRecordResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    // Mapping methods
    private Cow mapToCowEntity(CowRegistrationRequest request) {
        return Cow.builder()
                .tagId(request.getTagId())
                .breed(request.getBreed())
                .dateOfBirth(request.getDateOfBirth())
                .acquisitionDate(request.getAcquisitionDate())
                .status(request.getStatus() != null ? request.getStatus() : CowStatus.ACTIVE)
                .build();
    }

    private Cow mapToCowEntityFromUpdate(CowUpdateRequest request) {
        return Cow.builder()
                .tagId(request.getTagId())
                .breed(request.getBreed())
                .dateOfBirth(request.getDateOfBirth())
                .acquisitionDate(request.getAcquisitionDate())
                .status(request.getStatus())
                .build();
    }

    private CowResponse mapToCowResponse(Cow cow) {
        return CowResponse.builder()
                .id(cow.getId())
                .tagId(cow.getTagId())
                .breed(cow.getBreed())
                .dateOfBirth(cow.getDateOfBirth())
                .acquisitionDate(cow.getAcquisitionDate())
                .status(cow.getStatus())
                .createdAt(cow.getCreatedAt())
                .updatedAt(cow.getUpdatedAt())
                .build();
    }

    private BreedingRecord mapToBreedingRecordEntity(BreedingRecordRequest request) {
        return BreedingRecord.builder()
                .breedingDate(request.getBreedingDate())
                .bullId(request.getBullId())
                .expectedCalvingDate(request.getExpectedCalvingDate())
                .notes(request.getNotes())
                .build();
    }

    private BreedingRecordResponse mapToBreedingRecordResponse(BreedingRecord record) {
        return BreedingRecordResponse.builder()
                .id(record.getId())
                .cowId(record.getCow().getId())
                .cowTagId(record.getCow().getTagId())
                .breedingDate(record.getBreedingDate())
                .bullId(record.getBullId())
                .expectedCalvingDate(record.getExpectedCalvingDate())
                .actualCalvingDate(record.getActualCalvingDate())
                .notes(record.getNotes())
                .createdAt(record.getCreatedAt())
                .updatedAt(record.getUpdatedAt())
                .build();
    }
}
