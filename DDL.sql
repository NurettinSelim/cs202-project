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

-- User types enum
CREATE TABLE user_types (
    type_id INT PRIMARY KEY,
    type_name VARCHAR(20) NOT NULL UNIQUE
);

-- Users table
CREATE TABLE users (
    user_id INT PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    phone VARCHAR(20) CHECK (phone IS NULL OR phone REGEXP '^[0-9+][0-9-+]{9,19}$'),
    type_id INT NOT NULL,
    hotel_id INT,
    salary DECIMAL(10,2) CHECK (salary IS NULL OR salary >= 0),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (type_id) REFERENCES user_types(type_id),
    FOREIGN KEY (hotel_id) REFERENCES hotels(hotel_id)
);

-- Room type categories (base room types)
CREATE TABLE room_type_categories (
    category_id INT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    capacity INT NOT NULL CHECK (capacity BETWEEN 1 AND 10),
    description TEXT
);

-- Hotel-specific room types
CREATE TABLE hotel_room_types (
    type_id INT PRIMARY KEY,
    hotel_id INT NOT NULL,
    category_id INT NOT NULL,
    FOREIGN KEY (hotel_id) REFERENCES hotels(hotel_id),
    FOREIGN KEY (category_id) REFERENCES room_type_categories(category_id),
    UNIQUE (hotel_id, category_id)
);

-- Floor information table
CREATE TABLE floors (
    floor_id INT PRIMARY KEY,
    hotel_id INT NOT NULL,
    floor_number INT NOT NULL CHECK (floor_number BETWEEN -5 AND 20),
    UNIQUE(hotel_id, floor_number),
    FOREIGN KEY (hotel_id) REFERENCES hotels(hotel_id)
);

-- Rooms table
CREATE TABLE rooms (
    room_id INT PRIMARY KEY,
    room_number VARCHAR(10) NOT NULL,
    type_id INT NOT NULL,
    floor_id INT NOT NULL,
    price DECIMAL(10,2) NOT NULL CHECK (price > 0),
    status ENUM('AVAILABLE', 'OCCUPIED', 'MAINTENANCE', 'CLEANING') DEFAULT 'AVAILABLE',
    UNIQUE(floor_id, room_number),
    FOREIGN KEY (type_id) REFERENCES hotel_room_types(type_id),
    FOREIGN KEY (floor_id) REFERENCES floors(floor_id)
);

-- Booking status enum
CREATE TABLE booking_statuses (
    status_id INT PRIMARY KEY,
    status_name VARCHAR(20) NOT NULL UNIQUE
);

-- Bookings table
CREATE TABLE bookings (
    booking_id INT PRIMARY KEY,
    guest_id INT NOT NULL,
    room_id INT NOT NULL,
    check_in_date DATE NOT NULL CHECK (check_in_date >= CURRENT_DATE),
    check_out_date DATE NOT NULL CHECK (check_out_date > check_in_date),
    status_id INT NOT NULL,
    number_of_guests INT NOT NULL CHECK (number_of_guests BETWEEN 1 AND 10),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    confirmed_by INT,
    FOREIGN KEY (guest_id) REFERENCES users(user_id),
    FOREIGN KEY (room_id) REFERENCES rooms(room_id),
    FOREIGN KEY (status_id) REFERENCES booking_statuses(status_id),
    FOREIGN KEY (confirmed_by) REFERENCES users(user_id),
    -- Additional check to ensure number_of_guests doesn't exceed room capacity
    CONSTRAINT check_room_capacity CHECK (
        number_of_guests <= (
            SELECT rtc.capacity 
            FROM rooms r
            JOIN hotel_room_types hrt ON r.type_id = hrt.type_id
            JOIN room_type_categories rtc ON hrt.category_id = rtc.category_id
            WHERE r.room_id = bookings.room_id
        )
    )
);

-- Payments table
CREATE TABLE payments (
    payment_id INT PRIMARY KEY,
    booking_id INT NOT NULL,
    amount DECIMAL(10,2) NOT NULL CHECK (amount > 0),
    payment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    payment_method ENUM('CASH', 'CARD') NOT NULL,
    processed_by INT NOT NULL,
    FOREIGN KEY (booking_id) REFERENCES bookings(booking_id),
    FOREIGN KEY (processed_by) REFERENCES users(user_id)
);

-- Housekeeping schedule table
CREATE TABLE housekeeping_schedule (
    schedule_id INT PRIMARY KEY,
    room_id INT NOT NULL,
    staff_id INT NOT NULL,
    scheduled_date DATE NOT NULL CHECK (scheduled_date >= CURRENT_DATE),
    status ENUM('PENDING', 'COMPLETED', 'CANCELLED') DEFAULT 'PENDING',
    created_by INT NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (room_id) REFERENCES rooms(room_id),
    FOREIGN KEY (staff_id) REFERENCES users(user_id),
    FOREIGN KEY (created_by) REFERENCES users(user_id),
    -- Check to ensure staff is housekeeping type
    CONSTRAINT check_staff_type CHECK (
        staff_id IN (
            SELECT user_id 
            FROM users 
            WHERE type_id = (SELECT type_id FROM user_types WHERE type_name = 'HOUSEKEEPING')
        )
    )
);

-- Revenue reports table
CREATE TABLE revenue_reports (
    report_id INT PRIMARY KEY,
    hotel_id INT NOT NULL,
    report_date DATE NOT NULL,
    total_revenue DECIMAL(10,2) NOT NULL CHECK (total_revenue >= 0),
    generated_by INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (hotel_id) REFERENCES hotels(hotel_id),
    FOREIGN KEY (generated_by) REFERENCES users(user_id)
);
  