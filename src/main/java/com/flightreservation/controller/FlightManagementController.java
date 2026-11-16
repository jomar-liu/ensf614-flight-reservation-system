package com.flightreservation.controller;

import com.flightreservation.data.AircraftDAO;
import com.flightreservation.data.FlightDAO;
import com.flightreservation.model.Aircraft;
import com.flightreservation.model.Flight;

import java.util.List;

/**
 * Controller coordinating flight, route, and aircraft management (Admin use cases).
 * Stereotype: <<control>>
 */
public class FlightManagementController {
    private final FlightDAO flightDAO;
    private final AircraftDAO aircraftDAO;

    public FlightManagementController() {
        this.flightDAO = new FlightDAO();
        this.aircraftDAO = new AircraftDAO();
    }

    public List<Flight> getAllFlights() {
        return flightDAO.getAllFlights();
    }

    public boolean addFlight(Flight flight) {
        if (flight == null || flight.getFlightNumber() == null || flight.getFlightNumber().trim().isEmpty()) {
            return false;
        }
        int id = flightDAO.addFlight(flight);
        return id > 0;
    }

    public boolean updateFlight(Flight flight) {
        if (flight == null || flight.getFlightId() <= 0) {
            return false;
        }
        return flightDAO.updateFlight(flight);
    }

    public boolean removeFlight(int flightId) {
        return flightDAO.deleteFlight(flightId);
    }

    public List<Aircraft> getAllAircraft() {
        return aircraftDAO.findAll();
    }

    public boolean addAircraft(Aircraft aircraft) {
        return aircraftDAO.addAircraft(aircraft) > 0;
    }
}
