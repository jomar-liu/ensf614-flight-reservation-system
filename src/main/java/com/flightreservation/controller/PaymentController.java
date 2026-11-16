package com.flightreservation.controller;

import com.flightreservation.data.PaymentDAO;
import com.flightreservation.model.Payment;
import com.flightreservation.model.PaymentMethod;

/**
 * Controller coordinating simulated payment processing and validation.
 * Stereotype: <<control>>
 */
public class PaymentController {
    private final PaymentDAO paymentDAO;

    public PaymentController() {
        this.paymentDAO = new PaymentDAO();
    }

    public boolean validatePaymentInput(PaymentMethod method, String cardNumber, String expiry, String cvv) {
        if (method == PaymentMethod.PAYPAL) {
            return true;
        }
        if (cardNumber == null || cardNumber.replaceAll("\\s+", "").length() < 12) {
            return false;
        }
        if (expiry == null || !expiry.matches("(0[1-9]|1[0-2])/[0-9]{2}")) {
            return false;
        }
        return cvv != null && cvv.matches("[0-9]{3,4}");
    }

    public String maskCardNumber(String rawCard) {
        if (rawCard == null || rawCard.trim().isEmpty()) {
            return "N/A";
        }
        String clean = rawCard.replaceAll("\\s+", "");
        if (clean.length() <= 4) {
            return clean;
        }
        String lastFour = clean.substring(clean.length() - 4);
        return "****-****-****-" + lastFour;
    }

    public Payment getPaymentForReservation(int reservationId) {
        return paymentDAO.getPaymentByReservationId(reservationId);
    }
}
