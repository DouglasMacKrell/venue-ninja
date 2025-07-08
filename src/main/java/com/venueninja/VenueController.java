package com.venueninja;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VenueController {

    @GetMapping("/venues")
    public String[] getVenues() {
        return new String[] { "Madison Square Garden", "Red Rocks", "Barclays Center" };
    }
}
