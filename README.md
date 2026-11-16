# Flight Reservation System

**ENSF 614: Advanced System Analysis and Software Design**  
*Term Project: Standalone Desktop Flight Reservation System*

---

## 1. Project Overview

The **Flight Reservation System** is a standalone, role-based desktop application developed using object-oriented principles, domain-driven design, and a clean **three-layer architecture**.

The application uses an embedded **SQLite** backend with pre-configured schemas and seed data to support all required user roles:
- **Customer**: Search flights, select seats, book flights with simulated payments, apply monthly promotional discounts, view booking history, modify seats, and update customer profile details.
- **Flight Agent**: Manage bookings across customers, modify customer seats, cancel reservations, view customer profiles, and inspect flight schedules.
- **System Administrator**: Manage flights (add, update status/price, remove flights), manage aircraft fleet models, and inspect schedules.

---

## 2. System Architecture & Package Structure

The system strictly follows the recommended **Three-Layer Architecture** and maps cleanly to the required UML stereotypes (`<<entity>>`, `<<control>>`, and `<<boundary>>`):

```
flight_reservation_system/
│
├── pom.xml                                    # Maven project configuration & dependencies
├── README.md                                  # Project overview and run guide
│
└── src/
    └── main/
        ├── java/com/flightreservation/
        │   │
        │   ├── Main.java                      # Application entry point (initializes DB & launches GUI)
        │   │
        │   ├── model/                         # <<entity>> Domain Entities
        │   │   ├── UserRole.java              # Enum: CUSTOMER, FLIGHT_AGENT, ADMIN
        │   │   ├── User.java                  # Base authenticated account
        │   │   ├── Customer.java              # Customer profile entity
        │   │   ├── FlightAgent.java           # Flight agent employee entity
        │   │   ├── Admin.java                 # Administrator entity
        │   │   ├── Aircraft.java              # Aircraft model and seat capacity
        │   │   ├── SeatClass.java             # Enum: ECONOMY, BUSINESS, FIRST
        │   │   ├── Seat.java                  # Seat entity with pricing and availability
        │   │   ├── FlightStatus.java          # Enum: SCHEDULED, DELAYED, CANCELLED, COMPLETED
        │   │   ├── Flight.java                # Flight details and route
        │   │   ├── ReservationStatus.java     # Enum: CONFIRMED, CANCELLED, MODIFIED
        │   │   ├── Reservation.java           # Booking reservation entity
        │   │   ├── PaymentMethod.java         # Enum: CREDIT_CARD, DEBIT_CARD, PAYPAL
        │   │   ├── PaymentStatus.java         # Enum: COMPLETED, REFUNDED, FAILED
        │   │   ├── Payment.java               # Simulated payment transaction
        │   │   └── Promotion.java             # Monthly promotional offer entity
        │   │
        │   ├── controller/                    # <<control>> Business Logic Controllers (Use Cases)
        │   │   ├── AuthController.java        # Login, logout, customer registration
        │   │   ├── FlightSearchController.java# Flight search, seat availability query
        │   │   ├── BookingController.java     # Create, cancel, modify bookings
        │   │   ├── PaymentController.java     # Simulated card/payment validation & masking
        │   │   ├── FlightManagementController.java # Admin add/update/delete flights & aircraft
        │   │   ├── CustomerManagementController.java # Customer profile viewing & updates
        │   │   └── PromotionController.java   # Monthly promotional news selection
        │   │
        │   ├── data/                          # Data Access Layer (Persistence & SQLite DAOs)
        │   │   ├── DatabaseManager.java       # SQLite connection manager & schema initializer
        │   │   ├── UserDAO.java               # User account authentication & queries
        │   │   ├── CustomerDAO.java           # Customer profile queries & updates
        │   │   ├── AircraftDAO.java           # Aircraft fleet records
        │   │   ├── FlightDAO.java             # Flight schedule search & admin modifications
        │   │   ├── SeatDAO.java               # Seat generation & availability management
        │   │   ├── ReservationDAO.java        # Reservation storage & transactions
        │   │   ├── PaymentDAO.java            # Simulated payment records
        │   │   └── PromotionDAO.java          # Promotions and monthly view tracking
        │   │
        │   └── view/                          # <<boundary>> Presentation Layer (Swing GUI)
        │       ├── MainFrame.java             # Main application window & card navigation
        │       ├── LoginView.java             # Login panel with quick-fill demo buttons
        │       ├── CustomerDashboardView.java # Search, booking history & profile editing
        │       ├── AgentDashboardView.java    # Agent view: reservations, schedules, customers
        │       ├── AdminDashboardView.java    # Admin view: manage flights, aircraft fleet
        │       ├── FlightSearchView.java      # Reusable flight search and filter table
        │       ├── BookingDialog.java         # Seat selection & booking confirmation
        │       ├── PaymentDialog.java         # Simulated payment dialog with promo codes
        │       └── PromotionDialog.java       # Monthly promo popup dialog (Requirement 3.7)
        │
        └── resources/
            ├── schema.sql                     # SQLite DDL creating tables and constraints
            ├── seed-data.sql                  # Initial accounts, aircraft, flights, promos
            └── application.properties         # Database URL configuration
```

---

## 3. Database Schema (SQLite)

The SQLite database (`flight_reservation.db`) is automatically created and populated upon application launch using `schema.sql` and `seed-data.sql`:

1. **`users`**: User ID, username, password, role (`CUSTOMER`, `FLIGHT_AGENT`, `ADMIN`).
2. **`customers`**: Customer ID, user ID (foreign key), name, email, phone, address.
3. **`aircraft`**: Aircraft ID, model (e.g. *Boeing 737-800*, *Airbus A320*), total seats.
4. **`flights`**: Flight ID, flight number, airline, origin, destination, departure/arrival times, base price, aircraft ID, status.
5. **`seats`**: Seat ID, flight ID, seat number (e.g. *1A*, *10B*), class (`BUSINESS`, `ECONOMY`), price, availability (`0` or `1`).
6. **`reservations`**: Reservation ID, booking code (e.g. `BK-A1B2C3`), customer ID, flight ID, seat ID, booking date, status (`CONFIRMED`, `CANCELLED`, `MODIFIED`).
7. **`payments`**: Payment ID, reservation ID, amount, payment method, masked card number, status.
8. **`promotions`**: Promotion ID, title, description, discount percentage, promo code (e.g. `WINTER15`), valid month (`YYYY-MM`).
9. **`promo_views`**: User ID, month-year, viewed timestamp (ensures promotions appear only once per month).

---

## 4. Default Seed Accounts

The system includes pre-configured accounts for testing each role:

| Role | Username | Password | Notes |
|---|---|---|---|
| **Customer** | `customer` | `customer123` | Pre-linked to customer profile (John Doe) |
| **Customer** | `alice` | `alice123` | Second customer profile (Alice Smith) |
| **Flight Agent** | `agent` | `agent123` | Airline employee account |
| **System Admin** | `admin` | `admin123` | System administrator account |

> **Tip**: The login screen features **Quick Demo Login** buttons for instant testing of all three roles.

---

## 5. How to Run the Application

### Option A: Run via VS Code / IDE
1. Open the `flight_reservation_system` folder in VS Code or IntelliJ IDEA.
2. Locate and run [`Main.java`](file:///src/main/java/com/flightreservation/Main.java).
3. The database will automatically initialize, and the GUI will open.

### Option B: Run from Command Line
```bash
# Compile
javac -cp "target/classes;C:\Users\joe.liu1\.m2\repository\org\xerial\sqlite-jdbc\3.46.0.0\sqlite-jdbc-3.46.0.0.jar;C:\Users\joe.liu1\.m2\repository\org\slf4j\slf4j-api\1.7.36\slf4j-api-1.7.36.jar" -d target/classes $(Get-ChildItem -Recurse src/main/java/*.java | % { $_.FullName })

# Run Main GUI
java -cp "target/classes;C:\Users\joe.liu1\.m2\repository\org\xerial\sqlite-jdbc\3.46.0.0\sqlite-jdbc-3.46.0.0.jar;C:\Users\joe.liu1\.m2\repository\org\slf4j\slf4j-api\1.7.36\slf4j-api-1.7.36.jar" com.flightreservation.Main
```
