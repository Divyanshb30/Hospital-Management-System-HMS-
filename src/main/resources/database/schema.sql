-- Specializations
CREATE TABLE IF NOT EXISTS specializations (
  specialization_id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) NOT NULL UNIQUE,
  description TEXT
);

-- Doctors (references specialization)
CREATE TABLE IF NOT EXISTS doctors (
  doctor_id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  specialization_id INT,
  contact VARCHAR(15),
  department VARCHAR(50),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (specialization_id) REFERENCES specializations(specialization_id)
);

-- Doctor Schedules (one row per day slot)
CREATE TABLE IF NOT EXISTS doctor_schedules (
  schedule_id INT AUTO_INCREMENT PRIMARY KEY,
  doctor_id INT NOT NULL,
  day_of_week VARCHAR(20) NOT NULL, -- e.g. Monday
  start_time TIME NOT NULL,
  end_time TIME NOT NULL,
  FOREIGN KEY (doctor_id) REFERENCES doctors(doctor_id),
  UNIQUE (doctor_id, day_of_week, start_time, end_time)
);

-- Minimal appointments table (used to compute stats)
CREATE TABLE IF NOT EXISTS appointments (
  appointment_id INT AUTO_INCREMENT PRIMARY KEY,
  patient_id INT,
  doctor_id INT,
  appointment_datetime DATETIME,
  status VARCHAR(20) DEFAULT 'SCHEDULED'
);

-- Doctor stats table (optional; you can populate via batch job or triggers)
CREATE TABLE IF NOT EXISTS doctor_stats (
  doctor_id INT PRIMARY KEY,
  total_appointments INT DEFAULT 0,
  completed_appointments INT DEFAULT 0,
  average_rating DECIMAL(3,2) DEFAULT 0.00,
  FOREIGN KEY (doctor_id) REFERENCES doctors(doctor_id)
);


CREATE TABLE IF NOT EXISTS medical_records (
  medical_record_id INT AUTO_INCREMENT PRIMARY KEY,
  patient_id INT NOT NULL,
  created_by_doctor_id INT,
  chief_complaint TEXT,
  notes TEXT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS diagnoses (
  diagnosis_id INT AUTO_INCREMENT PRIMARY KEY,
  medical_record_id INT NOT NULL,
  code VARCHAR(50),
  description TEXT,
  severity VARCHAR(20),
  diagnosed_by_doctor_id INT,
  diagnosed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (medical_record_id) REFERENCES medical_records(medical_record_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS treatments (
  treatment_id INT AUTO_INCREMENT PRIMARY KEY,
  medical_record_id INT NOT NULL,
  treatment_plan TEXT,
  start_date DATE,
  end_date DATE,
  assigned_by_doctor_id INT,
  status VARCHAR(30) DEFAULT 'ACTIVE',
  FOREIGN KEY (medical_record_id) REFERENCES medical_records(medical_record_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS prescriptions (
  prescription_id INT AUTO_INCREMENT PRIMARY KEY,
  medical_record_id INT NOT NULL,
  notes TEXT,
  date_issued DATE,
  FOREIGN KEY (medical_record_id) REFERENCES medical_records(medical_record_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS medical_tests (
  test_id INT AUTO_INCREMENT PRIMARY KEY,
  medical_record_id INT NOT NULL,
  test_type VARCHAR(100),
  result TEXT,
  result_date DATE,
  status VARCHAR(30) DEFAULT 'ORDERED',
  FOREIGN KEY (medical_record_id) REFERENCES medical_records(medical_record_id) ON DELETE CASCADE
);
