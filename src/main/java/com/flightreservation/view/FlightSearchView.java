package com.flightreservation.view;

import com.flightreservation.controller.FlightSearchController;
import com.flightreservation.model.Flight;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Reusable Flight Search and display panel.
 * Stereotype: <<boundary>>
 */
public class FlightSearchView extends JPanel {
    private final FlightSearchController searchController;
    private JComboBox<String> originCombo;
    private JComboBox<String> destCombo;
    private JComboBox<String> airlineCombo;
    private JTextField dateField;
    private JTable flightTable;
    private DefaultTableModel tableModel;
    private List<Flight> currentResults;

    public FlightSearchView(FlightSearchController searchController) {
        this.searchController = searchController;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        initComponents();
        loadFilters();
        performSearch();
    }

    private void initComponents() {
        // Top Filter Panel
        JPanel filterPanel = new JPanel(new GridBagLayout());
        filterPanel.setBorder(BorderFactory.createTitledBorder("Search Flights"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Row 1: Origin & Destination
        gbc.gridx = 0; gbc.gridy = 0;
        filterPanel.add(new JLabel("Origin:"), gbc);
        gbc.gridx = 1;
        originCombo = new JComboBox<>();
        filterPanel.add(originCombo, gbc);

        gbc.gridx = 2;
        filterPanel.add(new JLabel("Destination:"), gbc);
        gbc.gridx = 3;
        destCombo = new JComboBox<>();
        filterPanel.add(destCombo, gbc);

        // Row 2: Date & Airline
        gbc.gridx = 0; gbc.gridy = 1;
        filterPanel.add(new JLabel("Departure Date (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1;
        dateField = new JTextField(10);
        filterPanel.add(dateField, gbc);

        gbc.gridx = 2;
        filterPanel.add(new JLabel("Airline:"), gbc);
        gbc.gridx = 3;
        airlineCombo = new JComboBox<>();
        filterPanel.add(airlineCombo, gbc);

        // Buttons
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 4;
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton searchBtn = new JButton("Search Flights");
        searchBtn.setBackground(new Color(33, 115, 70));
        searchBtn.setForeground(Color.WHITE);
        searchBtn.setFocusPainted(false);
        searchBtn.addActionListener(e -> performSearch());

        JButton resetBtn = new JButton("Reset Filters");
        resetBtn.addActionListener(e -> {
            originCombo.setSelectedIndex(0);
            destCombo.setSelectedIndex(0);
            airlineCombo.setSelectedIndex(0);
            dateField.setText("");
            performSearch();
        });

        btnPanel.add(resetBtn);
        btnPanel.add(searchBtn);
        filterPanel.add(btnPanel, gbc);

        add(filterPanel, BorderLayout.NORTH);

        // Flight Table
        String[] columns = {"ID", "Flight #", "Airline", "Origin", "Destination", "Departure", "Arrival", "Base Price", "Seats Available", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // read-only
            }
        };

        flightTable = new JTable(tableModel);
        flightTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        flightTable.setRowHeight(24);
        flightTable.getTableHeader().setReorderingAllowed(false);
        // Hide ID column
        flightTable.getColumnModel().getColumn(0).setMinWidth(0);
        flightTable.getColumnModel().getColumn(0).setMaxWidth(0);
        flightTable.getColumnModel().getColumn(0).setWidth(0);

        JScrollPane scrollPane = new JScrollPane(flightTable);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void loadFilters() {
        originCombo.removeAllItems();
        originCombo.addItem("ALL");
        for (String o : searchController.getAvailableOrigins()) {
            originCombo.addItem(o);
        }

        destCombo.removeAllItems();
        destCombo.addItem("ALL");
        for (String d : searchController.getAvailableDestinations()) {
            destCombo.addItem(d);
        }

        airlineCombo.removeAllItems();
        airlineCombo.addItem("ALL");
        for (String a : searchController.getAvailableAirlines()) {
            airlineCombo.addItem(a);
        }
    }

    public void performSearch() {
        String origin = (String) originCombo.getSelectedItem();
        String dest = (String) destCombo.getSelectedItem();
        String airline = (String) airlineCombo.getSelectedItem();
        String date = dateField.getText().trim();

        currentResults = searchController.searchFlights(origin, dest, date, airline);
        tableModel.setRowCount(0);

        for (Flight f : currentResults) {
            tableModel.addRow(new Object[]{
                    f.getFlightId(),
                    f.getFlightNumber(),
                    f.getAirline(),
                    f.getOrigin(),
                    f.getDestination(),
                    f.getDepartureTime(),
                    f.getArrivalTime(),
                    String.format("$%.2f", f.getBasePrice()),
                    f.getAvailableSeatsCount(),
                    f.getStatus().getDisplayName()
            });
        }
    }

    public Flight getSelectedFlight() {
        int selectedRow = flightTable.getSelectedRow();
        if (selectedRow < 0 || currentResults == null || selectedRow >= currentResults.size()) {
            return null;
        }
        int flightId = (int) tableModel.getValueAt(selectedRow, 0);
        for (Flight f : currentResults) {
            if (f.getFlightId() == flightId) {
                return f;
            }
        }
        return null;
    }
}
