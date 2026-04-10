package com.example.ziwa.repository;

import com.example.ziwa.model.MilkProduction;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MilkProductionRepository extends JpaRepository<MilkProduction, Long> {
    
    /**
     * Find production records by cow ID and date range with pagination
     */
    List<MilkProduction> findByCowIdAndDateBetween(Long cowId, LocalDate start, LocalDate end, Pageable pageable);
    
    /**
     * Find production record by cow ID and specific date (for uniqueness check)
     */
    Optional<MilkProduction> findByCowIdAndDate(Long cowId, LocalDate date);
    
    /**
     * Get production trends aggregated by date
     * Returns daily totals, averages, and record counts for the specified date range
     */
    @Query("SELECT mp.date as date, " +
           "SUM(mp.totalQuantity) as totalProduction, " +
           "AVG(mp.totalQuantity) as averagePerCow, " +
           "COUNT(mp) as recordCount " +
           "FROM MilkProduction mp " +
           "WHERE mp.date BETWEEN :start AND :end " +
           "GROUP BY mp.date " +
           "ORDER BY mp.date")
    List<Object[]> getProductionTrends(@Param("start") LocalDate start, @Param("end") LocalDate end);
    
    /**
     * Get cow productivity - average production per cow
     * Returns cow ID, tag ID, and average total quantity
     */
    @Query("SELECT c.id as cowId, " +
           "c.tagId as cowTagId, " +
           "AVG(mp.totalQuantity) as averageProduction " +
           "FROM MilkProduction mp " +
           "JOIN mp.cow c " +
           "GROUP BY c.id, c.tagId " +
           "ORDER BY AVG(mp.totalQuantity) DESC")
    List<Object[]> getCowProductivity(Pageable pageable);
    
    /**
     * Get top producing cows by total production volume
     */
    @Query("SELECT c.id as cowId, " +
           "c.tagId as cowTagId, " +
           "SUM(mp.totalQuantity) as totalProduction, " +
           "COUNT(mp) as recordCount " +
           "FROM MilkProduction mp " +
           "JOIN mp.cow c " +
           "GROUP BY c.id, c.tagId " +
           "ORDER BY SUM(mp.totalQuantity) DESC")
    List<Object[]> getTopProducers(Pageable pageable);
    
    /**
     * Get total production for a specific date (for dashboard)
     */
    @Query("SELECT SUM(mp.totalQuantity) FROM MilkProduction mp WHERE mp.date = :date")
    Double getTotalProductionForDate(@Param("date") LocalDate date);
    
    /**
     * Get production records for a specific date range (for analytics)
     */
    List<MilkProduction> findByDateBetween(LocalDate start, LocalDate end);
    
    // Backward compatibility methods (to be removed in task 4.2)
    
    /**
     * Find all production records for a specific cow
     * @deprecated Use findByCowIdAndDateBetween with appropriate date range instead
     */
    @Deprecated
    List<MilkProduction> findByCowId(Long cowId);
    
    /**
     * Find production records for a specific date
     * @deprecated Use findByDateBetween or getTotalProductionForDate instead
     */
    @Deprecated
    @Query("SELECT mp FROM MilkProduction mp WHERE mp.date = :date")
    List<MilkProduction> findByProductionDate(@Param("date") LocalDate date);
    
    /**
     * Get total production for a specific day
     * @deprecated Use getTotalProductionForDate instead
     */
    @Deprecated
    @Query("SELECT SUM(mp.totalQuantity) FROM MilkProduction mp WHERE mp.date = :date")
    Double getTotalProductionForDay(@Param("date") LocalDate date);
}
