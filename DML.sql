-- Insert sample hotels
INSERT INTO hotels (hotel_name, address, phone) VALUES
('Grand Hotel Istanbul', 'Taksim Square, Istanbul, Turkey', '+90 212 555 0001'),
('Seaside Resort Antalya', 'Beach Road, Antalya, Turkey', '+90 242 555 0002');

-- Insert user types
INSERT INTO user_types (type_id, type_name) VALUES 
(1, 'ADMINISTRATOR'),
(2, 'RECEPTIONIST'),
(3, 'HOUSEKEEPING'),
(4, 'GUEST');

-- Insert sample users (employees and admin)
INSERT INTO users (first_name, last_name, phone, type_id, hotel_id, salary) VALUES
-- Administrators
('John', 'Admin', '+90 532 111 1111', 1, 1, 10000.00),
('Mary', 'Manager', '+90 532 222 2222', 1, 2, 10000.00),
-- Receptionists
('Ali', 'Receptionist', '+90 532 333 3333', 2, 1, 5000.00),
('Ayse', 'Front', '+90 532 444 4444', 2, 2, 5000.00),
-- Housekeeping
('Mehmet', 'Cleaner', '+90 532 555 5555', 3, 1, 4000.00),
('Fatma', 'Cleaner', '+90 532 666 6666', 3, 2, 4000.00),
-- Sample guests (no hotel_id or salary needed)
('James', 'Guest', '+1 555 1234', 4, NULL, NULL),
('Emma', 'Tourist', '+1 555 5678', 4, NULL, NULL);

-- Insert room type categories
INSERT INTO room_type_categories (name, capacity, description) VALUES
('Single Room', 1, 'Standard single room with one bed'),
('Double Room', 2, 'Comfortable room with double bed'),
('Family Suite', 4, 'Spacious suite for families'),
('Beach View Single', 1, 'Single room with sea view'),
('Beach View Double', 2, 'Double room with sea view'),
('Presidential Suite', 4, 'Luxury suite with full sea view');

-- Insert hotel-specific room types
INSERT INTO hotel_room_types (hotel_id, category_id) VALUES
(1, 1), -- Grand Hotel - Single Room
(1, 2), -- Grand Hotel - Double Room
(1, 3), -- Grand Hotel - Family Suite
(2, 4), -- Seaside Resort - Beach View Single
(2, 5), -- Seaside Resort - Beach View Double
(2, 6); -- Seaside Resort - Presidential Suite

-- Insert floors for each hotel
INSERT INTO floors (hotel_id, floor_number) VALUES
(1, 1), (1, 2), (1, 3),
(2, 1), (2, 2), (2, 3);

-- Insert room prices
INSERT INTO room_type_prices (type_id, base_price) VALUES
(1, 100.00), -- Grand Hotel Single Room
(2, 150.00), -- Grand Hotel Double Room
(3, 300.00), -- Grand Hotel Family Suite
(4, 120.00), -- Seaside Resort Beach View Single
(5, 180.00), -- Seaside Resort Beach View Double
(6, 400.00); -- Seaside Resort Presidential Suite

-- Insert rooms
INSERT INTO rooms (room_number, type_id, floor_id) VALUES
-- Grand Hotel Istanbul
('101', 1, 1), ('102', 2, 1), -- First floor
('201', 2, 2), -- Second floor
('301', 3, 3), -- Third floor
-- Seaside Resort
('101', 4, 4), ('102', 5, 4), -- First floor
('201', 5, 5), -- Second floor
('301', 6, 6); -- Third floor

-- Insert booking statuses
INSERT INTO booking_statuses (status_id, status_name) VALUES
(1, 'PENDING'),
(2, 'CONFIRMED'),
(3, 'CHECKED_IN'),
(4, 'CHECKED_OUT'),
(5, 'CANCELLED');

-- Insert sample bookings
INSERT INTO bookings (guest_id, room_id, check_in_date, check_out_date, status_id, number_of_guests, confirmed_by) VALUES
(7, 1, '2024-01-10', '2024-01-15', 2, 1, 3),
(8, 5, '2024-01-12', '2024-01-14', 2, 2, 4);

-- Insert sample payments
INSERT INTO payments (booking_id, amount, payment_method, processed_by) VALUES
(1, 500.00, 'CREDIT_CARD', 3),
(2, 360.00, 'CREDIT_CARD', 4);

-- Insert sample housekeeping schedules
INSERT INTO housekeeping_schedule (room_id, staff_id, scheduled_date, status, created_by) VALUES
(1, 5, '2024-01-15', 'PENDING', 3),
(5, 6, '2024-01-14', 'PENDING', 4);

-- Insert sample revenue reports
INSERT INTO revenue_reports (hotel_id, report_date, total_revenue, generated_by) VALUES
(1, '2024-01-15', 500.00, 1),
(2, '2024-01-15', 360.00, 2);
  