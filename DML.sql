-- Populate addresses
INSERT INTO addresses (street, city, country, postal_code)
VALUES ('Hastane Caddesi No: 15', 'Kaş', 'Turkey', '07580'),
       ('Hacıfeyzullah, Yılancı Burnu Sk. No:17', 'Kuşadası', 'Turkey', '09400');

-- Populate hotels
INSERT INTO hotels (hotel_name, address_id, phone)
VALUES ('Kaş Otel', 1, '+905051234567'),
       ('Kuşadası Otel', 2, '+905051234568');

-- Populate room_statuses
INSERT INTO room_statuses (status_name)
VALUES ('AVAILABLE'),
       ('OCCUPIED'),
       ('MAINTENANCE'),
       ('CLEANING');

-- Populate room_types
INSERT INTO room_types (hotel_id, type_name, base_price, capacity, bed_count)
VALUES (1, 'Tek Kişilik Oda', 100.00, 1, 1),
       (1, 'Çift Kişilik Oda', 150.00, 2, 1),
       (1, 'Aile Odası', 300.00, 4, 3),
       (2, 'Tek Kişilik Oda', 120.00, 1, 1),
       (2, 'Çift Kişilik Oda', 180.00, 2, 2);

-- Populate rooms
INSERT INTO rooms (hotel_id, room_number, type_id, status_id)
VALUES (1, '101', 1, 1),
       (1, '102', 2, 1),
       (1, '201', 3, 1),
       (2, '101', 4, 1);

-- Populate booking_statuses
INSERT INTO booking_statuses (status_name)
VALUES ('PENDING'),
       ('CONFIRMED'),
       ('CHECKED_IN'),
       ('CHECKED_OUT'),
       ('CANCELLED');

-- Sample users
INSERT INTO users (first_name, last_name, phone, created_at)
VALUES ('Selim', 'Özyılmaz', '+905051234569', CURRENT_TIMESTAMP),
       ('İpek', 'Debreli', '+905051234570', CURRENT_TIMESTAMP),
       ('Ayşe', 'Yılmaz', '+905051234571', CURRENT_TIMESTAMP),
       ('Mehmet', 'Kaya', '+905051234572', CURRENT_TIMESTAMP),
       ('Zeynep', 'Demir', '+905051234573', CURRENT_TIMESTAMP),
       ('Can', 'Yücel', '+905051234574', CURRENT_TIMESTAMP);

-- Staff records
INSERT INTO staff (user_id, hotel_id, salary, hire_date)
VALUES (1, 1, 5000, CURRENT_DATE),
       (2, 1, 4000, CURRENT_DATE),
       (3, 1, 3500, CURRENT_DATE),
       (4, 1, 3500, CURRENT_DATE);

-- Staff type assignments
INSERT INTO administrator_staff (user_id)
VALUES (1),
       (2);
INSERT INTO receptionist_staff (user_id)
VALUES (3);
INSERT INTO housekeeping_staff (user_id)
VALUES (4);

-- Guest records
INSERT INTO guests (user_id)
VALUES (5),
       (6);

-- SELECT Queries

-- Guests can check in if it is confirmed by a receptionist.
SELECT b.*,
       r.room_number,
       rt.base_price,
       h.hotel_name,
       CONCAT(u.first_name, ' ', u.last_name) as guest_name,
       bs.status_name,
       br.guests_in_room
FROM bookings b
         JOIN booking_rooms br ON b.booking_id = br.booking_id
         JOIN rooms r ON br.hotel_id = r.hotel_id AND br.room_number = r.room_number
         JOIN room_types rt ON r.type_id = rt.type_id
         JOIN hotels h ON r.hotel_id = h.hotel_id
         JOIN users u ON b.guest_id = u.user_id
         JOIN booking_statuses bs ON b.status_id = bs.status_id
WHERE b.booking_id = ?;

-- Receptionists can list the availability of the rooms in the system.
SELECT r.hotel_id, r.room_number, h.hotel_name, rt.type_name, rt.base_price
FROM rooms r
         JOIN room_types rt ON r.type_id = rt.type_id
         JOIN hotels h ON r.hotel_id = h.hotel_id
WHERE r.status_id = 1 -- AVAILABLE
  AND NOT EXISTS (SELECT 1
                  FROM bookings b
                           JOIN booking_rooms br ON b.booking_id = br.booking_id
                  WHERE br.hotel_id = r.hotel_id
                    AND br.room_number = r.room_number
                    AND (b.check_in_date <= ? AND b.check_out_date >= ?)
                    AND b.status_id IN (2, 3) -- CONFIRMED, CHECKED_IN
)
  AND rt.capacity >= ?;

-- Receptionists can list bookings requested by guests
SELECT b.booking_id,
       CONCAT(u.first_name, ' ', u.last_name) as guest_name,
       u.phone                                as guest_phone,
       b.check_in_date,
       b.check_out_date,
       b.total_guests,
       bs.status_name                         as booking_status,
       h.hotel_name,
       GROUP_CONCAT(r.room_number)            as booked_rooms
FROM bookings b
         JOIN users u ON b.guest_id = u.user_id
         JOIN booking_statuses bs ON b.status_id = bs.status_id
         JOIN booking_rooms br ON b.booking_id = br.booking_id
         JOIN rooms r ON br.hotel_id = r.hotel_id AND br.room_number = r.room_number
         JOIN hotels h ON r.hotel_id = h.hotel_id
GROUP BY b.booking_id
ORDER BY b.check_in_date DESC;

-- Receptionists can confirm room bookings
UPDATE bookings
SET status_id = 2 -- CONFIRMED
WHERE booking_id = ?

-- Housekeeping can only view room availability but nothing about Guest information.
SELECT h.hotel_name,
       r.room_number,
       rt.type_name,
       rs.status_name as room_status,
       hs.scheduled_date,
       hs.status_id   as cleaning_status
FROM rooms r
         JOIN hotels h ON r.hotel_id = h.hotel_id
         JOIN room_types rt ON r.type_id = rt.type_id
         JOIN room_statuses rs ON r.status_id = rs.status_id
         JOIN housekeeping_schedule hs ON r.hotel_id = hs.hotel_id
    AND r.room_number = hs.room_number
    AND hs.scheduled_date = CURRENT_DATE
WHERE r.hotel_id = ? -- Filter by specific hotel
ORDER BY r.room_number;

-- Guest Menu
-- Add New Booking
INSERT INTO bookings (guest_id, check_in_date,
                      check_out_date, status_id, total_guests)
VALUES (?, ?, ?, ?, ?);
INSERT INTO booking_rooms (booking_id, hotel_id, room_number, guests_in_room)
VALUES (?, ?, ?, ?);

-- View Available Rooms
SELECT r.room_number,
       rt.type_name,
       rt.base_price
FROM rooms r
         JOIN room_types rt ON r.type_id = rt.type_id
WHERE NOT EXISTS (SELECT 1
                  FROM bookings b
                           JOIN booking_rooms br ON b.booking_id = br.booking_id
                  WHERE br.room_number = r.room_number
                    AND b.status_id IN (2, 3) -- CONFIRMED or CHECKED_IN
                    AND b.check_in_date < ?   -- check_out_date
                    AND b.check_out_date > ? -- check_in_date
);

-- View My Bookings
SELECT b.*,
       r.room_number,
       rt.base_price,
       h.hotel_name,
       CONCAT(u.first_name, ' ', u.last_name) as guest_name,
       bs.status_name,
       br.guests_in_room
FROM bookings b
         JOIN booking_rooms br ON b.booking_id = br.booking_id
         JOIN rooms r ON br.hotel_id = r.hotel_id AND br.room_number = r.room_number
         JOIN room_types rt ON r.type_id = rt.type_id
         JOIN hotels h ON r.hotel_id = h.hotel_id
         JOIN users u ON b.guest_id = u.user_id
         JOIN booking_statuses bs ON b.status_id = bs.status_id
WHERE b.guest_id = ?;

-- Cancel Booking
UPDATE bookings
SET status_id = 5 -- CANCELLED
WHERE booking_id = ?;


-- Administrator Menu
-- Add Room
INSERT INTO rooms (hotel_id, room_number, type_id, status_id)
VALUES (?, ?, ?, ?);

-- Delete Room
DELETE
FROM rooms
WHERE hotel_id = ?
  AND room_number = ?;

-- Manage Room Status
UPDATE rooms
SET status_id = ?
WHERE hotel_id = ?
  AND room_number = ?;

-- Add User
INSERT INTO users (first_name, last_name, phone, created_at)
VALUES (?, ?, ?, CURRENT_TIMESTAMP);

-- View User Accounts
SELECT *
FROM users;

-- Generate Revenue Report
SELECT COUNT(DISTINCT b.booking_id) as total_bookings,
       SUM(p.amount)                as total_revenue,
       AVG(p.amount)                as average_revenue_per_booking
FROM hotels h
         JOIN rooms r ON h.hotel_id = r.hotel_id
         JOIN room_types rt ON r.type_id = rt.type_id
         JOIN booking_rooms br ON r.hotel_id = br.hotel_id AND r.room_number = br.room_number
         JOIN bookings b ON br.booking_id = b.booking_id
         JOIN payments p ON b.booking_id = p.booking_id
WHERE b.status_id = 4 -- CHECKED_OUT
  AND h.hotel_id = ?;

-- View All Booking Records
SELECT b.*, u.*, bs.status_name
FROM bookings b
         JOIN users u ON b.guest_id = u.user_id
         JOIN booking_statuses bs ON b.status_id = bs.status_id

-- View All Housekeeping Records
SELECT *
FROM housekeeping_schedule;

-- View Most Booked Room Types
SELECT rt.type_name, COUNT(*) as booking_count
FROM bookings b
         JOIN booking_rooms br ON b.booking_id = br.booking_id
         JOIN rooms r ON br.hotel_id = r.hotel_id AND br.room_number = r.room_number
         JOIN room_types rt ON r.type_id = rt.type_id
WHERE b.check_in_date BETWEEN ? AND ?
  AND h.hotel_id = ?
GROUP BY rt.type_id
ORDER BY booking_count DESC;

-- View All the Employees with Their Role
SELECT u.*, 'ADMINISTRATOR' as role
FROM users u
         JOIN administrator_staff a ON u.user_id = a.user_id

UNION

SELECT u.*, 'RECEPTIONIST' as role
FROM users u
         JOIN receptionist_staff r ON u.user_id = r.user_id

UNION

SELECT u.*, 'HOUSEKEEPING' as role
FROM users u
         JOIN housekeeping_staff h ON u.user_id = h.user_id;

-- Receptionist Menu
-- Add New Booking
INSERT INTO bookings (guest_id, check_in_date,
                      check_out_date, status_id, total_guests)
VALUES (?, ?, ?, ?, ?);
INSERT INTO booking_rooms (booking_id, hotel_id, room_number, guests_in_room)
VALUES (?, ?, ?, ?);

-- Modify Booking
UPDATE bookings
SET check_in_date  = ?,
    check_out_date = ?,
    total_guests   = ?,
    status_id      = ?
WHERE booking_id = ?;

-- Delete Booking
DELETE
FROM bookings
WHERE booking_id = ?;

-- View Bookings
SELECT *
FROM bookings;

-- Process Payment
INSERT INTO payments (booking_id, payment_number, amount, processed_by)
VALUES (?, 1, ?, ?);
UPDATE bookings
SET status_id = 4
WHERE booking_id = ?;

-- Assign Housekeeping Task
INSERT INTO housekeeping_schedule (hotel_id,
                                   room_number,
                                   scheduled_date,
                                   staff_id,
                                   status_id,
                                   created_by)
VALUES (?, ?, ?, ?, ?, ?);

-- View All Housekeepers and Their Availability
SELECT hs_staff.user_id,
       u.first_name,
       u.last_name,
       u.phone,
       h.hotel_name,
       hs.room_number,
       hs.scheduled_date,
       rs.status_name
FROM housekeeping_staff hs_staff
         JOIN users u ON hs_staff.user_id = u.user_id
         JOIN staff s ON hs_staff.user_id = s.user_id
         JOIN hotels h ON s.hotel_id = h.hotel_id
         LEFT JOIN housekeeping_schedule hs ON h.hotel_id = hs.hotel_id
    AND hs.scheduled_date = CURRENT_DATE
         LEFT JOIN room_statuses rs ON hs.status_id = rs.status_id;

-- Housekeeping Menu
-- View Pending Housekeeping Tasks
SELECT *
FROM housekeeping_schedule
WHERE status_id = 1;

-- View Completed Housekeeping Tasks
SELECT *
FROM housekeeping_schedule
WHERE status_id = 2;

-- Update Task Status to Completed
UPDATE housekeeping_schedule
SET status_id = 2
WHERE hotel_id = ?
  AND room_number = ?
  AND scheduled_date = ?;

-- View My Cleaning Schedule
SELECT *
FROM housekeeping_schedule
WHERE user_id = ?;