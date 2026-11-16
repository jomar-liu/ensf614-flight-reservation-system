package com.flightreservation.controller;

import com.flightreservation.data.FlightDAO;
import com.flightreservation.data.SeatDAO;
import com.flightreservation.model.Flight;
import com.flightreservation.model.Seat;

import java.util.List;

/**
 * Controller coordinating flight search, filtering, and seat availability viewing.
 * Stereotype: <<control>>
 */
public class FlightSearchController {
    private final FlightDAO flightDAO;
    private final SeatDAO seatDAO;

    public FlightSearchController() {
        this.flightDAO = new FlightDAO();
        this.seatDAO = new SeatDAO();
    }

    public List<Flight> searchFlights(String origin, String destination, String date, String airline) {
        return flightDAO.searchFlights(origin, destination, date, airline);
    }

    public List<Flight> getAllFlights() {
        return flightDAO.getAllFlights();
    }

    public Flight getFlightDetails(int flightId) {
        return flightDAO.getFlightById(flightId);
    }

    public List<Seat> getAvailableSeats(int flightId) {
        return seatDAO.getAvailableSeatsByFlightId(flightId);
    }

    public List<Seat> getAllSeats(int flightId) {
        return seatDAO.getSeatsByFlightId(flightId);
    }

    public List<String> getAvailableOrigins() {
        return flightDAO.getDistinctOrigins();
    }

    public List<String> getAvailableDestinations() {
        return flightDAO.getDistinctDestinations();
    }

    public List<String> getAvailableAirlines() {
        return flightDAO.getDistinctAirlines();
    }
}
