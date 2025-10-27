package org.example.wayground.movieBooking.entities;

import java.util.UUID;

public class Movie{
    private String id;
    private String name;

    public Movie(String name) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
    }

    public String getId() {
        return id;
    }
}
