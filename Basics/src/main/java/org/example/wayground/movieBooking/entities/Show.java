package org.example.wayground.movieBooking.entities;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Show {
    private String id;
    private Movie movieId;
    private String screenId;
    private int numRows;
    private int numSeatsPerRow;
    private Map<Character, Map<Integer,Seat>> rows;
    private Map<Character, ReentrantReadWriteLock> rowLocks;
    private LocalDateTime showStartTime;
    private LocalDateTime showEndTime;

    public Show(Movie movieId, String screenId, int numRows, int numSeatsPerRow, LocalDateTime showStartTime, LocalDateTime showEndTime) {
        this.movieId = movieId;
        this.screenId = screenId;
        this.numRows = numRows;
        this.numSeatsPerRow = numSeatsPerRow;
        this.id = UUID.randomUUID().toString();
        this.rows = new ConcurrentHashMap<>();
        addRows();
        this.rowLocks = new ConcurrentHashMap<>();
        this.showStartTime = showStartTime;
        this.showEndTime = showEndTime;
    }

    public String getId() {
        return id;
    }

    private void addRows() {
        for (int i = 0; i < numRows; i++) {
            Map<Integer, Seat> seats = new HashMap<>();
            char rowKey = getRowKey(i);
            for (int j = 1; j <= numSeatsPerRow; j++) {
                seats.put(j, new Seat(j, true));
            }
            this.rows.put(rowKey, seats);
            this.rowLocks.put(rowKey, new ReentrantReadWriteLock());
        }
    }

    private char getRowKey(int i) {
        return (char) ('A' + i);
    }

    public LocalDateTime getShowStartTime() {
        return showStartTime;
    }

    public LocalDateTime getShowEndTime() {
        return showEndTime;
    }

    public void lockSeats(char rowId, List<Integer> seatIds){
        if(seatIds == null || seatIds.isEmpty()) {
            throw new RuntimeException("Invalid seat selection");
        }
        ReentrantReadWriteLock lock = rowLocks.get(getRowKey(rowId));
        try {
            lock.writeLock().lock();
            Map<Integer, Seat> seatMap = rows.get(rowId);
            for (int seatId : seatIds) {
                seatMap.get(seatId).setAvailable(false);
            }
        } finally {
            if (lock != null) {
                lock.writeLock().unlock();
            }
         }
    }
}
