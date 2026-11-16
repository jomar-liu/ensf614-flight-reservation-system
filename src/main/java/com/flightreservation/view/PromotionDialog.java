package com.flightreservation.view;

import com.flightreservation.model.Promotion;

import javax.swing.*;
import java.awt.*;

/**
 * Modal dialog displaying the randomly selected monthly promotional offer.
 * Stereotype: <<boundary>>
 */
public class PromotionDialog extends JDialog {

    public PromotionDialog(Frame parent, Promotion promotion) {
        super(parent, "Exclusive Monthly Promotion!", true);
        setSize(420, 260);
        setLocationRelativeTo(parent);
        setResizable(false);
        initComponents(promotion);
    }

    private void initComponents(Promotion promotion) {
        setLayout(new BorderLayout(15, 15));

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 15, 20));
        panel.setBackground(new Color(248, 249, 250));

        JLabel tagLabel = new JLabel("SPECIAL MONTHLY OFFER");
        tagLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        tagLabel.setForeground(new Color(220, 53, 69));
        tagLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(tagLabel);
        panel.add(Box.createVerticalStrut(8));

        JLabel titleLabel = new JLabel(promotion.getTitle());
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(10));

        JTextArea descArea = new JTextArea(promotion.getDescription());
        descArea.setWrapStyleWord(true);
        descArea.setLineWrap(true);
        descArea.setEditable(false);
        descArea.setOpaque(false);
        descArea.setFont(new Font("SansSerif", Font.PLAIN, 13));
        descArea.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(descArea);
        panel.add(Box.createVerticalStrut(15));

        JPanel codeBox = new JPanel(new FlowLayout(FlowLayout.CENTER));
        codeBox.setBorder(BorderFactory.createDashedBorder(new Color(33, 115, 70), 2, 4, 2, true));
        codeBox.setBackground(new Color(230, 245, 235));
        JLabel codeLabel = new JLabel("Use Promo Code: " + promotion.getPromoCode() + " (" + String.format("%.0f", promotion.getDiscountPercentage()) + "% OFF)");
        codeLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        codeLabel.setForeground(new Color(33, 115, 70));
        codeBox.add(codeLabel);
        codeBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(codeBox);

        add(panel, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton okBtn = new JButton("Claim Discount & Continue");
        okBtn.setBackground(new Color(33, 115, 70));
        okBtn.setForeground(Color.WHITE);
        okBtn.setFocusPainted(false);
        okBtn.addActionListener(e -> dispose());
        btnPanel.add(okBtn);
        add(btnPanel, BorderLayout.SOUTH);
    }
}
