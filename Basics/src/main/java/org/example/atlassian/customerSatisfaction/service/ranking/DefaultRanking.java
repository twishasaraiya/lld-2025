package org.example.atlassian.customerSatisfaction.service.ranking;

import org.example.atlassian.customerSatisfaction.dto.Agent;
import org.example.atlassian.customerSatisfaction.service.RankingStrategy;

import java.util.Comparator;

public class DefaultRanking implements RankingStrategy {

    @Override
    public Comparator<Agent> getComparator() {
        return (a,b) -> {
            if(a.getAvgRating().equals(b.getAvgRating())){
                return a.getAgentId().compareTo(b.getAgentId());
            }
            return Double.compare(b.getAvgRating(), a.getAvgRating());
        };
    }
}
