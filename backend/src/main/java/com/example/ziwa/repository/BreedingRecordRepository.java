package com.example.ziwa.repository;

import com.example.ziwa.model.BreedingRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BreedingRecordRepository extends JpaRepository<BreedingRecord, Long> {
    List<BreedingRecord> findByCowId(Long cowId);
    
    Page<BreedingRecord> findByCowId(Long cowId, Pageable pageable);
}
