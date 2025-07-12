![Venue Ninja Header](public/images/venue-ninja-headder.png)

# Venue Ninja 🎟️🗡️

<!-- RENDER_BADGE -->
[![Render Status](https://img.shields.io/badge/Render-passing-brightgreen?logo=render)](https://github.com/nia-cloud-official/render-status)
[![Java](https://img.shields.io/badge/Java-17-orange?logo=java)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.3-brightgreen?logo=spring)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-12+-blue?logo=postgresql)](https://www.postgresql.org/)
[![Docker](https://img.shields.io/badge/Docker-Enabled-blue?logo=docker)](https://www.docker.com/)

A **production-ready** Java + Spring Boot REST API that delivers smart seat recommendations for iconic venues. Built as a showcase project demonstrating enterprise-grade engineering, database integration, comprehensive testing, and modern deployment practices.

**[Live API](https://venue-ninja.onrender.com/venues)** | **[Live API Docs (Swagger)](https://venue-ninja.onrender.com/swagger-ui/index.html)** | **[Database Connection Test](./src/test/java/com/venueninja/ExternalDatabaseConnectionTest.java)**

---

## 🚀 QUICKLINKS

| Link | Description |
| :--- | :--- |
| 🚀 [Deployment Guide](./docs/deployment-notes.md) | Complete deployment walkthrough with PostgreSQL |
| 🗄️ [Database Architecture](./docs/database-architecture.md) | PostgreSQL setup, migrations, and connection details |
| 🧪 [Testing Strategy](./docs/testing-strategy.md) | Comprehensive test coverage and external DB testing |
| 📝 [Project Pitch](./docs/project-pitch.md) | Architecture decisions and design philosophy |
| 🧠 [Lessons Learned](./docs/lessons-learned.md) | Real-world deployment challenges and solutions |
| ⚠️ [Known Issues](./docs/known-issues.md) | Current limitations and roadmap |
| 📘 [Swagger Documentation](./docs/swagger-quickstart.md) | API documentation and testing |
| 🛰️ [Live API](https://venue-ninja.onrender.com/venues) | Production API endpoint |

---

## 🎯 What It Does

Venue Ninja is a **production-grade REST API** that provides intelligent seat recommendations for iconic venues. It features:

* **Real PostgreSQL Database** - Persistent data storage with proper migrations
* **Comprehensive Testing** - Unit tests, integration tests, and external database connectivity tests
* **Production Deployment** - Dockerized and deployed on Render with environment-specific configurations
* **API Documentation** - Auto-generated Swagger/OpenAPI documentation
* **Security** - Spring Security with CORS configuration for frontend integration
* **Monitoring** - Structured logging and health checks

---

## 🏗️ Architecture Overview

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Frontend      │    │   Spring Boot   │    │   PostgreSQL    │
│   (React/Vite)  │◄──►│   REST API      │◄──►│   Database      │
│                 │    │                 │    │                 │
│ • Venue Select  │    │ • Controllers   │    │ • Venues        │
│ • Seat Display  │    │ • Services      │    │ • Seat Recs     │
│ • Real-time UI  │    │ • Repositories  │    │ • Migrations    │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

---

## 🧱 Tech Stack

### Backend
* **Java 17** - Modern Java with latest features
* **Spring Boot 3.5.3** - Production-ready framework
* **Spring Data JPA** - Database abstraction layer
* **PostgreSQL** - Production database with SSL
* **HikariCP** - High-performance connection pooling
* **Spring Security** - Authentication and CORS handling

### Testing
* **JUnit 5** - Unit and integration testing
* **H2 Database** - In-memory testing database
* **Spring Boot Test** - Application context testing
* **External DB Tests** - Production database connectivity validation

### DevOps
* **Docker** - Containerized deployment
* **Render** - Cloud hosting platform
* **Maven** - Build and dependency management
* **GitHub Actions** - CI/CD pipeline (ready)

### Documentation
* **Swagger/OpenAPI 3** - Auto-generated API docs
* **Spring Boot Actuator** - Health checks and monitoring
* **Comprehensive README** - Project documentation

---

## 🔗 API Endpoints

### Core Endpoints

| Method | Endpoint | Description | Example |
|--------|----------|-------------|---------|
| `GET` | `/venues` | List all venues | [Live Demo](https://venue-ninja.onrender.com/venues) |
| `GET` | `/venues/{id}` | Get venue with seat recommendations | [MSG Example](https://venue-ninja.onrender.com/venues/msg) |
| `GET` | `/swagger-ui/index.html` | Interactive API documentation | [Swagger UI](https://venue-ninja.onrender.com/swagger-ui/index.html) |

### Example Response

```json
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
    },
    {
      "section": "200",
      "category": "Upper Bowl",
      "reason": "Great value for price-conscious fans",
      "estimatedPrice": "$75",
      "tip": "Bring binoculars for optimal viewing"
    }
  ]
}
```

---

## 🗄️ Database Schema

```sql
-- Venues table
CREATE TABLE venue (
    id VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

-- Seat recommendations table
CREATE TABLE seat_recommendation (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    venue_id VARCHAR(255) NOT NULL,
    section VARCHAR(255),
    category VARCHAR(255),
    reason VARCHAR(255),
    estimated_price VARCHAR(255),
    tip VARCHAR(255),
    FOREIGN KEY (venue_id) REFERENCES venue(id)
);
```

---

## 🧪 Testing Strategy

### Test Coverage
* **Unit Tests** - Service layer business logic
* **Integration Tests** - Repository and database operations
* **External DB Tests** - Production database connectivity
* **API Tests** - Endpoint functionality and responses

### Running Tests

```bash
# Run all tests
./mvnw test

# Run specific test class
./mvnw test -Dtest=ExternalDatabaseConnectionTest

# Run with coverage
./mvnw test jacoco:report
```

### Test Database
* **Local Development** - H2 in-memory database
* **Testing** - H2 with test data
* **Production** - PostgreSQL on Render

---

## 🚀 Local Development

### Prerequisites
* Java 17+
* Maven 3.6+
* PostgreSQL (optional for local dev)

### Quick Start

```bash
# Clone repository
git clone https://github.com/DouglasMacKrell/venue-ninja.git
cd venue-ninja

# Run with Maven (uses H2 by default)
./mvnw spring-boot:run

# Or run with PostgreSQL
export DB_HOST=localhost
export DB_PORT=5432
export DB_NAME=venueninja
export DB_USER=postgres
export DB_PASSWORD=your_password
./mvnw spring-boot:run -Dspring.profiles.active=production
```

### Access Points
* **API**: http://localhost:8080/venues
* **Swagger UI**: http://localhost:8080/swagger-ui/index.html
* **Health Check**: http://localhost:8080/actuator/health

---

## 🐳 Production Deployment

### Environment Variables (Render)

```bash
# Database Configuration
DB_HOST=dpg-d1ok8ek9c44c73fo8u9g-a.virginia-postgres.render.com
DB_PORT=5432
DB_NAME=venue_ninja_db
DB_USER=venue_ninja_db_user
DB_PASSWORD=8gCV7weUED662qAjWFdmxqhyqa4ZCwaZ

# Or use single DATABASE_URL
DATABASE_URL=jdbc:postgresql://dpg-d1ok8ek9c44c73fo8u9g-a.virginia-postgres.render.com:5432/venue_ninja_db?sslmode=require
```

### Docker Deployment

```bash
# Build image
docker build -t venue-ninja .

# Run container
docker run -p 8080:8080 \
  -e DB_HOST=your_host \
  -e DB_PORT=5432 \
  -e DB_NAME=your_db \
  -e DB_USER=your_user \
  -e DB_PASSWORD=your_password \
  venue-ninja
```

---

## 📊 Performance & Monitoring

### Connection Pooling
* **HikariCP** - High-performance connection pool
* **SSL Mode** - Secure database connections
* **Connection Timeout** - 30 seconds
* **Max Pool Size** - 10 connections

### Health Checks
* **Database Connectivity** - `/actuator/health`
* **Application Status** - `/actuator/info`
* **Custom Health Indicators** - Database and external service checks

### Logging
* **Structured Logging** - JSON format in production
* **Log Levels** - Configurable per environment
* **Performance Monitoring** - SQL query logging (development only)

---

## 🔒 Security Features

### CORS Configuration
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    // Configured for frontend integration
    // Supports localhost:5175 and production domains
}
```

### Database Security
* **SSL Connections** - All production database connections use SSL
* **Environment Variables** - Sensitive data stored securely
* **Connection Pooling** - Prevents connection exhaustion

---

## 📈 Scalability Considerations

### Current Architecture
* **Stateless Design** - Horizontal scaling ready
* **Connection Pooling** - Efficient database resource usage
* **Caching Ready** - Redis integration possible
* **Load Balancer Ready** - Multiple instances supported

### Future Enhancements
* **Redis Caching** - Frequently accessed data
* **Database Sharding** - Multi-tenant support
* **API Rate Limiting** - Protect against abuse
* **CDN Integration** - Static content delivery

---

## 🎯 Interview Highlights

### Technical Excellence
* **Production Database** - Real PostgreSQL with proper migrations
* **Comprehensive Testing** - Unit, integration, and external DB tests
* **Modern Java** - Java 17 with latest Spring Boot features
* **Security Best Practices** - CORS, SSL, environment variables

### Deployment Prowess
* **Docker Containerization** - Reproducible deployments
* **Cloud Deployment** - Render with PostgreSQL
* **Environment Management** - Profile-based configuration
* **Monitoring Ready** - Health checks and logging

### Code Quality
* **Clean Architecture** - Separation of concerns
* **SOLID Principles** - Maintainable and extensible code
* **Documentation** - Comprehensive API docs and guides
* **Error Handling** - Graceful failure management

---

## 🧠 Key Lessons Learned

### Database Deployment Challenges
* **URL Encoding Issues** - Special characters in passwords
* **Environment Variable Management** - Individual vs. single URL approach
* **SSL Configuration** - Production database security requirements
* **Connection Pooling** - Performance optimization for production

### Spring Boot Best Practices
* **Profile-based Configuration** - Environment-specific settings
* **External Database Testing** - Validate production connectivity
* **Security Configuration** - Proper CORS and authentication setup
* **Health Monitoring** - Application and database health checks

---

## 👤 Author

**Douglas MacKrell**
📍 NYC / EST
🔗 [linkedin.com/in/douglasmackrell](https://linkedin.com/in/douglasmackrell)
🐙 [github.com/DouglasMacKrell](https://github.com/DouglasMacKrell)

---

## 🥷 Final Word

This project demonstrates **enterprise-grade engineering** from concept to production deployment. It showcases:

* **Rapid Development** - From zero to production in 48 hours
* **Production Readiness** - Real database, comprehensive testing, security
* **Modern Practices** - Docker, cloud deployment, API documentation
* **Problem Solving** - Real-world deployment challenges and solutions

**Venue Ninja** isn't just a demo project—it's a **production-ready API** that could serve real users today. 🚀

---

## 📚 Additional Resources

* [Spring Boot Documentation](https://spring.io/projects/spring-boot)
* [PostgreSQL Documentation](https://www.postgresql.org/docs/)
* [Render Deployment Guide](https://render.com/docs)
* [Docker Best Practices](https://docs.docker.com/develop/dev-best-practices/)
