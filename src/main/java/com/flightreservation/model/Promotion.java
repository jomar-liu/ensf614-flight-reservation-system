package com.flightreservation.model;

/**
 * Promotion entity representing monthly promotional offers.
 * Stereotype: <<entity>>
 */
public class Promotion {
    private int promotionId;
    private String title;
    private String description;
    private double discountPercentage;
    private String promoCode;
    private String validMonth; // "YYYY-MM"
    private boolean active;

    public Promotion() {
        this.active = true;
    }

    public Promotion(int promotionId, String title, String description,
                     double discountPercentage, String promoCode, String validMonth, boolean active) {
        this.promotionId = promotionId;
        this.title = title;
        this.description = description;
        this.discountPercentage = discountPercentage;
        this.promoCode = promoCode;
        this.validMonth = validMonth;
        this.active = active;
    }

    public int getPromotionId() {
        return promotionId;
    }

    public void setPromotionId(int promotionId) {
        this.promotionId = promotionId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(double discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public String getPromoCode() {
        return promoCode;
    }

    public void setPromoCode(String promoCode) {
        this.promoCode = promoCode;
    }

    public String getValidMonth() {
        return validMonth;
    }

    public void setValidMonth(String validMonth) {
        this.validMonth = validMonth;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return title + " (" + discountPercentage + "% off with code " + promoCode + ")";
    }
}
