# Deployment Notes for Venue Ninja 🚀

> **Historical reference:** This guide describes how the Venue Ninja Spring Boot backend was originally deployed to **Render** with PostgreSQL. The public portfolio frontend now bundles venue data statically and does not require this API to be hosted. Keep this document for deployment architecture context; see the [README](../README.md) for the current portfolio setup.

This guide walks through the original Render deployment with PostgreSQL database integration.

---

## 🎯 Overview

Venue Ninja has evolved from a simple in-memory demo to a **production-grade application** with:

- ✅ **Real PostgreSQL Database** - Persistent data storage
- ✅ **Comprehensive Testing** - Unit, integration, and API tests (H2 in CI)
- ✅ **Production Security** - SSL connections, environment variables
- ✅ **Modern DevOps** - Docker containerization, cloud deployment
- ✅ **Monitoring Ready** - Health checks and structured logging
- ✅ **CI/CD Pipeline** - Automated testing and quality checks

---

## 🗄️ Database Setup

### PostgreSQL on Render

1. **Create Database Service**
   - Go to [Render Dashboard](https://dashboard.render.com)
   - Click "New" → "PostgreSQL"
   - Name: `venue-ninja-db`
   - Region: US East (Virginia)
   - Plan: Free (for demo) or Starter (for production)

2. **Database Credentials**
   ```
   Host: your_database_host_here
   Port: 5432
   Database: your_database_name_here
   Username: your_database_user_here
   Password: [REDACTED - Use environment variable DB_PASSWORD]
   ```

3. **Connection String**
   ```
   postgresql://your_user:[PASSWORD]@your_host:5432/your_database
   ```

---

## 🔧 Environment Configuration

### Required Environment Variables

Set these in your Render service dashboard under **Environment**:

#### Option 1: Individual Variables (Recommended)
```bash
DB_HOST=your_database_host_here
DB_PORT=5432
DB_NAME=your_database_name_here
DB_USER=your_database_user_here
DB_PASSWORD=your_actual_password_here
```

#### Option 2: Single DATABASE_URL
```bash
DATABASE_URL=jdbc:postgresql://your_host:5432/your_database?sslmode=require
```

### Why Individual Variables?

The application supports both approaches, but **individual variables are recommended** because:

- ✅ **No URL Encoding Issues** - Special characters in passwords work correctly
- ✅ **Better Security** - Easier to rotate individual credentials
- ✅ **Flexibility** - Can configure connection pool settings separately
- ✅ **Debugging** - Easier to troubleshoot connection issues

---

## 🐳 Docker Deployment

### Dockerfile

See the production-ready [Dockerfile](../Dockerfile) in the root directory for a multi-stage build with:
- **Security**: Non-root user execution
- **Optimization**: Multi-stage build for smaller image size
- **Health Checks**: Built-in health monitoring
- **Best Practices**: Alpine Linux base image

```bash
# Build the Docker image
docker build -t venue-ninja:latest .

# Run the container
docker run -d --name venue-ninja -p 8080:8080 venue-ninja:latest

# Test health endpoint
curl http://localhost:8080/health
```

### Build Configuration

**Build Command:**
```bash
./mvnw clean package -DskipTests
```

**Start Command:**
```bash
java -jar -Dspring.profiles.active=production target/venueninja-0.0.1-SNAPSHOT.jar
```

---

## 🚀 Render Deployment Steps

### 1. Prepare GitHub Repository

```bash
# Ensure your code is pushed to GitHub
git add .
git commit -m "Production-ready with PostgreSQL integration"
git push origin main
```

### 2. Create Web Service on Render

1. **Connect Repository**
   - Go to [Render Dashboard](https://dashboard.render.com)
   - Click "New" → "Web Service"
   - Connect your GitHub repository

2. **Configure Service**
   - **Name**: `venue-ninja`
   - **Environment**: `Docker`
   - **Region**: US East (Virginia)
   - **Branch**: `main`
   - **Root Directory**: `/` (leave empty)

3. **Build Settings**
   - **Build Command**: `./mvnw clean package -DskipTests`
   - **Start Command**: `java -jar -Dspring.profiles.active=production target/venueninja-0.0.1-SNAPSHOT.jar`

### 3. Set Environment Variables

In the **Environment** tab, add:

```bash
# Database Configuration
DB_HOST=your_database_host_here
DB_PORT=5432
DB_NAME=your_database_name_here
DB_USER=your_database_user_here
DB_PASSWORD=your_actual_password_here

# Application Configuration
SPRING_PROFILES_ACTIVE=production
```

### 4. Deploy

Click **Create Web Service** and wait for the build to complete.

---

## 🧪 Testing Deployment

### 1. Health Check

Visit the health endpoint to verify the application is running:
```
https://your-service.onrender.com/actuator/health
```

Expected response:
```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP"
    }
  }
}
```

### 2. API Endpoints

Test the core endpoints on your deployed host (when running this backend):

- **All Venues**: `GET /venues`
- **Specific Venue**: `GET /venues/msg`
- **Swagger UI**: `/swagger-ui/index.html`

### 3. Validate connectivity

Confirm `/actuator/health` reports the database as UP when PostgreSQL credentials and network access are correct. Automated tests in this repository use H2; they do not include a separate external PostgreSQL test class.

---

## 🔄 CI/CD Pipeline

### GitHub Actions Workflow

The project includes a streamlined CI/CD pipeline that runs on every push:

#### Pipeline Stages:
1. **Code Quality Checks**
   - Checkstyle validation
   - Code formatting compliance
   - Static analysis

2. **Testing**
   - Unit tests with H2 database
   - Integration tests
   - Performance tests
   - Error handling tests

3. **Build Verification**
   - Maven compilation
   - Package creation
   - Dependency resolution

#### Pipeline Configuration:
- **File**: `.github/workflows/ci.yml`
- **Triggers**: Push to main branch, Pull requests
- **Environment**: Ubuntu latest
- **Java Version**: 17

#### Recent Improvements:
- ✅ **Simplified Pipeline**: Removed Docker tests to focus on essential checks
- ✅ **Faster Execution**: Optimized test execution order
- ✅ **Better Error Reporting**: Clear failure messages and debugging info
- ✅ **Reliable Tests**: Fixed Checkstyle configuration and test dependencies

---

## 📊 Monitoring and Health Checks

### Built-in Health Endpoints

The application provides several health check endpoints:

- **Overall Health**: `/actuator/health`
- **Database Health**: `/actuator/health/db`
- **Application Info**: `/actuator/info`

### Logging Configuration

Production logging is configured for:
- **Application Logs**: INFO level
- **Database Queries**: INFO level (when needed)
- **Security Events**: WARN level
- **Error Tracking**: ERROR level with stack traces

### Performance Monitoring

The application includes:
- **Response Time Tracking**: Built-in timing for API endpoints
- **Database Query Monitoring**: Hibernate SQL logging
- **Memory Usage**: JVM metrics available via JMX

---

## 🔒 Security Considerations

### Production Security Features

1. **SSL/TLS**: All database connections use SSL
2. **Environment Variables**: No hardcoded credentials
3. **CORS Configuration**: Properly configured for frontend integration
4. **Input Validation**: Comprehensive validation for all inputs
5. **Error Handling**: Secure error messages (no sensitive data exposure)

### Security Best Practices

- ✅ **No Credentials in Code**: All secrets use environment variables
- ✅ **HTTPS Only**: Production deployment uses HTTPS
- ✅ **Input Sanitization**: All user inputs are validated and sanitized
- ✅ **Principle of Least Privilege**: Database user has minimal required permissions

---

## 🚨 Troubleshooting

### Common Issues

#### 1. Database Connection Failures

**Symptoms**: Application fails to start, database connection errors

**Solutions**:
- Verify environment variables are set correctly
- Check database credentials and permissions
- Ensure SSL configuration is correct
- Test database connectivity manually

#### 2. CORS Issues

**Symptoms**: Frontend can't access API, 403 Forbidden errors

**Solutions**:
- Verify CORS configuration in `SecurityConfig`
- Check allowed origins match frontend URL
- Ensure `@EnableWebSecurity` annotation is present

#### 3. Build Failures

**Symptoms**: CI/CD pipeline fails, compilation errors

**Solutions**:
- Run tests locally: `./mvnw clean test`
- Check Checkstyle: `./mvnw checkstyle:check`
- Verify all dependencies are resolved

### Debug Commands

```bash
# Run all tests locally
./mvnw clean test

# Check code quality
./mvnw checkstyle:check

# Build application
./mvnw clean package -DskipTests

# Run application locally
java -jar -Dspring.profiles.active=production target/venueninja-0.0.1-SNAPSHOT.jar
```

---

## 📈 Scaling Considerations

### Current Architecture Limitations

1. **Single Instance**: No load balancing
2. **No Caching**: Every request hits the database
3. **Limited Monitoring**: Basic health checks only

### Future Scaling Options

1. **Horizontal Scaling**: Multiple application instances
2. **Load Balancing**: Reverse proxy configuration
3. **Caching Layer**: Redis integration
4. **Database Optimization**: Connection pooling, query optimization
5. **Monitoring**: Prometheus metrics, Grafana dashboards

---

## 🎉 Success Metrics

### Deployment Checklist

- ✅ **Application Starts**: Health endpoint responds
- ✅ **Database Connected**: Database health check passes
- ✅ **API Endpoints Work**: All endpoints return expected responses
- ✅ **Frontend Integration**: CORS configured correctly
- ✅ **CI/CD Pipeline**: All tests pass in GitHub Actions
- ✅ **Security Validated**: No credentials exposed, SSL enabled

### Performance Benchmarks

- **Startup Time**: < 30 seconds
- **Health Check Response**: < 100ms
- **API Response Time**: < 500ms (average)
- **Database Query Time**: < 50ms (average)
- **Memory Usage**: < 512MB (typical)

---

## 📚 Additional Resources

- **API Documentation**: `/swagger-ui/index.html`
- **Health Checks**: `/actuator/health`
- **Application Info**: `/actuator/info`
- **GitHub Repository**: [Venue Ninja](https://github.com/your-username/venueninja)
- **Render Dashboard**: [Application Status](https://dashboard.render.com)

---

*This deployment guide ensures a smooth, production-ready deployment of Venue Ninja with comprehensive testing, security, and monitoring capabilities.* 🚀
