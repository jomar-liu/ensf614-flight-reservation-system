package com.flightreservation;

import com.flightreservation.data.DatabaseManager;
import com.flightreservation.view.MainFrame;

import javax.swing.*;

/**
 * Main application entry point for the Flight Reservation System.
 * Initializes SQLite database and launches the graphical user interface.
 */
public class Main {
    public static void main(String[] args) {
        // Set native look and feel for modern desktop appearance
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        // Initialize SQLite database schema and seed data
        DatabaseManager.getInstance().initializeDatabase();

        // Launch GUI on Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame();
            mainFrame.setVisible(true);
        });
    }
}
