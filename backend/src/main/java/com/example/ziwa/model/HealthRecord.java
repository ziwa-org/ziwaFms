package com.example.ziwa.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "health_records", indexes = {
    @Index(name = "idx_health_records_cow_id", columnList = "cow_id"),
    @Index(name = "idx_health_records_date", columnList = "date"),
    @Index(name = "idx_health_records_type", columnList = "recordType")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "cow")
@EqualsAndHashCode(exclude = "cow")
public class HealthRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cow_id", nullable = false)
    private Cow cow;

    @Column(nullable = false)
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private HealthRecordType recordType;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    private String veterinarianName;

    private String medication;

    @Column(nullable = false)
    @Builder.Default
    private Integer withdrawalPeriodDays = 0;

    private Double cost;

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
        if (withdrawalPeriodDays == null) {
            withdrawalPeriodDays = 0;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum HealthRecordType {
        VACCINATION, TREATMENT, CHECKUP
    }
}
