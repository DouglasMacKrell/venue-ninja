package com.venueninja;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class DatabaseConnectionTest {

    @Autowired
    private DataSource dataSource;

    @Test
    void testDatabaseConnection() throws SQLException {
        assertNotNull(dataSource, "DataSource should not be null");
        
        try (Connection connection = dataSource.getConnection()) {
            assertNotNull(connection, "Database connection should not be null");
            assertFalse(connection.isClosed(), "Database connection should be open");
            
            // Test a simple query
            var resultSet = connection.createStatement().executeQuery("SELECT 1");
            assertTrue(resultSet.next(), "Should be able to execute a simple query");
            assertEquals(1, resultSet.getInt(1), "Query should return 1");
        }
    }

    @Test
    void testDatabaseUrlFormat() {
        String url = dataSource.toString();
        System.out.println("Database URL: " + url);
        
        // For H2 in test profile, should contain HikariPool
        assertTrue(url.contains("HikariPool") || url.contains("hikari"), 
                   "Test database should use Hikari connection pool");
    }
} 