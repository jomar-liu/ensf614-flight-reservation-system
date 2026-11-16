-- Flight Reservation System Database Schema (SQLite)

PRAGMA foreign_keys = ON;

-- 1. Users Table (Core authentication & role-based access)
CREATE TABLE IF NOT EXISTS users (
    user_id INTEGER PRIMARY KEY AUTOINCREMENT,
    username TEXT NOT NULL UNIQUE,
    password TEXT NOT NULL,
    role TEXT NOT NULL CHECK(role IN ('CUSTOMER', 'FLIGHT_AGENT', 'ADMIN')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Customers Profile Table
CREATE TABLE IF NOT EXISTS customers (
    customer_id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER UNIQUE,
    first_name TEXT NOT NULL,
    last_name TEXT NOT NULL,
    email TEXT NOT NULL UNIQUE,
    phone TEXT,
    address TEXT,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- 3. Aircraft Table
CREATE TABLE IF NOT EXISTS aircraft (
    aircraft_id INTEGER PRIMARY KEY AUTOINCREMENT,
    model TEXT NOT NULL,
    total_seats INTEGER NOT NULL CHECK(total_seats > 0)
);

-- 4. Flights Table
CREATE TABLE IF NOT EXISTS flights (
    flight_id INTEGER PRIMARY KEY AUTOINCREMENT,
    flight_number TEXT NOT NULL UNIQUE,
    airline TEXT NOT NULL,
    origin TEXT NOT NULL,
    destination TEXT NOT NULL,
    departure_time TEXT NOT NULL, -- ISO-8601 string: YYYY-MM-DD HH:MM
    arrival_time TEXT NOT NULL,
    base_price REAL NOT NULL CHECK(base_price >= 0),
    aircraft_id INTEGER NOT NULL,
    status TEXT NOT NULL DEFAULT 'SCHEDULED' CHECK(status IN ('SCHEDULED', 'DELAYED', 'CANCELLED', 'COMPLETED')),
    FOREIGN KEY (aircraft_id) REFERENCES aircraft(aircraft_id) ON DELETE RESTRICT
);

-- 5. Seats Table
CREATE TABLE IF NOT EXISTS seats (
    seat_id INTEGER PRIMARY KEY AUTOINCREMENT,
    flight_id INTEGER NOT NULL,
    seat_number TEXT NOT NULL, -- e.g., '1A', '12C'
    seat_class TEXT NOT NULL CHECK(seat_class IN ('ECONOMY', 'BUSINESS', 'FIRST')),
    price REAL NOT NULL CHECK(price >= 0),
    is_available INTEGER NOT NULL DEFAULT 1 CHECK(is_available IN (0, 1)),
    FOREIGN KEY (flight_id) REFERENCES flights(flight_id) ON DELETE CASCADE,
    UNIQUE(flight_id, seat_number)
);

-- 6. Reservations Table
CREATE TABLE IF NOT EXISTS reservations (
    reservation_id INTEGER PRIMARY KEY AUTOINCREMENT,
    reservation_code TEXT NOT NULL UNIQUE,
    customer_id INTEGER NOT NULL,
    flight_id INTEGER NOT NULL,
    seat_id INTEGER NOT NULL,
    booking_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status TEXT NOT NULL DEFAULT 'CONFIRMED' CHECK(status IN ('CONFIRMED', 'CANCELLED', 'MODIFIED')),
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE CASCADE,
    FOREIGN KEY (flight_id) REFERENCES flights(flight_id) ON DELETE RESTRICT,
    FOREIGN KEY (seat_id) REFERENCES seats(seat_id) ON DELETE RESTRICT
);

-- 7. Payments Table (Simulated payments)
CREATE TABLE IF NOT EXISTS payments (
    payment_id INTEGER PRIMARY KEY AUTOINCREMENT,
    reservation_id INTEGER NOT NULL,
    amount REAL NOT NULL CHECK(amount >= 0),
    payment_method TEXT NOT NULL, -- 'CREDIT_CARD', 'DEBIT_CARD', 'PAYPAL'
    card_number_masked TEXT,
    payment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status TEXT NOT NULL DEFAULT 'COMPLETED' CHECK(status IN ('COMPLETED', 'REFUNDED', 'FAILED')),
    FOREIGN KEY (reservation_id) REFERENCES reservations(reservation_id) ON DELETE CASCADE
);

-- 8. Monthly Promotions Table
CREATE TABLE IF NOT EXISTS promotions (
    promotion_id INTEGER PRIMARY KEY AUTOINCREMENT,
    title TEXT NOT NULL,
    description TEXT NOT NULL,
    discount_percentage REAL NOT NULL CHECK(discount_percentage > 0 AND discount_percentage <= 100),
    promo_code TEXT NOT NULL UNIQUE,
    valid_month TEXT NOT NULL, -- Format: YYYY-MM
    is_active INTEGER NOT NULL DEFAULT 1 CHECK(is_active IN (0, 1))
);

-- 9. Promotion View History (Tracks whether a user was already notified for the month)
CREATE TABLE IF NOT EXISTS promo_views (
    view_id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    month_year TEXT NOT NULL, -- Format: YYYY-MM
    viewed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    UNIQUE(user_id, month_year)
);
