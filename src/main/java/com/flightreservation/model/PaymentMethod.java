package com.flightreservation.model;

/**
 * PaymentMethod represents available payment options in the simulation.
 * Stereotype: <<entity>>
 */
public enum PaymentMethod {
    CREDIT_CARD("Credit Card"),
    DEBIT_CARD("Debit Card"),
    PAYPAL("PayPal");

    private final String displayName;

    PaymentMethod(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static PaymentMethod fromString(String str) {
        if (str == null) return CREDIT_CARD;
        for (PaymentMethod pm : PaymentMethod.values()) {
            if (pm.name().equalsIgnoreCase(str) || pm.displayName.equalsIgnoreCase(str)) {
                return pm;
            }
        }
        return CREDIT_CARD;
    }
}
