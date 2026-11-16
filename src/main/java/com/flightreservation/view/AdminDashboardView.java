package com.flightreservation.view;

import com.flightreservation.controller.FlightManagementController;
import com.flightreservation.model.Aircraft;
import com.flightreservation.model.Flight;
import com.flightreservation.model.FlightStatus;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * System Administrator dashboard for managing flights, routes, schedules, and aircraft.
 * Stereotype: <<boundary>>
 */
public class AdminDashboardView extends JPanel {
    private final FlightManagementController adminController;
    private final Frame parentFrame;

    private JTable flightTable;
    private DefaultTableModel flightModel;
    private List<Flight> flights;

    private JTable aircraftTable;
    private DefaultTableModel aircraftModel;

    public AdminDashboardView(Frame parentFrame) {
        this.parentFrame = parentFrame;
        this.adminController = new FlightManagementController();

        setLayout(new BorderLayout());
        initComponents();
    }

    private void initComponents() {
        JTabbedPane tabbedPane = new JTabbedPane();

        tabbedPane.addTab("Manage Flights & Schedules", createFlightsTab());
        tabbedPane.addTab("Manage Aircraft Fleet", createAircraftTab());

        add(tabbedPane, BorderLayout.CENTER);
        loadFlights();
        loadAircraft();
    }

    private JPanel createFlightsTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] cols = {"ID", "Flight #", "Airline", "Origin", "Destination", "Departure", "Arrival", "Base Price", "Aircraft", "Status"};
        flightModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        flightTable = new JTable(flightModel);
        flightTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        flightTable.setRowHeight(24);
        flightTable.getColumnModel().getColumn(0).setMinWidth(0);
        flightTable.getColumnModel().getColumn(0).setMaxWidth(0);
        flightTable.getColumnModel().getColumn(0).setWidth(0);

        panel.add(new JScrollPane(flightTable), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> loadFlights());

        JButton addBtn = new JButton("Add Flight");
        addBtn.setBackground(new Color(33, 115, 70));
        addBtn.setForeground(Color.WHITE);
        addBtn.setFocusPainted(false);
        addBtn.addActionListener(e -> showAddFlightDialog());

        JButton editBtn = new JButton("Update Status / Details");
        editBtn.addActionListener(e -> showEditFlightDialog());

        JButton removeBtn = new JButton("Remove Flight");
        removeBtn.setForeground(new Color(180, 40, 40));
        removeBtn.addActionListener(e -> removeSelectedFlight());

        btnPanel.add(refreshBtn);
        btnPanel.add(addBtn);
        btnPanel.add(editBtn);
        btnPanel.add(removeBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createAircraftTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] cols = {"Aircraft ID", "Aircraft Model", "Total Seats Capacity"};
        aircraftModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        aircraftTable = new JTable(aircraftModel);
        aircraftTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        aircraftTable.setRowHeight(24);

        panel.add(new JScrollPane(aircraftTable), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> loadAircraft());

        JButton addBtn = new JButton("Add Aircraft Model");
        addBtn.addActionListener(e -> showAddAircraftDialog());

        btnPanel.add(refreshBtn);
        btnPanel.add(addBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void loadFlights() {
        flights = adminController.getAllFlights();
        flightModel.setRowCount(0);

        for (Flight f : flights) {
            flightModel.addRow(new Object[]{
                    f.getFlightId(),
                    f.getFlightNumber(),
                    f.getAirline(),
                    f.getOrigin(),
                    f.getDestination(),
                    f.getDepartureTime(),
                    f.getArrivalTime(),
                    String.format("$%.2f", f.getBasePrice()),
                    f.getAircraft() != null ? f.getAircraft().getModel() : "Aircraft #" + f.getAircraftId(),
                    f.getStatus().getDisplayName()
            });
        }
    }

    private void loadAircraft() {
        List<Aircraft> fleet = adminController.getAllAircraft();
        aircraftModel.setRowCount(0);
        for (Aircraft a : fleet) {
            aircraftModel.addRow(new Object[]{a.getAircraftId(), a.getModel(), a.getTotalSeats()});
        }
    }

    private void showAddFlightDialog() {
        List<Aircraft> fleet = adminController.getAllAircraft();
        if (fleet.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please add at least one aircraft model first.", "Aircraft Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog(parentFrame, "Add New Flight", true);
        dialog.setSize(440, 380);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel form = new JPanel(new GridLayout(8, 2, 8, 8));
        form.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JTextField numField = new JTextField("AC305");
        JTextField airlineField = new JTextField("Air Canada");
        JTextField originField = new JTextField("Calgary (YYC)");
        JTextField destField = new JTextField("Edmonton (YEG)");
        JTextField depField = new JTextField("2026-12-05 11:00");
        JTextField arrField = new JTextField("2026-12-05 11:50");
        JTextField priceField = new JTextField("149.00");
        JComboBox<Aircraft> aircraftCombo = new JComboBox<>(fleet.toArray(new Aircraft[0]));

        form.add(new JLabel("Flight Number:")); form.add(numField);
        form.add(new JLabel("Airline:")); form.add(airlineField);
        form.add(new JLabel("Origin:")); form.add(originField);
        form.add(new JLabel("Destination:")); form.add(destField);
        form.add(new JLabel("Departure (YYYY-MM-DD HH:MM):")); form.add(depField);
        form.add(new JLabel("Arrival (YYYY-MM-DD HH:MM):")); form.add(arrField);
        form.add(new JLabel("Base Price ($):")); form.add(priceField);
        form.add(new JLabel("Aircraft:")); form.add(aircraftCombo);

        dialog.add(form, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.addActionListener(e -> dialog.dispose());

        JButton saveBtn = new JButton("Create Flight");
        saveBtn.setBackground(new Color(33, 115, 70));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.addActionListener(e -> {
            try {
                double price = Double.parseDouble(priceField.getText().trim());
                Aircraft selectedAircraft = (Aircraft) aircraftCombo.getSelectedItem();
                Flight newFlight = new Flight(0,
                        numField.getText().trim(),
                        airlineField.getText().trim(),
                        originField.getText().trim(),
                        destField.getText().trim(),
                        depField.getText().trim(),
                        arrField.getText().trim(),
                        price,
                        selectedAircraft.getAircraftId(),
                        FlightStatus.SCHEDULED);

                boolean ok = adminController.addFlight(newFlight);
                if (ok) {
                    JOptionPane.showMessageDialog(dialog, "Flight created and seats automatically generated!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    loadFlights();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed to create flight. Check flight number uniqueness.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(dialog, "Invalid price number format.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnPanel.add(cancelBtn);
        btnPanel.add(saveBtn);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void showEditFlightDialog() {
        int row = flightTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a flight to update.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int flightId = (int) flightModel.getValueAt(row, 0);
        Flight target = null;
        for (Flight f : flights) {
            if (f.getFlightId() == flightId) {
                target = f;
                break;
            }
        }
        if (target == null) return;

        JComboBox<FlightStatus> statusCombo = new JComboBox<>(FlightStatus.values());
        statusCombo.setSelectedItem(target.getStatus());

        JTextField priceField = new JTextField(String.valueOf(target.getBasePrice()), 10);

        JPanel editPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        editPanel.add(new JLabel("Flight Status:"));
        editPanel.add(statusCombo);
        editPanel.add(new JLabel("Base Price ($):"));
        editPanel.add(priceField);

        int opt = JOptionPane.showConfirmDialog(this, editPanel, "Update Flight " + target.getFlightNumber(), JOptionPane.OK_CANCEL_OPTION);
        if (opt == JOptionPane.OK_OPTION) {
            try {
                target.setStatus((FlightStatus) statusCombo.getSelectedItem());
                target.setBasePrice(Double.parseDouble(priceField.getText().trim()));
                boolean ok = adminController.updateFlight(target);
                if (ok) {
                    JOptionPane.showMessageDialog(this, "Flight updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadFlights();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update flight.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(this, "Invalid price format.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void removeSelectedFlight() {
        int row = flightTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a flight to remove.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int flightId = (int) flightModel.getValueAt(row, 0);
        String flightNum = (String) flightModel.getValueAt(row, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete flight " + flightNum + "?\nAll associated seats will be deleted.",
                "Confirm Flight Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean ok = adminController.removeFlight(flightId);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Flight deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadFlights();
            } else {
                JOptionPane.showMessageDialog(this, "Cannot delete flight with existing active reservations.", "Delete Blocked", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showAddAircraftDialog() {
        JTextField modelField = new JTextField(15);
        JTextField seatsField = new JTextField(5);

        JPanel p = new JPanel(new GridLayout(2, 2, 5, 5));
        p.add(new JLabel("Aircraft Model (e.g. Boeing 777):"));
        p.add(modelField);
        p.add(new JLabel("Total Capacity:"));
        p.add(seatsField);

        int opt = JOptionPane.showConfirmDialog(this, p, "Add New Aircraft", JOptionPane.OK_CANCEL_OPTION);
        if (opt == JOptionPane.OK_OPTION) {
            try {
                int seats = Integer.parseInt(seatsField.getText().trim());
                if (modelField.getText().trim().isEmpty() || seats <= 0) {
                    throw new IllegalArgumentException();
                }
                adminController.addAircraft(new Aircraft(0, modelField.getText().trim(), seats));
                loadAircraft();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid model name and positive seat count.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
