package com.venueninja;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
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
            
            // Get database metadata
            DatabaseMetaData metaData = connection.getMetaData();
            String databaseProductName = metaData.getDatabaseProductName();
            String databaseProductVersion = metaData.getDatabaseProductVersion();
            
            System.out.println("Connected to: " + databaseProductName + " " + databaseProductVersion);
            
            // Verify we're using the expected database type
            if (System.getProperty("spring.profiles.active", "").contains("test")) {
                assertTrue(databaseProductName.toLowerCase().contains("h2"), 
                          "Test profile should use H2 database");
            } else {
                assertTrue(databaseProductName.toLowerCase().contains("postgresql"), 
                          "Production profile should use PostgreSQL database");
            }
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

    @Test
    void testDatabaseTransactionSupport() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            // Test transaction support
            connection.setAutoCommit(false);
            
            // Execute a simple transaction
            connection.createStatement().execute("CREATE TABLE IF NOT EXISTS test_transaction (id INT)");
            connection.createStatement().execute("INSERT INTO test_transaction VALUES (1)");
            
            // Verify the insert worked
            var resultSet = connection.createStatement().executeQuery("SELECT COUNT(*) FROM test_transaction");
            assertTrue(resultSet.next());
            assertEquals(1, resultSet.getInt(1));
            
            // Rollback to clean up
            connection.rollback();
            connection.setAutoCommit(true);
            
            // Clean up
            connection.createStatement().execute("DROP TABLE IF EXISTS test_transaction");
        }
    }
} 