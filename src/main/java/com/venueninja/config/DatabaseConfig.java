package com.venueninja.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
@Profile("production")
public class DatabaseConfig {

    @Value("${DATABASE_URL:}")
    private String databaseUrl;

    @Bean
    @ConditionalOnProperty(name = "spring.profiles.active", havingValue = "production")
    public DataSource dataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        
        // Transform Render's postgresql:// URL to jdbc:postgresql:// format
        String jdbcUrl = transformDatabaseUrl(databaseUrl);
        
        dataSource.setJdbcUrl(jdbcUrl);
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setMaximumPoolSize(10);
        dataSource.setMinimumIdle(2);
        dataSource.setConnectionTimeout(30000);
        dataSource.setIdleTimeout(600000);
        dataSource.setMaxLifetime(1800000);
        
        return dataSource;
    }
    
    private String transformDatabaseUrl(String url) {
        if (url == null || url.isEmpty()) {
            throw new IllegalStateException("DATABASE_URL environment variable is not set");
        }
        
        // If URL already starts with jdbc:, return as is
        if (url.startsWith("jdbc:")) {
            return url + "?sslmode=require";
        }
        
        // Transform postgresql:// to jdbc:postgresql://
        if (url.startsWith("postgresql://")) {
            return "jdbc:" + url + "?sslmode=require";
        }
        
        // If it's a postgres:// URL, transform to jdbc:postgresql://
        if (url.startsWith("postgres://")) {
            return url.replace("postgres://", "jdbc:postgresql://") + "?sslmode=require";
        }
        
        throw new IllegalArgumentException("Unsupported database URL format: " + url);
    }
} 