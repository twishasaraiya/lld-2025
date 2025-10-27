package customerSatisfaction;
import org.example.atlassian.customerSatisfaction.IRatingService;
import org.example.atlassian.customerSatisfaction.dto.Agent;
import org.example.atlassian.customerSatisfaction.service.RatingService;
import org.example.atlassian.customerSatisfaction.service.TieBreaker;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;


import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SampleSampleTest {

    private static IRatingService ratingService;
    @BeforeAll
    public static void init(){
        ratingService = new RatingService();
    }
    @Test
    public void testAddRating(){
        Agent agent = ratingService.rateAgent("A", "good", 1, "123");
        assertEquals(agent.getAvgRating(), 1.0);
        assertEquals(agent.getNumRatings(), 1);
    }

    @Test
    public void testGetAllAgents(){
        ratingService.rateAgent("A", "good", 1, "123");
        ratingService.rateAgent("B", "good", 3, "123");

        ratingService.rateAgent("A", "good", 1, "311");
        ratingService.rateAgent("B", "good", 3, "312");

        List<Agent> agentList = ratingService.getAllAgents(TieBreaker.NONE);
        System.out.println(agentList);
        assertEquals(agentList.size(), 3);
        assertEquals(agentList.get(0).getAgentId(), "312");
        assertEquals(agentList.get(0).getAvgRating(), 3.0);
    }

    @Test
    public void getTopAgentForMonth(){
        ratingService.rateAgent("A", "good", 1, "123");
        ratingService.rateAgent("B", "good", 3, "123");

        ratingService.rateAgent("A", "good", 1, "311");
        ratingService.rateAgent("B", "good", 3, "312");

        List<Agent> agentList = ratingService.getTopAgentForMonth(2025, 9, TieBreaker.NONE);
        System.out.println(agentList);
        assertEquals(agentList.size(), 3);
    }
}
