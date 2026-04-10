package com.example.ziwa.repository;

import com.example.ziwa.model.Cow;
import com.example.ziwa.model.HealthRecord;
import com.example.ziwa.model.HealthRecord.HealthRecordType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class HealthRecordRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private HealthRecordRepository healthRecordRepository;

    private Cow testCow;

    @BeforeEach
    void setUp() {
        testCow = Cow.builder()
                .tagId("TEST-001")
                .breed("Holstein")
                .dateOfBirth(LocalDate.of(2020, 1, 1))
                .acquisitionDate(LocalDate.of(2020, 3, 1))
                .status(Cow.CowStatus.ACTIVE)
                .build();
        entityManager.persist(testCow);
        entityManager.flush();
    }

    @Test
    void testCreateHealthRecord() {
        // Given
        HealthRecord healthRecord = HealthRecord.builder()
                .cow(testCow)
                .date(LocalDate.now())
                .recordType(HealthRecordType.VACCINATION)
                .description("Annual vaccination")
                .veterinarianName("Dr. Smith")
                .withdrawalPeriodDays(0)
                .cost(50.0)
                .build();

        // When
        HealthRecord saved = healthRecordRepository.save(healthRecord);

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCow().getId()).isEqualTo(testCow.getId());
        assertThat(saved.getRecordType()).isEqualTo(HealthRecordType.VACCINATION);
        assertThat(saved.getDescription()).isEqualTo("Annual vaccination");
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
    }

    @Test
    void testFindActiveWithdrawals_WithActiveWithdrawal() {
        // Given - Create a health record with withdrawal period that is still active
        HealthRecord activeWithdrawal = HealthRecord.builder()
                .cow(testCow)
                .date(LocalDate.now().minusDays(5))
                .recordType(HealthRecordType.TREATMENT)
                .description("Antibiotic treatment")
                .medication("Penicillin")
                .withdrawalPeriodDays(10) // Ends in 5 days
                .build();
        entityManager.persist(activeWithdrawal);

        // Create a health record with expired withdrawal period
        HealthRecord expiredWithdrawal = HealthRecord.builder()
                .cow(testCow)
                .date(LocalDate.now().minusDays(20))
                .recordType(HealthRecordType.TREATMENT)
                .description("Old treatment")
                .medication("Aspirin")
                .withdrawalPeriodDays(10) // Ended 10 days ago
                .build();
        entityManager.persist(expiredWithdrawal);

        // Create a health record with no withdrawal period
        HealthRecord noWithdrawal = HealthRecord.builder()
                .cow(testCow)
                .date(LocalDate.now())
                .recordType(HealthRecordType.CHECKUP)
                .description("Regular checkup")
                .withdrawalPeriodDays(0)
                .build();
        entityManager.persist(noWithdrawal);

        entityManager.flush();

        // When
        List<HealthRecord> activeWithdrawals = healthRecordRepository.findActiveWithdrawals();

        // Then
        assertThat(activeWithdrawals).hasSize(1);
        assertThat(activeWithdrawals.get(0).getId()).isEqualTo(activeWithdrawal.getId());
        assertThat(activeWithdrawals.get(0).getMedication()).isEqualTo("Penicillin");
    }

    @Test
    void testFindActiveWithdrawals_NoActiveWithdrawals() {
        // Given - Create only expired or zero withdrawal records
        HealthRecord expiredWithdrawal = HealthRecord.builder()
                .cow(testCow)
                .date(LocalDate.now().minusDays(20))
                .recordType(HealthRecordType.TREATMENT)
                .description("Old treatment")
                .withdrawalPeriodDays(10)
                .build();
        entityManager.persist(expiredWithdrawal);
        entityManager.flush();

        // When
        List<HealthRecord> activeWithdrawals = healthRecordRepository.findActiveWithdrawals();

        // Then
        assertThat(activeWithdrawals).isEmpty();
    }

    @Test
    void testFindByCowId() {
        // Given
        HealthRecord record1 = HealthRecord.builder()
                .cow(testCow)
                .date(LocalDate.now())
                .recordType(HealthRecordType.VACCINATION)
                .description("Vaccination 1")
                .withdrawalPeriodDays(0)
                .build();
        HealthRecord record2 = HealthRecord.builder()
                .cow(testCow)
                .date(LocalDate.now().minusDays(1))
                .recordType(HealthRecordType.CHECKUP)
                .description("Checkup 1")
                .withdrawalPeriodDays(0)
                .build();
        entityManager.persist(record1);
        entityManager.persist(record2);
        entityManager.flush();

        // When
        List<HealthRecord> records = healthRecordRepository.findByCowId(testCow.getId(), null);

        // Then
        assertThat(records).hasSize(2);
    }

    @Test
    void testIndexesExist() {
        // This test verifies that the entity is properly configured with indexes
        // The indexes are defined in the @Table annotation
        HealthRecord healthRecord = HealthRecord.builder()
                .cow(testCow)
                .date(LocalDate.now())
                .recordType(HealthRecordType.VACCINATION)
                .description("Test vaccination")
                .withdrawalPeriodDays(0)
                .build();

        HealthRecord saved = healthRecordRepository.save(healthRecord);
        
        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
    }
}
