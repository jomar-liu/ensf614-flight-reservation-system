package com.flightreservation.data;

import com.flightreservation.model.Payment;
import com.flightreservation.model.PaymentMethod;
import com.flightreservation.model.PaymentStatus;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Data Access Object for recorded simulation payments.
 * Stereotype: <<control>> / Data Access
 */
public class PaymentDAO {
    private final DatabaseManager dbManager;

    public PaymentDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }

    public int createPayment(Payment payment, Connection conn) throws SQLException {
        String sql = "INSERT INTO payments (reservation_id, amount, payment_method, card_number_masked, status) VALUES (?, ?, ?, ?, ?)";
        boolean closeAfter = false;
        if (conn == null) {
            conn = dbManager.getConnection();
            closeAfter = true;
        }
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, payment.getReservationId());
            stmt.setDouble(2, payment.getAmount());
            stmt.setString(3, payment.getPaymentMethod().name());
            stmt.setString(4, payment.getCardNumberMasked());
            stmt.setString(5, payment.getStatus().name());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    payment.setPaymentId(id);
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

    public Payment getPaymentByReservationId(int reservationId) {
        String sql = "SELECT payment_id, reservation_id, amount, payment_method, card_number_masked, payment_date, status " +
                     "FROM payments WHERE reservation_id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, reservationId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Payment(
                            rs.getInt("payment_id"),
                            rs.getInt("reservation_id"),
                            rs.getDouble("amount"),
                            PaymentMethod.fromString(rs.getString("payment_method")),
                            rs.getString("card_number_masked"),
                            rs.getString("payment_date"),
                            PaymentStatus.fromString(rs.getString("status"))
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
