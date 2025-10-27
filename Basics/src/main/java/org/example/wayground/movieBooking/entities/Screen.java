package org.example.wayground.movieBooking.entities;

public class Screen {
    private String name;
    private int numRows;
    private int numSeatsPerRow;

    public Screen(String name, int numRows, int numSeatsPerRow) {
        this.name = name;
        this.numRows = numRows;
        this.numSeatsPerRow = numSeatsPerRow;
    }

    public int getNumRows() {
        return numRows;
    }

    public int getNumSeatsPerRow() {
        return numSeatsPerRow;
    }
}
