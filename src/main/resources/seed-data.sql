-- Seed Data for Flight Reservation System

-- 1. Insert Initial Users
INSERT OR IGNORE INTO users (user_id, username, password, role) VALUES
(1, 'admin', 'admin123', 'ADMIN'),
(2, 'agent', 'agent123', 'FLIGHT_AGENT'),
(3, 'customer', 'customer123', 'CUSTOMER'),
(4, 'alice', 'alice123', 'CUSTOMER');

-- 2. Insert Customer Profiles
INSERT OR IGNORE INTO customers (customer_id, user_id, first_name, last_name, email, phone, address) VALUES
(1, 3, 'John', 'Doe', 'john.doe@example.com', '403-555-0101', '2500 University Dr NW, Calgary, AB'),
(2, 4, 'Alice', 'Smith', 'alice.smith@example.com', '403-555-0102', '123 8th Ave SW, Calgary, AB');

-- 3. Insert Aircraft
INSERT OR IGNORE INTO aircraft (aircraft_id, model, total_seats) VALUES
(1, 'Boeing 737-800', 160),
(2, 'Airbus A320', 150),
(3, 'Boeing 787-9 Dreamliner', 290);

-- 4. Insert Flights
INSERT OR IGNORE INTO flights (flight_id, flight_number, airline, origin, destination, departure_time, arrival_time, base_price, aircraft_id, status) VALUES
(1, 'AC201', 'Air Canada', 'Calgary (YYC)', 'Vancouver (YVR)', '2026-12-01 08:00', '2026-12-01 08:35', 189.00, 1, 'SCHEDULED'),
(2, 'WS402', 'WestJet', 'Calgary (YYC)', 'Toronto (YYZ)', '2026-12-01 10:30', '2026-12-01 16:15', 299.00, 2, 'SCHEDULED'),
(3, 'AC105', 'Air Canada', 'Vancouver (YVR)', 'Calgary (YYC)', '2026-12-02 14:00', '2026-12-02 16:30', 195.00, 1, 'SCHEDULED'),
(4, 'WS618', 'WestJet', 'Calgary (YYC)', 'Montreal (YUL)', '2026-12-03 09:15', '2026-12-03 15:45', 349.00, 3, 'SCHEDULED');

-- 5. Insert Sample Seats for Flight 1 (AC201)
INSERT OR IGNORE INTO seats (flight_id, seat_number, seat_class, price, is_available) VALUES
(1, '1A', 'BUSINESS', 350.00, 1),
(1, '1B', 'BUSINESS', 350.00, 1),
(1, '2A', 'BUSINESS', 350.00, 1),
(1, '2B', 'BUSINESS', 350.00, 1),
(1, '10A', 'ECONOMY', 189.00, 1),
(1, '10B', 'ECONOMY', 189.00, 1),
(1, '10C', 'ECONOMY', 189.00, 1),
(1, '11A', 'ECONOMY', 189.00, 1),
(1, '11B', 'ECONOMY', 189.00, 1),
(1, '11C', 'ECONOMY', 189.00, 1);

-- Insert Sample Seats for Flight 2 (WS402)
INSERT OR IGNORE INTO seats (flight_id, seat_number, seat_class, price, is_available) VALUES
(2, '1A', 'BUSINESS', 499.00, 1),
(2, '1B', 'BUSINESS', 499.00, 1),
(2, '12A', 'ECONOMY', 299.00, 1),
(2, '12B', 'ECONOMY', 299.00, 1),
(2, '12C', 'ECONOMY', 299.00, 1),
(2, '14A', 'ECONOMY', 299.00, 1),
(2, '14B', 'ECONOMY', 299.00, 1);

-- 6. Insert Promotions
INSERT OR IGNORE INTO promotions (promotion_id, title, description, discount_percentage, promo_code, valid_month, is_active) VALUES
(1, 'Winter Holiday Getaway', 'Save 15% on all domestic flights booked this month!', 15.0, 'WINTER15', '2026-11', 1),
(2, 'Rocky Mountain Explorer', 'Get 20% off flights departing from or arriving in Calgary!', 20.0, 'ROCKY20', '2026-11', 1),
(3, 'Early Bird Special', 'Enjoy 10% off flights booked 30 days in advance.', 10.0, 'EARLY10', '2026-12', 1);
