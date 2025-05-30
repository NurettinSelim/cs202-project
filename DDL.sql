DROP DATABASE IF EXISTS hotel_management;

-- create the database
CREATE DATABASE hotel_management;
USE hotel_management;

-- address table
CREATE TABLE addresses
(
    address_id  INT AUTO_INCREMENT PRIMARY KEY,
    street      VARCHAR(100) NOT NULL,
    city        VARCHAR(50)  NOT NULL,
    country     VARCHAR(50)  NOT NULL,
    postal_code VARCHAR(20)  NOT NULL,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- hotels table
CREATE TABLE hotels
(
    hotel_id   INT AUTO_INCREMENT PRIMARY KEY,
    hotel_name VARCHAR(100) NOT NULL,
    address_id INT          NOT NULL,
    phone      VARCHAR(20)  NOT NULL CHECK (phone REGEXP '^[0-9+][0-9-+]{9,19}$'),
    FOREIGN KEY (address_id) REFERENCES addresses (address_id)
);

-- users table
CREATE TABLE users
(
    user_id    INT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name  VARCHAR(50) NOT NULL,
    phone      VARCHAR(20) CHECK (phone IS NULL OR phone REGEXP '^[0-9+][0-9-+]{9,19}$'),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- staff table (for employees)
CREATE TABLE staff
(
    user_id   INT PRIMARY KEY,
    hotel_id  INT            NOT NULL,
    salary    DECIMAL(10, 2) NOT NULL CHECK (salary >= 0),
    hire_date DATE           NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (user_id),
    FOREIGN KEY (hotel_id) REFERENCES hotels (hotel_id)
);

-- housekeeping staff table
CREATE TABLE housekeeping_staff
(
    user_id INT PRIMARY KEY,
    FOREIGN KEY (user_id) REFERENCES staff (user_id)
);

-- administrator staff table
CREATE TABLE administrator_staff
(
    user_id INT PRIMARY KEY,
    FOREIGN KEY (user_id) REFERENCES staff (user_id)
);

-- receptionist staff table
CREATE TABLE receptionist_staff
(
    user_id INT PRIMARY KEY,
    FOREIGN KEY (user_id) REFERENCES staff (user_id)
);

-- guests table
CREATE TABLE guests
(
    user_id INT PRIMARY KEY,
    FOREIGN KEY (user_id) REFERENCES users (user_id)
);

-- room types table
CREATE TABLE room_types
(
    type_id    INT AUTO_INCREMENT PRIMARY KEY,
    hotel_id   INT            NOT NULL,
    type_name  VARCHAR(50)    NOT NULL,
    base_price DECIMAL(10, 2) NOT NULL CHECK (base_price > 0),
    capacity   INT            NOT NULL CHECK (capacity BETWEEN 1 AND 10),
    bed_count  INT            NOT NULL CHECK (bed_count BETWEEN 1 AND 6),
    FOREIGN KEY (hotel_id) REFERENCES hotels (hotel_id),
    UNIQUE (hotel_id, type_name)
);

-- room statuses table
CREATE TABLE room_statuses
(
    status_id   INT AUTO_INCREMENT PRIMARY KEY,
    status_name VARCHAR(20) NOT NULL UNIQUE
);

-- rooms table
CREATE TABLE rooms
(
    hotel_id    INT,
    room_number VARCHAR(10),
    type_id     INT NOT NULL,
    status_id   INT NOT NULL DEFAULT 1,
    PRIMARY KEY (hotel_id, room_number),
    FOREIGN KEY (hotel_id) REFERENCES hotels (hotel_id),
    FOREIGN KEY (type_id) REFERENCES room_types (type_id),
    FOREIGN KEY (status_id) REFERENCES room_statuses (status_id)
);

-- booking statuses table
CREATE TABLE booking_statuses
(
    status_id   INT AUTO_INCREMENT PRIMARY KEY,
    status_name VARCHAR(20) NOT NULL UNIQUE
);

-- bookings table
CREATE TABLE bookings
(
    booking_id     INT AUTO_INCREMENT PRIMARY KEY,
    guest_id       INT  NOT NULL,
    check_in_date  DATE NOT NULL,
    check_out_date DATE NOT NULL,
    status_id      INT  NOT NULL,
    total_guests   INT  NOT NULL CHECK (total_guests > 0),
    created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    confirmed_by   INT,
    FOREIGN KEY (guest_id) REFERENCES users (user_id),
    FOREIGN KEY (status_id) REFERENCES booking_statuses (status_id),
    FOREIGN KEY (confirmed_by) REFERENCES users (user_id)
);

-- booking rooms table
CREATE TABLE booking_rooms
(
    booking_id     INT,
    hotel_id       INT,
    room_number    VARCHAR(10),
    guests_in_room INT NOT NULL CHECK (guests_in_room > 0),
    PRIMARY KEY (booking_id, hotel_id, room_number),
    FOREIGN KEY (booking_id) REFERENCES bookings (booking_id),
    FOREIGN KEY (hotel_id, room_number) REFERENCES rooms (hotel_id, room_number),
    CONSTRAINT check_room_availability UNIQUE (hotel_id, room_number, booking_id)
);

-- payments table
CREATE TABLE payments
(
    booking_id     INT,
    payment_number INT,
    amount         DECIMAL(10, 2) NOT NULL CHECK (amount > 0),
    payment_date   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    processed_by   INT            NOT NULL,
    PRIMARY KEY (booking_id, payment_number),
    FOREIGN KEY (booking_id) REFERENCES bookings (booking_id),
    FOREIGN KEY (processed_by) REFERENCES receptionist_staff (user_id)
);

-- housekeeping schedule table
CREATE TABLE housekeeping_schedule
(
    schedule_id    INT AUTO_INCREMENT,
    hotel_id       INT,
    room_number    VARCHAR(10),
    scheduled_date DATE NOT NULL,
    staff_id       INT  NOT NULL,
    status_id      INT  NOT NULL,
    created_by     INT  NOT NULL,
    updated_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (schedule_id),
    FOREIGN KEY (hotel_id, room_number) REFERENCES rooms (hotel_id, room_number),
    FOREIGN KEY (staff_id) REFERENCES housekeeping_staff (user_id),
    FOREIGN KEY (created_by) REFERENCES users (user_id),
    FOREIGN KEY (status_id) REFERENCES room_statuses (status_id)
);

-- triggers for validation

DELIMITER //

-- booking date check
CREATE TRIGGER before_booking_insert
    BEFORE INSERT
    ON bookings
    FOR EACH ROW
BEGIN
    IF NEW.check_in_date < CURRENT_DATE THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Check-in date cannot be in the past';
    END IF;
    IF NEW.check_out_date <= NEW.check_in_date THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Check-out date must be after check-in date';
    END IF;
END//

CREATE TRIGGER before_booking_update
    BEFORE UPDATE
    ON bookings
    FOR EACH ROW
BEGIN
    IF NEW.check_in_date < CURRENT_DATE THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Check-in date cannot be in the past';
    END IF;
    IF NEW.check_out_date <= NEW.check_in_date THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Check-out date must be after check-in date';
    END IF;
END//

-- room capacity check
CREATE TRIGGER before_booking_room_insert
    BEFORE INSERT
    ON booking_rooms
    FOR EACH ROW
BEGIN
    DECLARE room_capacity INT;

    SELECT rt.capacity
    INTO room_capacity
    FROM rooms r
             JOIN room_types rt ON r.type_id = rt.type_id
    WHERE r.hotel_id = NEW.hotel_id
      AND r.room_number = NEW.room_number;

    IF NEW.guests_in_room > room_capacity THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Number of guests exceeds room capacity';
    END IF;
END//

CREATE TRIGGER before_booking_room_update
    BEFORE UPDATE
    ON booking_rooms
    FOR EACH ROW
BEGIN
    DECLARE room_capacity INT;

    SELECT rt.capacity
    INTO room_capacity
    FROM rooms r
             JOIN room_types rt ON r.type_id = rt.type_id
    WHERE r.hotel_id = NEW.hotel_id
      AND r.room_number = NEW.room_number;

    IF NEW.guests_in_room > room_capacity THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Number of guests exceeds room capacity';
    END IF;
END//

-- booking total guests check
CREATE TRIGGER after_booking_room_insert
    AFTER INSERT
    ON booking_rooms
    FOR EACH ROW
BEGIN
    DECLARE total INT;
    SELECT SUM(guests_in_room)
    INTO total
    FROM booking_rooms
    WHERE booking_id = NEW.booking_id;

    UPDATE bookings
    SET total_guests = total
    WHERE booking_id = NEW.booking_id;
END//

CREATE TRIGGER after_booking_room_update
    AFTER UPDATE
    ON booking_rooms
    FOR EACH ROW
BEGIN
    DECLARE total INT;
    SELECT SUM(guests_in_room)
    INTO total
    FROM booking_rooms
    WHERE booking_id = NEW.booking_id;

    UPDATE bookings
    SET total_guests = total
    WHERE booking_id = NEW.booking_id;
END//

CREATE TRIGGER after_booking_room_delete
    AFTER DELETE
    ON booking_rooms
    FOR EACH ROW
BEGIN
    DECLARE total INT;
    SELECT SUM(guests_in_room)
    INTO total
    FROM booking_rooms
    WHERE booking_id = OLD.booking_id;

    UPDATE bookings
    SET total_guests = total
    WHERE booking_id = OLD.booking_id;
END//

-- housekeeping schedule date check
CREATE TRIGGER before_housekeeping_insert
    BEFORE INSERT
    ON housekeeping_schedule
    FOR EACH ROW
BEGIN
    IF NEW.scheduled_date < CURRENT_DATE THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Scheduled date cannot be in the past';
    END IF;
END//

CREATE TRIGGER before_housekeeping_update
    BEFORE UPDATE
    ON housekeeping_schedule
    FOR EACH ROW
BEGIN
    IF NEW.scheduled_date < CURRENT_DATE THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Scheduled date cannot be in the past';
    END IF;
END//

-- booking cancellation check
CREATE TRIGGER before_booking_update_cancel_check
    BEFORE UPDATE
    ON bookings
    FOR EACH ROW
BEGIN
    DECLARE has_payments BOOLEAN;

    -- 5 means cancelled
    IF NEW.status_id = 5 AND OLD.status_id != 5 THEN
        SELECT EXISTS (SELECT 1
                       FROM payments
                       WHERE booking_id = OLD.booking_id)
        INTO has_payments;

        IF has_payments THEN
            SIGNAL SQLSTATE '45000'
                SET MESSAGE_TEXT = 'Cannot cancel booking with existing payments';
        END IF;
    END IF;
END//

-- Check for overbooking
CREATE TRIGGER check_overbooked
    BEFORE INSERT
    ON booking_rooms
    FOR EACH ROW
BEGIN
    DECLARE booking_start DATE;
    DECLARE booking_end DATE;

    SELECT check_in_date, check_out_date
    INTO booking_start, booking_end
    FROM bookings
    WHERE booking_id = NEW.booking_id;

    IF EXISTS (SELECT 1
               FROM booking_rooms br
                        JOIN bookings b ON br.booking_id = b.booking_id
               WHERE br.hotel_id = NEW.hotel_id
                 AND br.room_number = NEW.room_number
                 -- 2 and 3 are CONFIRMED and CHECKED_IN
                 AND b.status_id IN (2, 3)
                 AND NOT (
                   booking_end <= b.check_in_date OR
                   booking_start >= b.check_out_date
                   )) THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Room is already booked for these dates';
    END IF;
END//

-- checkout payment check
CREATE TRIGGER before_checkout_payment_check
    BEFORE UPDATE
    ON bookings
    FOR EACH ROW
BEGIN
    DECLARE has_payments BOOLEAN;

    -- Check if booking has any payments
    SELECT EXISTS (SELECT 1
                   FROM payments
                   WHERE booking_id = NEW.booking_id)
    INTO has_payments;

    -- if trying to check out and has no payments
    IF NEW.status_id = 4 AND NOT has_payments THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Cannot check out: No payments found';
    END IF;
END//

-- Room deletion check trigger
CREATE TRIGGER before_room_delete
    BEFORE DELETE
    ON rooms
    FOR EACH ROW
BEGIN
    IF EXISTS (SELECT 1
               FROM booking_rooms br
                        JOIN bookings b ON br.booking_id = b.booking_id
               WHERE br.hotel_id = OLD.hotel_id
                 AND br.room_number = OLD.room_number
                 AND b.status_id IN (2, 3) -- 2 means confirmed, 3 means checked in
    ) THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Cannot delete room with active bookings';
    END IF;

    IF EXISTS (SELECT 1
               FROM housekeeping_schedule
               WHERE hotel_id = OLD.hotel_id
                 AND room_number = OLD.room_number
                 AND scheduled_date >= CURRENT_DATE
                 AND status_id = 1 -- 1 means pending
    ) THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Cannot delete room with pending housekeeping tasks';
    END IF;
END//

DELIMITER ;

-- Unique index for hotels table
CREATE UNIQUE INDEX idx_hotel_name_address ON hotels(hotel_name, address_id);

-- Index for users table
CREATE INDEX idx_users_phone ON users(phone);

-- Index for staff table
CREATE INDEX idx_staff_hotelID ON staff(hotel_id);

-- Index for room_types table
CREATE INDEX idx_room_types_hotelID ON room_types(hotel_id);

-- Unique index for room types (hotel and type name)
CREATE UNIQUE INDEX idx_room_type_name ON room_types(hotel_id, type_name);

-- Index for rooms table
CREATE INDEX idx_rooms_statusID ON rooms(status_id);
CREATE INDEX idx_rooms_typeID ON rooms(type_id);

-- Index for bookings table
CREATE INDEX idx_bookings_guestID ON bookings(guest_id);
CREATE INDEX idx_bookings_check_in_date ON bookings(check_in_date);
CREATE INDEX idx_bookings_statusID ON bookings(status_id);

-- Index for booking_rooms table
CREATE INDEX idx_booking_rooms_bookingID ON booking_rooms(booking_id);

-- Index for payments table
CREATE INDEX idx_payments_bookingID ON payments(booking_id);
CREATE INDEX idx_payments_processed_by ON payments(processed_by);

-- Index for housekeeping_schedule table
CREATE INDEX idx_housekeeping_hotelID_room ON housekeeping_schedule(hotel_id, room_number);
CREATE INDEX idx_housekeeping_staffID ON housekeeping_schedule(staff_id);
CREATE INDEX idx_housekeeping_created_by ON housekeeping_schedule(created_by);

