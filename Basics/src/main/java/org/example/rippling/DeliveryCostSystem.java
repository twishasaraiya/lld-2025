package org.example.rippling;

import com.sun.source.tree.Tree;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalField;
import java.util.*;

/**
 * Delivery cost tracking system
 * Build a delivery cost tracking system
 * Asked in 3 parts:
 *
 * 1. **Cost Calculation**
 *
 *     ```python
 *     add_driver(driverId)
 *     add_delivery(driverId, startTime, endTime)
 *     get_total_cost()
 *     ```
 *
 *
 * Discussed approach, data structures, and time complexity before coding.
 *
 * 2. Payment Tracking
 *
 *     ```python
 *     pay_up_to_time(upToTime)
 *     get_cost_to_be_paid()
 *     ```
 *
 * 3. Analytics
 *
 *     ```python
 *     get_max_active_drivers_in_last_24_hours(currentTime)
 *     ```
 */

interface IDriverManager{
    void addDriver(String driverId);
    void addDelivery(String driverId, LocalDateTime startTime, LocalDateTime endTime);
    double getTotalCost(String driverId);
}

class Delivery{
    final LocalDateTime startTime;
    final LocalDateTime endTime;

    public Delivery(LocalDateTime startTime, LocalDateTime endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
class Driver{
    final String driverId;
//    List<Delivery> deliveryList;
    TreeMap<LocalDateTime, Delivery> deliveryTreeMap;
    Double totalCost;
    Map<LocalDateTime, Double> prefixSum;
    LocalDateTime lastPayTime;

    Driver(String driverId) {
        this.driverId = driverId;
//        this.deliveryList = new ArrayList<>();
        this.deliveryTreeMap = new TreeMap<>();
        this.totalCost = 0D;
        this.prefixSum = new HashMap<>();
        this.lastPayTime = LocalDateTime.now();

    }
}

public class DeliveryCostSystem implements IDriverManager{

    Map<String, Driver> driverMap;
    private static final Integer perHourRate = 10;
    public DeliveryCostSystem() {
        this.driverMap = new HashMap<>();
    }

    @Override
    public void addDriver(String driverId) {
      if(driverId == null)  throw new RuntimeException("Invalid driverId");
      if(driverMap.containsKey(driverId)) throw new RuntimeException("Driver already exists");
      driverMap.put(driverId, new Driver(driverId));
    }

    @Override
    public void addDelivery(String driverId, LocalDateTime startTime, LocalDateTime endTime) {
        if(driverId == null)  throw new RuntimeException("Invalid driverId");
        if(!driverMap.containsKey(driverId)) throw new RuntimeException("Driver does not exists");
        Delivery delivery = new Delivery(startTime, endTime);
        Driver driver = driverMap.get(driverId);
//        driver.deliveryList.add(delivery);
        driver.deliveryTreeMap.put(endTime, delivery);
        driver.totalCost += (perHourRate * Duration.between(startTime, endTime).toHours());
        driver.prefixSum.put(endTime, driver.totalCost);
    }

    @Override
    public double getTotalCost(String driverId) {
        if(driverId == null)  throw new RuntimeException("Invalid driverId");
        return driverMap.get(driverId).totalCost;
    }

    public double payUptoTime(String driverId, LocalDateTime payUpto){
        if(driverId == null)  throw new RuntimeException("Invalid driverId");
        if(!driverMap.containsKey(driverId)) throw new RuntimeException("Driver does not exists");
        Driver driver = driverMap.get(driverId);
        LocalDateTime floorKey = driver.deliveryTreeMap.floorKey(payUpto);
        if(floorKey == null) return 0D;
        double payment = driver.prefixSum.get(floorKey) - driver.prefixSum.getOrDefault(driver.lastPayTime, 0D);
        driver.lastPayTime = floorKey;
        return payment;
    }

    public double getCostToBePaid(String driverId){
        Driver driver = driverMap.get(driverId);
        LocalDateTime latestDeliveryKey = driver.deliveryTreeMap.lastKey();
        return driver.prefixSum.get(latestDeliveryKey) - driver.prefixSum.getOrDefault(driver.lastPayTime, 0D);
    }
}