package org.example.wayground.movieBooking;

import org.example.wayground.movieBooking.entities.MovieHall;
import org.example.wayground.movieBooking.entities.Show;
import org.example.wayground.movieBooking.entities.Ticket;

import java.time.LocalDateTime;
import java.util.List;

interface IMovieManager {
    String addMovie(String name);

    String addMovieHall(String name);

    String addScreen(String name, String movieHallId, int numRows, int numSeatsPerRow);

    String addMovieShow(String movieName, String movieHallId, String screen, LocalDateTime startTime, LocalDateTime endTime);

    List<MovieHall> searchMovie(String name);

    List<Show> searchMovieInHall(String name, String movieHallId);

    Ticket bookTicket(String movieId, String hallId, String showId, String rowId, List<String> seatIds);
}
