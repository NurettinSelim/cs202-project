-- Populate user_types
INSERT INTO user_types (type_id, type_name) VALUES
(1, 'ADMIN'),
(2, 'RECEPTIONIST'),
(3, 'HOUSEKEEPING'),
(4, 'GUEST');

-- Populate hotels
INSERT INTO hotels (hotel_id, hotel_name, address, phone) VALUES
(1, 'Kaş Otel', 'Kaş, Antalya', '+905051234567'),
(2, 'Kuşadası Otel', 'Kuşadası, İzmir', '+905051234568');

-- Populate room_type_categories
INSERT INTO room_type_categories (category_id, name, capacity, description) VALUES
(1, 'Tek Kişilik Oda', 1, 'tek kişilik yatak'),
(2, 'Çift Kişilik Oda', 2, 'çift kişilik yatak'),
(3, 'Aile Odası', 4, 'çift kişilik yatak ve iki adet tek kişilik yatak'),
(4, 'Lüks Oda', 3, 'çift kişilik yatak ve bir adet tek kişilik yatak');

-- Populate hotel_room_types
INSERT INTO hotel_room_types (type_id, hotel_id, category_id) VALUES
(1, 1, 1),
(2, 1, 2),
(3, 1, 3),
(4, 2, 1),
(5, 2, 2);

-- Populate floors
INSERT INTO floors (floor_id, hotel_id, floor_number) VALUES
(1, 1, 1),
(2, 1, 2),
(3, 2, 1);

-- Populate rooms
INSERT INTO rooms (room_id, room_number, type_id, floor_id, price, status) VALUES
(1, '101', 1, 1, 100.00, 'AVAILABLE'),
(2, '102', 2, 1, 150.00, 'AVAILABLE'),
(3, '201', 3, 2, 300.00, 'AVAILABLE'),
(4, '101', 4, 3, 120.00, 'AVAILABLE');

-- Populate booking_statuses
INSERT INTO booking_statuses (status_id, status_name) VALUES
(1, 'PENDING'),
(2, 'CONFIRMED'),
(3, 'CHECKED_IN'),
(4, 'CHECKED_OUT'),
(5, 'CANCELLED');

-- Sample users for different roles
INSERT INTO users (user_id, first_name, last_name, phone, type_id, hotel_id, salary) VALUES
(1, 'Selim', 'Özyılmaz', '+905051234569', 1, 1, 5000),  -- Admin
(2, 'İpek', 'Debreli', '+905051234570', 2, 1, 4000),    -- Receptionist
(3, 'Ayşe', 'Yılmaz', '+905051234571', 3, 1, 3500),     -- Housekeeping
(4, 'Mehmet', 'Kaya', '+905051234572', 4, NULL, NULL),  -- Guest
(5, 'Zeynep', 'Demir', '+905051234573', 3, 1, 3500),    -- Housekeeping
(6, 'Can', 'Yücel', '+905051234574', 4, NULL, NULL);    -- Guest

-- Essential SELECT Queries

-- 1. Find available rooms for a specific date range and capacity
SELECT r.room_id, r.room_number, h.hotel_name, rtc.name as room_type, r.price
FROM rooms r
JOIN hotel_room_types hrt ON r.type_id = hrt.type_id
JOIN hotels h ON hrt.hotel_id = h.hotel_id
JOIN room_type_categories rtc ON hrt.category_id = rtc.category_id
WHERE r.status = 'AVAILABLE'
AND r.room_id NOT IN (
    SELECT room_id FROM bookings 
    WHERE (check_in_date <= ? AND check_out_date >= ?)
    AND status_id IN (SELECT status_id FROM booking_statuses WHERE status_name IN ('CONFIRMED', 'CHECKED_IN'))
)
AND rtc.capacity >= ?;

-- 2. Get user details with type
SELECT u.*, ut.type_name
FROM users u
JOIN user_types ut ON u.type_id = ut.type_id
WHERE u.user_id = ?;

-- 3. Get booking details with room and guest information
SELECT b.*, r.room_number, r.price, h.hotel_name,
       CONCAT(u.first_name, ' ', u.last_name) as guest_name,
       bs.status_name
FROM bookings b
JOIN rooms r ON b.room_id = r.room_id
JOIN hotel_room_types hrt ON r.type_id = hrt.type_id
JOIN hotels h ON hrt.hotel_id = h.hotel_id
JOIN users u ON b.guest_id = u.user_id
JOIN booking_statuses bs ON b.status_id = bs.status_id
WHERE b.booking_id = ?;

-- 4. Get housekeeping schedule for a specific date
SELECT hs.*, r.room_number, 
       CONCAT(u.first_name, ' ', u.last_name) as staff_name
FROM housekeeping_schedule hs
JOIN rooms r ON hs.room_id = r.room_id
JOIN users u ON hs.staff_id = u.user_id
WHERE hs.scheduled_date = ?
AND r.floor_id IN (SELECT floor_id FROM floors WHERE hotel_id = ?);

-- 5. Get room booking statistics and revenue
SELECT r.room_number, r.price, COUNT(b.booking_id) as total_bookings,
       SUM(p.amount) as total_revenue
FROM rooms r
LEFT JOIN bookings b ON r.room_id = b.room_id
LEFT JOIN payments p ON b.booking_id = p.booking_id
WHERE b.check_out_date BETWEEN ? AND ?
GROUP BY r.room_id, r.room_number, r.price;

-- DELETE Queries

-- 1. Cancel a booking (only if no payment)
DELETE FROM bookings 
WHERE booking_id = ? 
AND NOT EXISTS (SELECT 1 FROM payments WHERE booking_id = bookings.booking_id);

-- 2. Remove a room (only if no active bookings)
DELETE FROM rooms 
WHERE room_id = ?
AND room_id NOT IN (
    SELECT room_id FROM bookings 
    WHERE status_id IN (
        SELECT status_id FROM booking_statuses 
        WHERE status_name IN ('CONFIRMED', 'CHECKED_IN')
    )
);

-- 3. Delete a housekeeping schedule
DELETE FROM housekeeping_schedule 
WHERE schedule_id = ? 
AND status = 'PENDING';

-- 4. Remove a user (only if no active bookings or assignments)
DELETE FROM users 
WHERE user_id = ?
AND user_id NOT IN (
    SELECT guest_id FROM bookings WHERE status_id IN (
        SELECT status_id FROM booking_statuses 
        WHERE status_name IN ('CONFIRMED', 'CHECKED_IN')
    )
)
AND user_id NOT IN (
    SELECT staff_id FROM housekeeping_schedule 
    WHERE status = 'PENDING'
);

-- INSERT Queries for Application Use

-- 1. Create a new booking
INSERT INTO bookings (
    booking_id, guest_id, room_id, check_in_date, 
    check_out_date, status_id, number_of_guests
) VALUES (?, ?, ?, ?, ?, ?, ?);

-- 2. Add a new housekeeping schedule
INSERT INTO housekeeping_schedule (
    schedule_id, room_id, staff_id, scheduled_date, 
    created_by
) VALUES (?, ?, ?, ?, ?);

-- 3. Record a payment
INSERT INTO payments (
    payment_id, booking_id, amount, 
    payment_method, processed_by
) VALUES (?, ?, ?, ?, ?);

-- 4. Add a new guest
INSERT INTO users (
    user_id, first_name, last_name, phone, 
    type_id
) VALUES (?, ?, ?, ?, (SELECT type_id FROM user_types WHERE type_name = 'GUEST'));

-- Receptionist Queries

-- Modify booking
UPDATE bookings 
SET check_in_date = ?,
    check_out_date = ?,
    number_of_guests = ?,
    status_id = ?
WHERE booking_id = ?
AND status_id IN (
    SELECT status_id FROM booking_statuses 
    WHERE status_name IN ('PENDING', 'CONFIRMED')
);

-- View housekeepers availability
SELECT u.user_id, u.first_name, u.last_name,
       COUNT(hs.schedule_id) as pending_tasks
FROM users u
LEFT JOIN housekeeping_schedule hs ON u.user_id = hs.staff_id
AND hs.scheduled_date = ? AND hs.status = 'PENDING'
WHERE u.type_id = (SELECT type_id FROM user_types WHERE type_name = 'HOUSEKEEPING')
AND u.hotel_id = ?
GROUP BY u.user_id, u.first_name, u.last_name;

-- Housekeeping Queries

-- View my cleaning schedule
SELECT hs.schedule_id, r.room_number, h.hotel_name, 
       hs.scheduled_date, hs.status
FROM housekeeping_schedule hs
JOIN rooms r ON hs.room_id = r.room_id
JOIN hotel_room_types hrt ON r.type_id = hrt.type_id
JOIN hotels h ON hrt.hotel_id = h.hotel_id
WHERE hs.staff_id = ?
AND hs.scheduled_date >= CURRENT_DATE
ORDER BY hs.scheduled_date;

-- Update task status to completed
UPDATE housekeeping_schedule 
SET status = 'COMPLETED'
WHERE schedule_id = ?
AND staff_id = ?
AND status = 'PENDING';

-- Administrator Queries

-- View most booked room types
SELECT rtc.name as room_type, h.hotel_name, COUNT(*) as booking_count
FROM bookings b
JOIN rooms r ON b.room_id = r.room_id
JOIN hotel_room_types hrt ON r.type_id = hrt.type_id
JOIN room_type_categories rtc ON hrt.category_id = rtc.category_id
JOIN hotels h ON hrt.hotel_id = h.hotel_id
WHERE b.check_in_date BETWEEN ? AND ?
GROUP BY rtc.category_id, h.hotel_id
ORDER BY booking_count DESC;

-- View all employees with their roles
SELECT u.user_id, u.first_name, u.last_name, ut.type_name as role, 
       h.hotel_name, u.salary
FROM users u
JOIN user_types ut ON u.type_id = ut.type_id
LEFT JOIN hotels h ON u.hotel_id = h.hotel_id
WHERE ut.type_name != 'GUEST'
ORDER BY h.hotel_name, ut.type_name;

-- Generate revenue report
INSERT INTO revenue_reports (report_id, hotel_id, report_date, total_revenue, generated_by)
SELECT ?, h.hotel_id, CURRENT_DATE, 
       COALESCE(SUM(p.amount), 0) as total_revenue, ?
FROM hotels h
LEFT JOIN hotel_room_types hrt ON h.hotel_id = hrt.hotel_id
LEFT JOIN rooms r ON hrt.type_id = r.type_id
LEFT JOIN bookings b ON r.room_id = b.room_id
LEFT JOIN payments p ON b.booking_id = p.booking_id
WHERE h.hotel_id = ?
AND p.payment_date BETWEEN ? AND ?;
