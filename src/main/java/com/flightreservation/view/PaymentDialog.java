package com.flightreservation.view;

import com.flightreservation.controller.PaymentController;
import com.flightreservation.model.Flight;
import com.flightreservation.model.PaymentMethod;
import com.flightreservation.model.Seat;

import javax.swing.*;
import java.awt.*;

/**
 * Dialog for simulated payment processing.
 * Stereotype: <<boundary>>
 */
public class PaymentDialog extends JDialog {
    private final PaymentController paymentController;
    private final Flight flight;
    private final Seat seat;

    private double totalAmount;
    private double discountPercentage = 0.0;
    private boolean paymentConfirmed = false;
    private PaymentMethod selectedMethod = PaymentMethod.CREDIT_CARD;
    private String maskedCardNumber = "N/A";

    private JLabel totalLabel;
    private JTextField promoField;
    private JTextField cardNumField;
    private JTextField expiryField;
    private JTextField cvvField;
    private JRadioButton creditRadio;
    private JRadioButton debitRadio;
    private JRadioButton paypalRadio;

    public PaymentDialog(Frame parent, Flight flight, Seat seat) {
        super(parent, "Payment Processing (Simulation)", true);
        this.paymentController = new PaymentController();
        this.flight = flight;
        this.seat = seat;
        this.totalAmount = seat.getPrice();

        setSize(480, 420);
        setLocationRelativeTo(parent);
        setResizable(false);
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Order Summary Box
        JPanel summaryBox = new JPanel(new GridLayout(4, 2, 5, 5));
        summaryBox.setBorder(BorderFactory.createTitledBorder("Booking Summary"));
        summaryBox.add(new JLabel("Flight:"));
        summaryBox.add(new JLabel(flight.getFlightNumber() + " (" + flight.getAirline() + ")"));
        summaryBox.add(new JLabel("Route:"));
        summaryBox.add(new JLabel(flight.getOrigin() + " -> " + flight.getDestination()));
        summaryBox.add(new JLabel("Seat:"));
        summaryBox.add(new JLabel(seat.getSeatNumber() + " (" + seat.getSeatClass().getDisplayName() + ")"));
        summaryBox.add(new JLabel("Base Fare:"));
        summaryBox.add(new JLabel(String.format("$%.2f", seat.getPrice())));
        contentPanel.add(summaryBox);
        contentPanel.add(Box.createVerticalStrut(10));

        // Promotion Code Panel
        JPanel promoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        promoPanel.setBorder(BorderFactory.createTitledBorder("Promotional Discount"));
        promoField = new JTextField(10);
        JButton applyPromoBtn = new JButton("Apply Code");
        applyPromoBtn.addActionListener(e -> applyPromotion());
        promoPanel.add(new JLabel("Promo Code:"));
        promoPanel.add(promoField);
        promoPanel.add(applyPromoBtn);
        contentPanel.add(promoPanel);
        contentPanel.add(Box.createVerticalStrut(10));

        // Payment Method & Card Details
        JPanel payPanel = new JPanel(new GridBagLayout());
        payPanel.setBorder(BorderFactory.createTitledBorder("Simulated Payment Method"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        creditRadio = new JRadioButton("Credit Card", true);
        debitRadio = new JRadioButton("Debit Card");
        paypalRadio = new JRadioButton("PayPal");
        ButtonGroup bg = new ButtonGroup();
        bg.add(creditRadio);
        bg.add(debitRadio);
        bg.add(paypalRadio);

        JPanel methodRadioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        methodRadioPanel.add(creditRadio);
        methodRadioPanel.add(debitRadio);
        methodRadioPanel.add(paypalRadio);

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        payPanel.add(methodRadioPanel, gbc);

        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 1;
        payPanel.add(new JLabel("Card Number:"), gbc);
        gbc.gridx = 1;
        cardNumField = new JTextField("4500123456789012", 16);
        payPanel.add(cardNumField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        payPanel.add(new JLabel("Expiry (MM/YY):"), gbc);
        gbc.gridx = 1;
        expiryField = new JTextField("12/28", 5);
        payPanel.add(expiryField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        payPanel.add(new JLabel("CVV:"), gbc);
        gbc.gridx = 1;
        cvvField = new JTextField("123", 4);
        payPanel.add(cvvField, gbc);

        contentPanel.add(payPanel);
        contentPanel.add(Box.createVerticalStrut(10));

        // Total Price & Actions
        totalLabel = new JLabel(String.format("Total Payable: $%.2f", totalAmount));
        totalLabel.setFont(new Font("SansSerif", Font.BOLD, 15));
        totalLabel.setForeground(new Color(33, 115, 70));
        contentPanel.add(totalLabel);

        add(contentPanel, BorderLayout.CENTER);

        // Buttons
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.addActionListener(e -> dispose());

        JButton payBtn = new JButton("Confirm & Pay");
        payBtn.setBackground(new Color(33, 115, 70));
        payBtn.setForeground(Color.WHITE);
        payBtn.setFocusPainted(false);
        payBtn.addActionListener(e -> processPayment());

        actionPanel.add(cancelBtn);
        actionPanel.add(payBtn);
        add(actionPanel, BorderLayout.SOUTH);
    }

    private void applyPromotion() {
        String code = promoField.getText().trim().toUpperCase();
        if (code.equals("WINTER15")) {
            discountPercentage = 15.0;
            updateTotal();
            JOptionPane.showMessageDialog(this, "Winter Holiday Promo applied! 15% discount.", "Promo Applied", JOptionPane.INFORMATION_MESSAGE);
        } else if (code.equals("ROCKY20")) {
            discountPercentage = 20.0;
            updateTotal();
            JOptionPane.showMessageDialog(this, "Rocky Mountain Promo applied! 20% discount.", "Promo Applied", JOptionPane.INFORMATION_MESSAGE);
        } else if (code.equals("EARLY10")) {
            discountPercentage = 10.0;
            updateTotal();
            JOptionPane.showMessageDialog(this, "Early Bird Promo applied! 10% discount.", "Promo Applied", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Invalid or expired promotional code.", "Promo Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void updateTotal() {
        totalAmount = seat.getPrice() * (1.0 - (discountPercentage / 100.0));
        totalLabel.setText(String.format("Total Payable: $%.2f (%.0f%% off)", totalAmount, discountPercentage));
    }

    private void processPayment() {
        if (paypalRadio.isSelected()) {
            selectedMethod = PaymentMethod.PAYPAL;
            maskedCardNumber = "PAYPAL-ACCOUNT";
        } else {
            selectedMethod = creditRadio.isSelected() ? PaymentMethod.CREDIT_CARD : PaymentMethod.DEBIT_CARD;
            String cardNum = cardNumField.getText();
            String expiry = expiryField.getText().trim();
            String cvv = cvvField.getText().trim();

            if (!paymentController.validatePaymentInput(selectedMethod, cardNum, expiry, cvv)) {
                JOptionPane.showMessageDialog(this,
                        "Please enter valid simulated card details:\n- Card number (min 12 digits)\n- Expiry (MM/YY)\n- CVV (3-4 digits)",
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            maskedCardNumber = paymentController.maskCardNumber(cardNum);
        }

        paymentConfirmed = true;
        dispose();
    }

    public boolean isPaymentConfirmed() {
        return paymentConfirmed;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public PaymentMethod getSelectedMethod() {
        return selectedMethod;
    }

    public String getMaskedCardNumber() {
        return maskedCardNumber;
    }
}
