INSERT INTO users (ID, NAME, LINE1, TOWN, COUNTY, POSTCODE, PHONE_NUMBER, EMAIL, PASSWORD, CREATED_TIMESTAMP, UPDATED_TIMESTAMP) VALUES
('999', 'Test User One', '123 Test St', 'Testville', 'Testshire', 'TE1 2ST', '+447123456789', 'user1@example.com', '$2a$10$v5fCSW/zfa8Xl08IO9sXku3KNSBH23wK3jI5sM3EJ8AjLif3nK9PK', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),
('998', 'Test User Two', '456 Test Ave', 'Sampleton', 'Sampleland', 'SA2 3MP', '+447987654321', 'user2@example.com', '$2a$10$v5fCSW/zfa8Xl08IO9sXku3KNSBH23wK3jI5sM3EJ8AjLif3nK9PK', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());
-- Password for both is 'password' (bcrypt encoded)

