package org.example.atlassian.customerSatisfaction.service;

import org.example.atlassian.customerSatisfaction.dto.Agent;

import java.util.Comparator;

public interface RankingStrategy{
    Comparator<Agent> getComparator();
}
