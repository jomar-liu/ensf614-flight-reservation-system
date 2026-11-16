package com.flightreservation.data;

import com.flightreservation.model.Flight;
import com.flightreservation.model.FlightStatus;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Flight schedules and search operations.
 * Stereotype: <<control>> / Data Access
 */
public class FlightDAO {
    private final DatabaseManager dbManager;
    private final AircraftDAO aircraftDAO;

    public FlightDAO() {
        this.dbManager = DatabaseManager.getInstance();
        this.aircraftDAO = new AircraftDAO();
    }

    public List<Flight> searchFlights(String origin, String destination, String date, String airline) {
        List<Flight> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT f.flight_id, f.flight_number, f.airline, f.origin, f.destination, " +
                "f.departure_time, f.arrival_time, f.base_price, f.aircraft_id, f.status, " +
                "(SELECT COUNT(*) FROM seats s WHERE s.flight_id = f.flight_id AND s.is_available = 1) AS available_seats " +
                "FROM flights f WHERE 1=1 "
        );

        List<Object> params = new ArrayList<>();
        if (origin != null && !origin.trim().isEmpty() && !origin.equalsIgnoreCase("ALL")) {
            sql.append("AND LOWER(f.origin) LIKE ? ");
            params.add("%" + origin.trim().toLowerCase() + "%");
        }
        if (destination != null && !destination.trim().isEmpty() && !destination.equalsIgnoreCase("ALL")) {
            sql.append("AND LOWER(f.destination) LIKE ? ");
            params.add("%" + destination.trim().toLowerCase() + "%");
        }
        if (date != null && !date.trim().isEmpty()) {
            sql.append("AND f.departure_time LIKE ? ");
            params.add(date.trim() + "%");
        }
        if (airline != null && !airline.trim().isEmpty() && !airline.equalsIgnoreCase("ALL")) {
            sql.append("AND LOWER(f.airline) LIKE ? ");
            params.add("%" + airline.trim().toLowerCase() + "%");
        }

        sql.append("ORDER BY f.departure_time ASC");

        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToFlight(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Flight> getAllFlights() {
        return searchFlights(null, null, null, null);
    }

    public Flight getFlightById(int flightId) {
        String sql = "SELECT f.flight_id, f.flight_number, f.airline, f.origin, f.destination, " +
                     "f.departure_time, f.arrival_time, f.base_price, f.aircraft_id, f.status, " +
                     "(SELECT COUNT(*) FROM seats s WHERE s.flight_id = f.flight_id AND s.is_available = 1) AS available_seats " +
                     "FROM flights f WHERE f.flight_id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, flightId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToFlight(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int addFlight(Flight flight) {
        String sql = "INSERT INTO flights (flight_number, airline, origin, destination, departure_time, arrival_time, base_price, aircraft_id, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, flight.getFlightNumber());
            stmt.setString(2, flight.getAirline());
            stmt.setString(3, flight.getOrigin());
            stmt.setString(4, flight.getDestination());
            stmt.setString(5, flight.getDepartureTime());
            stmt.setString(6, flight.getArrivalTime());
            stmt.setDouble(7, flight.getBasePrice());
            stmt.setInt(8, flight.getAircraftId());
            stmt.setString(9, flight.getStatus() != null ? flight.getStatus().name() : FlightStatus.SCHEDULED.name());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int flightId = rs.getInt(1);
                    flight.setFlightId(flightId);
                    // Generate default seats for the newly created flight
                    SeatDAO seatDAO = new SeatDAO();
                    seatDAO.generateDefaultSeatsForFlight(flightId, flight.getBasePrice());
                    return flightId;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public boolean updateFlight(Flight flight) {
        String sql = "UPDATE flights SET flight_number = ?, airline = ?, origin = ?, destination = ?, " +
                     "departure_time = ?, arrival_time = ?, base_price = ?, aircraft_id = ?, status = ? " +
                     "WHERE flight_id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, flight.getFlightNumber());
            stmt.setString(2, flight.getAirline());
            stmt.setString(3, flight.getOrigin());
            stmt.setString(4, flight.getDestination());
            stmt.setString(5, flight.getDepartureTime());
            stmt.setString(6, flight.getArrivalTime());
            stmt.setDouble(7, flight.getBasePrice());
            stmt.setInt(8, flight.getAircraftId());
            stmt.setString(9, flight.getStatus().name());
            stmt.setInt(10, flight.getFlightId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteFlight(int flightId) {
        String sql = "DELETE FROM flights WHERE flight_id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, flightId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<String> getDistinctOrigins() {
        return fetchDistinctString("SELECT DISTINCT origin FROM flights ORDER BY origin");
    }

    public List<String> getDistinctDestinations() {
        return fetchDistinctString("SELECT DISTINCT destination FROM flights ORDER BY destination");
    }

    public List<String> getDistinctAirlines() {
        return fetchDistinctString("SELECT DISTINCT airline FROM flights ORDER BY airline");
    }

    private List<String> fetchDistinctString(String sql) {
        List<String> results = new ArrayList<>();
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                results.add(rs.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    private Flight mapRowToFlight(ResultSet rs) throws SQLException {
        Flight f = new Flight(
                rs.getInt("flight_id"),
                rs.getString("flight_number"),
                rs.getString("airline"),
                rs.getString("origin"),
                rs.getString("destination"),
                rs.getString("departure_time"),
                rs.getString("arrival_time"),
                rs.getDouble("base_price"),
                rs.getInt("aircraft_id"),
                FlightStatus.fromString(rs.getString("status"))
        );
        f.setAvailableSeatsCount(rs.getInt("available_seats"));
        f.setAircraft(aircraftDAO.findById(f.getAircraftId()));
        return f;
    }
}
