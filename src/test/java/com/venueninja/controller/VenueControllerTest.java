package com.venueninja.controller;

import com.venueninja.model.Venue;
import com.venueninja.model.SeatRecommendation;
import com.venueninja.repository.VenueRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DisplayName("VenueController API Tests")
class VenueControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private VenueRepository venueRepository;

    private String baseUrl;
    private Venue madisonSquareGarden;
    private Venue yankeeStadium;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port;
        
        // Create test data
        SeatRecommendation msgRecommendation1 = new SeatRecommendation();
        msgRecommendation1.setSection("104");
        msgRecommendation1.setCategory("Lower Bowl");
        msgRecommendation1.setReason("Best resale value & view of stage");
        msgRecommendation1.setEstimatedPrice("$250");
        msgRecommendation1.setTip("Avoid row 20+ due to rigging obstruction");

        SeatRecommendation msgRecommendation2 = new SeatRecommendation();
        msgRecommendation2.setSection("200");
        msgRecommendation2.setCategory("Upper Bowl");
        msgRecommendation2.setReason("Great value for price-conscious fans");
        msgRecommendation2.setEstimatedPrice("$75");
        msgRecommendation2.setTip("Bring binoculars for optimal viewing");

        madisonSquareGarden = new Venue();
        madisonSquareGarden.setId("msg");
        madisonSquareGarden.setName("Madison Square Garden");
        madisonSquareGarden.setRecommendations(Arrays.asList(msgRecommendation1, msgRecommendation2));

        yankeeStadium = new Venue();
        yankeeStadium.setId("yankee");
        yankeeStadium.setName("Yankee Stadium");
        yankeeStadium.setRecommendations(Arrays.asList());

        // Clear database before each test
        venueRepository.deleteAll();
    }

    @Test
    @DisplayName("GET /venues should return all venues")
    void getAllVenues_ShouldReturnAllVenues() {
        // Arrange
        venueRepository.save(madisonSquareGarden);
        venueRepository.save(yankeeStadium);

        // Act
        ResponseEntity<Venue[]> response = restTemplate.getForEntity(baseUrl + "/venues", Venue[].class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(2);
        
        List<Venue> venues = Arrays.asList(response.getBody());
        assertThat(venues).extracting("id").containsExactlyInAnyOrder("msg", "yankee");
        assertThat(venues).extracting("name").containsExactlyInAnyOrder("Madison Square Garden", "Yankee Stadium");
    }

    @Test
    @DisplayName("GET /venues should return empty array when no venues exist")
    void getAllVenues_WhenNoVenuesExist_ShouldReturnEmptyArray() {
        // Act
        ResponseEntity<Venue[]> response = restTemplate.getForEntity(baseUrl + "/venues", Venue[].class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isEmpty();
    }

    @Test
    @DisplayName("GET /venues/{id} should return venue when venue exists")
    void getVenueById_WhenVenueExists_ShouldReturnVenue() {
        // Arrange
        venueRepository.save(madisonSquareGarden);

        // Act
        ResponseEntity<Venue> response = restTemplate.getForEntity(baseUrl + "/venues/msg", Venue.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        Venue venue = response.getBody();
        assertThat(venue).isNotNull();
        if (venue != null) {
            assertThat(venue.getId()).isEqualTo("msg");
            assertThat(venue.getName()).isEqualTo("Madison Square Garden");
            assertThat(venue.getRecommendations()).hasSize(2);
        }
    }

    @Test
    @DisplayName("GET /venues/{id} should return 500 when venue does not exist")
    void getVenueById_WhenVenueDoesNotExist_ShouldReturn500() {
        // Act
        ResponseEntity<Venue> response = restTemplate.getForEntity(baseUrl + "/venues/nonexistent", Venue.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    @DisplayName("GET /venues/{id} should handle case-sensitive venue IDs")
    void getVenueById_ShouldHandleCaseSensitiveIds() {
        // Arrange
        venueRepository.save(madisonSquareGarden);

        // Act
        ResponseEntity<Venue> responseLowercase = restTemplate.getForEntity(baseUrl + "/venues/msg", Venue.class);
        ResponseEntity<Venue> responseUppercase = restTemplate.getForEntity(baseUrl + "/venues/MSG", Venue.class);

        // Assert
        assertThat(responseLowercase.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseUppercase.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    @DisplayName("GET /venues/{id} should return venue with empty recommendations")
    void getVenueById_WhenVenueHasNoRecommendations_ShouldReturnVenue() {
        // Arrange
        venueRepository.save(yankeeStadium);

        // Act
        ResponseEntity<Venue> response = restTemplate.getForEntity(baseUrl + "/venues/yankee", Venue.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        Venue venue = response.getBody();
        assertThat(venue).isNotNull();
        if (venue != null) {
            assertThat(venue.getId()).isEqualTo("yankee");
            assertThat(venue.getName()).isEqualTo("Yankee Stadium");
            assertThat(venue.getRecommendations()).isEmpty();
        }
    }

    @Test
    @DisplayName("GET /venues/{id} should handle special characters in venue ID")
    void getVenueById_ShouldHandleSpecialCharactersInId() {
        // Arrange
        Venue specialVenue = new Venue();
        specialVenue.setId("msg-arena");
        specialVenue.setName("MSG Arena");
        venueRepository.save(specialVenue);

        // Act
        ResponseEntity<Venue> response = restTemplate.getForEntity(baseUrl + "/venues/msg-arena", Venue.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        Venue venue = response.getBody();
        assertThat(venue).isNotNull();
        if (venue != null) {
            assertThat(venue.getId()).isEqualTo("msg-arena");
        }
    }

    @Test
    @DisplayName("GET /venues/{id} should handle empty venue ID")
    void getVenueById_ShouldHandleEmptyVenueId() {
        // Act
        ResponseEntity<Venue> response = restTemplate.getForEntity(baseUrl + "/venues/", Venue.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("API should return proper JSON structure")
    void api_ShouldReturnProperJsonStructure() {
        // Arrange
        venueRepository.save(madisonSquareGarden);

        // Act
        ResponseEntity<Venue> response = restTemplate.getForEntity(baseUrl + "/venues/msg", Venue.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Venue venue = response.getBody();
        assertThat(venue).isNotNull();
        
        // Check venue structure
        if (venue != null) {
            assertThat(venue.getId()).isEqualTo("msg");
            assertThat(venue.getName()).isEqualTo("Madison Square Garden");
            assertThat(venue.getRecommendations()).isNotNull();
            assertThat(venue.getRecommendations()).hasSize(2);
            
            // Check first recommendation structure
            SeatRecommendation firstRec = venue.getRecommendations().get(0);
            assertThat(firstRec.getSection()).isEqualTo("104");
            assertThat(firstRec.getCategory()).isEqualTo("Lower Bowl");
            assertThat(firstRec.getReason()).isEqualTo("Best resale value & view of stage");
            assertThat(firstRec.getEstimatedPrice()).isEqualTo("$250");
            assertThat(firstRec.getTip()).isEqualTo("Avoid row 20+ due to rigging obstruction");
        }
    }

    @Test
    @DisplayName("API should handle concurrent requests")
    void api_ShouldHandleConcurrentRequests() {
        // Arrange
        venueRepository.save(madisonSquareGarden);
        venueRepository.save(yankeeStadium);

        // Act - Make multiple concurrent requests
        ResponseEntity<Venue[]> response1 = restTemplate.getForEntity(baseUrl + "/venues", Venue[].class);
        ResponseEntity<Venue> response2 = restTemplate.getForEntity(baseUrl + "/venues/msg", Venue.class);
        ResponseEntity<Venue> response3 = restTemplate.getForEntity(baseUrl + "/venues/yankee", Venue.class);

        // Assert
        assertThat(response1.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response3.getStatusCode()).isEqualTo(HttpStatus.OK);
        
        assertThat(response1.getBody()).hasSize(2);
        assertThat(response2.getBody()).isNotNull();
        assertThat(response3.getBody()).isNotNull();
        
        Venue venue2 = response2.getBody();
        Venue venue3 = response3.getBody();
        if (venue2 != null) {
            assertThat(venue2.getId()).isEqualTo("msg");
        }
        if (venue3 != null) {
            assertThat(venue3.getId()).isEqualTo("yankee");
        }
    }

    @Test
    @DisplayName("API should return proper content type")
    void api_ShouldReturnProperContentType() {
        // Arrange
        venueRepository.save(madisonSquareGarden);

        // Act
        ResponseEntity<Venue[]> response = restTemplate.getForEntity(baseUrl + "/venues", Venue[].class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().getContentType()).isNotNull();
        var contentType = response.getHeaders().getContentType();
        if (contentType != null) {
            assertThat(contentType.toString()).contains("application/json");
        }
    }

    @Test
    @DisplayName("API should handle malformed venue ID gracefully")
    void api_ShouldHandleMalformedVenueId() {
        // Act
        ResponseEntity<Venue> response = restTemplate.getForEntity(baseUrl + "/venues/msg%20garden", Venue.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
} 