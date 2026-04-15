package com.example.ziwa.config;

import com.example.ziwa.model.*;
import com.example.ziwa.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@Order(2) // Run after DataInitializer
@RequiredArgsConstructor
@Slf4j
public class SampleDataInitializer implements CommandLineRunner {

    private final CowRepository cowRepository;
    private final MilkProductionRepository milkProductionRepository;
    private final HealthRecordRepository healthRecordRepository;
    private final FinancialTransactionRepository financialTransactionRepository;

    @Override
    public void run(String... args) {
        // Only create sample data if no cows exist
        if (cowRepository.count() == 0) {
            log.info("No cows found. Creating sample data...");
            createSampleData();
            log.info("Sample data created successfully!");
        } else {
            log.info("Sample data already exists. Skipping initialization.");
        }
    }

    private void createSampleData() {
        // Create sample cows
        Cow cow1 = Cow.builder()
                .tagId("COW-001")
                .breed("Holstein")
                .dateOfBirth(LocalDate.of(2020, 1, 15))
                .acquisitionDate(LocalDate.of(2021, 3, 20))
                .status(Cow.CowStatus.ACTIVE)
                .build();
        cowRepository.save(cow1);
        log.info("Created cow: {}", cow1.getTagId());

        Cow cow2 = Cow.builder()
                .tagId("COW-002")
                .breed("Jersey")
                .dateOfBirth(LocalDate.of(2019, 6, 10))
                .acquisitionDate(LocalDate.of(2020, 8, 15))
                .status(Cow.CowStatus.ACTIVE)
                .build();
        cowRepository.save(cow2);
        log.info("Created cow: {}", cow2.getTagId());

        Cow cow3 = Cow.builder()
                .tagId("COW-003")
                .breed("Holstein")
                .dateOfBirth(LocalDate.of(2021, 3, 5))
                .acquisitionDate(LocalDate.of(2022, 5, 10))
                .status(Cow.CowStatus.ACTIVE)
                .build();
        cowRepository.save(cow3);
        log.info("Created cow: {}", cow3.getTagId());

        // Create sample milk production records
        for (int i = 0; i < 7; i++) {
            LocalDate date = LocalDate.now().minusDays(i);
            
            double morning1 = 15.5 + (Math.random() * 2);
            double evening1 = 14.2 + (Math.random() * 2);
            MilkProduction prod1 = MilkProduction.builder()
                    .cow(cow1)
                    .date(date)
                    .morningQuantity(morning1)
                    .eveningQuantity(evening1)
                    .totalQuantity(morning1 + evening1)
                    .notes("Normal production")
                    .build();
            milkProductionRepository.save(prod1);

            double morning2 = 12.5 + (Math.random() * 2);
            double evening2 = 11.8 + (Math.random() * 2);
            MilkProduction prod2 = MilkProduction.builder()
                    .cow(cow2)
                    .date(date)
                    .morningQuantity(morning2)
                    .eveningQuantity(evening2)
                    .totalQuantity(morning2 + evening2)
                    .notes("Normal production")
                    .build();
            milkProductionRepository.save(prod2);

            double morning3 = 13.0 + (Math.random() * 2);
            double evening3 = 12.5 + (Math.random() * 2);
            MilkProduction prod3 = MilkProduction.builder()
                    .cow(cow3)
                    .date(date)
                    .morningQuantity(morning3)
                    .eveningQuantity(evening3)
                    .totalQuantity(morning3 + evening3)
                    .notes("Normal production")
                    .build();
            milkProductionRepository.save(prod3);
        }
        log.info("Created milk production records for the last 7 days");

        // Create sample health records
        HealthRecord health1 = HealthRecord.builder()
                .cow(cow1)
                .recordType(HealthRecord.HealthRecordType.VACCINATION)
                .date(LocalDate.now().minusDays(30))
                .description("Annual vaccination - FMD")
                .veterinarianName("Dr. Smith")
                .cost(50.00)
                .withdrawalPeriodDays(0)
                .build();
        healthRecordRepository.save(health1);

        HealthRecord health2 = HealthRecord.builder()
                .cow(cow2)
                .recordType(HealthRecord.HealthRecordType.CHECKUP)
                .date(LocalDate.now().minusDays(15))
                .description("Routine health checkup")
                .veterinarianName("Dr. Johnson")
                .cost(30.00)
                .withdrawalPeriodDays(0)
                .build();
        healthRecordRepository.save(health2);

        HealthRecord health3 = HealthRecord.builder()
                .cow(cow3)
                .recordType(HealthRecord.HealthRecordType.VACCINATION)
                .date(LocalDate.now().minusDays(45))
                .description("Brucellosis vaccination")
                .veterinarianName("Dr. Smith")
                .cost(45.00)
                .withdrawalPeriodDays(0)
                .build();
        healthRecordRepository.save(health3);
        log.info("Created health records");

        // Create sample financial transactions
        FinancialTransaction income1 = FinancialTransaction.builder()
                .type(FinancialTransaction.TransactionType.INCOME)
                .category("MILK_SALES")
                .amount(1500.00)
                .date(LocalDate.now().minusDays(1))
                .description("Weekly milk sales")
                .build();
        financialTransactionRepository.save(income1);

        FinancialTransaction expense1 = FinancialTransaction.builder()
                .type(FinancialTransaction.TransactionType.EXPENSE)
                .category("FEED")
                .amount(500.00)
                .date(LocalDate.now().minusDays(2))
                .description("Monthly feed purchase")
                .build();
        financialTransactionRepository.save(expense1);

        FinancialTransaction expense2 = FinancialTransaction.builder()
                .type(FinancialTransaction.TransactionType.EXPENSE)
                .category("VETERINARY")
                .amount(125.00)
                .date(LocalDate.now().minusDays(3))
                .description("Veterinary services")
                .build();
        financialTransactionRepository.save(expense2);

        FinancialTransaction income2 = FinancialTransaction.builder()
                .type(FinancialTransaction.TransactionType.INCOME)
                .category("MILK_SALES")
                .amount(1450.00)
                .date(LocalDate.now().minusDays(8))
                .description("Weekly milk sales")
                .build();
        financialTransactionRepository.save(income2);
        log.info("Created financial transactions");
    }
}
