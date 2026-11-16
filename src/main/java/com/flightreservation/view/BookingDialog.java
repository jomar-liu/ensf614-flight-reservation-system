package com.flightreservation.view;

import com.flightreservation.controller.BookingController;
import com.flightreservation.controller.FlightSearchController;
import com.flightreservation.model.Customer;
import com.flightreservation.model.Flight;
import com.flightreservation.model.Reservation;
import com.flightreservation.model.Seat;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Dialog for selecting a seat and confirming flight reservation.
 * Stereotype: <<boundary>>
 */
public class BookingDialog extends JDialog {
    private final FlightSearchController searchController;
    private final BookingController bookingController;
    private final Customer customer;
    private final Flight flight;

    private JComboBox<Seat> seatCombo;
    private JLabel seatPriceLabel;
    private Reservation createdReservation;

    public BookingDialog(Frame parent, Customer customer, Flight flight) {
        super(parent, "Book Flight: " + flight.getFlightNumber(), true);
        this.searchController = new FlightSearchController();
        this.bookingController = new BookingController();
        this.customer = customer;
        this.flight = flight;

        setSize(480, 360);
        setLocationRelativeTo(parent);
        setResizable(false);
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Flight Info Box
        JPanel flightBox = new JPanel(new GridLayout(3, 2, 5, 5));
        flightBox.setBorder(BorderFactory.createTitledBorder("Selected Flight Details"));
        flightBox.add(new JLabel("Flight / Airline:"));
        flightBox.add(new JLabel(flight.getFlightNumber() + " (" + flight.getAirline() + ")"));
        flightBox.add(new JLabel("Route:"));
        flightBox.add(new JLabel(flight.getOrigin() + " -> " + flight.getDestination()));
        flightBox.add(new JLabel("Departure Time:"));
        flightBox.add(new JLabel(flight.getDepartureTime()));
        formPanel.add(flightBox);
        formPanel.add(Box.createVerticalStrut(10));

        // Passenger Info Box
        JPanel passengerBox = new JPanel(new GridLayout(2, 2, 5, 5));
        passengerBox.setBorder(BorderFactory.createTitledBorder("Passenger Details"));
        passengerBox.add(new JLabel("Passenger Name:"));
        passengerBox.add(new JLabel(customer.getFullName()));
        passengerBox.add(new JLabel("Contact Email:"));
        passengerBox.add(new JLabel(customer.getEmail()));
        formPanel.add(passengerBox);
        formPanel.add(Box.createVerticalStrut(10));

        // Seat Selection
        JPanel seatPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        seatPanel.setBorder(BorderFactory.createTitledBorder("Seat Selection"));
        seatCombo = new JComboBox<>();
        List<Seat> seats = searchController.getAvailableSeats(flight.getFlightId());
        for (Seat s : seats) {
            seatCombo.addItem(s);
        }

        seatPriceLabel = new JLabel();
        updateSeatPrice();

        seatCombo.addActionListener(e -> updateSeatPrice());
        seatPanel.add(new JLabel("Available Seat:"));
        seatPanel.add(seatCombo);
        seatPanel.add(seatPriceLabel);
        formPanel.add(seatPanel);

        add(formPanel, BorderLayout.CENTER);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.addActionListener(e -> dispose());

        JButton proceedBtn = new JButton("Proceed to Payment");
        proceedBtn.setBackground(new Color(33, 115, 70));
        proceedBtn.setForeground(Color.WHITE);
        proceedBtn.setFocusPainted(false);
        proceedBtn.setEnabled(!seats.isEmpty());
        proceedBtn.addActionListener(e -> proceedToPayment());

        btnPanel.add(cancelBtn);
        btnPanel.add(proceedBtn);
        add(btnPanel, BorderLayout.SOUTH);
    }

    private void updateSeatPrice() {
        Seat selected = (Seat) seatCombo.getSelectedItem();
        if (selected != null) {
            seatPriceLabel.setText(String.format("Price: $%.2f (%s)", selected.getPrice(), selected.getSeatClass().getDisplayName()));
        } else {
            seatPriceLabel.setText("No seats available");
        }
    }

    private void proceedToPayment() {
        Seat selectedSeat = (Seat) seatCombo.getSelectedItem();
        if (selectedSeat == null) {
            JOptionPane.showMessageDialog(this, "Please select an available seat.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        PaymentDialog paymentDialog = new PaymentDialog((Frame) getOwner(), flight, selectedSeat);
        paymentDialog.setVisible(true);

        if (paymentDialog.isPaymentConfirmed()) {
            createdReservation = bookingController.createBooking(
                    customer.getCustomerId(),
                    flight.getFlightId(),
                    selectedSeat.getSeatId(),
                    paymentDialog.getSelectedMethod(),
                    paymentDialog.getMaskedCardNumber(),
                    paymentDialog.getTotalAmount()
            );

            if (createdReservation != null) {
                JOptionPane.showMessageDialog(this,
                        "Booking successfully confirmed!\n\n" +
                        "Confirmation Code: " + createdReservation.getReservationCode() + "\n" +
                        "Flight: " + flight.getFlightNumber() + "\n" +
                        "Seat: " + selectedSeat.getSeatNumber() + " (" + selectedSeat.getSeatClass().getDisplayName() + ")\n" +
                        "Amount Paid: $" + String.format("%.2f", paymentDialog.getTotalAmount()),
                        "Booking Confirmed", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Unable to complete booking. The selected seat may have just been reserved.",
                        "Booking Failed", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public Reservation getCreatedReservation() {
        return createdReservation;
    }
}
