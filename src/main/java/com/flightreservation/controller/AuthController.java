package com.flightreservation.controller;

import com.flightreservation.data.CustomerDAO;
import com.flightreservation.data.UserDAO;
import com.flightreservation.model.Customer;
import com.flightreservation.model.User;
import com.flightreservation.model.UserRole;

/**
 * Controller coordinating user authentication and registration use cases.
 * Stereotype: <<control>>
 */
public class AuthController {
    private final UserDAO userDAO;
    private final CustomerDAO customerDAO;
    private User currentUser;

    public AuthController() {
        this.userDAO = new UserDAO();
        this.customerDAO = new CustomerDAO();
    }

    public User login(String username, String password) {
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            return null;
        }
        User user = userDAO.authenticate(username.trim(), password.trim());
        if (user != null) {
            this.currentUser = user;
        }
        return user;
    }

    public void logout() {
        this.currentUser = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean registerCustomer(String username, String password, String firstName,
                                    String lastName, String email, String phone, String address) {
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            return false;
        }
        if (userDAO.findByUsername(username.trim()) != null) {
            return false; // Username already taken
        }

        int userId = userDAO.createUser(username.trim(), password.trim(), UserRole.CUSTOMER);
        if (userId <= 0) {
            return false;
        }

        Customer customer = new Customer(userId, username.trim(), password.trim(), 0,
                firstName != null ? firstName.trim() : "",
                lastName != null ? lastName.trim() : "",
                email != null ? email.trim() : "",
                phone != null ? phone.trim() : "",
                address != null ? address.trim() : "");

        int customerId = customerDAO.createCustomer(customer);
        return customerId > 0;
    }
}
