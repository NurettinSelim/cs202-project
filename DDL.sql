-- Create the database
CREATE DATABASE hotel_management;
USE hotel_management;

-- Hotels table
CREATE TABLE hotels (
    hotel_id INT PRIMARY KEY,
    hotel_name VARCHAR(100) NOT NULL,
    address TEXT NOT NULL,
    phone VARCHAR(20) NOT NULL CHECK (phone REGEXP '^[0-9+][0-9-+]{9,19}$')
);

-- Users table
CREATE TABLE users (
    user_id INT PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    phone VARCHAR(20) CHECK (phone IS NULL OR phone REGEXP '^[0-9+][0-9-+]{9,19}$'),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Staff table (for employees)
CREATE TABLE staff (
    user_id INT PRIMARY KEY,
    hotel_id INT NOT NULL,
    salary DECIMAL(10,2) NOT NULL CHECK (salary >= 0),
    hire_date DATE NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (hotel_id) REFERENCES hotels(hotel_id)
);

-- Different types of staff
CREATE TABLE housekeeping_staff (
    user_id INT PRIMARY KEY,
    FOREIGN KEY (user_id) REFERENCES staff(user_id)
);

CREATE TABLE administrator_staff (
    user_id INT PRIMARY KEY,
    FOREIGN KEY (user_id) REFERENCES staff(user_id)
);

CREATE TABLE receptionist_staff (
    user_id INT PRIMARY KEY,
    FOREIGN KEY (user_id) REFERENCES staff(user_id)
);

-- Guests table
CREATE TABLE guests (
    user_id INT PRIMARY KEY,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- Room types table
CREATE TABLE room_types (
    type_id INT PRIMARY KEY,
    hotel_id INT NOT NULL,
    type_name VARCHAR(50) NOT NULL,
    base_price DECIMAL(10,2) NOT NULL CHECK (base_price > 0),
    capacity INT NOT NULL CHECK (capacity BETWEEN 1 AND 10),
    bed_count INT NOT NULL CHECK (bed_count BETWEEN 1 AND 6),
    FOREIGN KEY (hotel_id) REFERENCES hotels(hotel_id),
    UNIQUE (hotel_id, type_name)
);

-- Room statuses table
CREATE TABLE room_statuses (
    status_id INT PRIMARY KEY,
    status_name VARCHAR(20) NOT NULL UNIQUE
);

-- Rooms table
CREATE TABLE rooms (
    hotel_id INT,
    room_number VARCHAR(10),
    type_id INT NOT NULL,
    status_id INT NOT NULL DEFAULT 1,
    PRIMARY KEY (hotel_id, room_number),
    FOREIGN KEY (hotel_id) REFERENCES hotels(hotel_id),
    FOREIGN KEY (type_id) REFERENCES room_types(type_id),
    FOREIGN KEY (status_id) REFERENCES room_statuses(status_id)
);

-- Booking statuses table
CREATE TABLE booking_statuses (
    status_id INT PRIMARY KEY,
    status_name VARCHAR(20) NOT NULL UNIQUE
);

-- Bookings table
CREATE TABLE bookings (
    booking_id INT PRIMARY KEY,
    guest_id INT NOT NULL,
    check_in_date DATE NOT NULL CHECK (check_in_date >= CURRENT_DATE),
    check_out_date DATE NOT NULL CHECK (check_out_date > check_in_date),
    status_id INT NOT NULL,
    total_guests INT NOT NULL CHECK (total_guests > 0),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    confirmed_by INT,
    FOREIGN KEY (guest_id) REFERENCES users(user_id),
    FOREIGN KEY (status_id) REFERENCES booking_statuses(status_id),
    FOREIGN KEY (confirmed_by) REFERENCES users(user_id),
    CONSTRAINT check_total_guests CHECK (
        total_guests = (
            SELECT SUM(guests_in_room)
            FROM booking_rooms
            WHERE booking_rooms.booking_id = bookings.booking_id
        )
    )
);

-- Booking rooms table
CREATE TABLE booking_rooms (
    booking_id INT,
    hotel_id INT,
    room_number VARCHAR(10),
    guests_in_room INT NOT NULL CHECK (guests_in_room > 0),
    PRIMARY KEY (booking_id, hotel_id, room_number),
    FOREIGN KEY (booking_id) REFERENCES bookings(booking_id),
    FOREIGN KEY (hotel_id, room_number) REFERENCES rooms(hotel_id, room_number),
    CONSTRAINT check_room_capacity CHECK (
        guests_in_room <= (
            SELECT rt.capacity 
            FROM rooms r
            JOIN room_types rt ON r.type_id = rt.type_id
            WHERE r.hotel_id = booking_rooms.hotel_id 
            AND r.room_number = booking_rooms.room_number
        )
    ),
    CONSTRAINT check_room_availability UNIQUE (hotel_id, room_number, booking_id)
);

-- Payments table
CREATE TABLE payments (
    booking_id INT,
    payment_number INT,
    amount DECIMAL(10,2) NOT NULL CHECK (amount > 0),
    payment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    processed_by INT NOT NULL,
    PRIMARY KEY (booking_id, payment_number),
    FOREIGN KEY (booking_id) REFERENCES bookings(booking_id),
    FOREIGN KEY (processed_by) REFERENCES receptionist_staff(user_id)
);

-- Housekeeping schedule table
CREATE TABLE housekeeping_schedule (
    schedule_id INT PRIMARY KEY,
    hotel_id INT,
    room_number VARCHAR(10),
    staff_id INT NOT NULL,
    scheduled_date DATE NOT NULL CHECK (scheduled_date >= CURRENT_DATE),
    status_id INT NOT NULL,
    created_by INT NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (hotel_id, room_number) REFERENCES rooms(hotel_id, room_number),
    FOREIGN KEY (staff_id) REFERENCES housekeeping_staff(user_id),
    FOREIGN KEY (created_by) REFERENCES users(user_id),
    FOREIGN KEY (status_id) REFERENCES room_statuses(status_id)
);
