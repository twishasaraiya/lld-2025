package org.example.wayground.movieBooking.entities;


public class Seat {
    private int id;
    private boolean isAvailable;

    public Seat(int id, boolean isAvailable) {
        this.id = id;
        this.isAvailable = isAvailable;
    }

    public int getId() {
        return id;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }
}
