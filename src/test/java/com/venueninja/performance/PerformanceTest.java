package com.venueninja.performance;

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

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DisplayName("Performance Tests")
class PerformanceTest {

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

        // Clear and populate database
        venueRepository.deleteAll();
        venueRepository.save(madisonSquareGarden);
        venueRepository.save(yankeeStadium);
    }

    @Test
    @DisplayName("Single request should complete within 1 second")
    void singleRequest_ShouldCompleteWithin1Second() {
        // Act
        Instant start = Instant.now();
        ResponseEntity<Venue[]> response = restTemplate.getForEntity(baseUrl + "/venues", Venue[].class);
        Instant end = Instant.now();

        // Assert
        Duration duration = Duration.between(start, end);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(duration.toMillis()).isLessThan(1000);
        System.out.println("Single request completed in: " + duration.toMillis() + "ms");
    }

    @Test
    @DisplayName("Single venue request should complete within 500ms")
    void singleVenueRequest_ShouldCompleteWithin500ms() {
        // Act
        Instant start = Instant.now();
        ResponseEntity<Venue> response = restTemplate.getForEntity(baseUrl + "/venues/msg", Venue.class);
        Instant end = Instant.now();

        // Assert
        Duration duration = Duration.between(start, end);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(duration.toMillis()).isLessThan(500);
        System.out.println("Single venue request completed in: " + duration.toMillis() + "ms");
    }

    @Test
    @DisplayName("Multiple sequential requests should complete within reasonable time")
    void multipleSequentialRequests_ShouldCompleteWithinReasonableTime() {
        // Act
        Instant start = Instant.now();
        
        for (int i = 0; i < 10; i++) {
            ResponseEntity<Venue[]> response = restTemplate.getForEntity(baseUrl + "/venues", Venue[].class);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        }
        
        Instant end = Instant.now();

        // Assert
        Duration duration = Duration.between(start, end);
        assertThat(duration.toMillis()).isLessThan(5000); // 5 seconds for 10 requests
        System.out.println("10 sequential requests completed in: " + duration.toMillis() + "ms");
    }

    @Test
    @DisplayName("Concurrent requests should handle load gracefully")
    void concurrentRequests_ShouldHandleLoadGracefully() throws InterruptedException, ExecutionException {
        // Arrange
        int numberOfRequests = 20;
        ExecutorService executor = Executors.newFixedThreadPool(10);
        List<CompletableFuture<ResponseEntity<Venue[]>>> futures = new ArrayList<>();

        // Act
        Instant start = Instant.now();
        
        for (int i = 0; i < numberOfRequests; i++) {
            CompletableFuture<ResponseEntity<Venue[]>> future = CompletableFuture.supplyAsync(() -> 
                restTemplate.getForEntity(baseUrl + "/venues", Venue[].class), executor);
            futures.add(future);
        }

        // Wait for all requests to complete
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        Instant end = Instant.now();

        // Assert
        Duration duration = Duration.between(start, end);
        assertThat(duration.toMillis()).isLessThan(10000); // 10 seconds for 20 concurrent requests
        
        // Verify all requests succeeded
        for (CompletableFuture<ResponseEntity<Venue[]>> future : futures) {
            ResponseEntity<Venue[]> response = future.get();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).hasSize(2);
        }

        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);
        
        System.out.println(numberOfRequests + " concurrent requests completed in: " + duration.toMillis() + "ms");
    }

    @Test
    @DisplayName("Mixed request types should perform consistently")
    void mixedRequestTypes_ShouldPerformConsistently() {
        // Act
        Instant start = Instant.now();
        
        // Mix of different request types
        ResponseEntity<Venue[]> allVenuesResponse = restTemplate.getForEntity(baseUrl + "/venues", Venue[].class);
        ResponseEntity<Venue> msgResponse = restTemplate.getForEntity(baseUrl + "/venues/msg", Venue.class);
        ResponseEntity<Venue> yankeeResponse = restTemplate.getForEntity(baseUrl + "/venues/yankee", Venue.class);
        ResponseEntity<String> notFoundResponse = restTemplate.getForEntity(baseUrl + "/venues/nonexistent", String.class);
        
        Instant end = Instant.now();

        // Assert
        Duration duration = Duration.between(start, end);
        assertThat(duration.toMillis()).isLessThan(2000); // 2 seconds for mixed requests
        
        assertThat(allVenuesResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(msgResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(yankeeResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(notFoundResponse.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        
        System.out.println("Mixed request types completed in: " + duration.toMillis() + "ms");
    }

    @Test
    @DisplayName("Response size should be reasonable")
    void responseSize_ShouldBeReasonable() {
        // Act
        ResponseEntity<Venue[]> response = restTemplate.getForEntity(baseUrl + "/venues", Venue[].class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
        
        // Check response headers for content length
        String contentLength = response.getHeaders().getFirst("Content-Length");
        if (contentLength != null) {
            int size = Integer.parseInt(contentLength);
            assertThat(size).isLessThan(10000); // Less than 10KB for venue list
            System.out.println("Response size: " + size + " bytes");
        }
    }

    @Test
    @DisplayName("Database query performance should be consistent")
    void databaseQueryPerformance_ShouldBeConsistent() {
        // Act - Measure multiple database queries
        List<Long> queryTimes = new ArrayList<>();
        
        for (int i = 0; i < 5; i++) {
            Instant start = Instant.now();
            venueRepository.findAll();
            Instant end = Instant.now();
            queryTimes.add(Duration.between(start, end).toMillis());
        }

        // Assert
        assertThat(queryTimes).allMatch(time -> time < 100); // All queries under 100ms
        
        long averageTime = queryTimes.stream().mapToLong(Long::longValue).sum() / queryTimes.size();
        System.out.println("Average database query time: " + averageTime + "ms");
        
        // Verify consistency (no outliers)
        long maxTime = queryTimes.stream().mapToLong(Long::longValue).max().orElse(0);
        long minTime = queryTimes.stream().mapToLong(Long::longValue).min().orElse(0);
        assertThat(maxTime - minTime).isLessThan(50); // Variation less than 50ms
    }

    @Test
    @DisplayName("Memory usage should remain stable under load")
    void memoryUsage_ShouldRemainStableUnderLoad() {
        // Arrange
        Runtime runtime = Runtime.getRuntime();
        long initialMemory = runtime.totalMemory() - runtime.freeMemory();

        // Act - Make many requests to test memory stability
        for (int i = 0; i < 50; i++) {
            ResponseEntity<Venue[]> response = restTemplate.getForEntity(baseUrl + "/venues", Venue[].class);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        }

        // Force garbage collection and measure memory
        System.gc();
        long finalMemory = runtime.totalMemory() - runtime.freeMemory();
        long memoryIncrease = finalMemory - initialMemory;

        // Assert - Memory increase should be reasonable (less than 10MB)
        assertThat(memoryIncrease).isLessThan(10 * 1024 * 1024); // 10MB
        System.out.println("Memory increase after 50 requests: " + (memoryIncrease / 1024) + "KB");
    }

    @Test
    @DisplayName("Connection pool should handle concurrent requests efficiently")
    void connectionPool_ShouldHandleConcurrentRequestsEfficiently() throws InterruptedException, ExecutionException {
        // Arrange
        int numberOfConcurrentRequests = 15;
        ExecutorService executor = Executors.newFixedThreadPool(numberOfConcurrentRequests);
        List<CompletableFuture<ResponseEntity<Venue[]>>> futures = new ArrayList<>();

        // Act
        Instant start = Instant.now();
        
        for (int i = 0; i < numberOfConcurrentRequests; i++) {
            CompletableFuture<ResponseEntity<Venue[]>> future = CompletableFuture.supplyAsync(() -> 
                restTemplate.getForEntity(baseUrl + "/venues", Venue[].class), executor);
            futures.add(future);
        }

        // Wait for all requests to complete
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        Instant end = Instant.now();

        // Assert
        Duration duration = Duration.between(start, end);
        assertThat(duration.toMillis()).isLessThan(5000); // 5 seconds for 15 concurrent requests
        
        // All requests should succeed
        for (CompletableFuture<ResponseEntity<Venue[]>> future : futures) {
            ResponseEntity<Venue[]> response = future.get();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        }

        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);
        
        System.out.println("Connection pool handled " + numberOfConcurrentRequests + 
                          " concurrent requests in: " + duration.toMillis() + "ms");
    }

    @Test
    @DisplayName("Error responses should be fast")
    void errorResponses_ShouldBeFast() {
        // Act
        Instant start = Instant.now();
        ResponseEntity<String> response = restTemplate.getForEntity(baseUrl + "/venues/nonexistent", String.class);
        Instant end = Instant.now();

        // Assert
        Duration duration = Duration.between(start, end);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(duration.toMillis()).isLessThan(200); // Error responses should be very fast
        System.out.println("Error response completed in: " + duration.toMillis() + "ms");
    }
} 