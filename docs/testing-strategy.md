# Testing Strategy - Venue Ninja 🧪

This document outlines the comprehensive testing strategy for the Venue Ninja application, covering unit tests, integration tests, error handling tests, performance tests, regression tests, and testing best practices.

---

## 🎯 Testing Philosophy

### Testing Pyramid
```
        /\
       /  \     E2E Tests (Manual)
      /____\    
     /      \   Integration Tests
    /________\  
   /          \ Unit Tests
  /____________\
```

**Principles**:
- **Fast Feedback**: Unit tests run in milliseconds
- **Reliable**: Tests are deterministic and repeatable
- **Comprehensive**: Cover critical paths and edge cases
- **Maintainable**: Tests are readable and well-structured
- **Robust**: Handle null safety and static analysis compliance

---

## 🧪 Test Categories

### 1. Unit Tests
**Purpose**: Test individual components in isolation
**Scope**: Service layer business logic, utility methods
**Framework**: JUnit 5 + Mockito
**Speed**: Fast (< 100ms per test)

### 2. Integration Tests
**Purpose**: Test component interactions
**Scope**: Repository operations, database mappings
**Framework**: Spring Boot Test + H2 Database
**Speed**: Medium (1-5 seconds per test)

### 3. Error Handling Tests
**Purpose**: Validate error scenarios and edge cases
**Scope**: Malformed inputs, SQL injection, XSS, path traversal
**Framework**: JUnit 5 + Spring Boot Test
**Speed**: Medium (1-3 seconds per test)

### 4. Performance Tests
**Purpose**: Validate response times and throughput
**Scope**: Load testing, memory usage, concurrent requests
**Framework**: JUnit 5 + Spring Boot Test
**Speed**: Slow (5-15 seconds per test)

### 5. Regression Tests
**Purpose**: Guard against breaking changes
**Scope**: Core functionality validation, data structure consistency
**Framework**: JUnit 5 + Spring Boot Test
**Speed**: Medium (2-5 seconds per test)

### 6. External Database Tests
**Purpose**: Validate production database connectivity
**Scope**: Real PostgreSQL database connections
**Framework**: JUnit 5 + JDBC
**Speed**: Slow (5-30 seconds per test)

### 7. API Tests
**Purpose**: Test HTTP endpoints and responses
**Scope**: Controller layer, request/response handling
**Framework**: Spring Boot Test + TestRestTemplate
**Speed**: Medium (1-3 seconds per test)

---

## 🏗️ Test Architecture

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Unit Tests    │    │ Integration     │    │ Error Handling  │
│                 │    │ Tests           │    │ Tests           │
│ • Service Layer │    │ • Repository    │    │ • Edge Cases    │
│ • Business Logic│    │ • JPA Mappings  │    │ • Security      │
│ • Mocked Deps   │    │ • H2 Database   │    │ • Null Safety   │
└─────────────────┘    └─────────────────┘    └─────────────────┘

┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│ Performance     │    │ Regression      │    │ External DB     │
│ Tests           │    │ Tests           │    │ Tests           │
│ • Load Testing  │    │ • Core Features │    │ • PostgreSQL    │
│ • Memory Usage  │    │ • Data Structure│    │ • SSL Config    │
│ • Concurrency   │    │ • Breaking Changes│  │ • Env Variables │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

---

## 📝 Unit Tests

### Service Layer Testing

```java
@ExtendWith(MockitoExtension.class)
class VenueServiceTest {

    @Mock
    private VenueRepository venueRepository;

    @InjectMocks
    private VenueService venueService;

    @Test
    void getAllVenues_ShouldReturnAllVenues() {
        // Arrange
        List<Venue> expectedVenues = Arrays.asList(
            new Venue("msg", "Madison Square Garden"),
            new Venue("yankee", "Yankee Stadium")
        );
        when(venueRepository.findAll()).thenReturn(expectedVenues);

        // Act
        List<Venue> actualVenues = venueService.getAllVenues();

        // Assert
        assertThat(actualVenues).hasSize(2);
        assertThat(actualVenues.get(0).getId()).isEqualTo("msg");
        verify(venueRepository).findAll();
    }

    @Test
    void getVenue_WhenVenueExists_ShouldReturnVenue() {
        // Arrange
        String venueId = "msg";
        Venue expectedVenue = new Venue(venueId, "Madison Square Garden");
        when(venueRepository.findById(venueId)).thenReturn(Optional.of(expectedVenue));

        // Act
        Venue actualVenue = venueService.getVenue(venueId);

        // Assert
        assertThat(actualVenue).isEqualTo(expectedVenue);
        verify(venueRepository).findById(venueId);
    }

    @Test
    void getVenue_WhenVenueNotFound_ShouldThrowException() {
        // Arrange
        String venueId = "nonexistent";
        when(venueRepository.findById(venueId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> venueService.getVenue(venueId))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Venue not found");
    }
}
```

### Testing Best Practices

#### 1. Arrange-Act-Assert Pattern
```java
@Test
void testMethod() {
    // Arrange - Set up test data and mocks
    String input = "test";
    when(mock.method(input)).thenReturn("expected");

    // Act - Execute the method under test
    String result = service.method(input);

    // Assert - Verify the results
    assertThat(result).isEqualTo("expected");
}
```

#### 2. Descriptive Test Names
```java
// ✅ Good - Clear what is being tested
@Test
void getAllVenues_WhenRepositoryReturnsEmptyList_ShouldReturnEmptyList()

// ❌ Bad - Unclear purpose
@Test
void testGetAllVenues()
```

#### 3. Mock Verification
```java
@Test
void testMethod() {
    // Arrange & Act
    service.method("input");

    // Assert - Verify interactions
    verify(mock).method("input");
    verifyNoMoreInteractions(mock);
}
```

#### 4. Null Safety
```java
@Test
void testWithNullSafety() {
    // Arrange
    assertThat(response.getBody()).isNotNull();
    var body = response.getBody();
    if (body != null) {
        assertThat(body.getId()).isEqualTo("expected");
    }
}
```

---

## 🚨 Error Handling Tests

### Comprehensive Error Testing

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class ErrorHandlingTest {

    @Test
    @DisplayName("Should handle malformed venue ID gracefully")
    void shouldHandleMalformedVenueId() {
        // Act
        ResponseEntity<Venue> response = restTemplate.getForEntity(
            baseUrl + "/venues/msg%20garden", Venue.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("Should handle SQL injection attempts")
    void shouldHandleSqlInjectionAttempts() {
        // Act
        ResponseEntity<Venue> response = restTemplate.getForEntity(
            baseUrl + "/venues/'; DROP TABLE venues; --", Venue.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("Should handle XSS attempts")
    void shouldHandleXssAttempts() {
        // Act
        ResponseEntity<Venue> response = restTemplate.getForEntity(
            baseUrl + "/venues/<script>alert('xss')</script>", Venue.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("Should handle path traversal attempts")
    void shouldHandlePathTraversalAttempts() {
        // Act
        ResponseEntity<Venue> response = restTemplate.getForEntity(
            baseUrl + "/venues/../../../etc/passwd", Venue.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("Should handle concurrent requests")
    void shouldHandleConcurrentRequests() {
        // Arrange
        venueRepository.save(madisonSquareGarden);

        // Act - Make multiple concurrent requests
        CompletableFuture<ResponseEntity<Venue[]>> future1 = 
            CompletableFuture.supplyAsync(() -> 
                restTemplate.getForEntity(baseUrl + "/venues", Venue[].class));
        
        CompletableFuture<ResponseEntity<Venue>> future2 = 
            CompletableFuture.supplyAsync(() -> 
                restTemplate.getForEntity(baseUrl + "/venues/msg", Venue.class));

        // Assert
        ResponseEntity<Venue[]> response1 = future1.get(5, TimeUnit.SECONDS);
        ResponseEntity<Venue> response2 = future2.get(5, TimeUnit.SECONDS);
        
        assertThat(response1.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
```

---

## ⚡ Performance Tests

### Load and Performance Validation

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class PerformanceTest {

    @Test
    @DisplayName("Should handle high load requests")
    void shouldHandleHighLoadRequests() {
        // Arrange
        venueRepository.save(madisonSquareGarden);
        int requestCount = 100;
        List<CompletableFuture<ResponseEntity<Venue[]>>> futures = new ArrayList<>();

        // Act
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < requestCount; i++) {
            futures.add(CompletableFuture.supplyAsync(() -> 
                restTemplate.getForEntity(baseUrl + "/venues", Venue[].class)));
        }

        // Wait for all requests to complete
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;

        // Assert
        assertThat(totalTime).isLessThan(5000); // Should complete within 5 seconds
        
        // Verify all responses are successful
        for (CompletableFuture<ResponseEntity<Venue[]>> future : futures) {
            ResponseEntity<Venue[]> response = future.getNow(null);
            assertThat(response).isNotNull();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        }
    }

    @Test
    @DisplayName("Should maintain response time under load")
    void shouldMaintainResponseTimeUnderLoad() {
        // Arrange
        venueRepository.save(madisonSquareGarden);
        int iterations = 50;
        List<Long> responseTimes = new ArrayList<>();

        // Act
        for (int i = 0; i < iterations; i++) {
            long startTime = System.nanoTime();
            
            ResponseEntity<Venue[]> response = restTemplate.getForEntity(
                baseUrl + "/venues", Venue[].class);
            
            long endTime = System.nanoTime();
            long responseTime = (endTime - startTime) / 1_000_000; // Convert to milliseconds
            
            responseTimes.add(responseTime);
            
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        }

        // Assert
        double averageResponseTime = responseTimes.stream()
            .mapToLong(Long::longValue)
            .average()
            .orElse(0.0);
            
        assertThat(averageResponseTime).isLessThan(100.0); // Average < 100ms
    }

    @Test
    @DisplayName("Should handle memory efficiently")
    void shouldHandleMemoryEfficiently() {
        // Arrange
        Runtime runtime = Runtime.getRuntime();
        long initialMemory = runtime.totalMemory() - runtime.freeMemory();

        // Act - Perform memory-intensive operations
        for (int i = 0; i < 1000; i++) {
            Venue venue = TestDataBuilder.createVenueWithLongName();
            venueRepository.save(venue);
        }

        // Force garbage collection
        System.gc();
        
        long finalMemory = runtime.totalMemory() - runtime.freeMemory();
        long memoryIncrease = finalMemory - initialMemory;

        // Assert
        assertThat(memoryIncrease).isLessThan(50 * 1024 * 1024); // < 50MB increase
    }
}
```

---

## 🔄 Regression Tests

### Core Functionality Validation

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class RegressionTestSuite {

    @Test
    @DisplayName("Core API endpoints should remain functional")
    void coreApiEndpoints_ShouldRemainFunctional() {
        // Arrange
        Venue msg = TestDataBuilder.createMadisonSquareGarden();
        venueRepository.save(msg);

        // Act & Assert - Test all core endpoints
        ResponseEntity<Venue[]> allVenuesResponse = restTemplate.getForEntity(
            baseUrl + "/venues", Venue[].class);
        assertThat(allVenuesResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(allVenuesResponse.getBody()).hasSize(1);

        ResponseEntity<Venue> singleVenueResponse = restTemplate.getForEntity(
            baseUrl + "/venues/msg", Venue.class);
        assertThat(singleVenueResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(singleVenueResponse.getBody()).isNotNull();
    }

    @Test
    @DisplayName("Venue data structure should remain consistent")
    void venueDataStructure_ShouldRemainConsistent() {
        // Arrange
        Venue msg = TestDataBuilder.createMadisonSquareGarden();
        venueRepository.save(msg);

        // Act
        ResponseEntity<Venue> response = restTemplate.getForEntity(
            baseUrl + "/venues/msg", Venue.class);

        // Assert - Verify all expected fields are present and correctly typed
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Venue venue = response.getBody();
        
        if (venue != null) {
            assertThat(venue.getId()).isEqualTo("msg");
            assertThat(venue.getName()).isEqualTo("Madison Square Garden");
            assertThat(venue.getRecommendations()).isNotNull();
            assertThat(venue.getRecommendations()).hasSize(3);
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
    }
}
```

---

## 🏗️ Test Data Management

### Test Data Builder Pattern

```java
public class TestDataBuilder {

    public static Venue createMadisonSquareGarden() {
        SeatRecommendation rec1 = new SeatRecommendation();
        rec1.setSection("104");
        rec1.setCategory("Lower Bowl");
        rec1.setReason("Best resale value & view of stage");
        rec1.setEstimatedPrice("$250");
        rec1.setTip("Avoid row 20+ due to rigging obstruction");

        SeatRecommendation rec2 = new SeatRecommendation();
        rec2.setSection("200");
        rec2.setCategory("Upper Bowl");
        rec2.setReason("Great value for price-conscious fans");
        rec2.setEstimatedPrice("$75");
        rec2.setTip("Bring binoculars for optimal viewing");

        Venue venue = new Venue();
        venue.setId("msg");
        venue.setName("Madison Square Garden");
        venue.setRecommendations(Arrays.asList(rec1, rec2));
        
        return venue;
    }

    public static Venue createVenueWithNoRecommendations() {
        Venue venue = new Venue();
        venue.setId("empty");
        venue.setName("Empty Venue");
        venue.setRecommendations(Arrays.asList());
        return venue;
    }

    public static Venue createVenueWithNullRecommendations() {
        Venue venue = new Venue();
        venue.setId("null");
        venue.setName("Null Recommendations Venue");
        venue.setRecommendations(null);
        return venue;
    }

    public static List<Venue> createAllTestVenues() {
        return Arrays.asList(
            createMadisonSquareGarden(),
            createYankeeStadium(),
            createBarclaysCenter()
        );
    }
}
```

---

## 🔗 Integration Tests

### Repository Testing

```java
@DataJpaTest
@ActiveProfiles("test")
class VenueRepositoryTest {

    @Autowired
    private VenueRepository venueRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void save_ShouldPersistVenue() {
        // Arrange
        Venue venue = new Venue("test", "Test Venue");

        // Act
        Venue savedVenue = venueRepository.save(venue);

        // Assert
        assertThat(savedVenue.getId()).isEqualTo("test");
        assertThat(savedVenue.getName()).isEqualTo("Test Venue");
        
        // Verify in database
        Venue found = entityManager.find(Venue.class, "test");
        assertThat(found).isNotNull();
    }

    @Test
    void findById_WhenVenueExists_ShouldReturnVenue() {
        // Arrange
        Venue venue = new Venue("test", "Test Venue");
        entityManager.persistAndFlush(venue);

        // Act
        Optional<Venue> found = venueRepository.findById("test");

        // Assert
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Test Venue");
    }

    @Test
    void findById_WhenVenueNotExists_ShouldReturnEmpty() {
        // Act
        Optional<Venue> found = venueRepository.findById("nonexistent");

        // Assert
        assertThat(found).isEmpty();
    }
}
```

### Test Configuration

```properties
# application-test.properties
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driver-class-name=org.h2.Driver
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.format_sql=false
```

---

## 🌐 External PostgreSQL connectivity

The repository **does not include** an `ExternalDatabaseConnectionTest` class. Automated tests run against **H2** under the `test` profile. To validate PostgreSQL manually, run the application with `spring.profiles.active=production` and appropriate `DB_*` or `DATABASE_URL` variables, then check `/actuator/health` and `GET /venues`.

---

## 🌐 API Tests

### Controller Testing

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class VenueControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private VenueRepository venueRepository;

    @Test
    void getAllVenues_ShouldReturnVenues() {
        // Arrange
        venueRepository.save(new Venue("test", "Test Venue"));

        // Act
        ResponseEntity<Venue[]> response = restTemplate.getForEntity("/venues", Venue[].class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSizeGreaterThan(0);
    }

    @Test
    void getVenueById_WhenVenueExists_ShouldReturnVenue() {
        // Arrange
        String venueId = "test";
        venueRepository.save(new Venue(venueId, "Test Venue"));

        // Act
        ResponseEntity<Venue> response = restTemplate.getForEntity("/venues/" + venueId, Venue.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getId()).isEqualTo(venueId);
    }

    @Test
    void getVenueById_WhenVenueNotExists_ShouldReturn404() {
        // Act
        ResponseEntity<Venue> response = restTemplate.getForEntity("/venues/nonexistent", Venue.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
```

---

## 🚀 Test Execution

### Running Tests

```bash
# Run all tests
./mvnw test

# Run specific test class
./mvnw test -Dtest=VenueServiceTest

# Run tests with specific profile
./mvnw test -Dspring.profiles.active=test

# Run error handling tests only
./mvnw test -Dtest=ErrorHandlingTest

# Run performance tests only
./mvnw test -Dtest=PerformanceTest

# Run regression tests only
./mvnw test -Dtest=RegressionTestSuite

# Run with coverage
./mvnw test jacoco:report
```

### Test Profiles

```java
@ActiveProfiles("test")  // Use test configuration
@ActiveProfiles("production")  // Use production configuration
@ActiveProfiles({"test", "integration"})  // Multiple profiles
```

---

## 📊 Test Coverage

### Coverage Goals
- **Unit Tests**: 90%+ line coverage
- **Integration Tests**: 80%+ critical path coverage
- **Error Handling Tests**: 100% edge case coverage
- **Performance Tests**: Response time validation
- **Regression Tests**: Core functionality validation
- **API Tests**: 100% endpoint coverage
- **External Tests**: Production connectivity validation

### Coverage Reporting

```xml
<!-- pom.xml -->
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.11</version>
    <executions>
        <execution>
            <goals>
                <goal>prepare-agent</goal>
            </goals>
        </execution>
        <execution>
            <id>report</id>
            <phase>test</phase>
            <goals>
                <goal>report</goal>
            </goals>
        </execution>
        <execution>
            <id>check</id>
            <goals>
                <goal>check</goal>
            </goals>
            <configuration>
                <rules>
                    <rule>
                        <element>BUNDLE</element>
                        <limits>
                            <limit>
                                <counter>LINE</counter>
                                <value>COVEREDRATIO</value>
                                <minimum>0.80</minimum>
                            </limit>
                            <limit>
                                <counter>BRANCH</counter>
                                <value>COVEREDRATIO</value>
                                <minimum>0.70</minimum>
                            </limit>
                        </limits>
                    </rule>
                </rules>
            </configuration>
        </execution>
    </executions>
</plugin>
```

---

## 🔧 Test Configuration

### Test Dependencies

```xml
<!-- pom.xml -->
<dependencies>
    <!-- Testing dependencies -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
    
    <!-- H2 Database for testing -->
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

### Test Properties

```properties
# application-test.properties
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driver-class-name=org.h2.Driver
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.format_sql=false

# Disable security for testing
spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
```

---

## 🎯 Testing Best Practices

### 1. Test Isolation
```java
@BeforeEach
void setUp() {
    // Clean up before each test
    venueRepository.deleteAll();
}

@AfterEach
void tearDown() {
    // Clean up after each test
    venueRepository.deleteAll();
}
```

### 2. Meaningful Assertions
```java
// ✅ Good - Specific assertions
assertThat(venue.getName()).isEqualTo("Madison Square Garden");
assertThat(venue.getRecommendations()).hasSize(3);

// ❌ Bad - Generic assertions
assertThat(venue).isNotNull();
```

### 3. Test Data Management
```java
// Use test data builders
Venue venue = TestDataBuilder.createMadisonSquareGarden();
```

### 4. Error Testing
```java
@Test
void testErrorConditions() {
    // Test null inputs
    assertThatThrownBy(() -> service.method(null))
        .isInstanceOf(IllegalArgumentException.class);
    
    // Test empty inputs
    assertThatThrownBy(() -> service.method(""))
        .isInstanceOf(IllegalArgumentException.class);
}
```

### 5. Null Safety
```java
@Test
void testWithNullSafety() {
    // Assert non-null before accessing properties
    assertThat(response.getBody()).isNotNull();
    var body = response.getBody();
    if (body != null) {
        assertThat(body.getId()).isEqualTo("expected");
    }
}
```

### 6. Static Analysis Compliance
```java
@Test
void testWithStaticAnalysisCompliance() {
    // Store result in variable after null check
    assertThat(response.getHeaders().getContentType()).isNotNull();
    var contentType = response.getHeaders().getContentType();
    if (contentType != null) {
        assertThat(contentType.toString()).contains("application/json");
    }
}
```

---

## 🔮 Future Testing Enhancements

### Planned Improvements
1. **Contract Tests**: API contract validation with Pact
2. **Mutation Testing**: PIT for test quality validation
3. **Visual Regression Tests**: Frontend testing with Selenium
4. **Security Tests**: OWASP ZAP integration
5. **Load Testing**: JMeter integration for performance testing
6. **TestContainers**: Real database testing in containers

### Continuous Testing
- **GitHub Actions**: Automated test execution
- **Test Reports**: Automated coverage reporting
- **Quality Gates**: Minimum coverage requirements
- **Performance Monitoring**: Test execution time tracking
- **Static Analysis**: SonarQube integration

---

## 📚 Resources

- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)
- [Spring Boot Testing](https://docs.spring.io/spring-boot/docs/current/reference/html/spring-boot-features.html#boot-features-testing)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
- [H2 Database](https://www.h2database.com/html/main.html)
- [TestContainers](https://www.testcontainers.org/) (for future use)
- [JaCoCo Coverage](https://www.jacoco.org/jacoco/trunk/doc/)
- [AssertJ Assertions](https://assertj.github.io/doc/) 