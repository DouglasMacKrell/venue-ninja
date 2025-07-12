package com.venueninja.service;

import com.venueninja.model.Venue;
import com.venueninja.model.SeatRecommendation;
import com.venueninja.repository.VenueRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("VenueService Unit Tests")
class VenueServiceTest {

    @Mock
    private VenueRepository venueRepository;

    @InjectMocks
    private VenueService venueService;

    private Venue madisonSquareGarden;
    private Venue yankeeStadium;
    private List<Venue> allVenues;

    @BeforeEach
    void setUp() {
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

        allVenues = Arrays.asList(madisonSquareGarden, yankeeStadium);
    }

    @Test
    @DisplayName("Should return all venues when repository has data")
    void getAllVenues_WhenVenuesExist_ShouldReturnAllVenues() {
        // Arrange
        when(venueRepository.findAll()).thenReturn(allVenues);

        // Act
        List<Venue> result = venueService.getAllVenues();

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result).containsExactlyInAnyOrder(madisonSquareGarden, yankeeStadium);
        verify(venueRepository).findAll();
        verifyNoMoreInteractions(venueRepository);
    }

    @Test
    @DisplayName("Should return empty list when repository has no data")
    void getAllVenues_WhenNoVenuesExist_ShouldReturnEmptyList() {
        // Arrange
        when(venueRepository.findAll()).thenReturn(Arrays.asList());

        // Act
        List<Venue> result = venueService.getAllVenues();

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
        verify(venueRepository).findAll();
        verifyNoMoreInteractions(venueRepository);
    }

    @Test
    @DisplayName("Should return venue when venue exists")
    void getVenue_WhenVenueExists_ShouldReturnVenue() {
        // Arrange
        String venueId = "msg";
        when(venueRepository.findById(venueId)).thenReturn(Optional.of(madisonSquareGarden));

        // Act
        Venue result = venueService.getVenue(venueId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("msg");
        assertThat(result.getName()).isEqualTo("Madison Square Garden");
        assertThat(result.getRecommendations()).hasSize(2);
        verify(venueRepository).findById(venueId);
        verifyNoMoreInteractions(venueRepository);
    }

    @Test
    @DisplayName("Should throw exception when venue does not exist")
    void getVenue_WhenVenueDoesNotExist_ShouldThrowException() {
        // Arrange
        String venueId = "nonexistent";
        when(venueRepository.findById(venueId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> venueService.getVenue(venueId))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Venue not found with id: " + venueId);
        
        verify(venueRepository).findById(venueId);
        verifyNoMoreInteractions(venueRepository);
    }

    @Test
    @DisplayName("Should handle null venue ID")
    @SuppressWarnings("all")
    void getVenue_WhenVenueIdIsNull_ShouldThrowException() {
        // Arrange
        @SuppressWarnings("all")
        String venueId = null;
        when(venueRepository.findById(null)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> venueService.getVenue(venueId))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Venue not found with id: null");
        
        verify(venueRepository).findById(null);
        verifyNoMoreInteractions(venueRepository);
    }

    @Test
    @DisplayName("Should handle empty venue ID")
    void getVenue_WhenVenueIdIsEmpty_ShouldThrowException() {
        // Arrange
        String venueId = "";
        when(venueRepository.findById("")).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> venueService.getVenue(venueId))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Venue not found with id: ");
        
        verify(venueRepository).findById("");
        verifyNoMoreInteractions(venueRepository);
    }

    @Test
    @DisplayName("Should return venue with empty recommendations")
    void getVenue_WhenVenueHasNoRecommendations_ShouldReturnVenue() {
        // Arrange
        String venueId = "yankee";
        when(venueRepository.findById(venueId)).thenReturn(Optional.of(yankeeStadium));

        // Act
        Venue result = venueService.getVenue(venueId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("yankee");
        assertThat(result.getName()).isEqualTo("Yankee Stadium");
        assertThat(result.getRecommendations()).isEmpty();
        verify(venueRepository).findById(venueId);
        verifyNoMoreInteractions(venueRepository);
    }

    @Test
    @DisplayName("Should handle repository exception gracefully")
    void getAllVenues_WhenRepositoryThrowsException_ShouldPropagateException() {
        // Arrange
        RuntimeException repositoryException = new RuntimeException("Database connection failed");
        when(venueRepository.findAll()).thenThrow(repositoryException);

        // Act & Assert
        assertThatThrownBy(() -> venueService.getAllVenues())
            .isInstanceOf(RuntimeException.class)
            .isEqualTo(repositoryException);
        
        verify(venueRepository).findAll();
        verifyNoMoreInteractions(venueRepository);
    }

    @Test
    @DisplayName("Should handle repository exception for getVenue")
    void getVenue_WhenRepositoryThrowsException_ShouldPropagateException() {
        // Arrange
        String venueId = "msg";
        RuntimeException repositoryException = new RuntimeException("Database connection failed");
        when(venueRepository.findById(venueId)).thenThrow(repositoryException);

        // Act & Assert
        assertThatThrownBy(() -> venueService.getVenue(venueId))
            .isInstanceOf(RuntimeException.class)
            .isEqualTo(repositoryException);
        
        verify(venueRepository).findById(venueId);
        verifyNoMoreInteractions(venueRepository);
    }
} 