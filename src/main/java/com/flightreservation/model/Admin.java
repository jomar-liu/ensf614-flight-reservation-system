package com.flightreservation.model;

/**
 * Admin entity representing the system administrator who manages flights, routes, and schedules.
 * Stereotype: <<entity>>
 */
public class Admin extends User {
    private String department;

    public Admin() {
        setRole(UserRole.ADMIN);
    }

    public Admin(int userId, String username, String password, String department) {
        super(userId, username, password, UserRole.ADMIN);
        this.department = department;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }
}
