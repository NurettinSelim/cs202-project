-- Populate hotels
INSERT INTO hotels (hotel_id, hotel_name, address, phone) VALUES
(1, 'Kaş Otel', 'Kaş, Antalya', '+905051234567'),
(2, 'Kuşadası Otel', 'Kuşadası, İzmir', '+905051234568');

-- Populate room_statuses
INSERT INTO room_statuses (status_id, status_name) VALUES
(1, 'AVAILABLE'),
(2, 'OCCUPIED'),
(3, 'MAINTENANCE'),
(4, 'CLEANING');

-- Populate room_types
INSERT INTO room_types (type_id, hotel_id, type_name, base_price, capacity, bed_count) VALUES
(1, 1, 'Tek Kişilik Oda', 100.00, 1, 1),
(2, 1, 'Çift Kişilik Oda', 150.00, 2, 1),
(3, 1, 'Aile Odası', 300.00, 4, 2),
(4, 2, 'Tek Kişilik Oda', 120.00, 1, 1),
(5, 2, 'Çift Kişilik Oda', 180.00, 2, 1);

-- Populate rooms
INSERT INTO rooms (room_id, room_number, hotel_id, type_id, status_id) VALUES
(1, '101', 1, 1, 1),
(2, '102', 1, 2, 1),
(3, '201', 1, 3, 1),
(4, '101', 2, 4, 1);

-- Populate booking_statuses
INSERT INTO booking_statuses (status_id, status_name) VALUES
(1, 'PENDING'),
(2, 'CONFIRMED'),
(3, 'CHECKED_IN'),
(4, 'CHECKED_OUT'),
(5, 'CANCELLED');

-- Sample users
INSERT INTO users (user_id, first_name, last_name, phone, created_at) VALUES
(1, 'Selim', 'Özyılmaz', '+905051234569', CURRENT_TIMESTAMP),
(2, 'İpek', 'Debreli', '+905051234570', CURRENT_TIMESTAMP),
(3, 'Ayşe', 'Yılmaz', '+905051234571', CURRENT_TIMESTAMP),
(4, 'Mehmet', 'Kaya', '+905051234572', CURRENT_TIMESTAMP),
(5, 'Zeynep', 'Demir', '+905051234573', CURRENT_TIMESTAMP),
(6, 'Can', 'Yücel', '+905051234574', CURRENT_TIMESTAMP);

-- Staff records
INSERT INTO staff (user_id, hotel_id, salary, hire_date) VALUES
(1, 1, 5000, CURRENT_DATE),
(2, 1, 4000, CURRENT_DATE),
(3, 1, 3500, CURRENT_DATE),
(5, 1, 3500, CURRENT_DATE);

-- Staff type assignments
INSERT INTO administrator_staff (user_id) VALUES (1,2);
INSERT INTO receptionist_staff (user_id) VALUES (3);
INSERT INTO housekeeping_staff (user_id) VALUES (4);

-- Guest records
INSERT INTO guests (user_id) VALUES (5), (6);

-- Essential SELECT Queries

-- 1. Find available rooms for a specific date range and capacity
SELECT r.room_id, r.room_number, h.hotel_name, rt.type_name, rt.base_price
FROM rooms r
JOIN room_types rt ON r.type_id = rt.type_id
JOIN hotels h ON r.hotel_id = h.hotel_id
WHERE r.status_id = 1  -- AVAILABLE
AND r.room_id NOT IN (
    SELECT br.room_id 
    FROM bookings b
    JOIN booking_rooms br ON b.booking_id = br.booking_id
    WHERE (check_in_date <= ? AND check_out_date >= ?)
    AND b.status_id IN (2, 3)  -- CONFIRMED, CHECKED_IN
)
AND rt.capacity >= ?;

-- 2. Get user details with role
SELECT u.*, 
    CASE 
        WHEN g.user_id IS NOT NULL THEN 'GUEST'
        WHEN a.user_id IS NOT NULL THEN 'ADMINISTRATOR'
        WHEN r.user_id IS NOT NULL THEN 'RECEPTIONIST'
        WHEN h.user_id IS NOT NULL THEN 'HOUSEKEEPING'
    END as role
FROM users u
LEFT JOIN guests g ON u.user_id = g.user_id
LEFT JOIN administrator_staff a ON u.user_id = a.user_id
LEFT JOIN receptionist_staff r ON u.user_id = r.user_id
LEFT JOIN housekeeping_staff h ON u.user_id = h.user_id
WHERE u.user_id = ?;

-- 3. Get booking details with room and guest information
SELECT b.*, r.room_number, rt.base_price, h.hotel_name,
       CONCAT(u.first_name, ' ', u.last_name) as guest_name,
       bs.status_name,
       br.guests_in_room
FROM bookings b
JOIN booking_rooms br ON b.booking_id = br.booking_id
JOIN rooms r ON br.room_id = r.room_id
JOIN room_types rt ON r.type_id = rt.type_id
JOIN hotels h ON r.hotel_id = h.hotel_id
JOIN users u ON b.guest_id = u.user_id
JOIN booking_statuses bs ON b.status_id = bs.status_id
WHERE b.booking_id = ?;

-- 4. Get housekeeping schedule for a specific date
SELECT hs.*, r.room_number, 
       CONCAT(u.first_name, ' ', u.last_name) as staff_name
FROM housekeeping_schedule hs
JOIN rooms r ON hs.room_id = r.room_id
JOIN users u ON hs.staff_id = u.user_id
JOIN housekeeping_staff hs_staff ON u.user_id = hs_staff.user_id
WHERE hs.scheduled_date = ?
AND r.hotel_id = ?;

-- 5. Get room booking statistics and revenue
SELECT r.room_number, rt.base_price, COUNT(br.booking_id) as total_bookings,
       SUM(p.amount) as total_revenue
FROM rooms r
JOIN room_types rt ON r.type_id = rt.type_id
LEFT JOIN booking_rooms br ON r.room_id = br.room_id
LEFT JOIN bookings b ON br.booking_id = b.booking_id
LEFT JOIN payments p ON b.booking_id = p.booking_id
WHERE b.check_out_date BETWEEN ? AND ?
GROUP BY r.room_id, r.room_number, rt.base_price;

-- DELETE Queries

-- 1. Cancel a booking (only if no payment)
DELETE FROM bookings 
WHERE booking_id = ? 
AND NOT EXISTS (SELECT 1 FROM payments WHERE booking_id = bookings.booking_id);

-- 2. Remove a room (only if no active bookings)
DELETE FROM rooms 
WHERE room_id = ?
AND room_id NOT IN (
    SELECT br.room_id 
    FROM booking_rooms br
    JOIN bookings b ON br.booking_id = b.booking_id
    WHERE b.status_id IN (2, 3)  -- CONFIRMED, CHECKED_IN
);

-- 3. Delete a housekeeping schedule
DELETE FROM housekeeping_schedule 
WHERE schedule_id = ? 
AND status_id = 1;  -- PENDING

-- INSERT Queries for Application Use

-- 1. Create a new booking
INSERT INTO bookings (
    booking_id, guest_id, check_in_date, 
    check_out_date, status_id, total_guests
) VALUES (?, ?, ?, ?, ?, ?);

-- 2. Add rooms to a booking
INSERT INTO booking_rooms (
    booking_id, room_id, guests_in_room
) VALUES (?, ?, ?);

-- 3. Add a new housekeeping schedule
INSERT INTO housekeeping_schedule (
    schedule_id, room_id, staff_id, scheduled_date, 
    status_id, created_by
) VALUES (?, ?, ?, ?, ?, ?);

-- 4. Record a payment
INSERT INTO payments (
    payment_id, booking_id, amount, processed_by
) VALUES (?, ?, ?, ?);

-- Receptionist Queries

-- Modify booking
UPDATE bookings 
SET check_in_date = ?,
    check_out_date = ?,
    total_guests = ?,
    status_id = ?
WHERE booking_id = ?
AND status_id IN (1, 2);  -- PENDING, CONFIRMED

-- View housekeepers availability
SELECT u.user_id, u.first_name, u.last_name,
       COUNT(hs.schedule_id) as pending_tasks
FROM users u
JOIN housekeeping_staff hs_staff ON u.user_id = hs_staff.user_id
JOIN staff s ON u.user_id = s.user_id
LEFT JOIN housekeeping_schedule hs ON u.user_id = hs.staff_id
    AND hs.scheduled_date = ? AND hs.status_id = 1  -- PENDING
WHERE s.hotel_id = ?
GROUP BY u.user_id, u.first_name, u.last_name;

-- Housekeeping Queries

-- View my cleaning schedule
SELECT hs.schedule_id, r.room_number, h.hotel_name, 
       hs.scheduled_date, rs.status_name
FROM housekeeping_schedule hs
JOIN rooms r ON hs.room_id = r.room_id
JOIN hotels h ON r.hotel_id = h.hotel_id
JOIN room_statuses rs ON hs.status_id = rs.status_id
WHERE hs.staff_id = ?
AND hs.scheduled_date >= CURRENT_DATE
ORDER BY hs.scheduled_date;

-- Update task status to completed
UPDATE housekeeping_schedule 
SET status_id = 2  -- COMPLETED
WHERE schedule_id = ?
AND staff_id = ?
AND status_id = 1;  -- PENDING

-- Administrator Queries

-- View most booked room types
SELECT rt.type_name, h.hotel_name, COUNT(*) as booking_count
FROM bookings b
JOIN booking_rooms br ON b.booking_id = br.booking_id
JOIN rooms r ON br.room_id = r.room_id
JOIN room_types rt ON r.type_id = rt.type_id
JOIN hotels h ON r.hotel_id = h.hotel_id
WHERE b.check_in_date BETWEEN ? AND ?
GROUP BY rt.type_id, h.hotel_id
ORDER BY booking_count DESC;

-- View all employees with their roles
SELECT u.user_id, u.first_name, u.last_name,
       CASE 
           WHEN a.user_id IS NOT NULL THEN 'ADMINISTRATOR'
           WHEN r.user_id IS NOT NULL THEN 'RECEPTIONIST'
           WHEN h.user_id IS NOT NULL THEN 'HOUSEKEEPING'
       END as role,
       h.hotel_name, s.salary
FROM users u
JOIN staff s ON u.user_id = s.user_id
JOIN hotels h ON s.hotel_id = h.hotel_id
LEFT JOIN administrator_staff a ON u.user_id = a.user_id
LEFT JOIN receptionist_staff r ON u.user_id = r.user_id
LEFT JOIN housekeeping_staff h ON u.user_id = h.user_id
ORDER BY h.hotel_name, role;

-- Add revenue report query
SELECT 
    h.hotel_name,
    DATE_FORMAT(b.check_out_date, '%Y-%m') as month,
    COUNT(DISTINCT b.booking_id) as total_bookings,
    SUM(p.amount) as total_revenue
FROM hotels h
JOIN rooms r ON h.hotel_id = r.hotel_id
JOIN booking_rooms br ON r.hotel_id = br.hotel_id 
    AND r.room_number = br.room_number
JOIN bookings b ON br.booking_id = b.booking_id
JOIN payments p ON b.booking_id = p.booking_id
WHERE b.status_id = 4  -- CHECKED_OUT
GROUP BY h.hotel_id, month
ORDER BY h.hotel_name, month DESC;

-- Add booking modification constraints
ALTER TABLE bookings
ADD CONSTRAINT valid_booking_modification
CHECK (
    CASE 
        WHEN status_id IN (3, 4) THEN FALSE  -- Cannot modify CHECKED_IN or CHECKED_OUT
        ELSE TRUE
    END
);

-- Add booking modification query
UPDATE bookings 
SET check_in_date = ?,
    check_out_date = ?,
    total_guests = ?,
    status_id = ?
WHERE booking_id = ?
AND status_id IN (1, 2)  -- Only PENDING or CONFIRMED
AND NOT EXISTS (
    SELECT 1 FROM payments 
    WHERE booking_id = bookings.booking_id
);
