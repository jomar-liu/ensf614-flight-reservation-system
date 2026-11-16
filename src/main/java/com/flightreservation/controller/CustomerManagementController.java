package com.flightreservation.controller;

import com.flightreservation.data.CustomerDAO;
import com.flightreservation.model.Customer;

import java.util.List;

/**
 * Controller coordinating customer profile viewing, editing, and listing.
 * Stereotype: <<control>>
 */
public class CustomerManagementController {
    private final CustomerDAO customerDAO;

    public CustomerManagementController() {
        this.customerDAO = new CustomerDAO();
    }

    public Customer getCustomerByUserId(int userId) {
        return customerDAO.findByUserId(userId);
    }

    public Customer getCustomerById(int customerId) {
        return customerDAO.findByCustomerId(customerId);
    }

    public List<Customer> getAllCustomers() {
        return customerDAO.findAll();
    }

    public boolean updateCustomerProfile(Customer customer) {
        if (customer == null || customer.getCustomerId() <= 0) {
            return false;
        }
        return customerDAO.updateCustomer(customer);
    }
}
