package org.example.atlassian.customerSatisfaction.dto;

import java.time.LocalDate;

public class Rating{
    private String userId;
    private Integer rating;
    private String text;
    private LocalDate localDate;

    public Rating(String userId, Integer rating, String text) {
        this.userId = userId;
        this.rating = rating;
        this.text = text;
        this.localDate = LocalDate.now();
    }

    public Integer getRating() {
        return rating;
    }

    public LocalDate getLocalDate() {
        return localDate;
    }
}
