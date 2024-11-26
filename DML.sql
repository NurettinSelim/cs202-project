-- Insert sample hotels
INSERT INTO hotels (hotel_name, address, phone, email) VALUES
('Grand Hotel Istanbul', 'Taksim Square, Istanbul, Turkey', '+90 212 555 0001', 'info@grandhotel.com'),
('Seaside Resort Antalya', 'Beach Road, Antalya, Turkey', '+90 242 555 0002', 'info@seasideresort.com');

-- Insert user types
INSERT INTO user_types (type_id, type_name) VALUES 
(1, 'ADMINISTRATOR'),
(2, 'RECEPTIONIST'),
(3, 'HOUSEKEEPING'),
(4, 'GUEST');

-- Insert sample users (employees and admin)
INSERT INTO users (first_name, last_name, email, phone, type_id, hotel_id, hire_date, salary) VALUES
-- Administrators
('John', 'Admin', 'admin1@grandhotel.com', '+90 532 111 1111', 1, 1, '2023-01-01', 10000.00),
('Mary', 'Manager', 'admin2@seasideresort.com', '+90 532 222 2222', 1, 2, '2023-01-01', 10000.00),
-- Receptionists
('Ali', 'Receptionist', 'recep1@grandhotel.com', '+90 532 333 3333', 2, 1, '2023-02-01', 5000.00),
('Ayse', 'Front', 'recep2@seasideresort.com', '+90 532 444 4444', 2, 2, '2023-02-01', 5000.00),
-- Housekeeping
('Mehmet', 'Cleaner', 'house1@grandhotel.com', '+90 532 555 5555', 3, 1, '2023-03-01', 4000.00),
('Fatma', 'Cleaner', 'house2@seasideresort.com', '+90 532 666 6666', 3, 2, '2023-03-01', 4000.00),
-- Sample guests (no hotel_id or hire_date needed)
('James', 'Guest', 'james@email.com', '+1 555 1234', 4, NULL, NULL, NULL),
('Emma', 'Tourist', 'emma@email.com', '+1 555 5678', 4, NULL, NULL, NULL);

-- Insert room types for each hotel
INSERT INTO room_types (hotel_id, name, capacity, description) VALUES
-- For Grand Hotel Istanbul
(1, 'Single Room', 1, 'Standard single room with one bed'),
(1, 'Double Room', 2, 'Comfortable room with double bed'),
(1, 'Family Suite', 4, 'Spacious suite for families'),
-- For Seaside Resort
(2, 'Beach View Single', 1, 'Single room with sea view'),
(2, 'Beach View Double', 2, 'Double room with sea view'),
(2, 'Presidential Suite', 4, 'Luxury suite with full sea view');

-- Insert floors for each hotel
INSERT INTO floors (hotel_id, floor_number, wing) VALUES
(1, 1, 'A'), (1, 2, 'A'), (1, 3, 'A'),
(2, 1, 'EAST'), (2, 2, 'EAST'), (2, 3, 'EAST');

-- Insert room prices
INSERT INTO room_type_prices (type_id, base_price) VALUES
(1, 100.00), (2, 150.00), (3, 300.00),
(4, 120.00), (5, 180.00), (6, 400.00);

-- Insert rooms
INSERT INTO rooms (hotel_id, room_number, type_id, floor_id) VALUES
-- Grand Hotel Istanbul
(1, '101', 1, 1), (1, '102', 2, 1), (1, '201', 2, 2), (1, '301', 3, 3),
-- Seaside Resort
(2, '101', 4, 4), (2, '102', 5, 4), (2, '201', 5, 5), (2, '301', 6, 6);

-- Insert booking statuses
INSERT INTO booking_statuses (status_id, status_name) VALUES
(1, 'PENDING'),
(2, 'CONFIRMED'),
(3, 'CHECKED_IN'),
(4, 'CHECKED_OUT'),
(5, 'CANCELLED');

-- Insert sample bookings
INSERT INTO bookings (hotel_id, guest_id, room_id, check_in_date, check_out_date, status_id, number_of_guests, confirmed_by) VALUES
(1, 7, 1, '2024-01-10', '2024-01-15', 2, 1, 3),
(2, 8, 5, '2024-01-12', '2024-01-14', 2, 2, 4);

-- Insert sample payments
INSERT INTO payments (booking_id, amount, payment_method, processed_by) VALUES
(1, 500.00, 'CREDIT_CARD', 3),
(2, 360.00, 'CREDIT_CARD', 4);

-- Insert sample housekeeping schedules
INSERT INTO housekeeping_schedule (hotel_id, room_id, staff_id, scheduled_date, status, created_by) VALUES
(1, 1, 5, '2024-01-15', 'PENDING', 3),
(2, 5, 6, '2024-01-14', 'PENDING', 4);

-- Insert sample revenue reports
INSERT INTO revenue_reports (hotel_id, report_date, total_revenue, generated_by) VALUES
(1, '2024-01-15', 500.00, 1),
(2, '2024-01-15', 360.00, 2);
  