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
(1, 7, 1, '2024-12-10', '2024-12-15', 2, 1, 3),
(2, 8, 5, '2024-12-12', '2024-12-14', 2, 2, 4);

-- Insert sample payments
INSERT INTO payments (payment_id, booking_id, amount, payment_method, processed_by) VALUES
(1, 1, 500.00, 'CARD', 3),
(2, 2, 360.00, 'CARD', 4);

-- Insert sample housekeeping schedules
INSERT INTO housekeeping_schedule (schedule_id, room_id, staff_id, scheduled_date, status, created_by) VALUES
(1, 1, 5, '2024-12-15', 'PENDING', 3),
(2, 5, 6, '2024-12-14', 'PENDING', 4);

-- Insert sample revenue reports
INSERT INTO revenue_reports (report_id, hotel_id, report_date, total_revenue, generated_by) VALUES
(1, 1, '2024-12-15', 500.00, 1),
(2, 2, '2024-12-15', 360.00, 2);

-- Essential SELECT Queries --

-- Get available rooms for a date range
SELECT r.room_id, h.hotel_name, r.room_number, rtc.name as room_type, 
       rtc.capacity, rtp.base_price
FROM rooms r
JOIN hotel_room_types hrt ON r.type_id = hrt.type_id
JOIN hotels h ON hrt.hotel_id = h.hotel_id
JOIN room_type_categories rtc ON hrt.category_id = rtc.category_id
JOIN room_type_prices rtp ON hrt.type_id = rtp.type_id
WHERE r.status = 'AVAILABLE'
AND NOT EXISTS (
    SELECT 1 FROM bookings b
    WHERE b.room_id = r.room_id
    AND b.status_id IN (SELECT status_id FROM booking_statuses WHERE status_name IN ('CONFIRMED', 'CHECKED_IN'))
    AND (b.check_in_date <= :check_out_date AND b.check_out_date >= :check_in_date)
);

-- Get bookings for a guest
SELECT b.booking_id, h.hotel_name, r.room_number, rtc.name as room_type,
       b.check_in_date, b.check_out_date, bs.status_name,
       SUM(p.amount) as total_paid
FROM bookings b
JOIN rooms r ON b.room_id = r.room_id
JOIN hotel_room_types hrt ON r.type_id = hrt.type_id
JOIN hotels h ON hrt.hotel_id = h.hotel_id
JOIN room_type_categories rtc ON hrt.category_id = rtc.category_id
JOIN booking_statuses bs ON b.status_id = bs.status_id
LEFT JOIN payments p ON b.booking_id = p.booking_id
WHERE b.guest_id = :user_id
GROUP BY b.booking_id, h.hotel_name, r.room_number, rtc.name, 
         b.check_in_date, b.check_out_date, bs.status_name;

-- Get housekeeping tasks for a staff member
SELECT hs.schedule_id, h.hotel_name, r.room_number, 
       hs.scheduled_date, hs.status
FROM housekeeping_schedule hs
JOIN rooms r ON hs.room_id = r.room_id
JOIN hotel_room_types hrt ON r.type_id = hrt.type_id
JOIN hotels h ON hrt.hotel_id = h.hotel_id
WHERE hs.staff_id = :staff_id
AND hs.scheduled_date >= CURRENT_DATE
ORDER BY hs.scheduled_date;

-- Get hotel revenue report
SELECT h.hotel_name,
       COUNT(DISTINCT b.booking_id) as total_bookings,
       SUM(p.amount) as total_revenue,
       AVG(p.amount) as average_booking_value
FROM hotels h
JOIN hotel_room_types hrt ON h.hotel_id = hrt.hotel_id
JOIN rooms r ON hrt.type_id = r.type_id
JOIN bookings b ON r.room_id = b.room_id
JOIN payments p ON b.booking_id = p.booking_id
WHERE b.check_in_date BETWEEN :start_date AND :end_date
GROUP BY h.hotel_id, h.hotel_name;
  