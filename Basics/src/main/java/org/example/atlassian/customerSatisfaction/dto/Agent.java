package org.example.atlassian.customerSatisfaction.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * Agent
 *  - name
 *  - avgRating
 *  - numOfRatings
 *
 *  Rating Log
 *      - String agentId
 *      - String ticketId
 *      - int rating
 * RatingService
 *  - rateAgent(String agentId, int rating, String ticketId)
 *  - List<Agent> getAllAgents()
 *  - List<Agent> getBestAgent(Integer month)
 */


public class Agent{
    private String agentId;
    private Double avgRating;
    private Integer numRatings;
    private Map<String,List<Rating>> monthlyRatingMap;

    public Agent(String agentId) {
        this.agentId = agentId;
        this.avgRating = 0.0;
        this.numRatings = 0;
        this.monthlyRatingMap = new HashMap<>();
    }

    public Agent(String agentId, Double avgRating, Integer numRatings) {
        this.agentId = agentId;
        this.avgRating = avgRating;
        this.numRatings = numRatings;
    }

    public void updateRating(Rating rating){
        this.avgRating = ((avgRating *  numRatings) + rating.getRating()) / (numRatings + 1) ;
        this.numRatings += 1;
        String yearMonth = rating.getLocalDate().getYear() + "_" + rating.getLocalDate().getMonthValue();
        this.monthlyRatingMap.computeIfAbsent(yearMonth, k -> new ArrayList<>()).add(rating);
    }

    public String getAgentId() {
        return agentId;
    }

    public Double getAvgRating() {
        return avgRating;
    }

    public Integer getNumRatings() {
        return numRatings;
    }

    public Map<String, List<Rating>> getMonthlyRatingMap() {
        return monthlyRatingMap;
    }

    @Override
    public String toString() {
        return "Agent{" +
                "agentId='" + agentId + '\'' +
                ", avgRating=" + avgRating +
                ", numRatings=" + numRatings +
                ", ratingList=" + monthlyRatingMap +
                '}';
    }
}
