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
