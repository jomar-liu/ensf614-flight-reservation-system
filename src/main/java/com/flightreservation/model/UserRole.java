package com.flightreservation.model;

/**
 * UserRole represents the authorization roles defined in the system.
 * Stereotype: <<entity>>
 */
public enum UserRole {
    CUSTOMER("Customer"),
    FLIGHT_AGENT("Flight Agent"),
    ADMIN("System Administrator");

    private final String displayName;

    UserRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static UserRole fromString(String roleStr) {
        if (roleStr == null) return null;
        for (UserRole role : UserRole.values()) {
            if (role.name().equalsIgnoreCase(roleStr) || role.displayName.equalsIgnoreCase(roleStr)) {
                return role;
            }
        }
        return null;
    }
}
