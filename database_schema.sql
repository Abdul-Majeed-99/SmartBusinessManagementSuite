 -- =====================================================
-- Smart Business Management Suite - Database Schema
-- =====================================================
-- Drop existing database if exists
DROP DATABASE IF EXISTS smart_business_suite;

-- Create database
CREATE DATABASE smart_business_suite CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE smart_business_suite;

-- =====================================================
-- USERS TABLE
-- =====================================================
CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    role ENUM('ADMIN', 'MANAGER', 'EMPLOYEE') DEFAULT 'EMPLOYEE',
    status ENUM('ACTIVE', 'INACTIVE', 'BLOCKED') DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    last_login TIMESTAMP NULL,
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_role (role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- CATEGORIES TABLE
-- =====================================================
CREATE TABLE categories (
    category_id INT AUTO_INCREMENT PRIMARY KEY,
    category_name VARCHAR(100) UNIQUE NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_category_name (category_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- PRODUCTS TABLE
-- =====================================================
CREATE TABLE products (
    product_id INT AUTO_INCREMENT PRIMARY KEY,
    product_code VARCHAR(50) UNIQUE NOT NULL,
    product_name VARCHAR(100) NOT NULL,
    category_id INT NOT NULL,
    description TEXT,
    unit_price DECIMAL(10, 2) NOT NULL,
    quantity_in_stock INT DEFAULT 0,
    reorder_level INT DEFAULT 10,
    supplier_name VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES categories(category_id) ON DELETE RESTRICT,
    INDEX idx_product_code (product_code),
    INDEX idx_category_id (category_id),
    INDEX idx_quantity (quantity_in_stock)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- SALES TABLE
-- =====================================================
CREATE TABLE sales (
    sale_id INT AUTO_INCREMENT PRIMARY KEY,
    sale_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    user_id INT NOT NULL,
    customer_name VARCHAR(100) NOT NULL,
    subtotal DECIMAL(10, 2) NOT NULL,
    tax_amount DECIMAL(10, 2) DEFAULT 0,
    discount_amount DECIMAL(10, 2) DEFAULT 0,
    total_amount DECIMAL(10, 2) NOT NULL,
    payment_method ENUM('CASH', 'CARD', 'CHECK', 'ONLINE') DEFAULT 'CASH',
    payment_status ENUM('PAID', 'PARTIAL', 'PENDING') DEFAULT 'PENDING',
    status ENUM('COMPLETED', 'CANCELLED', 'RETURNED') DEFAULT 'COMPLETED',
    notes TEXT,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE RESTRICT,
    INDEX idx_sale_date (sale_date),
    INDEX idx_user_id (user_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- SALES_ITEMS TABLE
-- =====================================================
CREATE TABLE sales_items (
    sale_item_id INT AUTO_INCREMENT PRIMARY KEY,
    sale_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(10, 2) NOT NULL,
    total_price DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (sale_id) REFERENCES sales(sale_id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE RESTRICT,
    INDEX idx_sale_id (sale_id),
    INDEX idx_product_id (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- INVOICES TABLE
-- =====================================================
CREATE TABLE invoices (
    invoice_id INT AUTO_INCREMENT PRIMARY KEY,
    sale_id INT NOT NULL UNIQUE,
    invoice_number VARCHAR(50) UNIQUE NOT NULL,
    customer_name VARCHAR(100) NOT NULL,
    customer_email VARCHAR(100),
    customer_phone VARCHAR(20),
    invoice_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    total_amount DECIMAL(10, 2) NOT NULL,
    paid_amount DECIMAL(10, 2) DEFAULT 0,
    remaining_amount DECIMAL(10, 2) NOT NULL,
    tax_percent DECIMAL(5, 2) DEFAULT 0,
    discount_percent DECIMAL(5, 2) DEFAULT 0,
    status ENUM('DRAFT', 'ISSUED', 'PAID', 'CANCELLED') DEFAULT 'DRAFT',
    notes TEXT,
    FOREIGN KEY (sale_id) REFERENCES sales(sale_id) ON DELETE RESTRICT,
    INDEX idx_invoice_number (invoice_number),
    INDEX idx_invoice_date (invoice_date),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- SALARY_RECORDS TABLE
-- =====================================================
CREATE TABLE salary_records (
    salary_id INT AUTO_INCREMENT PRIMARY KEY,
    employee_id INT NOT NULL,
    salary_date DATE NOT NULL,
    type VARCHAR(50) NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (employee_id) REFERENCES users(user_id) ON DELETE RESTRICT,
    INDEX idx_employee_id (employee_id),
    INDEX idx_salary_date (salary_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- SALES_BY_CATEGORY TABLE (Simple category-based sales tracking)
-- =====================================================
CREATE TABLE IF NOT EXISTS sales_by_category (
    sale_id INT AUTO_INCREMENT PRIMARY KEY,
    sale_date DATE NOT NULL DEFAULT CURDATE(),
    category VARCHAR(100) NOT NULL,
    product_name VARCHAR(150) NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(10, 2) NOT NULL,
    discount DECIMAL(5, 2) DEFAULT 0,
    total_amount DECIMAL(10, 2) NOT NULL,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_sale_date (sale_date),
    INDEX idx_category (category)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- SAMPLE DATA
-- =====================================================

-- Insert sample users (password: admin123 hashed with SHA-256)
INSERT INTO users (username, email, password_hash, full_name, phone, role, status) VALUES
('admin', 'admin@company.com', '8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918', 'Administrator', '1234567890', 'ADMIN', 'ACTIVE'),
('manager', 'manager@company.com', '8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918', 'Manager User', '0987654321', 'MANAGER', 'ACTIVE'),
('employee', 'employee@company.com', '8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918', 'Employee User', '5555555555', 'EMPLOYEE', 'ACTIVE');

-- Insert sample categories
INSERT INTO categories (category_name, description) VALUES
('Electronics', 'Electronic devices and accessories'),
('Clothing', 'Apparel and fashion items'),
('Food & Beverages', 'Food items and drinks'),
('Office Supplies', 'Office equipment and supplies'),
('Furniture', 'Office and household furniture');

-- Insert sample products
INSERT INTO products (product_code, product_name, category_id, description, unit_price, quantity_in_stock, reorder_level, supplier_name) VALUES
('ELEC-001', 'Laptop Computer', 1, 'High performance laptop', 999.99, 15, 5, 'Tech Supplies Inc'),
('ELEC-002', 'Wireless Mouse', 1, 'USB wireless mouse', 29.99, 50, 20, 'Tech Supplies Inc'),
('CLOTH-001', 'Office Shirt', 2, 'Professional office shirt', 49.99, 30, 10, 'Fashion Imports'),
('FOOD-001', 'Coffee Beans 1kg', 3, 'Premium coffee beans', 15.99, 100, 30, 'Coffee Works'),
('FURN-001', 'Office Chair', 5, 'Ergonomic office chair', 199.99, 8, 3, 'Furniture Plus');

-- Insert sample sale
INSERT INTO sales (user_id, customer_name, subtotal, tax_amount, discount_amount, total_amount, payment_method, payment_status) VALUES
(2, 'John Doe', 500.00, 50.00, 25.00, 525.00, 'CASH', 'PAID');

-- Insert sample sale items
INSERT INTO sales_items (sale_id, product_id, quantity, unit_price, total_price) VALUES
(1, 2, 5, 29.99, 149.95),
(1, 1, 1, 999.99, 999.99);

-- Insert sample invoice
INSERT INTO invoices (sale_id, invoice_number, customer_name, customer_email, total_amount, paid_amount, remaining_amount, status) VALUES
(1, 'INV-001', 'John Doe', 'john@example.com', 525.00, 525.00, 0.00, 'PAID');

-- =====================================================
-- VERIFY SETUP
-- =====================================================
SELECT 'Database Setup Complete!' as Status;
SHOW TABLES;
