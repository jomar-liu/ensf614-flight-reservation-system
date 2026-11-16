package com.flightreservation.model;

/**
 * Reservation entity representing a customer's booked flight seat.
 * Stereotype: <<entity>>
 */
public class Reservation {
    private int reservationId;
    private String reservationCode;
    private int customerId;
    private int flightId;
    private int seatId;
    private String bookingDate;
    private ReservationStatus status;

    // Associated entities for UI display & reporting
    private Customer customer;
    private Flight flight;
    private Seat seat;

    public Reservation() {
        this.status = ReservationStatus.CONFIRMED;
    }

    public Reservation(int reservationId, String reservationCode, int customerId,
                       int flightId, int seatId, String bookingDate, ReservationStatus status) {
        this.reservationId = reservationId;
        this.reservationCode = reservationCode;
        this.customerId = customerId;
        this.flightId = flightId;
        this.seatId = seatId;
        this.bookingDate = bookingDate;
        this.status = status != null ? status : ReservationStatus.CONFIRMED;
    }

    public int getReservationId() {
        return reservationId;
    }

    public void setReservationId(int reservationId) {
        this.reservationId = reservationId;
    }

    public String getReservationCode() {
        return reservationCode;
    }

    public void setReservationCode(String reservationCode) {
        this.reservationCode = reservationCode;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getFlightId() {
        return flightId;
    }

    public void setFlightId(int flightId) {
        this.flightId = flightId;
    }

    public int getSeatId() {
        return seatId;
    }

    public void setSeatId(int seatId) {
        this.seatId = seatId;
    }

    public String getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(String bookingDate) {
        this.bookingDate = bookingDate;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Flight getFlight() {
        return flight;
    }

    public void setFlight(Flight flight) {
        this.flight = flight;
    }

    public Seat getSeat() {
        return seat;
    }

    public void setSeat(Seat seat) {
        this.seat = seat;
    }

    @Override
    public String toString() {
        return "Reservation " + reservationCode + " [" + status + "] " +
                (flight != null ? "- Flight " + flight.getFlightNumber() : "") +
                (seat != null ? ", Seat " + seat.getSeatNumber() : "");
    }
}
