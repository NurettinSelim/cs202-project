-- Create the database
CREATE DATABASE hotel_management;
USE hotel_management;

-- Hotels table
CREATE TABLE hotels (
    hotel_id INT PRIMARY KEY AUTO_INCREMENT,
    hotel_name VARCHAR(100) NOT NULL,
    address TEXT NOT NULL,
    phone VARCHAR(20) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE
);

-- User types enum
CREATE TABLE user_types (
    type_id INT PRIMARY KEY,
    type_name VARCHAR(20) NOT NULL UNIQUE
);

-- Users table
CREATE TABLE users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(20),
    type_id INT NOT NULL,
    hotel_id INT,
    hire_date DATE,
    salary DECIMAL(10,2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (type_id) REFERENCES user_types(type_id),
    FOREIGN KEY (hotel_id) REFERENCES hotels(hotel_id)
);

-- Room type categories (base room types)
CREATE TABLE room_type_categories (
    category_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    capacity INT NOT NULL,
    description TEXT
);

-- Hotel-specific room types
CREATE TABLE hotel_room_types (
    type_id INT PRIMARY KEY AUTO_INCREMENT,
    hotel_id INT NOT NULL,
    category_id INT NOT NULL,
    FOREIGN KEY (hotel_id) REFERENCES hotels(hotel_id),
    FOREIGN KEY (category_id) REFERENCES room_type_categories(category_id),
    UNIQUE (hotel_id, category_id)
);

-- Floor information table
CREATE TABLE floors (
    floor_id INT PRIMARY KEY AUTO_INCREMENT,
    hotel_id INT NOT NULL,
    floor_number INT NOT NULL,
    wing VARCHAR(50),
    UNIQUE(hotel_id, floor_number, wing),
    FOREIGN KEY (hotel_id) REFERENCES hotels(hotel_id)
);

-- Room base prices table
CREATE TABLE room_type_prices (
    price_id INT PRIMARY KEY AUTO_INCREMENT,
    type_id INT NOT NULL,
    base_price DECIMAL(10,2) NOT NULL,
    effective_from TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    effective_to TIMESTAMP NULL,
    FOREIGN KEY (type_id) REFERENCES hotel_room_types(type_id)
);

-- Rooms table (removed redundant hotel_id)
CREATE TABLE rooms (
    room_id INT PRIMARY KEY AUTO_INCREMENT,
    room_number VARCHAR(10) NOT NULL,
    type_id INT NOT NULL,
    floor_id INT NOT NULL,
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

-- Bookings table (removed redundant hotel_id)
CREATE TABLE bookings (
    booking_id INT PRIMARY KEY AUTO_INCREMENT,
    guest_id INT NOT NULL,
    room_id INT NOT NULL,
    check_in_date DATE NOT NULL,
    check_out_date DATE NOT NULL,
    status_id INT NOT NULL,
    number_of_guests INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    confirmed_by INT,
    FOREIGN KEY (guest_id) REFERENCES users(user_id),
    FOREIGN KEY (room_id) REFERENCES rooms(room_id),
    FOREIGN KEY (status_id) REFERENCES booking_statuses(status_id),
    FOREIGN KEY (confirmed_by) REFERENCES users(user_id)
);

-- Payments table
CREATE TABLE payments (
    payment_id INT PRIMARY KEY AUTO_INCREMENT,
    booking_id INT NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    payment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    payment_method ENUM('CASH', 'CREDIT_CARD', 'DEBIT_CARD') NOT NULL,
    processed_by INT NOT NULL,
    FOREIGN KEY (booking_id) REFERENCES bookings(booking_id),
    FOREIGN KEY (processed_by) REFERENCES users(user_id)
);

-- Housekeeping schedule table (removed redundant hotel_id)
CREATE TABLE housekeeping_schedule (
    schedule_id INT PRIMARY KEY AUTO_INCREMENT,
    room_id INT NOT NULL,
    staff_id INT NOT NULL,
    scheduled_date DATE NOT NULL,
    status ENUM('PENDING', 'COMPLETED', 'CANCELLED') DEFAULT 'PENDING',
    notes TEXT,
    created_by INT NOT NULL,
    FOREIGN KEY (room_id) REFERENCES rooms(room_id),
    FOREIGN KEY (staff_id) REFERENCES users(user_id),
    FOREIGN KEY (created_by) REFERENCES users(user_id)
);

-- Revenue reports table
CREATE TABLE revenue_reports (
    report_id INT PRIMARY KEY AUTO_INCREMENT,
    hotel_id INT NOT NULL,
    report_date DATE NOT NULL,
    total_revenue DECIMAL(10,2) NOT NULL,
    generated_by INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (hotel_id) REFERENCES hotels(hotel_id),
    FOREIGN KEY (generated_by) REFERENCES users(user_id)
);
  