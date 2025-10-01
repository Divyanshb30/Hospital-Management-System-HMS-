-- Hospital Management System - Doctors Data Migration (Safe)

-- Version: 1.0.1
-- Description: Insert sample doctors data into users and doctors tables (Safe mode)
-- Dependencies: V1_0_0__create_base_schema.sql
-- Note: Uses INSERT IGNORE to prevent duplicate key errors

-- Insert doctors into users table (using INSERT IGNORE to avoid duplicates)
INSERT IGNORE INTO users (username, password_hash, email, phone, role) VALUES
('Doc1', '6L21ESFtrPX8AOC2DpNjAw==:gAkkUgYB3ZsHiPylJxmU7qTdBzhVpm/SRoK02+V8xl0=', 'doc1@gmail.com', '9876543210', 'DOCTOR'),
('Doc2', '408Fi6WZGnBmTxcZM/2BqA==:CGCWkkT66osCz+8KFNgyLAYA5I5MYgaEYW7/WlSb79w=', 'doc2@gmail.com', '9876543211', 'DOCTOR'),
('Doc3', 'TCZUIzAWhb114m8BuAutvg==:HzRZ/xH6udIrMGMSokDvYbKOz3Qt//JHYUgStmC1zyo=', 'doc3@gmail.com', '9876543212', 'DOCTOR'),
('Doc4', 'TCoKPrXXdLffUsOnQ+nlsQ==:rQz6v+yapXhWjVfIghMyQFUnBLVwW5vnMfO8y0vjzH8=', 'doc4@gmail.com', '9876543213', 'DOCTOR'),
('Doc5', 'ume2w/dwBBXbTQfiKBrfIQ==:+0JplJSF9O8CyibwfV+zYvpkkOlA8C0dGyQptwEUxs4=', 'doc5@gmail.com', '9876543214', 'DOCTOR'),
('Doc6', 'ume2w/dwBBXbTQfiKBrfIQ==:+0JplJSF9O8CyibwfV+zYvpkkOlA8C0dGyQptwEUxs4=', 'doc6@gmail.com', '9876543215', 'DOCTOR'),
('Doc7', 'qZ6b/NjzKwytaiYWugN3OQ==:xLytLh7FaezmPxCn/o2Uhu8HY260M3MIMpI/fjRo1fE=', 'doc7@gmail.com', '9876543216', 'DOCTOR'),
('Doc8', 'i0AQOr+FIJHQQJGrEYAAuA==:XzC3aQgoVL1Yhu8KrEyoDyL88SPcjntsW35735fMeLA=', 'doc8@gmail.com', '9876543217', 'DOCTOR'),
('Doc9', 'kk11P6wO4uIlpssCjRY3gQ==:Pq070rxJb3ZFBwQlzQG7UgPn1C1VY0TUYr62osOG058=', 'doc9@gmail.com', '9876543218', 'DOCTOR'),
('Doc10', 'RCMzCzPMUcy+zRdxbISp0w==:ubDn67xf15BosUdsG7gsPcrs9FfZB1qhPYdia81hSqM=', 'doc10@gmail.com', '9876543219', 'DOCTOR');

-- Insert doctors into doctors table using dynamic user_id lookup
-- This approach ensures we get the correct user_id even if some users already exist

INSERT IGNORE INTO doctors (user_id, first_name, last_name, specialization, license_number, department_id, qualification, experience_years, consultation_fee)
SELECT u.id, 'Rajesh', 'Kumar', 'Interventional Cardiology', 'LIC0001', 1, 'MBBS, MD', 6, 600.00
FROM users u WHERE u.username = 'Doc1' AND NOT EXISTS (SELECT 1 FROM doctors d WHERE d.user_id = u.id)
UNION ALL
SELECT u.id, 'Sunita', 'Verma', 'Cardiac Surgery', 'LIC0002', 1, 'MBBS, MD', 7, 700.00
FROM users u WHERE u.username = 'Doc2' AND NOT EXISTS (SELECT 1 FROM doctors d WHERE d.user_id = u.id)
UNION ALL
SELECT u.id, 'Suresh', 'Patel', 'Clinical Neurology', 'LIC0003', 2, 'MBBS, MD', 8, 800.00
FROM users u WHERE u.username = 'Doc3' AND NOT EXISTS (SELECT 1 FROM doctors d WHERE d.user_id = u.id)
UNION ALL
SELECT u.id, 'Meera', 'Reddy', 'Neurosurgery', 'LIC0004', 2, 'MBBS, MD', 9, 900.00
FROM users u WHERE u.username = 'Doc4' AND NOT EXISTS (SELECT 1 FROM doctors d WHERE d.user_id = u.id)
UNION ALL
SELECT u.id, 'Arun', 'Gupta', 'Joint Replacement', 'LIC0005', 3, 'MBBS, MD', 10, 1000.00
FROM users u WHERE u.username = 'Doc5' AND NOT EXISTS (SELECT 1 FROM doctors d WHERE d.user_id = u.id)
UNION ALL
SELECT u.id, 'Priya', 'Agarwal', 'Sports Medicine', 'LIC0006', 3, 'MBBS, MD', 11, 1100.00
FROM users u WHERE u.username = 'Doc6' AND NOT EXISTS (SELECT 1 FROM doctors d WHERE d.user_id = u.id)
UNION ALL
SELECT u.id, 'Amit', 'Sharma', 'General Pediatrics', 'LIC0007', 4, 'MBBS, MD', 12, 1200.00
FROM users u WHERE u.username = 'Doc7' AND NOT EXISTS (SELECT 1 FROM doctors d WHERE d.user_id = u.id)
UNION ALL
SELECT u.id, 'Kavita', 'Joshi', 'Pediatric Surgery', 'LIC0008', 4, 'MBBS, MD', 13, 1300.00
FROM users u WHERE u.username = 'Doc8' AND NOT EXISTS (SELECT 1 FROM doctors d WHERE d.user_id = u.id)
UNION ALL
SELECT u.id, 'Vikram', 'Singh', 'Emergency Medicine', 'LIC0009', 5, 'MBBS, MD', 14, 1400.00
FROM users u WHERE u.username = 'Doc9' AND NOT EXISTS (SELECT 1 FROM doctors d WHERE d.user_id = u.id)
UNION ALL
SELECT u.id, 'Anita', 'Mehta', 'Trauma Surgery', 'LIC0010', 5, 'MBBS, MD', 5, 1500.00
FROM users u WHERE u.username = 'Doc10' AND NOT EXISTS (SELECT 1 FROM doctors d WHERE d.user_id = u.id);

-- Migration completed successfully
-- Total doctors to be added: 10 (2 per department)
-- Departments covered: Cardiology, Neurology, Orthopedics, Pediatrics, Emergency
-- Safe mode: Uses INSERT IGNORE and NOT EXISTS to prevent duplicate entries
-- Dynamic user_id lookup ensures correct foreign key relationships