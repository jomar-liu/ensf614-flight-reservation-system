package com.flightreservation.model;

/**
 * Seat entity representing a seat on a specific flight.
 * Stereotype: <<entity>>
 */
public class Seat {
    private int seatId;
    private int flightId;
    private String seatNumber;
    private SeatClass seatClass;
    private double price;
    private boolean available;

    public Seat() {}

    public Seat(int seatId, int flightId, String seatNumber, SeatClass seatClass, double price, boolean available) {
        this.seatId = seatId;
        this.flightId = flightId;
        this.seatNumber = seatNumber;
        this.seatClass = seatClass;
        this.price = price;
        this.available = available;
    }

    public int getSeatId() {
        return seatId;
    }

    public void setSeatId(int seatId) {
        this.seatId = seatId;
    }

    public int getFlightId() {
        return flightId;
    }

    public void setFlightId(int flightId) {
        this.flightId = flightId;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }

    public SeatClass getSeatClass() {
        return seatClass;
    }

    public void setSeatClass(SeatClass seatClass) {
        this.seatClass = seatClass;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    @Override
    public String toString() {
        return seatNumber + " [" + seatClass.getDisplayName() + " - $" + String.format("%.2f", price) + "]";
    }
}
