CREATE TABLE IF NOT EXISTS patients (id INT PRIMARY KEY AUTO_INCREMENT, patient_id VARCHAR(20) UNIQUE NOT NULL, first_name VARCHAR(50) NOT NULL, last_name VARCHAR(50) NOT NULL, date_of_birth DATE NOT NULL, phone VARCHAR(20), email VARCHAR(100), created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP);

CREATE TABLE IF NOT EXISTS migration_test (id INT PRIMARY KEY AUTO_INCREMENT, test_message VARCHAR(255), created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP);

INSERT INTO patients (patient_id, first_name, last_name, date_of_birth, phone, email) VALUES ('PAT001', 'John', 'Doe', '1990-01-01', '123-456-7890', 'john.doe@email.com');

INSERT INTO migration_test (test_message) VALUES ('Migration system is working!');
