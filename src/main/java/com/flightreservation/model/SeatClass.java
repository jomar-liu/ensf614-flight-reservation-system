package com.flightreservation.model;

/**
 * SeatClass represents travel cabins (Economy, Business, First).
 * Stereotype: <<entity>>
 */
public enum SeatClass {
    ECONOMY("Economy"),
    BUSINESS("Business"),
    FIRST("First Class");

    private final String displayName;

    SeatClass(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static SeatClass fromString(String str) {
        if (str == null) return ECONOMY;
        for (SeatClass sc : SeatClass.values()) {
            if (sc.name().equalsIgnoreCase(str) || sc.displayName.equalsIgnoreCase(str)) {
                return sc;
            }
        }
        return ECONOMY;
    }
}
