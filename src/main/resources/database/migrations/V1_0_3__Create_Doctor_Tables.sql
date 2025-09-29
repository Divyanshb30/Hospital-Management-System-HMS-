CREATE TABLE IF NOT EXISTS specializations (
    specialization_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) UNIQUE NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_name (name)
);

CREATE TABLE IF NOT EXISTS doctors (
    doctor_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    specialization_id INT NULL,
    contact VARCHAR(20),
    department VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (specialization_id) REFERENCES specializations(specialization_id) ON DELETE SET NULL,
    INDEX idx_name (name),
    INDEX idx_specialization (specialization_id),
    INDEX idx_department (department)
);

CREATE TABLE IF NOT EXISTS doctor_schedules (
    schedule_id INT PRIMARY KEY AUTO_INCREMENT,
    doctor_id INT NOT NULL,
    day_of_week VARCHAR(20) NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (doctor_id) REFERENCES doctors(doctor_id) ON DELETE CASCADE,
    INDEX idx_doctor (doctor_id),
    INDEX idx_day (day_of_week),
    UNIQUE KEY unique_doctor_day_time (doctor_id, day_of_week, start_time)
);

CREATE TABLE IF NOT EXISTS doctor_stats (
    doctor_id INT PRIMARY KEY,
    total_appointments INT DEFAULT 0,
    completed_appointments INT DEFAULT 0,
    cancelled_appointments INT DEFAULT 0,
    average_rating DECIMAL(3,2) DEFAULT 0.00,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (doctor_id) REFERENCES doctors(doctor_id) ON DELETE CASCADE,
    INDEX idx_rating (average_rating),
    INDEX idx_appointments (total_appointments)
);

INSERT INTO specializations (name, description) VALUES
('Cardiology', 'Heart and cardiovascular system specialists'),
('Neurology', 'Brain and nervous system specialists'),
('Orthopedics', 'Bone, joint, and muscle specialists'),
('Pediatrics', 'Child healthcare specialists'),
('Dermatology', 'Skin, hair, and nail specialists'),
('Gastroenterology', 'Digestive system specialists'),
('Oncology', 'Cancer treatment specialists'),
('Psychiatry', 'Mental health specialists'),
('General Medicine', 'General healthcare and family medicine');

INSERT INTO doctors (name, specialization_id, contact, department) VALUES
('Dr. Sarah Johnson', 1, '555-0101', 'Cardiology'),
('Dr. Michael Chen', 2, '555-0102', 'Neurology'),
('Dr. Emily Williams', 3, '555-0103', 'Orthopedics'),
('Dr. David Brown', 4, '555-0104', 'Pediatrics'),
('Dr. Lisa Davis', 5, '555-0105', 'Dermatology'),
('Dr. James Wilson', 9, '555-0106', 'General Medicine');

INSERT INTO doctor_schedules (doctor_id, day_of_week, start_time, end_time) VALUES
(1, 'MONDAY', '09:00:00', '17:00:00'),
(1, 'WEDNESDAY', '09:00:00', '17:00:00'),
(1, 'FRIDAY', '09:00:00', '13:00:00'),
(2, 'TUESDAY', '10:00:00', '18:00:00'),
(2, 'THURSDAY', '10:00:00', '18:00:00'),
(3, 'MONDAY', '08:00:00', '16:00:00'),
(3, 'TUESDAY', '08:00:00', '16:00:00'),
(3, 'WEDNESDAY', '08:00:00', '16:00:00'),
(4, 'MONDAY', '09:00:00', '17:00:00'),
(4, 'TUESDAY', '09:00:00', '17:00:00'),
(4, 'WEDNESDAY', '09:00:00', '17:00:00'),
(4, 'THURSDAY', '09:00:00', '17:00:00'),
(4, 'FRIDAY', '09:00:00', '17:00:00');

INSERT INTO doctor_stats (doctor_id, total_appointments, completed_appointments, average_rating) VALUES
(1, 0, 0, 0.00),
(2, 0, 0, 0.00),
(3, 0, 0, 0.00),
(4, 0, 0, 0.00),
(5, 0, 0, 0.00),
(6, 0, 0, 0.00);
