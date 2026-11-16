package com.flightreservation.controller;

import com.flightreservation.data.PromotionDAO;
import com.flightreservation.model.Promotion;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

/**
 * Controller coordinating monthly promotional news notifications.
 * Stereotype: <<control>>
 */
public class PromotionController {
    private final PromotionDAO promotionDAO;
    private final Random random;

    public PromotionController() {
        this.promotionDAO = new PromotionDAO();
        this.random = new Random();
    }

    public Promotion getEligibleMonthlyPromotion(int userId) {
        String currentMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));

        // Check if customer already viewed promotional news for this month
        if (promotionDAO.hasUserViewedPromoThisMonth(userId, currentMonth)) {
            return null; // Already shown this month
        }

        List<Promotion> promos = promotionDAO.getActivePromotionsForMonth(currentMonth);
        if (promos.isEmpty()) {
            return null;
        }

        // Randomly select one promotion
        Promotion selected = promos.get(random.nextInt(promos.size()));

        // Record that user has seen this month's promotion
        promotionDAO.recordPromoView(userId, currentMonth);

        return selected;
    }
}
