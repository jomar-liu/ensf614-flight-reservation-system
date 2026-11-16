package com.flightreservation.view;

import com.flightreservation.controller.AuthController;
import com.flightreservation.model.User;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

/**
 * Login interface for role-based authentication and new customer registration.
 * Stereotype: <<boundary>>
 */
public class LoginView extends JPanel {
    private final AuthController authController;
    private final Consumer<User> onLoginSuccess;

    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginView(AuthController authController, Consumer<User> onLoginSuccess) {
        this.authController = authController;
        this.onLoginSuccess = onLoginSuccess;

        setLayout(new GridBagLayout());
        setBackground(new Color(245, 247, 250));
        initComponents();
    }

    private void initComponents() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 215, 225), 1),
                BorderFactory.createEmptyBorder(25, 30, 25, 30)
        ));
        card.setBackground(Color.WHITE);

        // Header Title
        JLabel titleLabel = new JLabel("Flight Reservation System");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        titleLabel.setForeground(new Color(25, 45, 75));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(titleLabel);

        JLabel subLabel = new JLabel("ENSF 614 Term Project - Desktop Simulation");
        subLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        subLabel.setForeground(Color.GRAY);
        subLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(subLabel);
        card.add(Box.createVerticalStrut(20));

        // Form Fields
        JPanel form = new JPanel(new GridLayout(2, 2, 8, 10));
        form.setOpaque(false);
        form.add(new JLabel("Username:"));
        usernameField = new JTextField("customer", 15);
        form.add(usernameField);

        form.add(new JLabel("Password:"));
        passwordField = new JPasswordField("customer123", 15);
        form.add(passwordField);

        card.add(form);
        card.add(Box.createVerticalStrut(15));

        // Action Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        btnPanel.setOpaque(false);

        JButton loginBtn = new JButton("Login");
        loginBtn.setBackground(new Color(33, 115, 70));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFocusPainted(false);
        loginBtn.setPreferredSize(new Dimension(100, 32));
        loginBtn.addActionListener(e -> attemptLogin());

        JButton registerBtn = new JButton("Register");
        registerBtn.setPreferredSize(new Dimension(100, 32));
        registerBtn.addActionListener(e -> showRegistrationDialog());

        btnPanel.add(loginBtn);
        btnPanel.add(registerBtn);
        card.add(btnPanel);
        card.add(Box.createVerticalStrut(20));

        // Demo Quick-Fill Buttons for testing
        JPanel demoBox = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        demoBox.setOpaque(false);
        demoBox.setBorder(BorderFactory.createTitledBorder("Quick Demo Login"));

        JButton demoCust = new JButton("Customer");
        demoCust.setFont(new Font("SansSerif", Font.PLAIN, 11));
        demoCust.addActionListener(e -> {
            usernameField.setText("customer");
            passwordField.setText("customer123");
        });

        JButton demoAgent = new JButton("Flight Agent");
        demoAgent.setFont(new Font("SansSerif", Font.PLAIN, 11));
        demoAgent.addActionListener(e -> {
            usernameField.setText("agent");
            passwordField.setText("agent123");
        });

        JButton demoAdmin = new JButton("Administrator");
        demoAdmin.setFont(new Font("SansSerif", Font.PLAIN, 11));
        demoAdmin.addActionListener(e -> {
            usernameField.setText("admin");
            passwordField.setText("admin123");
        });

        demoBox.add(demoCust);
        demoBox.add(demoAgent);
        demoBox.add(demoAdmin);
        card.add(demoBox);

        add(card);
    }

    private void attemptLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        User user = authController.login(username, password);
        if (user != null) {
            onLoginSuccess.accept(user);
        } else {
            JOptionPane.showMessageDialog(this, "Invalid username or password.", "Authentication Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showRegistrationDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "New Customer Registration", true);
        dialog.setSize(400, 320);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel form = new JPanel(new GridLayout(6, 2, 8, 8));
        form.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JTextField uField = new JTextField();
        JPasswordField pField = new JPasswordField();
        JTextField fnField = new JTextField();
        JTextField lnField = new JTextField();
        JTextField emField = new JTextField();
        JTextField phField = new JTextField();

        form.add(new JLabel("Username:")); form.add(uField);
        form.add(new JLabel("Password:")); form.add(pField);
        form.add(new JLabel("First Name:")); form.add(fnField);
        form.add(new JLabel("Last Name:")); form.add(lnField);
        form.add(new JLabel("Email Address:")); form.add(emField);
        form.add(new JLabel("Phone Number:")); form.add(phField);

        dialog.add(form, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.addActionListener(e -> dialog.dispose());

        JButton submitBtn = new JButton("Create Account");
        submitBtn.setBackground(new Color(33, 115, 70));
        submitBtn.setForeground(Color.WHITE);
        submitBtn.addActionListener(e -> {
            boolean ok = authController.registerCustomer(
                    uField.getText(),
                    new String(pField.getPassword()),
                    fnField.getText(),
                    lnField.getText(),
                    emField.getText(),
                    phField.getText(),
                    ""
            );
            if (ok) {
                JOptionPane.showMessageDialog(dialog, "Account created successfully! You can now login.", "Registration Success", JOptionPane.INFORMATION_MESSAGE);
                usernameField.setText(uField.getText().trim());
                passwordField.setText(new String(pField.getPassword()));
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "Registration failed. Username may already be in use.", "Registration Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnPanel.add(cancelBtn);
        btnPanel.add(submitBtn);
        dialog.add(btnPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }
}
