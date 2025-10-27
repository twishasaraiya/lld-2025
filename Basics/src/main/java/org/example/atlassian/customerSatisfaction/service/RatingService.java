package org.example.atlassian.customerSatisfaction.service;

import org.example.atlassian.customerSatisfaction.IRatingService;
import org.example.atlassian.customerSatisfaction.dto.Agent;
import org.example.atlassian.customerSatisfaction.dto.Rating;
import org.example.atlassian.customerSatisfaction.service.ranking.DefaultRanking;
import org.example.atlassian.customerSatisfaction.service.ranking.CountBasedRanking;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RatingService implements IRatingService {
    private Map<String, Agent> agentMap;

    public RatingService() {
        this.agentMap = new HashMap<>();
    }

    @Override
    public Agent rateAgent(String userId, String text, Integer rating, String agentId) {
        if(userId == null || agentId == null){
            throw new RuntimeException("UserId/AgentId/rating cannot be null");
        }
        if(rating < 1 || rating > 5){
           throw new RuntimeException("rating should be between 1 and 5");
        }
        if(text.trim().isEmpty()){
            text = "";
        }
        Agent agent = this.agentMap.computeIfAbsent(agentId, id -> new Agent(agentId));
        Rating newRating = new Rating(userId, rating, text);
        agent.updateRating(newRating);
        return agent;
    }

    @Override
    public List<Agent> getAllAgents(TieBreaker tieBreaker) {
        RankingStrategy rankingStrategy = getRankingStrategy(tieBreaker);
        return agentMap.values()
                .stream()
                .sorted(rankingStrategy.getComparator())
                .collect(Collectors.toList());
    }

    /**
     * Questions
     * 1. What time should be considered? will it be provided as input
     * or system default local date -
     *  - Inject a custom data so that it can be updated also useful for testing purpose
     *  -
     * 2. Move separator to constant
     */
    @Override
    public List<Agent> getTopAgentForMonth(int year, int month, TieBreaker tieBreaker) {
        String key = year + "_" + month;
        RankingStrategy rankingStrategy = getRankingStrategy(tieBreaker);
        return agentMap.values()
                .stream()
                .filter(agent -> agent.getMonthlyRatingMap().get(key) != null)
                .map(agent ->  {
                    List<Rating> ratingList = agent.getMonthlyRatingMap().getOrDefault(key, new ArrayList<>());
                    double avgRating = calcAvgRating(ratingList);
                    return new Agent(agent.getAgentId(), avgRating, ratingList.size());
                })
                .sorted(rankingStrategy.getComparator())
                .collect(Collectors.toList());

    }

    private RankingStrategy getRankingStrategy(TieBreaker tieBreaker){
        switch (tieBreaker){
            case NUM_REVIEWS: return new CountBasedRanking();
            case NONE:
            case AGENT_ID: return new DefaultRanking();
            default:
                throw new RuntimeException("Invalid tie breaker");
        }
    }

    private double calcAvgRating(List<Rating> ratingList){
        Integer sum = ratingList.stream().map(rating -> rating.getRating()).reduce(0, Integer::sum);
        return (double) sum/ratingList.size();
    }


}

