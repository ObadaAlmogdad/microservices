-- Insert default admin user
-- Password: 123456 (BCrypt encoded)
INSERT INTO user (full_name, email, password, role, active, wallet) 
VALUES ('System Admin', 'admin@lms.com', '$2a$10$C952mqiOp9TfMAclwO4gp.5e26zXR7MCBMxCz.yXQdFoJdnQ/kLSy', 'ADMIN', true, 1000.0)
ON DUPLICATE KEY UPDATE id=id; 