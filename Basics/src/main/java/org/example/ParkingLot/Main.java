package org.example.ParkingLot;

import java.security.InvalidKeyException;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import static java.time.LocalTime.now;

/**
 *  Design a Parking Lot System
 * Requirements: Multi-level parking, different vehicle types, payment processing
 */
public class Main {

    public static void main(String[] args) {
        int gates = 1;
        int floor = 4;
        List<Floor> floors = new ArrayList<>();
        Map<VehicleType, Integer> vehicleTypeIntegerMap = Map.of(VehicleType.CAR, 10, VehicleType.BUS, 8, VehicleType.SUV, 10);
        for (int i = 0; i < floor; i++) {
            floors.add(new Floor(i, vehicleTypeIntegerMap));
        }
        PricingStrategy pricingStrategy = new BasePricingStrategy();
        ParkingLot parkingLot = new ParkingLotImpl(floors,gates, pricingStrategy);

        String ticket = parkingLot.entry(1, VehicleType.CAR);
        double fees = parkingLot.exit(1, ticket);
        System.out.println(ticket.toString() + " " + fees);

    }
}


/**
 *
 * Entities
 *  - Vehicle
 *      - id
 *      - type
 *  - Spot
 *      - spotId
 *      - floorId
 *  - Ticket
 *      - price
 *      - List<Spots>
 *      - vehicle
 *      - entryTime
 *      - exitTime
 *      - fees
 */

enum VehicleType{
    MOTORCYCLE(1),
    CAR(1),
    SUV(2),
    TRUCK(2),
    BUS(4);
    int numSpots;

    VehicleType(int i) {
        this.numSpots = i;
    }
}

class Spot{
    int spotId;
    int floorId;
    boolean isAvailable;
}

class Ticket{
    String id;
    double fees;
    List<Spot> spots;
    VehicleType vehicleType;
    long entryTime;
    long exitTime;
    int entryGateId;
    int exitGateId;
    Floor floor;

    @Override
    public String toString() {
        return "Ticket{" +
                "fees=" + fees +
                ", spots=" + spots +
                ", vehicleType=" + vehicleType +
                ", entryTime=" + entryTime +
                ", exitTime=" + exitTime +
                ", entryGateId=" + entryGateId +
                ", exitGateId=" + exitGateId +
                ", floor=" + floor +
                '}';
    }
}
interface ParkingLot{
    String entry(int gateId, VehicleType vehicleType);
    double exit(int gateId, String ticket);
}

class Floor{
    int id;
    Map<VehicleType, BlockingQueue<Spot>> zoneMap;

    public Floor(int id, Map<VehicleType, Integer> vehicleTypes) {
        this.id = id;
        this.zoneMap = new ConcurrentHashMap<>();
        for (VehicleType type: vehicleTypes.keySet()){
            int size = vehicleTypes.get(type);
            BlockingQueue<Spot> queue = new ArrayBlockingQueue<>(size);
            for (int i=0;i<size;i++){
                queue.add(new Spot());
            }
            zoneMap.put(type, queue);
        }
    }
}
class ParkingLotImpl implements ParkingLot{
    List<Floor> floors;
    Map<String, Ticket> ticketMap;
    Map<Integer, ReentrantLock> gatesMap;
    PricingStrategy pricingStrategy;
    ConcurrentHashMap<VehicleType, PriorityQueue<Floor>> availableFloors;
    public ParkingLotImpl(List<Floor> floors, int gates, PricingStrategy pricingStrategy) {
        this.floors = floors;
        this.ticketMap = new HashMap<>();
        this.gatesMap = new ConcurrentHashMap<>(gates);
        for (int i = 1; i <= gates; i++) {
            gatesMap.put(i, new ReentrantLock());
        }
        this.pricingStrategy = pricingStrategy;
        for (Floor floor: floors){
            for (VehicleType type: floor.zoneMap.keySet()){
                availableFloors.computeIfAbsent(type, k -> new PriorityQueue<>((a, b) -> b.zoneMap.get(type).size() - a.zoneMap.get(type).size()))
                        .add(floor);
            }
        }
    }

    @Override
    public String entry(int gateId, VehicleType vehicleType) {
        ReentrantLock gateLock = this.gatesMap.get(gateId);
        gateLock.lock();
        try {
            // find available spot or block
            Floor floor = availableFloors.get(vehicleType).poll();
            int spotsNeeded = vehicleType.numSpots;
            if(floor == null || floor.zoneMap.get(vehicleType).size() < spotsNeeded) {
                throw new RuntimeException("Enough spots not available");
            }
            List<Spot> reservedSpots = new ArrayList<>();
            BlockingQueue<Spot> spotBlockingQueue = floor.zoneMap.get(vehicleType);
            synchronized (spotBlockingQueue) {
                for (int i = 0; i < spotsNeeded; i++) {
                    Spot spot = spotBlockingQueue.poll();
                    if (spot == null) {
                        spotBlockingQueue.addAll(reservedSpots);
                        throw new RuntimeException("Spots not available");
                    }
                    reservedSpots.add(spot);
                    availableFloors.get(vehicleType).add(floor);
                }
            }
            Ticket ticket = new Ticket();
            ticket.id = UUID.randomUUID().toString();
            ticket.entryGateId = gateId;
            ticket.entryTime = System.currentTimeMillis();
            ticket.vehicleType = vehicleType;
            ticket.spots = reservedSpots;
            ticket.floor = floor;
            this.ticketMap.put(ticket.id, ticket);
            return ticket.id;
        } catch (Exception e){
            throw new RuntimeException(e);
        }
        finally {
            gateLock.unlock();
        }
    }

    @Override
    public double exit(int gateId, String ticketId) {
        // calculate fees release spot
        ReentrantLock gateLock = this.gatesMap.get(gateId);
        gateLock.lock();
        try {
            if (!ticketMap.containsKey(ticketId)){
                throw new InvalidKeyException("Ticket Id is invalid");
            }
            Ticket ticket = ticketMap.get(ticketId);
            double fees = pricingStrategy.calculateFees(ticket);
            Floor floor = ticket.floor;
            VehicleType vehicleType = ticket.vehicleType;
            BlockingQueue<Spot> spotBlockingQueue = floor.zoneMap.get(vehicleType);
            synchronized (spotBlockingQueue){
                spotBlockingQueue.addAll(ticket.spots);
            }
            return fees;
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        } finally {
            gateLock.unlock();
        }
    }
}

interface PricingStrategy{
    double calculateFees(Ticket ticket);
}

class BasePricingStrategy implements PricingStrategy{
    private static final Double HOURLY_RATE = 10.0;
    private static final long millisToHour = 60 * 1000;
    private static final Random random = new Random();
    @Override
    public double calculateFees(Ticket ticket) {
        ticket.exitTime = System.currentTimeMillis() + random.nextInt(5) * millisToHour; // Temp for testing only
        long timeDiffInMillis = ticket.exitTime - ticket.entryTime;
        double fees = Math.round((double) (timeDiffInMillis / millisToHour)) * HOURLY_RATE;
        return fees;
    }
}

