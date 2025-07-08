package com.venueninja.service;

import com.venueninja.model.SeatRecommendation;
import com.venueninja.model.Venue;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class VenueService {

    private List<Venue> venues = Arrays.asList(
        new Venue("msg", "Madison Square Garden", Arrays.asList(
            new SeatRecommendation("104", "Lower Bowl", "Best resale value & view of stage", "$250", "Avoid row 20+ due to rigging obstruction")
        )),
        new Venue("barclays", "Barclays Center", Arrays.asList(
            new SeatRecommendation("112", "Mid Bowl", "Close to player benches", "$180", "Row 10 = best balance of price/view")
        ))
    );

    public List<Venue> getAllVenues() {
        return venues;
    }

    public Venue getVenueById(String id) {
        return venues.stream()
                .filter(v -> v.getId().equalsIgnoreCase(id))
                .findFirst()
                .orElse(null);
    }
}
