package com.flightreservation.data;

import com.flightreservation.model.Promotion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Monthly Promotions and user view tracking.
 * Stereotype: <<control>> / Data Access
 */
public class PromotionDAO {
    private final DatabaseManager dbManager;

    public PromotionDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }

    public List<Promotion> getActivePromotionsForMonth(String monthYear) {
        List<Promotion> list = new ArrayList<>();
        String sql = "SELECT promotion_id, title, description, discount_percentage, promo_code, valid_month, is_active " +
                     "FROM promotions WHERE is_active = 1 AND valid_month = ? ORDER BY promotion_id";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, monthYear);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToPromotion(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean hasUserViewedPromoThisMonth(int userId, String monthYear) {
        String sql = "SELECT 1 FROM promo_views WHERE user_id = ? AND month_year = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setString(2, monthYear);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void recordPromoView(int userId, String monthYear) {
        String sql = "INSERT OR IGNORE INTO promo_views (user_id, month_year) VALUES (?, ?)";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setString(2, monthYear);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Promotion mapRowToPromotion(ResultSet rs) throws SQLException {
        return new Promotion(
                rs.getInt("promotion_id"),
                rs.getString("title"),
                rs.getString("description"),
                rs.getDouble("discount_percentage"),
                rs.getString("promo_code"),
                rs.getString("valid_month"),
                rs.getInt("is_active") == 1
        );
    }
}
