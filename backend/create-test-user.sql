-- Create a test user with password: Test@123
-- BCrypt hash for "Test@123"
INSERT INTO users (username, password_hash, full_name, role, active, created_at, updated_at)
VALUES (
  'testuser',
  '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
  'Test User',
  'USER',
  1,
  NOW(),
  NOW()
);

-- Create an admin user with password: Admin@123
-- BCrypt hash for "Admin@123"
INSERT INTO users (username, password_hash, full_name, role, active, created_at, updated_at)
VALUES (
  'admin',
  '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cyhQQl3MpbEVBIxqoeui.lxW9qaF6',
  'System Administrator',
  'ADMIN',
  1,
  NOW(),
  NOW()
);
