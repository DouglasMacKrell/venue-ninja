package com.venueninja;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class ExternalDatabaseConnectionTest {

    @Test
    @EnabledIfEnvironmentVariable(named = "DATABASE_URL", matches = ".*")
    void testExternalDatabaseConnection() throws SQLException {
        // Test connection to Render PostgreSQL database
        String url = System.getenv("DATABASE_URL");
        String user = System.getenv("DB_USER");
        String password = System.getenv("DB_PASSWORD");
        
        assertNotNull(url, "DATABASE_URL environment variable must be set for this test");
        assertNotNull(user, "DB_USER environment variable must be set for this test");
        assertNotNull(password, "DB_PASSWORD environment variable must be set for this test");
        
        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            assertNotNull(connection, "External database connection should not be null");
            assertFalse(connection.isClosed(), "External database connection should be open");
            
            // Test a simple query
            var resultSet = connection.createStatement().executeQuery("SELECT 1");
            assertTrue(resultSet.next(), "Should be able to execute a simple query on external DB");
            assertEquals(1, resultSet.getInt(1), "Query should return 1 from external DB");
            
            System.out.println("✅ External database connection successful!");
        } catch (SQLException e) {
            System.err.println("❌ External database connection failed: " + e.getMessage());
            throw e;
        }
    }

    @Test
    void testDatabaseSecurityConfiguration() {
        // Test that database security configuration is properly set up
        String dbHost = System.getenv("DB_HOST");
        String dbPort = System.getenv("DB_PORT");
        String dbName = System.getenv("DB_NAME");
        String dbUser = System.getenv("DB_USER");
        String dbPassword = System.getenv("DB_PASSWORD");
        String databaseUrl = System.getenv("DATABASE_URL");
        
        System.out.println("Database Security Configuration:");
        System.out.println("DATABASE_URL: " + (databaseUrl != null ? "SET (length: " + databaseUrl.length() + ")" : "NOT SET"));
        System.out.println("DB_HOST: " + (dbHost != null ? dbHost : "NOT SET"));
        System.out.println("DB_PORT: " + (dbPort != null ? dbPort : "NOT SET"));
        System.out.println("DB_NAME: " + (dbName != null ? dbName : "NOT SET"));
        System.out.println("DB_USER: " + (dbUser != null ? dbUser : "NOT SET"));
        System.out.println("DB_PASSWORD: " + (dbPassword != null ? "SET (length: " + dbPassword.length() + ")" : "NOT SET"));
        
        // Security verification - ensure we're not using default/localhost values
        if (databaseUrl != null) {
            assertFalse(databaseUrl.contains("localhost"), "Database URL should not point to localhost in production");
            assertTrue(databaseUrl.contains("render.com") || databaseUrl.contains("postgresql://"), 
                      "Database URL should be a proper external database URL");
        }
        
        if (dbHost != null) {
            assertFalse(dbHost.equals("localhost"), "Database host should not be localhost in production");
            assertTrue(dbHost.contains("render.com") || dbHost.contains("postgres"), 
                      "Database host should be a proper external database host");
        }
        
        // Verify SSL is required for external connections
        if (databaseUrl != null && databaseUrl.contains("render.com")) {
            assertTrue(databaseUrl.contains("sslmode=require") || databaseUrl.contains("ssl=true"), 
                      "External database connections should require SSL");
        }
    }

    @Test
    void testDatabaseConnectionPoolConfiguration() {
        // Test that connection pool is properly configured for security
        String maxPoolSize = System.getenv("SPRING_DATASOURCE_HIKARI_MAXIMUM_POOL_SIZE");
        String connectionTimeout = System.getenv("SPRING_DATASOURCE_HIKARI_CONNECTION_TIMEOUT");
        
        System.out.println("Connection Pool Configuration:");
        System.out.println("Max Pool Size: " + (maxPoolSize != null ? maxPoolSize : "DEFAULT"));
        System.out.println("Connection Timeout: " + (connectionTimeout != null ? connectionTimeout : "DEFAULT"));
        
        // Verify reasonable defaults for production
        if (maxPoolSize != null) {
            int maxSize = Integer.parseInt(maxPoolSize);
            assertTrue(maxSize > 0 && maxSize <= 20, "Max pool size should be between 1 and 20");
        }
        
        if (connectionTimeout != null) {
            int timeout = Integer.parseInt(connectionTimeout);
            assertTrue(timeout >= 1000 && timeout <= 60000, "Connection timeout should be between 1-60 seconds");
        }
    }
} 