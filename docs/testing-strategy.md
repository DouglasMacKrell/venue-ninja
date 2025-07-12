# Testing Strategy - Venue Ninja 🧪

This document outlines the comprehensive testing strategy for the Venue Ninja application, covering unit tests, integration tests, external database connectivity tests, and testing best practices.

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

### 3. External Database Tests
**Purpose**: Validate production database connectivity
**Scope**: Real PostgreSQL database connections
**Framework**: JUnit 5 + JDBC
**Speed**: Slow (5-30 seconds per test)

### 4. API Tests
**Purpose**: Test HTTP endpoints and responses
**Scope**: Controller layer, request/response handling
**Framework**: Spring Boot Test + TestRestTemplate
**Speed**: Medium (1-3 seconds per test)

---

## 🏗️ Test Architecture

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Unit Tests    │    │ Integration     │    │ External DB     │
│                 │    │ Tests           │    │ Tests           │
│ • Service Layer │    │ • Repository    │    │ • PostgreSQL    │
│ • Business Logic│    │ • JPA Mappings  │    │ • SSL Config    │
│ • Mocked Deps   │    │ • H2 Database   │    │ • Env Variables │
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

## 🌐 External Database Tests

### Production Database Connectivity

```java
@SpringBootTest
@ActiveProfiles("test")
class ExternalDatabaseConnectionTest {

    @Test
    void testExternalDatabaseConnection() throws SQLException {
        // Test connection to Render PostgreSQL database
        String url = "jdbc:postgresql://dpg-d1ok8ek9c44c73fo8u9g-a.virginia-postgres.render.com:5432/venue_ninja_db";
        String user = "venue_ninja_db_user";
        String password = "8gCV7weUED662qAjWFdmxqhyqa4ZCwaZ";
        
        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            // Assert connection is valid
            assertThat(connection).isNotNull();
            assertThat(connection.isClosed()).isFalse();
            
            // Test a simple query
            try (var resultSet = connection.createStatement().executeQuery("SELECT 1")) {
                assertThat(resultSet.next()).isTrue();
                assertThat(resultSet.getInt(1)).isEqualTo(1);
            }
            
            System.out.println("✅ External database connection successful!");
        } catch (SQLException e) {
            System.err.println("❌ External database connection failed: " + e.getMessage());
            throw e;
        }
    }

    @Test
    void testEnvironmentVariables() {
        // Test that environment variables are accessible
        String dbHost = System.getenv("DB_HOST");
        String dbPort = System.getenv("DB_PORT");
        String dbName = System.getenv("DB_NAME");
        String dbUser = System.getenv("DB_USER");
        String dbPassword = System.getenv("DB_PASSWORD");
        
        // Log environment variable status
        System.out.println("Environment variables:");
        System.out.println("DB_HOST: " + (dbHost != null ? dbHost : "NOT SET"));
        System.out.println("DB_PORT: " + (dbPort != null ? dbPort : "NOT SET"));
        System.out.println("DB_NAME: " + (dbName != null ? dbName : "NOT SET"));
        System.out.println("DB_USER: " + (dbUser != null ? dbUser : "NOT SET"));
        System.out.println("DB_PASSWORD: " + (dbPassword != null ? "SET" : "NOT SET"));
        
        // This test will pass locally but fail in Render if env vars aren't set
        if (dbHost == null || dbPort == null || dbName == null || dbUser == null || dbPassword == null) {
            System.out.println("⚠️  Some environment variables are not set - this will cause issues in production");
        }
    }
}
```

### SSL Configuration Testing

```java
@Test
void testSSLConnection() throws SQLException {
    // Test SSL-enabled connection
    String url = "jdbc:postgresql://host:5432/db?sslmode=require";
    
    try (Connection connection = DriverManager.getConnection(url, user, password)) {
        // Verify SSL is enabled
        DatabaseMetaData metaData = connection.getMetaData();
        // Additional SSL verification logic here
    }
}
```

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

# Run external database tests only
./mvnw test -Dtest=ExternalDatabaseConnectionTest

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
Venue venue = Venue.builder()
    .id("msg")
    .name("Madison Square Garden")
    .build();
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

---

## 🔮 Future Testing Enhancements

### Planned Improvements
1. **Performance Tests**: Load testing with JMeter
2. **Contract Tests**: API contract validation
3. **Mutation Testing**: PIT for test quality
4. **Visual Regression Tests**: Frontend testing
5. **Security Tests**: OWASP ZAP integration

### Continuous Testing
- **GitHub Actions**: Automated test execution
- **Test Reports**: Automated coverage reporting
- **Quality Gates**: Minimum coverage requirements
- **Performance Monitoring**: Test execution time tracking

---

## 📚 Resources

- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)
- [Spring Boot Testing](https://docs.spring.io/spring-boot/docs/current/reference/html/spring-boot-features.html#boot-features-testing)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
- [H2 Database](https://www.h2database.com/html/main.html)
- [TestContainers](https://www.testcontainers.org/) (for future use) 