package com.example.ziwa.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "milk_production", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"cow_id", "date"}),
       indexes = {
           @Index(name = "idx_milk_production_cow_id", columnList = "cow_id"),
           @Index(name = "idx_milk_production_date", columnList = "date")
       })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MilkProduction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "cow_id", nullable = false)
    private Cow cow;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private Double morningQuantity;

    @Column(nullable = false)
    private Double eveningQuantity;

    @Column(nullable = false)
    private Double totalQuantity;

    private String notes;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Version
    private Long version;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
