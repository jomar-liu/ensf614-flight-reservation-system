package com.flightreservation.model;

/**
 * PaymentStatus represents the simulated payment transaction outcome.
 * Stereotype: <<entity>>
 */
public enum PaymentStatus {
    COMPLETED("Completed"),
    REFUNDED("Refunded"),
    FAILED("Failed");

    private final String displayName;

    PaymentStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static PaymentStatus fromString(String str) {
        if (str == null) return COMPLETED;
        for (PaymentStatus ps : PaymentStatus.values()) {
            if (ps.name().equalsIgnoreCase(str) || ps.displayName.equalsIgnoreCase(str)) {
                return ps;
            }
        }
        return COMPLETED;
    }
}
