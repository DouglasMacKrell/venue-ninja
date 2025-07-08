package com.venueninja.service;

import com.venueninja.model.SeatRecommendation;
import com.venueninja.model.Venue;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class VenueService {
    private final Map<String, Venue> venueMap = new HashMap<>();

    public VenueService() {
        seedData();
    }

    private void seedData() {
        venueMap.put("msg", new Venue("msg", "Madison Square Garden", List.of(
            new SeatRecommendation("104", "Lower Bowl", "Best resale value & view of stage", "$250", "Avoid row 20+ due to rigging obstruction"),
            new SeatRecommendation("212", "Budget 200-Level", "Good price-to-view ratio", "$120", "Stick to first 5 rows for best view"),
            new SeatRecommendation("VIP C", "Courtside Celebrity", "Ultimate fan experience", "$750", "Dress sharp, cameras are always rolling")
        )));
        venueMap.put("yankee", new Venue("yankee", "Yankee Stadium", List.of(
            new SeatRecommendation("Legends", "Premium", "Close to the field, luxury service", "$450", "Includes all-you-can-eat buffet"),
            new SeatRecommendation("Upper Deck 423", "Budget Shade", "Great for day games", "$60", "Covered from sun and rain"),
            new SeatRecommendation("203", "Bleacher Creatures Zone", "Hardcore fan energy", "$45", "Chant with the crowd or be left behind")
        )));
        venueMap.put("barclays", new Venue("barclays", "Barclays Center", List.of(
            new SeatRecommendation("118", "Lower Bowl", "Great view of the action", "$180", "Behind team benches"),
            new SeatRecommendation("210", "Mid Bowl Value", "Good elevation for basketball", "$110", "Center seats best")
        )));
        venueMap.put("redrocks", new Venue("redrocks", "Red Rocks Amphitheatre", List.of(
            new SeatRecommendation("Center Terrace", "Acoustic Sweet Spot", "Best natural sound quality", "$140", "Bring a cushion!"),
            new SeatRecommendation("Upper Bowl", "Scenic Views", "Best sunset shots", "$95", "Come early, stairs are steep")
        )));
        venueMap.put("radiocity", new Venue("radiocity", "Radio City Music Hall", List.of(
            new SeatRecommendation("Orchestra A", "Front Orchestra", "Close to the Rockettes", "$200", "Rows A–E are prime"),
            new SeatRecommendation("Balcony R", "Side Balcony Gems", "Great acoustics & value", "$85", "Farther from crowd noise")
        )));
        venueMap.put("citi", new Venue("citi", "Citi Field", List.of(
            new SeatRecommendation("111", "Field Level", "Right behind home plate", "$280", "Watch the pitcher’s grip closely"),
            new SeatRecommendation("Bleachers", "Budget", "Cheapest option in the park", "$40", "Bring sunscreen, no shade"),
            new SeatRecommendation("305", "Family Zone", "Kiddie games nearby", "$65", "Kids eat free days are frequent")
        )));
        venueMap.put("att", new Venue("att", "AT&T Stadium", List.of(
            new SeatRecommendation("C210", "Club Level VIP", "Upscale amenities & views", "$325", "Includes access to lounges"),
            new SeatRecommendation("50-Yard Mid", "Center Field Premium", "Perfect strategic view", "$480", "Watch both sidelines like a coach")
        )));
        venueMap.put("crypto", new Venue("crypto", "Crypto.com Arena", List.of(
            new SeatRecommendation("Courtside", "Celebrity Experience", "Star sightings and up-close action", "$900", "Cameras always rolling"),
            new SeatRecommendation("325", "Upper Bowl Steal", "Budget with solid views", "$55", "Watch for halftime deals")
        )));
        venueMap.put("scg", new Venue("scg", "Sydney Cricket Ground", List.of(
            new SeatRecommendation("Members Pavilion", "Historic VIP", "Old-world charm and exclusivity", "$300", "Strict dress code enforced"),
            new SeatRecommendation("The Hill", "GA Lawn", "Bring a blanket, chill out", "$50", "Arrive early for shade")
        )));
        venueMap.put("marvel", new Venue("marvel", "Marvel Stadium", List.of(
            new SeatRecommendation("Medallion Club", "AFL Elite", "Centrally located and catered", "$340", "Includes bar access"),
            new SeatRecommendation("Cheer Squad", "Fan Section", "Most energetic area", "$60", "Wear team colors or be ready to sing")
        )));
    }

    public List<Venue> getAllVenues() {
        return new ArrayList<>(venueMap.values());
    }

    public Optional<Venue> getVenueById(String id) {
        return Optional.ofNullable(venueMap.get(id));
    }
}
