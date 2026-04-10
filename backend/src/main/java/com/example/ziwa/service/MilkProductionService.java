package com.example.ziwa.service;

import com.example.ziwa.exception.BusinessRuleException;
import com.example.ziwa.exception.DuplicateResourceException;
import com.example.ziwa.exception.ResourceNotFoundException;
import com.example.ziwa.model.Cow;
import com.example.ziwa.model.MilkProduction;
import com.example.ziwa.repository.CowRepository;
import com.example.ziwa.repository.MilkProductionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MilkProductionService {
    private final MilkProductionRepository milkProductionRepository;
    private final CowRepository cowRepository;

    /**
     * Record milk production with total calculation and uniqueness validation
     * Requirements: 3.1, 3.2, 3.3, 3.4
     */
    public MilkProduction recordProduction(Long cowId, LocalDate date, Double morningQuantity, 
                                          Double eveningQuantity, String notes) {
        // Validate cow exists
        Cow cow = cowRepository.findById(cowId)
                .orElseThrow(() -> new ResourceNotFoundException("Cow not found with id: " + cowId));
        
        // Validate quantities are not negative
        if (morningQuantity < 0) {
            throw new BusinessRuleException("Morning quantity cannot be negative");
        }
        if (eveningQuantity < 0) {
            throw new BusinessRuleException("Evening quantity cannot be negative");
        }
        
        // Validate uniqueness (cow + date)
        if (milkProductionRepository.findByCowIdAndDate(cowId, date).isPresent()) {
            throw new DuplicateResourceException(
                "Production record already exists for cow '" + cow.getTagId() + "' on date " + date
            );
        }
        
        // Calculate total
        Double totalQuantity = morningQuantity + eveningQuantity;
        
        // Create production record
        MilkProduction production = MilkProduction.builder()
                .cow(cow)
                .date(date)
                .morningQuantity(morningQuantity)
                .eveningQuantity(eveningQuantity)
                .totalQuantity(totalQuantity)
                .notes(notes)
                .build();
        
        return milkProductionRepository.save(production);
    }

    /**
     * Get production record by ID with not found handling
     * Requirements: 3.5
     */
    @Transactional(readOnly = true)
    public MilkProduction getProductionById(Long id) {
        return milkProductionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Production record not found with id: " + id));
    }

    /**
     * List production records with filtering by cow and date range
     * Requirements: 3.5
     */
    @Transactional(readOnly = true)
    public List<MilkProduction> listProduction(Long cowId, LocalDate startDate, LocalDate endDate, Pageable pageable) {
        if (cowId != null && startDate != null && endDate != null) {
            // Filter by cow and date range
            return milkProductionRepository.findByCowIdAndDateBetween(cowId, startDate, endDate, pageable);
        } else if (cowId != null) {
            // Filter by cow only - use a wide date range
            LocalDate minDate = LocalDate.of(1900, 1, 1);
            LocalDate maxDate = LocalDate.of(2100, 12, 31);
            return milkProductionRepository.findByCowIdAndDateBetween(cowId, minDate, maxDate, pageable);
        } else if (startDate != null && endDate != null) {
            // Filter by date range only
            return milkProductionRepository.findByDateBetween(startDate, endDate);
        } else {
            // No filters - return all
            return milkProductionRepository.findAll(pageable).getContent();
        }
    }

    /**
     * Update production record with total recalculation
     * Requirements: 3.6
     */
    public MilkProduction updateProduction(Long id, Double morningQuantity, Double eveningQuantity, String notes) {
        MilkProduction existingProduction = getProductionById(id);
        
        // Validate quantities are not negative
        if (morningQuantity < 0) {
            throw new BusinessRuleException("Morning quantity cannot be negative");
        }
        if (eveningQuantity < 0) {
            throw new BusinessRuleException("Evening quantity cannot be negative");
        }
        
        // Update fields and recalculate total
        existingProduction.setMorningQuantity(morningQuantity);
        existingProduction.setEveningQuantity(eveningQuantity);
        existingProduction.setTotalQuantity(morningQuantity + eveningQuantity);
        existingProduction.setNotes(notes);
        
        return milkProductionRepository.save(existingProduction);
    }

    /**
     * Delete production record
     * Requirements: 3.5
     */
    public void deleteProduction(Long id) {
        MilkProduction production = getProductionById(id);
        milkProductionRepository.delete(production);
    }

    /**
     * Get production trends with daily aggregation
     * Requirements: 4.1, 12.6
     */
    @Transactional(readOnly = true)
    public List<ProductionTrendDTO> getProductionTrends(LocalDate startDate, LocalDate endDate) {
        List<Object[]> results = milkProductionRepository.getProductionTrends(startDate, endDate);
        List<ProductionTrendDTO> trends = new ArrayList<>();
        
        for (Object[] result : results) {
            ProductionTrendDTO trend = new ProductionTrendDTO(
                (LocalDate) result[0],      // date
                (Double) result[1],          // totalProduction
                (Double) result[2],          // averagePerCow
                ((Long) result[3]).intValue() // recordCount
            );
            trends.add(trend);
        }
        
        return trends;
    }

    /**
     * Get cow productivity with average calculation
     * Requirements: 4.2
     */
    @Transactional(readOnly = true)
    public List<CowProductivityDTO> getCowProductivity(Integer limit) {
        Pageable pageable = limit != null ? PageRequest.of(0, limit) : Pageable.unpaged();
        List<Object[]> results = milkProductionRepository.getCowProductivity(pageable);
        List<CowProductivityDTO> productivity = new ArrayList<>();
        
        for (Object[] result : results) {
            CowProductivityDTO dto = new CowProductivityDTO(
                (Long) result[0],    // cowId
                (String) result[1],  // cowTagId
                (Double) result[2]   // averageProduction
            );
            productivity.add(dto);
        }
        
        return productivity;
    }

    /**
     * Get top producers with ranking logic
     * Requirements: 4.4
     */
    @Transactional(readOnly = true)
    public List<TopProducerDTO> getTopProducers(Integer limit) {
        Pageable pageable = limit != null ? PageRequest.of(0, limit) : PageRequest.of(0, 10);
        List<Object[]> results = milkProductionRepository.getTopProducers(pageable);
        List<TopProducerDTO> topProducers = new ArrayList<>();
        
        int rank = 1;
        for (Object[] result : results) {
            TopProducerDTO dto = new TopProducerDTO(
                rank++,
                (Long) result[0],              // cowId
                (String) result[1],            // cowTagId
                (Double) result[2],            // totalProduction
                ((Long) result[3]).intValue()  // recordCount
            );
            topProducers.add(dto);
        }
        
        return topProducers;
    }

    // Backward compatibility methods for existing controller (will be replaced in task 4.4)
    
    @Deprecated
    public List<MilkProduction> getAllMilkProductions() {
        return milkProductionRepository.findAll();
    }

    @Deprecated
    public List<MilkProduction> getMilkByDate(LocalDate date) {
        return milkProductionRepository.findByDateBetween(date, date);
    }

    @Deprecated
    public MilkProduction recordMilk(MilkProduction milkProduction) {
        return milkProductionRepository.save(milkProduction);
    }

    @Deprecated
    public Double getTotalDailyProduction(LocalDate date) {
        Double total = milkProductionRepository.getTotalProductionForDate(date);
        return total != null ? total : 0.0;
    }

    // DTO classes for analytics responses
    
    public static class ProductionTrendDTO {
        private final LocalDate date;
        private final Double totalProduction;
        private final Double averagePerCow;
        private final Integer recordCount;

        public ProductionTrendDTO(LocalDate date, Double totalProduction, Double averagePerCow, Integer recordCount) {
            this.date = date;
            this.totalProduction = totalProduction;
            this.averagePerCow = averagePerCow;
            this.recordCount = recordCount;
        }

        public LocalDate getDate() {
            return date;
        }

        public Double getTotalProduction() {
            return totalProduction;
        }

        public Double getAveragePerCow() {
            return averagePerCow;
        }

        public Integer getRecordCount() {
            return recordCount;
        }
    }

    public static class CowProductivityDTO {
        private final Long cowId;
        private final String cowTagId;
        private final Double averageProduction;

        public CowProductivityDTO(Long cowId, String cowTagId, Double averageProduction) {
            this.cowId = cowId;
            this.cowTagId = cowTagId;
            this.averageProduction = averageProduction;
        }

        public Long getCowId() {
            return cowId;
        }

        public String getCowTagId() {
            return cowTagId;
        }

        public Double getAverageProduction() {
            return averageProduction;
        }
    }

    public static class TopProducerDTO {
        private final Integer rank;
        private final Long cowId;
        private final String cowTagId;
        private final Double totalProduction;
        private final Integer recordCount;

        public TopProducerDTO(Integer rank, Long cowId, String cowTagId, Double totalProduction, Integer recordCount) {
            this.rank = rank;
            this.cowId = cowId;
            this.cowTagId = cowTagId;
            this.totalProduction = totalProduction;
            this.recordCount = recordCount;
        }

        public Integer getRank() {
            return rank;
        }

        public Long getCowId() {
            return cowId;
        }

        public String getCowTagId() {
            return cowTagId;
        }

        public Double getTotalProduction() {
            return totalProduction;
        }

        public Integer getRecordCount() {
            return recordCount;
        }
    }
}
