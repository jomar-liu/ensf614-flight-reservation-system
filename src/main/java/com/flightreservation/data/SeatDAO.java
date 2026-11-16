package com.flightreservation.data;

import com.flightreservation.model.Seat;
import com.flightreservation.model.SeatClass;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for flight seats.
 * Stereotype: <<control>> / Data Access
 */
public class SeatDAO {
    private final DatabaseManager dbManager;

    public SeatDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }

    public List<Seat> getSeatsByFlightId(int flightId) {
        List<Seat> list = new ArrayList<>();
        String sql = "SELECT seat_id, flight_id, seat_number, seat_class, price, is_available " +
                     "FROM seats WHERE flight_id = ? ORDER BY seat_class, seat_number";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, flightId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToSeat(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Seat> getAvailableSeatsByFlightId(int flightId) {
        List<Seat> list = new ArrayList<>();
        String sql = "SELECT seat_id, flight_id, seat_number, seat_class, price, is_available " +
                     "FROM seats WHERE flight_id = ? AND is_available = 1 ORDER BY seat_class, seat_number";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, flightId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToSeat(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Seat getSeatById(int seatId) {
        String sql = "SELECT seat_id, flight_id, seat_number, seat_class, price, is_available FROM seats WHERE seat_id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, seatId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToSeat(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean setSeatAvailability(int seatId, boolean isAvailable, Connection conn) throws SQLException {
        String sql = "UPDATE seats SET is_available = ? WHERE seat_id = ?";
        boolean closeAfter = false;
        if (conn == null) {
            conn = dbManager.getConnection();
            closeAfter = true;
        }
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, isAvailable ? 1 : 0);
            stmt.setInt(2, seatId);
            return stmt.executeUpdate() > 0;
        } finally {
            if (closeAfter && conn != null) {
                conn.close();
            }
        }
    }

    public void generateDefaultSeatsForFlight(int flightId, double basePrice) {
        // Generates 2 rows of Business Class (1A, 1B, 2A, 2B) and 4 rows of Economy Class (10A-10C, 11A-11C, etc.)
        String sql = "INSERT OR IGNORE INTO seats (flight_id, seat_number, seat_class, price, is_available) VALUES (?, ?, ?, ?, 1)";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            // Business seats
            String[] bizSeats = {"1A", "1B", "2A", "2B"};
            for (String s : bizSeats) {
                stmt.setInt(1, flightId);
                stmt.setString(2, s);
                stmt.setString(3, SeatClass.BUSINESS.name());
                stmt.setDouble(4, Math.round(basePrice * 1.6 * 100.0) / 100.0);
                stmt.addBatch();
            }

            // Economy seats
            String[] econSeats = {"10A", "10B", "10C", "11A", "11B", "11C", "12A", "12B", "12C"};
            for (String s : econSeats) {
                stmt.setInt(1, flightId);
                stmt.setString(2, s);
                stmt.setString(3, SeatClass.ECONOMY.name());
                stmt.setDouble(4, basePrice);
                stmt.addBatch();
            }

            stmt.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Seat mapRowToSeat(ResultSet rs) throws SQLException {
        return new Seat(
                rs.getInt("seat_id"),
                rs.getInt("flight_id"),
                rs.getString("seat_number"),
                SeatClass.fromString(rs.getString("seat_class")),
                rs.getDouble("price"),
                rs.getInt("is_available") == 1
        );
    }
}
