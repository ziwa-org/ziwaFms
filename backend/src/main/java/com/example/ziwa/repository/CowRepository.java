package com.example.ziwa.repository;

import com.example.ziwa.model.Cow;
import com.example.ziwa.model.Cow.CowStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CowRepository extends JpaRepository<Cow, Long> {
    Optional<Cow> findByTagId(String tagId);
    
    Page<Cow> findByStatus(CowStatus status, Pageable pageable);
    
    Page<Cow> findByStatusAndBreed(CowStatus status, String breed, Pageable pageable);
    
    Long countByStatus(CowStatus status);
    
    boolean existsByTagId(String tagId);
}
