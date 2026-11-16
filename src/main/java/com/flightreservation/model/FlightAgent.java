package com.flightreservation.model;

/**
 * FlightAgent entity representing an airline employee who manages customer data and reservations.
 * Stereotype: <<entity>>
 */
public class FlightAgent extends User {
    private String employeeNumber;

    public FlightAgent() {
        setRole(UserRole.FLIGHT_AGENT);
    }

    public FlightAgent(int userId, String username, String password, String employeeNumber) {
        super(userId, username, password, UserRole.FLIGHT_AGENT);
        this.employeeNumber = employeeNumber;
    }

    public String getEmployeeNumber() {
        return employeeNumber;
    }

    public void setEmployeeNumber(String employeeNumber) {
        this.employeeNumber = employeeNumber;
    }
}
