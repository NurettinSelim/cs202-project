-- Insert sample hotels
INSERT INTO hotels (hotel_id, hotel_name, address, phone) VALUES
(1, 'Grand Hotel Istanbul', 'Taksim Square, Istanbul, Turkey', '+90 212 555 0001'),
(2, 'Seaside Resort Antalya', 'Beach Road, Antalya, Turkey', '+90 242 555 0002');

-- Insert user types
INSERT INTO user_types (type_id, type_name) VALUES 
(1, 'ADMINISTRATOR'),
(2, 'RECEPTIONIST'),
(3, 'HOUSEKEEPING'),
(4, 'GUEST');

-- Insert sample users (employees and admin)
INSERT INTO users (user_id, first_name, last_name, phone, type_id, hotel_id, salary) VALUES
-- Administrators
(1, 'John', 'Admin', '+90 532 111 1111', 1, 1, 10000.00),
(2, 'Mary', 'Manager', '+90 532 222 2222', 1, 2, 10000.00),
-- Receptionists
(3, 'Ali', 'Receptionist', '+90 532 333 3333', 2, 1, 5000.00),
(4, 'Ayse', 'Front', '+90 532 444 4444', 2, 2, 5000.00),
-- Housekeeping
(5, 'Mehmet', 'Cleaner', '+90 532 555 5555', 3, 1, 4000.00),
(6, 'Fatma', 'Cleaner', '+90 532 666 6666', 3, 2, 4000.00),
-- Sample guests (no hotel_id or salary needed)
(7, 'James', 'Guest', '+1 555 1234', 4, NULL, NULL),
(8, 'Emma', 'Tourist', '+1 555 5678', 4, NULL, NULL);

-- Insert room type categories
INSERT INTO room_type_categories (category_id, name, capacity, description) VALUES
(1, 'Single Room', 1, 'Standard single room with one bed'),
(2, 'Double Room', 2, 'Comfortable room with double bed'),
(3, 'Family Suite', 4, 'Spacious suite for families'),
(4, 'Beach View Single', 1, 'Single room with sea view'),
(5, 'Beach View Double', 2, 'Double room with sea view'),
(6, 'Presidential Suite', 4, 'Luxury suite with full sea view');

-- Insert hotel-specific room types
INSERT INTO hotel_room_types (type_id, hotel_id, category_id) VALUES
(1, 1, 1), -- Grand Hotel - Single Room
(2, 1, 2), -- Grand Hotel - Double Room
(3, 1, 3), -- Grand Hotel - Family Suite
(4, 2, 4), -- Seaside Resort - Beach View Single
(5, 2, 5), -- Seaside Resort - Beach View Double
(6, 2, 6); -- Seaside Resort - Presidential Suite

-- Insert floors for each hotel
INSERT INTO floors (floor_id, hotel_id, floor_number) VALUES
(1, 1, 1), (2, 1, 2), (3, 1, 3),
(4, 2, 1), (5, 2, 2), (6, 2, 3);

-- Insert room prices
INSERT INTO room_type_prices (price_id, type_id, base_price) VALUES
(1, 1, 100.00), -- Grand Hotel Single Room
(2, 2, 150.00), -- Grand Hotel Double Room
(3, 3, 300.00), -- Grand Hotel Family Suite
(4, 4, 120.00), -- Seaside Resort Beach View Single
(5, 5, 180.00), -- Seaside Resort Beach View Double
(6, 6, 400.00); -- Seaside Resort Presidential Suite

-- Insert rooms
INSERT INTO rooms (room_id, room_number, type_id, floor_id) VALUES
-- Grand Hotel Istanbul
(1, '101', 1, 1), (2, '102', 2, 1), -- First floor
(3, '201', 2, 2), -- Second floor
(4, '301', 3, 3), -- Third floor
-- Seaside Resort
(5, '101', 4, 4), (6, '102', 5, 4), -- First floor
(7, '201', 5, 5), -- Second floor
(8, '301', 6, 6); -- Third floor

-- Insert booking statuses
INSERT INTO booking_statuses (status_id, status_name) VALUES
(1, 'PENDING'),
(2, 'CONFIRMED'),
(3, 'CHECKED_IN'),
(4, 'CHECKED_OUT'),
(5, 'CANCELLED');

-- Insert sample bookings
INSERT INTO bookings (booking_id, guest_id, room_id, check_in_date, check_out_date, status_id, number_of_guests, confirmed_by) VALUES
(1, 7, 1, '2024-01-10', '2024-01-15', 2, 1, 3),
(2, 8, 5, '2024-01-12', '2024-01-14', 2, 2, 4);

-- Insert sample payments
INSERT INTO payments (payment_id, booking_id, amount, payment_method, processed_by) VALUES
(1, 1, 500.00, 'CREDIT_CARD', 3),
(2, 2, 360.00, 'CREDIT_CARD', 4);

-- Insert sample housekeeping schedules
INSERT INTO housekeeping_schedule (schedule_id, room_id, staff_id, scheduled_date, status, created_by) VALUES
(1, 1, 5, '2024-01-15', 'PENDING', 3),
(2, 5, 6, '2024-01-14', 'PENDING', 4);

-- Insert sample revenue reports
INSERT INTO revenue_reports (report_id, hotel_id, report_date, total_revenue, generated_by) VALUES
(1, 1, '2024-01-15', 500.00, 1),
(2, 2, '2024-01-15', 360.00, 2);
  