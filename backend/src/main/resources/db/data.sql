-- =============================================
-- LogAp Fleet Management - Sample Data
-- =============================================

-- Default admin user (password: admin123) - BCrypt hash
INSERT INTO usuarios (nome, email, senha, role)
VALUES ('Administrador', 'admin@logap.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ADMIN')
ON CONFLICT (email) DO NOTHING;

