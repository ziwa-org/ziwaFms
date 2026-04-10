-- Ziwa Dairy Farm Database Schema
-- This file is for reference only. JPA will auto-create tables based on entities.

-- Users table
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    full_name VARCHAR(100),
    active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT
);

-- Cows table
CREATE TABLE IF NOT EXISTS cows (
    id BIGSERIAL PRIMARY KEY,
    tag_id VARCHAR(50) UNIQUE NOT NULL,
    breed VARCHAR(100) NOT NULL,
    date_of_birth DATE NOT NULL,
    acquisition_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT
);

-- Breeding records table
CREATE TABLE IF NOT EXISTS breeding_records (
    id BIGSERIAL PRIMARY KEY,
    cow_id BIGINT NOT NULL REFERENCES cows(id),
    breeding_date DATE NOT NULL,
    bull_id VARCHAR(50),
    expected_calving_date DATE,
    actual_calving_date DATE,
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT
);

-- Milk production table
CREATE TABLE IF NOT EXISTS milk_production (
    id BIGSERIAL PRIMARY KEY,
    cow_id BIGINT NOT NULL REFERENCES cows(id),
    date DATE NOT NULL,
    morning_quantity DOUBLE PRECISION NOT NULL,
    evening_quantity DOUBLE PRECISION NOT NULL,
    total_quantity DOUBLE PRECISION NOT NULL,
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT,
    UNIQUE(cow_id, date)
);

-- Health records table
CREATE TABLE IF NOT EXISTS health_records (
    id BIGSERIAL PRIMARY KEY,
    cow_id BIGINT NOT NULL REFERENCES cows(id),
    date DATE NOT NULL,
    record_type VARCHAR(20) NOT NULL,
    description TEXT NOT NULL,
    veterinarian_name VARCHAR(100),
    medication VARCHAR(255),
    withdrawal_period_days INTEGER NOT NULL DEFAULT 0,
    cost DOUBLE PRECISION,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT
);

-- Financial transactions table
CREATE TABLE IF NOT EXISTS financial_transactions (
    id BIGSERIAL PRIMARY KEY,
    date DATE NOT NULL,
    type VARCHAR(20) NOT NULL,
    category VARCHAR(100) NOT NULL,
    amount DOUBLE PRECISION NOT NULL,
    description TEXT NOT NULL,
    reference_id VARCHAR(100),
    deleted BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT
);

-- Indexes for performance
CREATE INDEX IF NOT EXISTS idx_cows_tag_id ON cows(tag_id);
CREATE INDEX IF NOT EXISTS idx_cows_status ON cows(status);
CREATE INDEX IF NOT EXISTS idx_milk_production_cow_id ON milk_production(cow_id);
CREATE INDEX IF NOT EXISTS idx_milk_production_date ON milk_production(date);
CREATE INDEX IF NOT EXISTS idx_health_records_cow_id ON health_records(cow_id);
CREATE INDEX IF NOT EXISTS idx_health_records_date ON health_records(date);
CREATE INDEX IF NOT EXISTS idx_health_records_type ON health_records(record_type);
CREATE INDEX IF NOT EXISTS idx_financial_transactions_date ON financial_transactions(date);
CREATE INDEX IF NOT EXISTS idx_financial_transactions_type ON financial_transactions(type);
CREATE INDEX IF NOT EXISTS idx_financial_transactions_category ON financial_transactions(category);
