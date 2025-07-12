package com.venueninja.controller;

import com.venueninja.model.Venue;
import com.venueninja.service.VenueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/venues")
@Tag(name = "Venue Management", description = "APIs for managing venue information and seat recommendations")
public class VenueController {

    private final VenueService venueService;

    public VenueController(VenueService venueService) {
        this.venueService = venueService;
    }

    @GetMapping
    @Operation(
        summary = "Get all venues",
        description = "Retrieves a list of all available venues with their seat recommendations"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved venues",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Venue.class),
                examples = @ExampleObject(
                    name = "Sample Response",
                    value = """
                    [
                      {
                        "id": "msg",
                        "name": "Madison Square Garden",
                        "recommendations": [
                          {
                            "section": "104",
                            "category": "Lower Bowl",
                            "reason": "Best resale value & view of stage",
                            "estimatedPrice": "$250",
                            "tip": "Avoid row 20+ due to rigging obstruction"
                          }
                        ]
                      }
                    ]
                    """
                )
            )
        ),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<Venue>> getAllVenues() {
        List<Venue> venues = venueService.getAllVenues();
        return ResponseEntity.ok(venues);
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Get venue by ID",
        description = "Retrieves a specific venue by its unique identifier"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved venue",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Venue.class)
            )
        ),
        @ApiResponse(responseCode = "404", description = "Venue not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Venue> getVenueById(
            @Parameter(description = "Unique identifier of the venue", example = "msg")
            @PathVariable String id) {
        Venue venue = venueService.getVenue(id);
        return ResponseEntity.ok(venue);
    }
}
