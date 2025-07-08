package com.venueninja;

import com.venueninja.model.Venue;
import com.venueninja.service.VenueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/venues")
public class VenueController {

    @Autowired
    private VenueService venueService;

    @GetMapping
    public List<Venue> getVenues() {
        return venueService.getAllVenues();
    }

    @GetMapping("/{id}/recommendations")
    public Venue getVenueById(@PathVariable String id) {
        return venueService.getVenueById(id);
    }
}
