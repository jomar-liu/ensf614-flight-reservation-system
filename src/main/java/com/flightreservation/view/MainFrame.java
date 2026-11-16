package com.flightreservation.view;

import com.flightreservation.controller.AuthController;
import com.flightreservation.controller.PromotionController;
import com.flightreservation.model.Customer;
import com.flightreservation.model.Promotion;
import com.flightreservation.model.User;
import com.flightreservation.model.UserRole;

import javax.swing.*;
import java.awt.*;

/**
 * Main application window coordinating role-based presentation views.
 * Stereotype: <<boundary>>
 */
public class MainFrame extends JFrame {
    private static final String CARD_LOGIN = "LOGIN";
    private static final String CARD_CUSTOMER = "CUSTOMER";
    private static final String CARD_AGENT = "AGENT";
    private static final String CARD_ADMIN = "ADMIN";

    private final AuthController authController;
    private final PromotionController promotionController;

    private CardLayout cardLayout;
    private JPanel mainContainer;
    private JPanel topBar;
    private JLabel userStatusLabel;
    private JButton logoutButton;

    public MainFrame() {
        super("Flight Reservation System - ENSF 614");
        this.authController = new AuthController();
        this.promotionController = new PromotionController();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1024, 680);
        setMinimumSize(new Dimension(860, 560));
        setLocationRelativeTo(null);

        initComponents();
        showLoginView();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Top Navigation / Status Bar
        topBar = new JPanel(new BorderLayout(15, 0));
        topBar.setBackground(new Color(25, 45, 75));
        topBar.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel brandLabel = new JLabel("Flight Reservation System");
        brandLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        brandLabel.setForeground(Color.WHITE);
        topBar.add(brandLabel, BorderLayout.WEST);

        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        userPanel.setOpaque(false);

        userStatusLabel = new JLabel("");
        userStatusLabel.setForeground(new Color(220, 230, 245));
        userStatusLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));

        logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("SansSerif", Font.PLAIN, 12));
        logoutButton.setFocusPainted(false);
        logoutButton.addActionListener(e -> logout());

        userPanel.add(userStatusLabel);
        userPanel.add(logoutButton);
        topBar.add(userPanel, BorderLayout.EAST);

        add(topBar, BorderLayout.NORTH);

        // Main Card Container
        cardLayout = new CardLayout();
        mainContainer = new JPanel(cardLayout);

        // Login Card
        LoginView loginView = new LoginView(authController, this::handleLoginSuccess);
        mainContainer.add(loginView, CARD_LOGIN);

        add(mainContainer, BorderLayout.CENTER);
    }

    private void showLoginView() {
        topBar.setVisible(false);
        cardLayout.show(mainContainer, CARD_LOGIN);
    }

    private void handleLoginSuccess(User user) {
        topBar.setVisible(true);
        userStatusLabel.setText("Logged in as: " + user.getUsername() + " (" + user.getRole().getDisplayName() + ")");

        if (user.getRole() == UserRole.CUSTOMER) {
            Customer customer = (Customer) user;
            CustomerDashboardView custView = new CustomerDashboardView(this, customer);
            mainContainer.add(custView, CARD_CUSTOMER);
            cardLayout.show(mainContainer, CARD_CUSTOMER);

            // Check Requirement 3.7: Monthly Promotional News notification
            SwingUtilities.invokeLater(() -> {
                Promotion promo = promotionController.getEligibleMonthlyPromotion(customer.getUserId());
                if (promo != null) {
                    PromotionDialog promoDialog = new PromotionDialog(this, promo);
                    promoDialog.setVisible(true);
                }
            });

        } else if (user.getRole() == UserRole.FLIGHT_AGENT) {
            AgentDashboardView agentView = new AgentDashboardView();
            mainContainer.add(agentView, CARD_AGENT);
            cardLayout.show(mainContainer, CARD_AGENT);

        } else if (user.getRole() == UserRole.ADMIN) {
            AdminDashboardView adminView = new AdminDashboardView(this);
            mainContainer.add(adminView, CARD_ADMIN);
            cardLayout.show(mainContainer, CARD_ADMIN);
        }
    }

    private void logout() {
        int opt = JOptionPane.showConfirmDialog(this, "Are you sure you want to log out?", "Confirm Logout", JOptionPane.YES_NO_OPTION);
        if (opt == JOptionPane.YES_OPTION) {
            authController.logout();
            showLoginView();
        }
    }
}
