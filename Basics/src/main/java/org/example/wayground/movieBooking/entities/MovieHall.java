package org.example.wayground.movieBooking.entities;


import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MovieHall {
    private String id;
    private String name;
    private Map<String, Screen> screenMap;
    private Map<String, List<Show>> movieShows;
    private Map<String, Set<Show>> screenWiseShows;
    private Map<String, Show> showMap;

    public MovieHall(String name) {
        this.name = name;
        this.id = UUID.randomUUID().toString();
        this.movieShows = new ConcurrentHashMap<>();
        this.screenMap = new ConcurrentHashMap<>();
        this.screenWiseShows = new ConcurrentHashMap<>();
        this.showMap = new ConcurrentHashMap<>();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }


    public Map<String, Set<Show>> getScreenWiseShows() {
        return screenWiseShows;
    }

    public Map<String, List<Show>> getMovieShows() {
        return movieShows;
    }

    public void addScreen(String name, int numRows, int numSeatsPerRow) {
        Screen screen = new Screen(name, numRows, numSeatsPerRow);
        Screen existingScreen = this.screenMap.putIfAbsent(name, screen);
        if (existingScreen != null) {
            throw new RuntimeException("Screen already exists");
        }
        this.screenWiseShows.put(name, new TreeSet<>(Comparator.comparing(Show::getShowStartTime)));
    }

    public void addShow(Movie movie, MovieHall hall, String screen, LocalDateTime startTime, LocalDateTime endTime) {
        Set<Show> shows = this.screenWiseShows.get(screen);
        if (shows == null) {
            throw new RuntimeException("No such show available in this screen");
        }
        for (Show show : shows) {
            // TODO : check this overlapping condition
            if ((startTime.isAfter(show.getShowStartTime()) && startTime.isBefore(show.getShowEndTime())) ||
                    (endTime.isAfter(show.getShowStartTime()) && endTime.isBefore(show.getShowEndTime()))) {
                throw new RuntimeException("The show times overlap with an existing show");
            }
        }
        Screen screenObj = this.screenMap.get(screen);
        Show newShow = new Show(movie, screen, screenObj.getNumRows(), screenObj.getNumSeatsPerRow(), startTime, endTime);
        shows.add(newShow);
        showMap.put(newShow.getId(), newShow);
    }

    public void bookTickets(String showId, String rowId, List<String> seatIds) {
        Show show = showMap.get(showId);
        if(show == null){
            throw new RuntimeException("Invalid show");
        }

    }
}
