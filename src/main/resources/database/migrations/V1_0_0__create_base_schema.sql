-- Hospital Management System - Base Schema Migration
-- Version: 1.0.0
-- Description: Create initial tables for users, patients, doctors, departments

-- Step 1: Create users table (base table for all user types)
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(20) NOT NULL,
    role ENUM('PATIENT', 'DOCTOR', 'ADMIN') NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_role (role),
    INDEX idx_active (is_active)
);

-- Step 2: Create departments table (independent table)
CREATE TABLE departments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    head_doctor_id BIGINT NULL,
    location VARCHAR(100),
    phone VARCHAR(20),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    INDEX idx_name (name),
    INDEX idx_head_doctor (head_doctor_id),
    INDEX idx_active (is_active)
);

-- Step 3: Create patients table (depends on users)
CREATE TABLE patients (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    date_of_birth DATE NOT NULL,
    gender ENUM('MALE', 'FEMALE', 'OTHER') NOT NULL,
    blood_group VARCHAR(5),
    address TEXT,
    emergency_contact_name VARCHAR(100),
    emergency_contact_phone VARCHAR(20),
    insurance_number VARCHAR(50),
    medical_history TEXT,
    allergies TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY unique_patient_user (user_id),
    INDEX idx_name (first_name, last_name),
    INDEX idx_dob (date_of_birth),
    INDEX idx_blood_group (blood_group)
);

-- Step 4: Create doctors table (depends on users and departments)
CREATE TABLE doctors (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    specialization VARCHAR(100) NOT NULL,
    license_number VARCHAR(50) NOT NULL UNIQUE,
    department_id BIGINT NOT NULL,
    qualification VARCHAR(255),
    experience_years INT DEFAULT 0,
    consultation_fee DECIMAL(10, 2) DEFAULT 500.00,
    available_from TIME DEFAULT '09:00:00',
    available_to TIME DEFAULT '17:00:00',
    is_available BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (department_id) REFERENCES departments(id) ON DELETE RESTRICT,
    UNIQUE KEY unique_doctor_user (user_id),
    UNIQUE KEY unique_license (license_number),
    INDEX idx_specialization (specialization),
    INDEX idx_department (department_id),
    INDEX idx_name (first_name, last_name),
    INDEX idx_available (is_available)
);

-- Step 5: Create appointments table (depends on patients and doctors)
CREATE TABLE appointments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id BIGINT NOT NULL,
    doctor_id BIGINT NOT NULL,
    appointment_date DATE NOT NULL,
    appointment_time TIME NOT NULL,
    status ENUM('SCHEDULED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED', 'NO_SHOW', 'RESCHEDULED') DEFAULT 'SCHEDULED',
    reason VARCHAR(255),
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE,
    FOREIGN KEY (doctor_id) REFERENCES doctors(id) ON DELETE CASCADE,
    INDEX idx_patient (patient_id),
    INDEX idx_doctor (doctor_id),
    INDEX idx_date (appointment_date),
    INDEX idx_status (status),
    INDEX idx_datetime (appointment_date, appointment_time),
    UNIQUE KEY unique_doctor_datetime (doctor_id, appointment_date, appointment_time)
);

-- Step 6: Create bills table (depends on appointments and patients)
CREATE TABLE bills (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    appointment_id BIGINT NOT NULL,
    patient_id BIGINT NOT NULL,
    total_amount DECIMAL(10, 2) NOT NULL,
    tax_amount DECIMAL(10, 2) DEFAULT 0.00,
    discount_amount DECIMAL(10, 2) DEFAULT 0.00,
    final_amount DECIMAL(10, 2) NOT NULL,
    status ENUM('PENDING', 'PROCESSING', 'COMPLETED', 'FAILED', 'REFUNDED', 'PARTIALLY_PAID') DEFAULT 'PENDING',
    bill_date DATE NOT NULL,
    due_date DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (appointment_id) REFERENCES appointments(id) ON DELETE CASCADE,
    FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE,
    INDEX idx_appointment (appointment_id),
    INDEX idx_patient (patient_id),
    INDEX idx_status (status),
    INDEX idx_bill_date (bill_date),
    INDEX idx_due_date (due_date)
);

-- Step 7: Create payments table (depends on bills)
CREATE TABLE payments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    bill_id BIGINT NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    payment_method ENUM('CASH', 'CREDIT_CARD', 'DEBIT_CARD', 'UPI', 'NET_BANKING', 'INSURANCE') NOT NULL,
    transaction_id VARCHAR(100),
    payment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status ENUM('PENDING', 'PROCESSING', 'COMPLETED', 'FAILED', 'REFUNDED') DEFAULT 'PENDING',
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (bill_id) REFERENCES bills(id) ON DELETE CASCADE,
    INDEX idx_bill (bill_id),
    INDEX idx_method (payment_method),
    INDEX idx_status (status),
    INDEX idx_payment_date (payment_date),
    INDEX idx_transaction (transaction_id)
);

-- Step 8: Add foreign key constraint for departments head_doctor (after doctors table exists)
ALTER TABLE departments
ADD CONSTRAINT fk_departments_head_doctor
FOREIGN KEY (head_doctor_id) REFERENCES doctors(id) ON DELETE SET NULL;

-- Step 9: Insert default data
INSERT INTO users (username, password_hash, email, phone, role) VALUES
('admin', 'salt123:hashedpassword123', 'admin@hospital.com', '9999999999', 'ADMIN');

INSERT INTO departments (name, description, location, phone) VALUES
('Cardiology', 'Heart and cardiovascular diseases', 'Building A, Floor 2', '011-1234-5601'),
('Neurology', 'Brain and nervous system disorders', 'Building B, Floor 3', '011-1234-5602'),
('Orthopedics', 'Bone, joint and muscle problems', 'Building A, Floor 1', '011-1234-5603'),
('Pediatrics', 'Medical care for children', 'Building C, Floor 1', '011-1234-5604'),
('Emergency', 'Emergency medical services', 'Building A, Ground Floor', '011-1234-5600');
