package com.venueninja.regression;

import com.venueninja.model.Venue;
import com.venueninja.model.SeatRecommendation;
import com.venueninja.repository.VenueRepository;
import com.venueninja.testdata.TestDataBuilder;
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

/**
 * Regression Test Suite
 * 
 * This test suite ensures that previously working functionality continues to work
 * after any changes to the codebase. It guards against breaking changes and
 * validates the core functionality remains intact.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DisplayName("Regression Test Suite")
class RegressionTestSuite {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private VenueRepository venueRepository;

    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port;
        venueRepository.deleteAll();
    }

    @Test
    @DisplayName("Core API endpoints should remain functional")
    void coreApiEndpoints_ShouldRemainFunctional() {
        // Arrange
        Venue msg = TestDataBuilder.createMadisonSquareGarden();
        venueRepository.save(msg);

        // Act & Assert - Test all core endpoints
        ResponseEntity<Venue[]> allVenuesResponse = restTemplate.getForEntity(baseUrl + "/venues", Venue[].class);
        assertThat(allVenuesResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(allVenuesResponse.getBody()).hasSize(1);

        ResponseEntity<Venue> singleVenueResponse = restTemplate.getForEntity(baseUrl + "/venues/msg", Venue.class);
        assertThat(singleVenueResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(singleVenueResponse.getBody()).isNotNull();
        Venue venue = singleVenueResponse.getBody();
        if (venue != null) {
            assertThat(venue.getId()).isEqualTo("msg");
            assertThat(venue.getName()).isEqualTo("Madison Square Garden");
        }
    }

    @Test
    @DisplayName("Venue data structure should remain consistent")
    void venueDataStructure_ShouldRemainConsistent() {
        // Arrange
        Venue msg = TestDataBuilder.createMadisonSquareGarden();
        venueRepository.save(msg);

        // Act
        ResponseEntity<Venue> response = restTemplate.getForEntity(baseUrl + "/venues/msg", Venue.class);

        // Assert - Verify all expected fields are present and correctly typed
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Venue venue = response.getBody();
        
        // Core venue fields
        assertThat(venue).isNotNull();
        if (venue != null) {
            assertThat(venue.getId()).isEqualTo("msg");
            assertThat(venue.getName()).isEqualTo("Madison Square Garden");
            assertThat(venue.getRecommendations()).isNotNull();
            assertThat(venue.getRecommendations()).hasSize(3);

            // Verify first recommendation structure
            SeatRecommendation firstRec = venue.getRecommendations().get(0);
            assertThat(firstRec.getSection()).isEqualTo("104");
            assertThat(firstRec.getCategory()).isEqualTo("Lower Bowl");
            assertThat(firstRec.getReason()).isEqualTo("Best resale value & view of stage");
            assertThat(firstRec.getEstimatedPrice()).isEqualTo("$250");
            assertThat(firstRec.getTip()).isEqualTo("Avoid row 20+ due to rigging obstruction");
        }
    }

    @Test
    @DisplayName("Empty venue list should return empty array")
    void emptyVenueList_ShouldReturnEmptyArray() {
        // Act
        ResponseEntity<Venue[]> response = restTemplate.getForEntity(baseUrl + "/venues", Venue[].class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isEmpty();
    }

    @Test
    @DisplayName("Non-existent venue should return 500")
    void nonExistentVenue_ShouldReturn500() {
        // Act
        ResponseEntity<String> response = restTemplate.getForEntity(baseUrl + "/venues/nonexistent", String.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    @DisplayName("Multiple venues should be returned correctly")
    void multipleVenues_ShouldBeReturnedCorrectly() {
        // Arrange
        List<Venue> venues = TestDataBuilder.createAllTestVenues();
        venueRepository.saveAll(venues);

        // Act
        ResponseEntity<Venue[]> response = restTemplate.getForEntity(baseUrl + "/venues", Venue[].class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(3);
        
        List<Venue> returnedVenues = Arrays.asList(response.getBody());
        assertThat(returnedVenues).extracting("id").containsExactlyInAnyOrder("msg", "yankee", "barclays");
        assertThat(returnedVenues).extracting("name").containsExactlyInAnyOrder(
            "Madison Square Garden", "Yankee Stadium", "Barclays Center");
    }

    @Test
    @DisplayName("Venue with no recommendations should be handled correctly")
    void venueWithNoRecommendations_ShouldBeHandledCorrectly() {
        // Arrange
        Venue emptyVenue = TestDataBuilder.createVenueWithNoRecommendations();
        venueRepository.save(emptyVenue);

        // Act
        ResponseEntity<Venue> response = restTemplate.getForEntity(baseUrl + "/venues/empty", Venue.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        Venue venue = response.getBody();
        if (venue != null) {
            assertThat(venue.getId()).isEqualTo("empty");
            assertThat(venue.getName()).isEqualTo("Empty Venue");
            assertThat(venue.getRecommendations()).isEmpty();
        }
    }

    @Test
    @DisplayName("Venue with null recommendations should be handled correctly")
    void venueWithNullRecommendations_ShouldBeHandledCorrectly() {
        // Arrange
        Venue nullVenue = TestDataBuilder.createVenueWithNullRecommendations();
        venueRepository.save(nullVenue);

        // Act
        ResponseEntity<Venue> response = restTemplate.getForEntity(baseUrl + "/venues/null", Venue.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        Venue venue = response.getBody();
        if (venue != null) {
            assertThat(venue.getId()).isEqualTo("null");
            assertThat(venue.getName()).isEqualTo("Null Recommendations Venue");
            // Hibernate converts null to empty list
            assertThat(venue.getRecommendations()).isEmpty();
        }
    }

    @Test
    @DisplayName("Case-sensitive venue IDs should work correctly")
    void caseSensitiveVenueIds_ShouldWorkCorrectly() {
        // Arrange
        Venue msg = TestDataBuilder.createMadisonSquareGarden();
        venueRepository.save(msg);

        // Act
        ResponseEntity<Venue> correctCase = restTemplate.getForEntity(baseUrl + "/venues/msg", Venue.class);
        ResponseEntity<Venue> wrongCase = restTemplate.getForEntity(baseUrl + "/venues/MSG", Venue.class);

        // Assert
        assertThat(correctCase.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(wrongCase.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    @DisplayName("Special characters in venue names should be preserved")
    void specialCharactersInVenueNames_ShouldBePreserved() {
        // Arrange
        Venue specialVenue = TestDataBuilder.createVenueWithSpecialCharacters();
        venueRepository.save(specialVenue);

        // Act
        ResponseEntity<Venue> response = restTemplate.getForEntity(baseUrl + "/venues/special", Venue.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        Venue venue = response.getBody();
        if (venue != null) {
            assertThat(venue.getName()).isEqualTo("Madison Square Garden & Arena (MSG) - The World's Most Famous Arena!");
        }
    }

    @Test
    @DisplayName("Long venue names should be handled correctly")
    void longVenueNames_ShouldBeHandledCorrectly() {
        // Arrange
        Venue longNameVenue = TestDataBuilder.createVenueWithLongName();
        venueRepository.save(longNameVenue);

        // Act
        ResponseEntity<Venue> response = restTemplate.getForEntity(baseUrl + "/venues/long", Venue.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        Venue venue = response.getBody();
        if (venue != null) {
            assertThat(venue.getName()).isEqualTo(
                "This is a very long venue name that might exceed normal expectations for venue naming conventions and could potentially cause issues with display or storage");
        }
    }

    @Test
    @DisplayName("Numeric venue IDs should work correctly")
    void numericVenueIds_ShouldWorkCorrectly() {
        // Arrange
        Venue numericVenue = TestDataBuilder.createVenueWithNumericId();
        venueRepository.save(numericVenue);

        // Act
        ResponseEntity<Venue> response = restTemplate.getForEntity(baseUrl + "/venues/123", Venue.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        Venue venue = response.getBody();
        if (venue != null) {
            assertThat(venue.getId()).isEqualTo("123");
            assertThat(venue.getName()).isEqualTo("Numeric ID Venue");
        }
    }

    @Test
    @DisplayName("Hyphenated venue IDs should work correctly")
    void hyphenatedVenueIds_ShouldWorkCorrectly() {
        // Arrange
        Venue hyphenatedVenue = TestDataBuilder.createVenueWithHyphenatedId();
        venueRepository.save(hyphenatedVenue);

        // Act
        ResponseEntity<Venue> response = restTemplate.getForEntity(baseUrl + "/venues/msg-arena", Venue.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        Venue venue = response.getBody();
        if (venue != null) {
            assertThat(venue.getId()).isEqualTo("msg-arena");
            assertThat(venue.getName()).isEqualTo("Hyphenated ID Venue");
        }
    }

    @Test
    @DisplayName("Underscore venue IDs should work correctly")
    void underscoreVenueIds_ShouldWorkCorrectly() {
        // Arrange
        Venue underscoreVenue = TestDataBuilder.createVenueWithUnderscoreId();
        venueRepository.save(underscoreVenue);

        // Act
        ResponseEntity<Venue> response = restTemplate.getForEntity(baseUrl + "/venues/msg_arena", Venue.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        Venue venue = response.getBody();
        if (venue != null) {
            assertThat(venue.getId()).isEqualTo("msg_arena");
            assertThat(venue.getName()).isEqualTo("Underscore ID Venue");
        }
    }

    @Test
    @DisplayName("Response headers should be correct")
    void responseHeaders_ShouldBeCorrect() {
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
    @DisplayName("Database persistence should work correctly")
    void databasePersistence_ShouldWorkCorrectly() {
        // Arrange
        Venue msg = TestDataBuilder.createMadisonSquareGarden();
        venueRepository.save(msg);

        // Act - Verify data is persisted correctly
        List<Venue> allVenues = venueRepository.findAll();
        Venue foundVenue = venueRepository.findById("msg").orElse(null);

        // Assert
        assertThat(allVenues).hasSize(1);
        assertThat(foundVenue).isNotNull();
        assertThat(foundVenue.getId()).isEqualTo("msg");
        assertThat(foundVenue.getName()).isEqualTo("Madison Square Garden");
        assertThat(foundVenue.getRecommendations()).hasSize(3);
    }

    @Test
    @DisplayName("Application context should load successfully")
    void applicationContext_ShouldLoadSuccessfully() {
        // This test ensures the Spring application context loads without errors
        // If this test passes, it means all beans are properly configured
        assertThat(restTemplate).isNotNull();
        assertThat(venueRepository).isNotNull();
    }

    @Test
    @DisplayName("Health check - basic functionality should work")
    void healthCheck_BasicFunctionalityShouldWork() {
        // This is a basic health check to ensure the application is working
        // Arrange
        Venue msg = TestDataBuilder.createMadisonSquareGarden();
        venueRepository.save(msg);

        // Act - Test basic CRUD operations
        List<Venue> savedVenues = venueRepository.findAll();
        Venue retrievedVenue = venueRepository.findById("msg").orElse(null);

        // Assert
        assertThat(savedVenues).hasSize(1);
        assertThat(retrievedVenue).isNotNull();
        assertThat(retrievedVenue.getId()).isEqualTo("msg");
    }
} 