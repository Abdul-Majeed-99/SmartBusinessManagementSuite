-- =====================================================
-- Fix salary_records table structure
-- =====================================================
-- This script fixes the salary_records table to include
-- the employee_id column and proper structure

USE smart_business_suite;

-- First, check if salary_records table exists
-- If it exists, we'll drop and recreate it
-- If not, we'll create it fresh

DROP TABLE IF EXISTS salary_records;

-- Create the corrected salary_records table with all required columns
CREATE TABLE salary_records (
    salary_id INT AUTO_INCREMENT PRIMARY KEY,
    employee_id INT NOT NULL,
    salary_date DATE NOT NULL,
    type VARCHAR(50) NOT NULL DEFAULT 'Salary',
    amount DECIMAL(10, 2) NOT NULL,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (employee_id) REFERENCES users(user_id) ON DELETE RESTRICT,
    INDEX idx_employee_id (employee_id),
    INDEX idx_salary_date (salary_date),
    INDEX idx_type (type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Verify the table structure
SHOW CREATE TABLE salary_records;

-- Display confirmation
SELECT 'Database table fixed successfully!' AS Status;
SELECT COUNT(*) AS TableCount FROM information_schema.tables 
WHERE table_schema = 'smart_business_suite' AND table_name = 'salary_records';
