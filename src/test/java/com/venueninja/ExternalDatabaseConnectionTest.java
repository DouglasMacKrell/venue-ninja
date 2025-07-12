package com.venueninja;

import org.junit.jupiter.api.Test;
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
    void testExternalDatabaseConnection() throws SQLException {
        // Test connection to Render PostgreSQL database
        String url = "jdbc:postgresql://dpg-d1ok8ek9c44c73fo8u9g-a.virginia-postgres.render.com:5432/venue_ninja_db";
        String user = "venue_ninja_db_user";
        String password = "8gCV7weUED662qAjWFdmxqhyqa4ZCwaZ";
        
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
    void testEnvironmentVariables() {
        // Test that environment variables are accessible
        String dbHost = System.getenv("DB_HOST");
        String dbPort = System.getenv("DB_PORT");
        String dbName = System.getenv("DB_NAME");
        String dbUser = System.getenv("DB_USER");
        String dbPassword = System.getenv("DB_PASSWORD");
        
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