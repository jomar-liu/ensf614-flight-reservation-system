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
 * Flight Agent dashboard for managing customer bookings, profiles, and flight schedules.
 * Stereotype: <<boundary>>
 */
public class AgentDashboardView extends JPanel {
    private final BookingController bookingController;
    private final CustomerManagementController customerController;
    private final FlightSearchController searchController;

    private JTable bookingsTable;
    private DefaultTableModel bookingsModel;
    private List<Reservation> allReservations;

    private JTable customersTable;
    private DefaultTableModel customersModel;

    public AgentDashboardView() {
        this.bookingController = new BookingController();
        this.customerController = new CustomerManagementController();
        this.searchController = new FlightSearchController();

        setLayout(new BorderLayout());
        initComponents();
    }

    private void initComponents() {
        JTabbedPane tabbedPane = new JTabbedPane();

        // Tab 1: All Bookings Management
        tabbedPane.addTab("Manage Reservations", createReservationsPanel());

        // Tab 2: Flight Schedules
        JPanel schedulesPanel = new JPanel(new BorderLayout());
        FlightSearchView searchView = new FlightSearchView(searchController);
        schedulesPanel.add(searchView, BorderLayout.CENTER);
        tabbedPane.addTab("Flight Schedules", schedulesPanel);

        // Tab 3: Customer Profiles
        tabbedPane.addTab("Customer Profiles", createCustomersPanel());

        add(tabbedPane, BorderLayout.CENTER);
        loadAllReservations();
        loadAllCustomers();
    }

    private JPanel createReservationsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] cols = {"ID", "Code", "Customer", "Flight #", "Route", "Departure", "Seat", "Status", "Booked At"};
        bookingsModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
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
        refreshBtn.addActionListener(e -> loadAllReservations());

        JButton modifyBtn = new JButton("Modify Customer Seat");
        modifyBtn.addActionListener(e -> modifySeat());

        JButton cancelBtn = new JButton("Cancel Reservation");
        cancelBtn.setForeground(new Color(180, 40, 40));
        cancelBtn.addActionListener(e -> cancelReservation());

        btnPanel.add(refreshBtn);
        btnPanel.add(modifyBtn);
        btnPanel.add(cancelBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createCustomersPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] cols = {"Customer ID", "Username", "Name", "Email", "Phone", "Address"};
        customersModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        customersTable = new JTable(customersModel);
        customersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        customersTable.setRowHeight(24);

        panel.add(new JScrollPane(customersTable), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton refreshBtn = new JButton("Refresh Customers");
        refreshBtn.addActionListener(e -> loadAllCustomers());
        btnPanel.add(refreshBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void loadAllReservations() {
        allReservations = bookingController.getAllReservations();
        bookingsModel.setRowCount(0);

        for (Reservation r : allReservations) {
            Customer c = r.getCustomer();
            Flight f = r.getFlight();
            Seat s = r.getSeat();

            bookingsModel.addRow(new Object[]{
                    r.getReservationId(),
                    r.getReservationCode(),
                    c != null ? c.getFullName() : "N/A",
                    f != null ? f.getFlightNumber() : "N/A",
                    f != null ? f.getOrigin() + " -> " + f.getDestination() : "N/A",
                    f != null ? f.getDepartureTime() : "N/A",
                    s != null ? s.getSeatNumber() : "N/A",
                    r.getStatus().getDisplayName(),
                    r.getBookingDate()
            });
        }
    }

    private void loadAllCustomers() {
        List<Customer> list = customerController.getAllCustomers();
        customersModel.setRowCount(0);

        for (Customer c : list) {
            customersModel.addRow(new Object[]{
                    c.getCustomerId(),
                    c.getUsername(),
                    c.getFullName(),
                    c.getEmail(),
                    c.getPhone(),
                    c.getAddress()
            });
        }
    }

    private void cancelReservation() {
        int row = bookingsTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a reservation to cancel.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int resId = (int) bookingsModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Cancel reservation #" + bookingsModel.getValueAt(row, 1) + "?",
                "Confirm Agent Cancellation", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean ok = bookingController.cancelBooking(resId);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Reservation cancelled.", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadAllReservations();
            } else {
                JOptionPane.showMessageDialog(this, "Cancellation failed.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void modifySeat() {
        int row = bookingsTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a reservation.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int resId = (int) bookingsModel.getValueAt(row, 0);
        Reservation res = null;
        for (Reservation r : allReservations) {
            if (r.getReservationId() == resId) {
                res = r;
                break;
            }
        }

        if (res == null || res.getStatus() == ReservationStatus.CANCELLED) {
            JOptionPane.showMessageDialog(this, "Cannot modify cancelled reservation.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<Seat> availableSeats = searchController.getAvailableSeats(res.getFlightId());
        if (availableSeats.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No other seats available on this flight.", "No Seats", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JComboBox<Seat> combo = new JComboBox<>(availableSeats.toArray(new Seat[0]));
        int opt = JOptionPane.showConfirmDialog(this, combo, "Select New Seat for " + res.getReservationCode(), JOptionPane.OK_CANCEL_OPTION);
        if (opt == JOptionPane.OK_OPTION) {
            Seat newSeat = (Seat) combo.getSelectedItem();
            if (newSeat != null) {
                boolean ok = bookingController.modifyBookingSeat(resId, newSeat.getSeatId());
                if (ok) {
                    JOptionPane.showMessageDialog(this, "Seat successfully changed!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadAllReservations();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to modify seat.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
}
