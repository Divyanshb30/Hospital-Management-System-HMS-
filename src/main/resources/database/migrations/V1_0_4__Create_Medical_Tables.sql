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


INSERT INTO medical_records (patient_id, created_by_doctor_id, chief_complaint, notes)
VALUES
(1, 1, 'Frequent chest pain', 'Patient complains of occasional chest pain and shortness of breath'),
(2, 2, 'Migraine attacks', 'Recurring migraine episodes for the last 2 weeks'),
(3, 3, 'Knee pain', 'Pain in right knee after physical activity'),
(1, 6, 'General fatigue', 'Feeling tired for several days with low energy');

INSERT INTO diagnoses (medical_record_id, code, description, severity, diagnosed_by_doctor_id)
VALUES
(1, 'C01', 'Angina', 'Moderate', 1),
(2, 'H02', 'Migraine', 'Mild', 2),
(3, 'O03', 'Knee Strain', 'Moderate', 3),
(4, 'G04', 'Chronic Fatigue', 'Mild', 6);


INSERT INTO treatments (medical_record_id, treatment_plan, start_date, end_date, assigned_by_doctor_id, status)
VALUES
(1, 'Prescribe Nitroglycerin as needed: lifestyle changes', '2025-09-01', '2025-12-01', 1, 'ACTIVE'),
(2, 'Prescribe analgesics: avoid triggers', '2025-09-10', '2025-09-30', 2, 'ACTIVE'),
(3, 'Physiotherapy 3x per week: knee support', '2025-09-05', '2025-11-05', 3, 'ACTIVE'),
(4, 'Vitamin supplements: regular exercise', '2025-09-15', '2025-10-15', 6, 'ACTIVE');


INSERT INTO prescriptions (medical_record_id, notes, date_issued)
VALUES
(1, 'Nitroglycerin 0.4mg as needed', '2025-09-01'),
(2, 'Ibuprofen 200mg 2x daily', '2025-09-10'),
(3, 'Knee support brace and analgesics', '2025-09-05'),
(4, 'Vitamin B12 1000mcg daily', '2025-09-15');


INSERT INTO medical_tests (medical_record_id, test_type, result, result_date, status)
VALUES
(1, 'ECG', 'ST depression observed', '2025-09-02', 'COMPLETED'),
(2, 'MRI Brain', 'No abnormalities', '2025-09-11', 'COMPLETED'),
(3, 'X-Ray Knee', 'Mild joint degeneration', '2025-09-06', 'COMPLETED'),
(4, 'Blood Test', 'Slight vitamin deficiency', '2025-09-16', 'COMPLETED');
