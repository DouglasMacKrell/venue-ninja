package com.venueninja.repository;

import com.venueninja.model.Venue;
import com.venueninja.model.SeatRecommendation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("VenueRepository Integration Tests")
class VenueRepositoryTest {

    @Autowired
    private VenueRepository venueRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Venue madisonSquareGarden;
    private Venue yankeeStadium;
    private SeatRecommendation msgRecommendation1;
    private SeatRecommendation msgRecommendation2;

    @BeforeEach
    void setUp() {
        // Create test data
        madisonSquareGarden = new Venue();
        madisonSquareGarden.setId("msg");
        madisonSquareGarden.setName("Madison Square Garden");

        yankeeStadium = new Venue();
        yankeeStadium.setId("yankee");
        yankeeStadium.setName("Yankee Stadium");

        msgRecommendation1 = new SeatRecommendation();
        msgRecommendation1.setSection("104");
        msgRecommendation1.setCategory("Lower Bowl");
        msgRecommendation1.setReason("Best resale value & view of stage");
        msgRecommendation1.setEstimatedPrice("$250");
        msgRecommendation1.setTip("Avoid row 20+ due to rigging obstruction");

        msgRecommendation2 = new SeatRecommendation();
        msgRecommendation2.setSection("200");
        msgRecommendation2.setCategory("Upper Bowl");
        msgRecommendation2.setReason("Great value for price-conscious fans");
        msgRecommendation2.setEstimatedPrice("$75");
        msgRecommendation2.setTip("Bring binoculars for optimal viewing");
        
        // Clear database before each test to ensure isolation
        venueRepository.deleteAll();
        entityManager.flush();
        entityManager.clear();
    }

    @Test
    @DisplayName("Should save venue successfully")
    void save_ShouldPersistVenue() {
        // Act
        Venue savedVenue = venueRepository.save(madisonSquareGarden);

        // Assert
        assertThat(savedVenue).isNotNull();
        assertThat(savedVenue.getId()).isEqualTo("msg");
        assertThat(savedVenue.getName()).isEqualTo("Madison Square Garden");
        
        // Verify in database
        Venue found = entityManager.find(Venue.class, "msg");
        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo("Madison Square Garden");
    }

    @Test
    @DisplayName("Should find venue by ID when venue exists")
    void findById_WhenVenueExists_ShouldReturnVenue() {
        // Arrange
        entityManager.persistAndFlush(madisonSquareGarden);

        // Act
        Optional<Venue> found = venueRepository.findById("msg");

        // Assert
        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo("msg");
        assertThat(found.get().getName()).isEqualTo("Madison Square Garden");
    }

    @Test
    @DisplayName("Should return empty when venue does not exist")
    void findById_WhenVenueNotExists_ShouldReturnEmpty() {
        // Act
        Optional<Venue> found = venueRepository.findById("nonexistent");

        // Assert
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Should find all venues when venues exist")
    void findAll_WhenVenuesExist_ShouldReturnAllVenues() {
        // Arrange
        entityManager.persistAndFlush(madisonSquareGarden);
        entityManager.persistAndFlush(yankeeStadium);

        // Act
        List<Venue> allVenues = venueRepository.findAll();

        // Assert
        assertThat(allVenues).hasSize(2);
        assertThat(allVenues).extracting("id").containsExactlyInAnyOrder("msg", "yankee");
        assertThat(allVenues).extracting("name").containsExactlyInAnyOrder("Madison Square Garden", "Yankee Stadium");
    }

    @Test
    @DisplayName("Should return empty list when no venues exist")
    void findAll_WhenNoVenuesExist_ShouldReturnEmptyList() {
        // Act
        List<Venue> allVenues = venueRepository.findAll();

        // Assert
        assertThat(allVenues).isEmpty();
    }

    @Test
    @DisplayName("Should save venue with seat recommendations")
    void save_ShouldPersistVenueWithRecommendations() {
        // Arrange
        madisonSquareGarden.setRecommendations(Arrays.asList(msgRecommendation1, msgRecommendation2));

        // Act
        Venue savedVenue = venueRepository.save(madisonSquareGarden);

        // Assert
        assertThat(savedVenue).isNotNull();
        assertThat(savedVenue.getRecommendations()).hasSize(2);
        
        // Verify in database
        Venue found = entityManager.find(Venue.class, "msg");
        assertThat(found).isNotNull();
        assertThat(found.getRecommendations()).hasSize(2);
    }

    @Test
    @DisplayName("Should handle venue with null recommendations")
    void save_ShouldHandleVenueWithNullRecommendations() {
        // Arrange
        madisonSquareGarden.setRecommendations(null);

        // Act
        Venue savedVenue = venueRepository.save(madisonSquareGarden);

        // Assert
        assertThat(savedVenue).isNotNull();
        // Accept both null and empty list as valid outcomes
        assertThat(savedVenue.getRecommendations() == null || savedVenue.getRecommendations().isEmpty()).isTrue();
    }

    @Test
    @DisplayName("Should handle venue with empty recommendations")
    void save_ShouldHandleVenueWithEmptyRecommendations() {
        // Arrange
        madisonSquareGarden.setRecommendations(Arrays.asList());

        // Act
        Venue savedVenue = venueRepository.save(madisonSquareGarden);

        // Assert
        assertThat(savedVenue).isNotNull();
        assertThat(savedVenue.getRecommendations()).isEmpty();
    }

    @Test
    @DisplayName("Should update existing venue")
    void save_ShouldUpdateExistingVenue() {
        // Arrange
        entityManager.persistAndFlush(madisonSquareGarden);
        
        // Update venue name
        madisonSquareGarden.setName("Updated Madison Square Garden");

        // Act
        Venue updatedVenue = venueRepository.save(madisonSquareGarden);

        // Assert
        assertThat(updatedVenue).isNotNull();
        assertThat(updatedVenue.getName()).isEqualTo("Updated Madison Square Garden");
        
        // Verify in database
        Venue found = entityManager.find(Venue.class, "msg");
        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo("Updated Madison Square Garden");
    }

    @Test
    @DisplayName("Should handle case-sensitive venue IDs")
    void findById_ShouldHandleCaseSensitiveIds() {
        // Arrange
        entityManager.persistAndFlush(madisonSquareGarden);

        // Act
        Optional<Venue> foundLowercase = venueRepository.findById("msg");
        Optional<Venue> foundUppercase = venueRepository.findById("MSG");

        // Assert
        assertThat(foundLowercase).isPresent();
        assertThat(foundUppercase).isEmpty();
    }

    @Test
    @DisplayName("Should handle special characters in venue names")
    void save_ShouldHandleSpecialCharactersInNames() {
        // Arrange
        Venue specialVenue = new Venue();
        specialVenue.setId("special");
        specialVenue.setName("Madison Square Garden & Arena (MSG)");

        // Act
        Venue savedVenue = venueRepository.save(specialVenue);

        // Assert
        assertThat(savedVenue).isNotNull();
        assertThat(savedVenue.getName()).isEqualTo("Madison Square Garden & Arena (MSG)");
        
        // Verify in database
        Venue found = entityManager.find(Venue.class, "special");
        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo("Madison Square Garden & Arena (MSG)");
    }

    @Test
    @DisplayName("Should handle long venue names")
    void save_ShouldHandleLongVenueNames() {
        // Arrange
        Venue longNameVenue = new Venue();
        longNameVenue.setId("long");
        longNameVenue.setName("This is a very long venue name that might exceed normal expectations for venue naming conventions");

        // Act
        Venue savedVenue = venueRepository.save(longNameVenue);

        // Assert
        assertThat(savedVenue).isNotNull();
        assertThat(savedVenue.getName()).isEqualTo("This is a very long venue name that might exceed normal expectations for venue naming conventions");
    }
} 