package org.example.atlassian.customerSatisfaction;

import org.example.atlassian.customerSatisfaction.dto.Agent;
import org.example.atlassian.customerSatisfaction.service.TieBreaker;

import java.util.List;

public interface IRatingService{
    Agent rateAgent(String userId, String text, Integer rating, String agentId);
    List<Agent> getAllAgents(TieBreaker tieBreaker);
    List<Agent> getTopAgentForMonth(int year, int month, TieBreaker tieBreaker);
}
