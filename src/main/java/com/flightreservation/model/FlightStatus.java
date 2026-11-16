package com.flightreservation.model;

/**
 * FlightStatus represents the operational status of a scheduled flight.
 * Stereotype: <<entity>>
 */
public enum FlightStatus {
    SCHEDULED("Scheduled"),
    DELAYED("Delayed"),
    CANCELLED("Cancelled"),
    COMPLETED("Completed");

    private final String displayName;

    FlightStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static FlightStatus fromString(String str) {
        if (str == null) return SCHEDULED;
        for (FlightStatus status : FlightStatus.values()) {
            if (status.name().equalsIgnoreCase(str) || status.displayName.equalsIgnoreCase(str)) {
                return status;
            }
        }
        return SCHEDULED;
    }
}
