package com.flightreservation.model;

/**
 * ReservationStatus represents the lifecycle state of a booking.
 * Stereotype: <<entity>>
 */
public enum ReservationStatus {
    CONFIRMED("Confirmed"),
    CANCELLED("Cancelled"),
    MODIFIED("Modified");

    private final String displayName;

    ReservationStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static ReservationStatus fromString(String str) {
        if (str == null) return CONFIRMED;
        for (ReservationStatus rs : ReservationStatus.values()) {
            if (rs.name().equalsIgnoreCase(str) || rs.displayName.equalsIgnoreCase(str)) {
                return rs;
            }
        }
        return CONFIRMED;
    }
}
