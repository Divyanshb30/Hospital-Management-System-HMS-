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

CREATE TABLE IF NOT EXISTS migration_test (
    id INT PRIMARY KEY AUTO_INCREMENT,
    test_message VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO patients (first_name, last_name, date_of_birth, gender, phone, email, blood_group, status)
VALUES ('John', 'Doe', '1990-01-01', 'MALE', '123-456-7890', 'john.doe@email.com', 'O+', 'ACTIVE');

INSERT INTO migration_test (test_message) VALUES ('Migration system is working!');
