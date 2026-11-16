package com.flightreservation.model;

/**
 * Aircraft entity representing an airplane model and capacity.
 * Stereotype: <<entity>>
 */
public class Aircraft {
    private int aircraftId;
    private String model;
    private int totalSeats;

    public Aircraft() {}

    public Aircraft(int aircraftId, String model, int totalSeats) {
        this.aircraftId = aircraftId;
        this.model = model;
        this.totalSeats = totalSeats;
    }

    public int getAircraftId() {
        return aircraftId;
    }

    public void setAircraftId(int aircraftId) {
        this.aircraftId = aircraftId;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(int totalSeats) {
        this.totalSeats = totalSeats;
    }

    @Override
    public String toString() {
        return model + " (" + totalSeats + " seats)";
    }
}
