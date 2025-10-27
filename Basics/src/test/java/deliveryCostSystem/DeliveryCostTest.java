package deliveryCostSystem;

import org.example.rippling.DeliveryCostSystem;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class DeliveryCostTest {

    static DeliveryCostSystem deliveryCostSystem;

    @BeforeEach
    public void init(){
        deliveryCostSystem = new DeliveryCostSystem();
    }

    @Test
    public void test(){
        deliveryCostSystem.addDriver("driverA");
        deliveryCostSystem.addDelivery("driverA", LocalDateTime.now(), LocalDateTime.now().plusHours(10));
        assertEquals(100, deliveryCostSystem.getTotalCost("driverA"));
    }

    @Test
    public void testPayUpto(){
        deliveryCostSystem.addDriver("driverA");
        LocalDateTime now = LocalDateTime.now();
        deliveryCostSystem.addDelivery("driverA", now, now.plusHours(10));
        deliveryCostSystem.addDelivery("driverA", now.plusHours(5), now.plusHours(20));

        assertEquals(250, deliveryCostSystem.getTotalCost("driverA"));
        assertEquals(100, deliveryCostSystem.payUptoTime("driverA" , now.plusHours(11)));
        assertEquals(150, deliveryCostSystem.getCostToBePaid("driverA"));
    }
}
