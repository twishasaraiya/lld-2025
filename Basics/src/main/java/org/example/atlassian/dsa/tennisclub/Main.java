package org.example.atlassian.dsa.tennisclub;

import java.util.Map;
import java.util.TreeMap;

public class Main {
    public static void main(String[] args) {
        TennisClub tennisClub = new TennisClub();
    }
}

class TennisClub{

    TreeMap<Integer, Booking> bookingTreeMap;
    int courtsCount;
    public TennisClub() {
        this.bookingTreeMap = new TreeMap<>();
        this.courtsCount = 0;
    }

    public int assignBookingToCourt(int startTime, int endTime){
        Booking newBooking = new Booking(startTime,endTime);
        if(bookingTreeMap.isEmpty()){
            newBooking.courtId = ++courtsCount;
            bookingTreeMap.put(endTime, newBooking);
            return newBooking.courtId;
        }
        Integer nextEndTime = bookingTreeMap.firstKey();
        if(nextEndTime < startTime){
            Map.Entry<Integer, Booking> availableBooking = bookingTreeMap.firstEntry();
            bookingTreeMap.remove(availableBooking.getKey());
            newBooking.courtId = availableBooking.getValue().courtId;
            return newBooking.courtId;
        }
        newBooking.courtId = ++courtsCount;
        bookingTreeMap.put(endTime, newBooking);
        return newBooking.courtId;
    }

    public boolean isOverlapping(Booking b1, Booking b2){
        if(b1 == null || b2 == null) return false;
        return b1.startTime < b2.endTime && b2.startTime < b1.endTime;
    }
    public int getMaxCourtsRequired(){
        return bookingTreeMap.size();
    }

    class Booking{
        int startTime;
        int endTime;
        int courtId;

        public Booking(int startTime, int endTime) {
            this.startTime = startTime;
            this.endTime = endTime;
        }
    }
}