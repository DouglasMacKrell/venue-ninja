package com.venueninja.model;

import jakarta.persistence.*;

@Entity
public class SeatRecommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String section;
    private String category;
    private String reason;
    private String estimatedPrice;
    private String tip;

    public SeatRecommendation() {}

    public SeatRecommendation(String section, String category, String reason, String estimatedPrice, String tip) {
        this.section = section;
        this.category = category;
        this.reason = reason;
        this.estimatedPrice = estimatedPrice;
        this.tip = tip;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSection() { return section; }
    public void setSection(String section) { this.section = section; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getEstimatedPrice() { return estimatedPrice; }
    public void setEstimatedPrice(String estimatedPrice) { this.estimatedPrice = estimatedPrice; }

    public String getTip() { return tip; }
    public void setTip(String tip) { this.tip = tip; }
}
