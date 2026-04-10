package com.example.ziwa.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cows", indexes = {
    @Index(name = "idx_cow_tag_id", columnList = "tagId"),
    @Index(name = "idx_cow_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"productionRecords", "healthRecords", "breedingRecords"})
@EqualsAndHashCode(exclude = {"productionRecords", "healthRecords", "breedingRecords"})
public class Cow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tagId", unique = true, nullable = false)
    private String tagId;

    @Column(nullable = false)
    private String breed;

    @Column(nullable = false)
    private LocalDate dateOfBirth;

    @Column(nullable = false)
    private LocalDate acquisitionDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private CowStatus status = CowStatus.ACTIVE;

    @OneToMany(mappedBy = "cow", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<MilkProduction> productionRecords = new ArrayList<>();

    @OneToMany(mappedBy = "cow", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<HealthRecord> healthRecords = new ArrayList<>();

    @OneToMany(mappedBy = "cow", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<BreedingRecord> breedingRecords = new ArrayList<>();

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
        if (status == null) {
            status = CowStatus.ACTIVE;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum CowStatus {
        ACTIVE, SOLD, DECEASED
    }
}
