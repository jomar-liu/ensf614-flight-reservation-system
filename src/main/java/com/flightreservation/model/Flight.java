package com.flightreservation.model;

/**
 * Flight entity representing a scheduled flight between two destinations.
 * Stereotype: <<entity>>
 */
public class Flight {
    private int flightId;
    private String flightNumber;
    private String airline;
    private String origin;
    private String destination;
    private String departureTime; // "YYYY-MM-DD HH:MM"
    private String arrivalTime;   // "YYYY-MM-DD HH:MM"
    private double basePrice;
    private int aircraftId;
    private FlightStatus status;

    // Derived / helper fields
    private Aircraft aircraft;
    private int availableSeatsCount;

    public Flight() {
        this.status = FlightStatus.SCHEDULED;
    }

    public Flight(int flightId, String flightNumber, String airline, String origin,
                  String destination, String departureTime, String arrivalTime,
                  double basePrice, int aircraftId, FlightStatus status) {
        this.flightId = flightId;
        this.flightNumber = flightNumber;
        this.airline = airline;
        this.origin = origin;
        this.destination = destination;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.basePrice = basePrice;
        this.aircraftId = aircraftId;
        this.status = status != null ? status : FlightStatus.SCHEDULED;
    }

    public int getFlightId() {
        return flightId;
    }

    public void setFlightId(int flightId) {
        this.flightId = flightId;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    public String getAirline() {
        return airline;
    }

    public void setAirline(String airline) {
        this.airline = airline;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(String arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(double basePrice) {
        this.basePrice = basePrice;
    }

    public int getAircraftId() {
        return aircraftId;
    }

    public void setAircraftId(int aircraftId) {
        this.aircraftId = aircraftId;
    }

    public FlightStatus getStatus() {
        return status;
    }

    public void setStatus(FlightStatus status) {
        this.status = status;
    }

    public Aircraft getAircraft() {
        return aircraft;
    }

    public void setAircraft(Aircraft aircraft) {
        this.aircraft = aircraft;
    }

    public int getAvailableSeatsCount() {
        return availableSeatsCount;
    }

    public void setAvailableSeatsCount(int availableSeatsCount) {
        this.availableSeatsCount = availableSeatsCount;
    }

    @Override
    public String toString() {
        return flightNumber + " (" + airline + "): " + origin + " -> " + destination +
                " [" + departureTime + " to " + arrivalTime + "] - $" + String.format("%.2f", basePrice);
    }
}
