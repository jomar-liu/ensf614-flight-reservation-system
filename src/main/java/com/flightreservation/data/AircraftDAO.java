package com.flightreservation.data;

import com.flightreservation.model.Aircraft;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Aircraft inventory.
 * Stereotype: <<control>> / Data Access
 */
public class AircraftDAO {
    private final DatabaseManager dbManager;

    public AircraftDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }

    public List<Aircraft> findAll() {
        List<Aircraft> list = new ArrayList<>();
        String sql = "SELECT aircraft_id, model, total_seats FROM aircraft ORDER BY model";
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Aircraft(
                        rs.getInt("aircraft_id"),
                        rs.getString("model"),
                        rs.getInt("total_seats")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Aircraft findById(int aircraftId) {
        String sql = "SELECT aircraft_id, model, total_seats FROM aircraft WHERE aircraft_id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, aircraftId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Aircraft(
                            rs.getInt("aircraft_id"),
                            rs.getString("model"),
                            rs.getInt("total_seats")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int addAircraft(Aircraft aircraft) {
        String sql = "INSERT INTO aircraft (model, total_seats) VALUES (?, ?)";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, aircraft.getModel());
            stmt.setInt(2, aircraft.getTotalSeats());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    aircraft.setAircraftId(id);
                    return id;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
}
