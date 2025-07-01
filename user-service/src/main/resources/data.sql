-- Insert default admin user
-- Password: admin123 (BCrypt encoded)
INSERT INTO user (full_name, email, password, role, active, wallet) 
VALUES ('System Admin', 'admin@lms.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'ADMIN', true, 1000.0)
ON DUPLICATE KEY UPDATE id=id; 