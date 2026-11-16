package com.flightreservation.data;

import com.flightreservation.model.Reservation;
import com.flightreservation.model.ReservationStatus;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Reservations and booking records.
 * Stereotype: <<control>> / Data Access
 */
public class ReservationDAO {
    private final DatabaseManager dbManager;
    private final CustomerDAO customerDAO;
    private final FlightDAO flightDAO;
    private final SeatDAO seatDAO;

    public ReservationDAO() {
        this.dbManager = DatabaseManager.getInstance();
        this.customerDAO = new CustomerDAO();
        this.flightDAO = new FlightDAO();
        this.seatDAO = new SeatDAO();
    }

    public int createReservation(Reservation reservation, Connection conn) throws SQLException {
        String sql = "INSERT INTO reservations (reservation_code, customer_id, flight_id, seat_id, status) VALUES (?, ?, ?, ?, ?)";
        boolean closeAfter = false;
        if (conn == null) {
            conn = dbManager.getConnection();
            closeAfter = true;
        }
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, reservation.getReservationCode());
            stmt.setInt(2, reservation.getCustomerId());
            stmt.setInt(3, reservation.getFlightId());
            stmt.setInt(4, reservation.getSeatId());
            stmt.setString(5, reservation.getStatus() != null ? reservation.getStatus().name() : ReservationStatus.CONFIRMED.name());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    reservation.setReservationId(id);
                    return id;
                }
            }
        } finally {
            if (closeAfter && conn != null) {
                conn.close();
            }
        }
        return -1;
    }

    public List<Reservation> getReservationsByCustomerId(int customerId) {
        List<Reservation> list = new ArrayList<>();
        String sql = "SELECT reservation_id, reservation_code, customer_id, flight_id, seat_id, booking_date, status " +
                     "FROM reservations WHERE customer_id = ? ORDER BY booking_date DESC";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, customerId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToReservation(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Reservation> getAllReservations() {
        List<Reservation> list = new ArrayList<>();
        String sql = "SELECT reservation_id, reservation_code, customer_id, flight_id, seat_id, booking_date, status " +
                     "FROM reservations ORDER BY booking_date DESC";
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRowToReservation(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Reservation getReservationById(int reservationId) {
        String sql = "SELECT reservation_id, reservation_code, customer_id, flight_id, seat_id, booking_date, status " +
                     "FROM reservations WHERE reservation_id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, reservationId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToReservation(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Reservation getReservationByCode(String code) {
        String sql = "SELECT reservation_id, reservation_code, customer_id, flight_id, seat_id, booking_date, status " +
                     "FROM reservations WHERE UPPER(reservation_code) = UPPER(?)";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, code.trim());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToReservation(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean cancelReservation(int reservationId) {
        Reservation res = getReservationById(reservationId);
        if (res == null || res.getStatus() == ReservationStatus.CANCELLED) {
            return false;
        }

        Connection conn = null;
        try {
            conn = dbManager.getConnection();
            conn.setAutoCommit(false);

            // Update reservation status
            String updateRes = "UPDATE reservations SET status = 'CANCELLED' WHERE reservation_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(updateRes)) {
                stmt.setInt(1, reservationId);
                stmt.executeUpdate();
            }

            // Free the seat
            seatDAO.setSeatAvailability(res.getSeatId(), true, conn);

            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) {}
            }
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                try { conn.close(); } catch (SQLException ignored) {}
            }
        }
    }

    public boolean modifyReservationSeat(int reservationId, int newSeatId) {
        Reservation res = getReservationById(reservationId);
        if (res == null || res.getStatus() == ReservationStatus.CANCELLED) {
            return false;
        }

        Connection conn = null;
        try {
            conn = dbManager.getConnection();
            conn.setAutoCommit(false);

            int oldSeatId = res.getSeatId();

            // Release old seat
            seatDAO.setSeatAvailability(oldSeatId, true, conn);

            // Reserve new seat
            seatDAO.setSeatAvailability(newSeatId, false, conn);

            // Update reservation record
            String sql = "UPDATE reservations SET seat_id = ?, status = 'MODIFIED' WHERE reservation_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, newSeatId);
                stmt.setInt(2, reservationId);
                stmt.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) {}
            }
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                try { conn.close(); } catch (SQLException ignored) {}
            }
        }
    }

    private Reservation mapRowToReservation(ResultSet rs) throws SQLException {
        Reservation r = new Reservation(
                rs.getInt("reservation_id"),
                rs.getString("reservation_code"),
                rs.getInt("customer_id"),
                rs.getInt("flight_id"),
                rs.getInt("seat_id"),
                rs.getString("booking_date"),
                ReservationStatus.fromString(rs.getString("status"))
        );
        r.setCustomer(customerDAO.findByCustomerId(r.getCustomerId()));
        r.setFlight(flightDAO.getFlightById(r.getFlightId()));
        r.setSeat(seatDAO.getSeatById(r.getSeatId()));
        return r;
    }
}
