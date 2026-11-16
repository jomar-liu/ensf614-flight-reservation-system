package com.flightreservation.data;

import com.flightreservation.model.Admin;
import com.flightreservation.model.Customer;
import com.flightreservation.model.FlightAgent;
import com.flightreservation.model.User;
import com.flightreservation.model.UserRole;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Data Access Object for User authentication and accounts.
 * Stereotype: <<control>> / Data Access
 */
public class UserDAO {
    private final DatabaseManager dbManager;

    public UserDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }

    public User authenticate(String username, String password) {
        String sql = "SELECT user_id, username, password, role FROM users WHERE username = ? AND password = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToUser(rs, conn);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public User findByUsername(String username) {
        String sql = "SELECT user_id, username, password, role FROM users WHERE username = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToUser(rs, conn);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public User findById(int userId) {
        String sql = "SELECT user_id, username, password, role FROM users WHERE user_id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToUser(rs, conn);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int createUser(String username, String password, UserRole role) {
        String sql = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, role.name());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private User mapRowToUser(ResultSet rs, Connection conn) throws SQLException {
        int userId = rs.getInt("user_id");
        String username = rs.getString("username");
        String password = rs.getString("password");
        UserRole role = UserRole.fromString(rs.getString("role"));

        if (role == UserRole.CUSTOMER) {
            String custSql = "SELECT customer_id, first_name, last_name, email, phone, address FROM customers WHERE user_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(custSql)) {
                stmt.setInt(1, userId);
                try (ResultSet crs = stmt.executeQuery()) {
                    if (crs.next()) {
                        return new Customer(
                                userId, username, password,
                                crs.getInt("customer_id"),
                                crs.getString("first_name"),
                                crs.getString("last_name"),
                                crs.getString("email"),
                                crs.getString("phone"),
                                crs.getString("address")
                        );
                    }
                }
            }
            return new Customer(userId, username, password, 0, username, "", "", "", "");
        } else if (role == UserRole.FLIGHT_AGENT) {
            return new FlightAgent(userId, username, password, "AGT-" + userId);
        } else {
            return new Admin(userId, username, password, "Flight Operations");
        }
    }
}
