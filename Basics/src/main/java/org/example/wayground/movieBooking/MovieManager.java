package org.example.wayground.movieBooking;

import org.example.wayground.movieBooking.entities.Show;
import org.example.wayground.movieBooking.entities.Movie;
import org.example.wayground.movieBooking.entities.MovieHall;
import org.example.wayground.movieBooking.entities.Ticket;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class MovieManager implements IMovieManager {
    private Map<String, Movie> moviesMap;
    private Map<String, MovieHall> movieHalls;

    public MovieManager() {
        this.moviesMap = new ConcurrentHashMap<>();
        this.movieHalls = new ConcurrentHashMap<>();
    }

    @Override
    public String addMovie(String name) {
        validateMovieName(name);
        Movie movie = new Movie(name);
        Movie addedMovie = moviesMap.putIfAbsent(name, movie);
        if (addedMovie != null) {
            throw new RuntimeException("Movie already exists");
        }
        return movie.getId();
    }

    @Override
    public String addMovieHall(String name) {
        MovieHall hall = new MovieHall(name);
        MovieHall existingHall = movieHalls.putIfAbsent(name, hall);
        if (existingHall != null) {
            throw new RuntimeException("Movie hall already exists");
        }
        return hall.getId();
    }

    @Override
    public String addScreen(String name, String movieHallId, int numRows, int numSeatsPerRow) {
        MovieHall hall = movieHalls.get(movieHallId);
        hall.addScreen(name, numRows, numSeatsPerRow);
        return name;
    }

    @Override
    public String addMovieShow(String movieName, String movieHallId, String screen, LocalDateTime startTime, LocalDateTime endTime) {
        validateMovieName(movieName);
        Movie movie = moviesMap.get(movieName);
        MovieHall hall = movieHalls.get(movieHallId);
        hall.addShow(movie, hall, screen, startTime, endTime);
        return "";
    }

    private void validateMovieName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new RuntimeException("Invalid movie name");
        }
    }

    @Override
    public List<MovieHall> searchMovie(String name) {
        validateMovieName(name);
        return movieHalls.values()
                .stream()
                .filter(movieHall -> movieHall.getMovieShows().containsKey(name))
                .collect(Collectors.toList());
    }

    @Override
    public List<Show> searchMovieInHall(String name, String movieHallId) {
        MovieHall movieHall = movieHalls.get(movieHallId);
        if (movieHall == null) {
            throw new RuntimeException("Invalid movie hall");
        }
        return movieHall.getMovieShows().getOrDefault(name, new ArrayList<>());
    }

    @Override
    public Ticket bookTicket(String movieId, String hallId, String showId, String rowId, List<String> seatIds)  {
        MovieHall movieHall = movieHalls.get(hallId);
        if (movieHall == null) {
            throw new RuntimeException("Invalid movie hall");
        }
        movieHall.bookTickets(showId, rowId, seatIds);
        return null;
    }
}
