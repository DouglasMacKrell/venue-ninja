package com.venueninja;

import com.venueninja.model.Venue;
import com.venueninja.service.VenueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Tag(name = "Venue API", description = "Operations related to venue information and recommendations")
@RestController
@RequestMapping("/venues")
public class VenueController {

    @Autowired
    private VenueService venueService;

    @Operation(summary = "Get all venues")
    @ApiResponse(responseCode = "200", description = "List of all available venues")
    @GetMapping
    public List<Venue> getVenues() {
        return venueService.getAllVenues();
    }

    @Operation(summary = "Get venue by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Found the venue"),
        @ApiResponse(responseCode = "404", description = "Venue not found")
    })
    @GetMapping("/{id}")
    public Venue getVenueById(@PathVariable String id) {
        return venueService.getVenueById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Venue not found"));
    }
}
