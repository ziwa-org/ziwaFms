package com.example.ziwa.repository;

import com.example.ziwa.model.HealthRecord;
import com.example.ziwa.model.HealthRecord.HealthRecordType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface HealthRecordRepository extends JpaRepository<HealthRecord, Long> {
    List<HealthRecord> findByCowId(Long cowId, Pageable pageable);
    
    List<HealthRecord> findByCowIdAndRecordType(Long cowId, HealthRecordType type, Pageable pageable);
    
    List<HealthRecord> findByDateBetween(LocalDate start, LocalDate end, Pageable pageable);
    
    @Query(value = "SELECT * FROM health_records hr WHERE hr.withdrawal_period_days > 0 " +
           "AND DATE_ADD(hr.date, INTERVAL hr.withdrawal_period_days DAY) >= CURRENT_DATE " +
           "ORDER BY hr.date DESC", nativeQuery = true)
    List<HealthRecord> findActiveWithdrawals();
}
