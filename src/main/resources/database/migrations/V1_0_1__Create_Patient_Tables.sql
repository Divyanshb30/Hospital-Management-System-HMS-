-- Hospital Management System - Patient Tables
-- Migration Version: 1.0.1
-- Description: Create patient management tables

CREATE TABLE IF NOT EXISTS patients (
    id INT PRIMARY KEY AUTO_INCREMENT,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    date_of_birth DATE,
    gender VARCHAR(10),
    phone VARCHAR(20),
    email VARCHAR(100),
    address TEXT,
    blood_group VARCHAR(5),
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_name (first_name, last_name),
    INDEX idx_phone (phone),
    INDEX idx_email (email),
    INDEX idx_status (status)
);

CREATE TABLE IF NOT EXISTS emergency_contacts (
    id INT PRIMARY KEY AUTO_INCREMENT,
    patient_id INT NOT NULL,
    name VARCHAR(100) NOT NULL,
    relation VARCHAR(50),
    phone VARCHAR(20) NOT NULL,
    email VARCHAR(100),
    address TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE,
    INDEX idx_patient (patient_id)
);

CREATE TABLE IF NOT EXISTS patient_history (
    id INT PRIMARY KEY AUTO_INCREMENT,
    patient_id INT NOT NULL,
    doctor_id INT,
    record_date DATE NOT NULL,
    diagnosis TEXT,
    treatment TEXT,
    prescriptions TEXT,
    tests_ordered TEXT,
    follow_up_date DATE,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE,
    INDEX idx_patient (patient_id),
    INDEX idx_date (record_date)
);

CREATE TABLE IF NOT EXISTS patient_stats (
    id INT PRIMARY KEY AUTO_INCREMENT,
    patient_id INT NOT NULL,
    record_date DATE NOT NULL,
    height DECIMAL(5,2),
    weight DECIMAL(5,2),
    blood_pressure VARCHAR(20),
    heart_rate INT,
    temperature DECIMAL(4,1),
    oxygen_saturation INT,
    total_visits INT DEFAULT 0,
    total_admissions INT DEFAULT 0,
    total_prescriptions INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE,
    INDEX idx_patient (patient_id),
    INDEX idx_date (record_date)
);

INSERT INTO patients (first_name, last_name, date_of_birth, gender, phone, email, blood_group, status)
VALUES
('John', 'Smith', '1985-03-15', 'MALE', '123-456-7890', 'john.smith@email.com', 'A+', 'ACTIVE'),
('Jane', 'Doe', '1990-07-22', 'FEMALE', '098-765-4321', 'jane.doe@email.com', 'B+', 'ACTIVE'),
('Bob', 'Johnson', '1978-12-10', 'MALE', '555-123-4567', 'bob.johnson@email.com', 'O-', 'ACTIVE');
