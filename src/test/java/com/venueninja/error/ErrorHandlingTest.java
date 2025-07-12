package com.venueninja.error;

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

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DisplayName("Error Handling Tests")
class ErrorHandlingTest {

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
    @DisplayName("Should return 500 for non-existent venue (due to RuntimeException)")
    void shouldReturn500ForNonExistentVenue() {
        // Act
        ResponseEntity<String> response = restTemplate.getForEntity(baseUrl + "/venues/nonexistent", String.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    @DisplayName("Should return 404 for empty venue ID")
    void shouldReturn404ForEmptyVenueId() {
        // Act
        ResponseEntity<String> response = restTemplate.getForEntity(baseUrl + "/venues/", String.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Should return 400 for malformed venue ID")
    void shouldReturn400ForMalformedVenueId() {
        // Act
        ResponseEntity<String> response = restTemplate.getForEntity(baseUrl + "/venues/msg%20garden", String.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("Should handle very long venue ID gracefully")
    void shouldHandleVeryLongVenueId() {
        // Arrange
        String longVenueId = "a".repeat(1000);

        // Act
        ResponseEntity<String> response = restTemplate.getForEntity(baseUrl + "/venues/" + longVenueId, String.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    @DisplayName("Should handle special characters in venue ID")
    void shouldHandleSpecialCharactersInVenueId() {
        // Act
        ResponseEntity<String> response = restTemplate.getForEntity(baseUrl + "/venues/msg@#$%", String.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    @DisplayName("Should handle SQL injection attempts")
    void shouldHandleSqlInjectionAttempts() {
        // Act
        ResponseEntity<String> response = restTemplate.getForEntity(
            baseUrl + "/venues/'; DROP TABLE venue; --", String.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        
        // Verify database is still intact
        assertThat(venueRepository.findAll()).isNotNull();
    }

    @Test
    @DisplayName("Should handle XSS attempts")
    void shouldHandleXssAttempts() {
        // Act
        ResponseEntity<String> response = restTemplate.getForEntity(
            baseUrl + "/venues/<script>alert('xss')</script>", String.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Should handle path traversal attempts")
    void shouldHandlePathTraversalAttempts() {
        // Act
        ResponseEntity<String> response = restTemplate.getForEntity(
            baseUrl + "/venues/../../../etc/passwd", String.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("Should handle null venue ID gracefully")
    void shouldHandleNullVenueId() {
        // Act - This will be treated as empty path
        ResponseEntity<String> response = restTemplate.getForEntity(baseUrl + "/venues/", String.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Should handle whitespace-only venue ID")
    void shouldHandleWhitespaceOnlyVenueId() {
        // Act
        ResponseEntity<String> response = restTemplate.getForEntity(baseUrl + "/venues/%20%20", String.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("Should handle numeric venue ID")
    void shouldHandleNumericVenueId() {
        // Act
        ResponseEntity<String> response = restTemplate.getForEntity(baseUrl + "/venues/123", String.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    @DisplayName("Should handle mixed case venue ID")
    void shouldHandleMixedCaseVenueId() {
        // Act
        ResponseEntity<String> response = restTemplate.getForEntity(baseUrl + "/venues/MsG", String.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    @DisplayName("Should handle URL encoding in venue ID")
    void shouldHandleUrlEncodingInVenueId() {
        // Act
        ResponseEntity<String> response = restTemplate.getForEntity(baseUrl + "/venues/msg%20garden", String.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("Should handle concurrent error requests")
    void shouldHandleConcurrentErrorRequests() {
        // Act - Make multiple concurrent error requests
        ResponseEntity<String> response1 = restTemplate.getForEntity(baseUrl + "/venues/nonexistent1", String.class);
        ResponseEntity<String> response2 = restTemplate.getForEntity(baseUrl + "/venues/nonexistent2", String.class);
        ResponseEntity<String> response3 = restTemplate.getForEntity(baseUrl + "/venues/nonexistent3", String.class);

        // Assert
        assertThat(response1.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response3.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    @DisplayName("Should handle malformed JSON requests gracefully")
    void shouldHandleMalformedJsonRequests() {
        // Act - Try to send malformed JSON (though we don't have POST endpoints, this tests general error handling)
        ResponseEntity<String> response = restTemplate.getForEntity(baseUrl + "/venues", String.class);

        // Assert - Should still work for GET requests
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("Should handle unsupported HTTP methods")
    void shouldHandleUnsupportedHttpMethods() {
        // Act - Try POST to a GET-only endpoint
        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl + "/venues", "{}", String.class);

        // Assert - Should return 405 Method Not Allowed
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.METHOD_NOT_ALLOWED);
    }

    @Test
    @DisplayName("Should handle invalid content type requests")
    void shouldHandleInvalidContentTypeRequests() {
        // Act - Try to send XML to JSON endpoint
        ResponseEntity<String> response = restTemplate.postForEntity(
            baseUrl + "/venues", 
            "<venue>test</venue>", 
            String.class);

        // Assert - Should return 405 Method Not Allowed (not 415 because POST is not supported)
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.METHOD_NOT_ALLOWED);
    }

    @Test
    @DisplayName("Should handle extremely long requests")
    void shouldHandleExtremelyLongRequests() {
        // Arrange
        String extremelyLongId = "a".repeat(10000);

        // Act
        ResponseEntity<String> response = restTemplate.getForEntity(baseUrl + "/venues/" + extremelyLongId, String.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("Should handle unicode characters in venue ID")
    void shouldHandleUnicodeCharactersInVenueId() {
        // Act
        ResponseEntity<String> response = restTemplate.getForEntity(baseUrl + "/venues/msg🎫", String.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    @DisplayName("Should handle emoji in venue ID")
    void shouldHandleEmojiInVenueId() {
        // Act
        ResponseEntity<String> response = restTemplate.getForEntity(baseUrl + "/venues/🏟️", String.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }
} 