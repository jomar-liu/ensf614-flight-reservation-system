package com.flightreservation.view;

import com.flightreservation.controller.BookingController;
import com.flightreservation.controller.CustomerManagementController;
import com.flightreservation.controller.FlightSearchController;
import com.flightreservation.model.Customer;
import com.flightreservation.model.Flight;
import com.flightreservation.model.Reservation;
import com.flightreservation.model.ReservationStatus;
import com.flightreservation.model.Seat;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Customer dashboard interface for searching flights, viewing/modifying bookings, and editing profiles.
 * Stereotype: <<boundary>>
 */
public class CustomerDashboardView extends JPanel {
    private final Customer customer;
    private final FlightSearchController searchController;
    private final BookingController bookingController;
    private final CustomerManagementController customerController;
    private final Frame parentFrame;

    private FlightSearchView flightSearchView;
    private JTable bookingsTable;
    private DefaultTableModel bookingsModel;
    private List<Reservation> currentReservations;

    // Profile fields
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JTextField addressField;

    public CustomerDashboardView(Frame parentFrame, Customer customer) {
        this.parentFrame = parentFrame;
        this.customer = customer;
        this.searchController = new FlightSearchController();
        this.bookingController = new BookingController();
        this.customerController = new CustomerManagementController();

        setLayout(new BorderLayout());
        initComponents();
    }

    private void initComponents() {
        JTabbedPane tabbedPane = new JTabbedPane();

        // Tab 1: Search & Book
        JPanel searchTab = new JPanel(new BorderLayout());
        flightSearchView = new FlightSearchView(searchController);
        searchTab.add(flightSearchView, BorderLayout.CENTER);

        JPanel bookBtnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton bookBtn = new JButton("Book Selected Flight");
        bookBtn.setBackground(new Color(33, 115, 70));
        bookBtn.setForeground(Color.WHITE);
        bookBtn.setFocusPainted(false);
        bookBtn.addActionListener(e -> bookSelectedFlight());
        bookBtnPanel.add(bookBtn);
        searchTab.add(bookBtnPanel, BorderLayout.SOUTH);

        tabbedPane.addTab("Search & Book Flights", searchTab);

        // Tab 2: My Reservations
        JPanel reservationsTab = createReservationsTab();
        tabbedPane.addTab("My Reservations", reservationsTab);

        // Tab 3: My Profile
        JPanel profileTab = createProfileTab();
        tabbedPane.addTab("My Profile", profileTab);

        // Refresh bookings when switching to My Reservations tab
        tabbedPane.addChangeListener(e -> {
            if (tabbedPane.getSelectedIndex() == 1) {
                loadReservations();
            }
        });

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createReservationsTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] cols = {"ID", "Code", "Flight #", "Airline", "Route", "Departure", "Seat", "Status", "Date Booked"};
        bookingsModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        bookingsTable = new JTable(bookingsModel);
        bookingsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bookingsTable.setRowHeight(24);
        bookingsTable.getColumnModel().getColumn(0).setMinWidth(0);
        bookingsTable.getColumnModel().getColumn(0).setMaxWidth(0);
        bookingsTable.getColumnModel().getColumn(0).setWidth(0);

        panel.add(new JScrollPane(bookingsTable), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> loadReservations());

        JButton cancelBtn = new JButton("Cancel Reservation");
        cancelBtn.setForeground(new Color(180, 40, 40));
        cancelBtn.addActionListener(e -> cancelSelectedReservation());

        JButton modifyBtn = new JButton("Modify Seat");
        modifyBtn.addActionListener(e -> modifySelectedSeat());

        btnPanel.add(refreshBtn);
        btnPanel.add(modifyBtn);
        btnPanel.add(cancelBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createProfileTab() {
        JPanel outer = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Customer Profile Information"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        form.add(new JLabel("First Name:"), gbc);
        gbc.gridx = 1;
        firstNameField = new JTextField(customer.getFirstName(), 20);
        form.add(firstNameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        form.add(new JLabel("Last Name:"), gbc);
        gbc.gridx = 1;
        lastNameField = new JTextField(customer.getLastName(), 20);
        form.add(lastNameField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        form.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        emailField = new JTextField(customer.getEmail(), 20);
        form.add(emailField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        form.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1;
        phoneField = new JTextField(customer.getPhone(), 20);
        form.add(phoneField, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        form.add(new JLabel("Address:"), gbc);
        gbc.gridx = 1;
        addressField = new JTextField(customer.getAddress(), 20);
        form.add(addressField, gbc);

        gbc.gridx = 1; gbc.gridy = 5;
        JButton saveBtn = new JButton("Save Profile Changes");
        saveBtn.setBackground(new Color(33, 115, 70));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setFocusPainted(false);
        saveBtn.addActionListener(e -> saveProfile());
        form.add(saveBtn, gbc);

        outer.add(form);
        return outer;
    }

    private void bookSelectedFlight() {
        Flight selected = flightSearchView.getSelectedFlight();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select a flight to book from the search results table.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        BookingDialog dialog = new BookingDialog(parentFrame, customer, selected);
        dialog.setVisible(true);

        if (dialog.getCreatedReservation() != null) {
            flightSearchView.performSearch(); // update seat count
            loadReservations();
        }
    }

    private void loadReservations() {
        currentReservations = bookingController.getCustomerReservations(customer.getCustomerId());
        bookingsModel.setRowCount(0);

        for (Reservation r : currentReservations) {
            Flight f = r.getFlight();
            Seat s = r.getSeat();
            bookingsModel.addRow(new Object[]{
                    r.getReservationId(),
                    r.getReservationCode(),
                    f != null ? f.getFlightNumber() : "N/A",
                    f != null ? f.getAirline() : "N/A",
                    f != null ? f.getOrigin() + " -> " + f.getDestination() : "N/A",
                    f != null ? f.getDepartureTime() : "N/A",
                    s != null ? s.getSeatNumber() + " (" + s.getSeatClass().getDisplayName() + ")" : "N/A",
                    r.getStatus().getDisplayName(),
                    r.getBookingDate()
            });
        }
    }

    private void cancelSelectedReservation() {
        int selectedRow = bookingsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a reservation to cancel.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int resId = (int) bookingsModel.getValueAt(selectedRow, 0);
        Reservation res = null;
        for (Reservation r : currentReservations) {
            if (r.getReservationId() == resId) {
                res = r;
                break;
            }
        }

        if (res == null || res.getStatus() == ReservationStatus.CANCELLED) {
            JOptionPane.showMessageDialog(this, "This reservation is already cancelled.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to cancel reservation " + res.getReservationCode() + "?\nYour reserved seat will be released.",
                "Confirm Cancellation", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = bookingController.cancelBooking(resId);
            if (success) {
                JOptionPane.showMessageDialog(this, "Reservation cancelled successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadReservations();
                flightSearchView.performSearch();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to cancel reservation.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void modifySelectedSeat() {
        int selectedRow = bookingsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a reservation to modify.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int resId = (int) bookingsModel.getValueAt(selectedRow, 0);
        Reservation res = null;
        for (Reservation r : currentReservations) {
            if (r.getReservationId() == resId) {
                res = r;
                break;
            }
        }

        if (res == null || res.getStatus() == ReservationStatus.CANCELLED) {
            JOptionPane.showMessageDialog(this, "Cannot modify a cancelled reservation.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<Seat> availableSeats = searchController.getAvailableSeats(res.getFlightId());
        if (availableSeats.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No other seats are currently available on this flight.", "No Seats", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JComboBox<Seat> combo = new JComboBox<>(availableSeats.toArray(new Seat[0]));
        int opt = JOptionPane.showConfirmDialog(this, combo, "Select New Seat for " + res.getReservationCode(), JOptionPane.OK_CANCEL_OPTION);
        if (opt == JOptionPane.OK_OPTION) {
            Seat newSeat = (Seat) combo.getSelectedItem();
            if (newSeat != null) {
                boolean ok = bookingController.modifyBookingSeat(resId, newSeat.getSeatId());
                if (ok) {
                    JOptionPane.showMessageDialog(this, "Seat successfully changed to " + newSeat.getSeatNumber() + "!", "Modified", JOptionPane.INFORMATION_MESSAGE);
                    loadReservations();
                    flightSearchView.performSearch();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to modify seat.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void saveProfile() {
        customer.setFirstName(firstNameField.getText().trim());
        customer.setLastName(lastNameField.getText().trim());
        customer.setEmail(emailField.getText().trim());
        customer.setPhone(phoneField.getText().trim());
        customer.setAddress(addressField.getText().trim());

        boolean ok = customerController.updateCustomerProfile(customer);
        if (ok) {
            JOptionPane.showMessageDialog(this, "Profile updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update profile. Email must be unique.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
