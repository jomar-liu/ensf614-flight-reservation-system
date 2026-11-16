package com.flightreservation.model;

/**
 * Payment entity representing simulated payment processing for a reservation.
 * Stereotype: <<entity>>
 */
public class Payment {
    private int paymentId;
    private int reservationId;
    private double amount;
    private PaymentMethod paymentMethod;
    private String cardNumberMasked;
    private String paymentDate;
    private PaymentStatus status;

    public Payment() {
        this.status = PaymentStatus.COMPLETED;
    }

    public Payment(int paymentId, int reservationId, double amount,
                   PaymentMethod paymentMethod, String cardNumberMasked,
                   String paymentDate, PaymentStatus status) {
        this.paymentId = paymentId;
        this.reservationId = reservationId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.cardNumberMasked = cardNumberMasked;
        this.paymentDate = paymentDate;
        this.status = status != null ? status : PaymentStatus.COMPLETED;
    }

    public int getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(int paymentId) {
        this.paymentId = paymentId;
    }

    public int getReservationId() {
        return reservationId;
    }

    public void setReservationId(int reservationId) {
        this.reservationId = reservationId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getCardNumberMasked() {
        return cardNumberMasked;
    }

    public void setCardNumberMasked(String cardNumberMasked) {
        this.cardNumberMasked = cardNumberMasked;
    }

    public String getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(String paymentDate) {
        this.paymentDate = paymentDate;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Payment #" + paymentId + " - $" + String.format("%.2f", amount) + " (" + status + ")";
    }
}
