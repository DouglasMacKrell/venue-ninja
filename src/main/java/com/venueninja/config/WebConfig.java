package com.venueninja.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    // CORS configuration moved to SecurityConfig.java
    // When Spring Security is present, it takes precedence over WebMvcConfigurer CORS
}
