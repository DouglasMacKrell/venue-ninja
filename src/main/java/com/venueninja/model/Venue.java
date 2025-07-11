package com.venueninja.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Venue {

    @Id
    private String id;

    private String name;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "venue_id") // Foreign key in seat_recommendation
    private List<SeatRecommendation> recommendations;

    public Venue() {}

    public Venue(String id, String name, List<SeatRecommendation> recommendations) {
        this.id = id;
        this.name = name;
        this.recommendations = recommendations;
    }

    // Getters & Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<SeatRecommendation> getRecommendations() { return recommendations; }
    public void setRecommendations(List<SeatRecommendation> recommendations) {
        this.recommendations = recommendations;
    }
}
