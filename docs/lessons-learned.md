# 🥷 Lessons Learned: Fixing CORS in Spring Boot with Vite Frontend

This document outlines the key lessons learned while troubleshooting a `CORS` issue between a Spring Boot backend and a Vite-powered React frontend, as well as testing improvements and null safety practices.

---

## 🧠 The Problem

The frontend at `http://localhost:5175` was failing to fetch data from the backend deployed at `https://venue-ninja.onrender.com`.

### ❌ Symptoms

- `403 Forbidden` errors in the browser
- `Access-Control-Allow-Origin` header was missing
- `AxiosError: Network Error` appeared in the console
- Spring Security was silently ignoring the CORS configuration

---

## ✅ The Fix

We resolved the issue by identifying and correcting **two critical problems**:

---

### 🔌 1. Missing `@EnableWebSecurity`: The "Master Switch"

#### What Was Wrong
The `SecurityConfig` class defined a `SecurityFilterChain` bean and a CORS policy, but Spring Security wasn't applying it.

#### Why
The class was missing the `@EnableWebSecurity` annotation, which tells Spring to enable web security features and use the provided filter chain.

#### Fix
Add:

```java
@EnableWebSecurity
```

---

### 🧾 2. Incorrect Allowed Origin Port

#### What Was Wrong

The `CorsConfiguration` had:

```java
config.setAllowedOrigins(Arrays.asList("http://localhost:5173"));
```

But the frontend was running on:

```text
http://localhost:5175
```

#### Why This Mattered

CORS policies require **exact string matches**. Even a port mismatch will cause the request to be blocked by the browser.

#### Fix

Update the config to:

```java
config.setAllowedOrigins(Arrays.asList(
    "http://localhost:5175",
    "https://venue-ninja.onrender.com"
));
```

---

## 🧩 Final Working Code

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(requests -> requests
                .anyRequest().permitAll());

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(Arrays.asList(
            "http://localhost:5175",
            "https://venue-ninja.onrender.com"
        ));
        config.setAllowedHeaders(Arrays.asList("*"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
```

---

## 🧪 Testing Improvements & Null Safety

### The Challenge

During comprehensive test suite development, we encountered numerous static analysis warnings about potential null pointer access, particularly with:
- `response.getBody()` method calls
- `response.getHeaders().getContentType()` method calls
- Property access on potentially null objects

### ✅ The Solution: Null Safety Patterns

#### 1. Store Results in Variables After Null Checks

**Problem:**
```java
assertThat(response.getHeaders().getContentType()).isNotNull();
assertThat(response.getHeaders().getContentType().toString()).contains("application/json");
```

**Solution:**
```java
assertThat(response.getHeaders().getContentType()).isNotNull();
var contentType = response.getHeaders().getContentType();
if (contentType != null) {
    assertThat(contentType.toString()).contains("application/json");
}
```

#### 2. Wrap Property Access in Null Checks

**Problem:**
```java
assertThat(venue).isNotNull();
assertThat(venue.getId()).isEqualTo("msg");
```

**Solution:**
```java
assertThat(venue).isNotNull();
if (venue != null) {
    assertThat(venue.getId()).isEqualTo("msg");
}
```

#### 3. Use Suppression Annotations for Intentional Null Testing

**Problem:**
```java
String venueId = null;
when(venueRepository.findById(null)).thenReturn(Optional.empty());
```

**Solution:**
```java
@SuppressWarnings("all")
String venueId = null;
when(venueRepository.findById(null)).thenReturn(Optional.empty());
```

### 🏗️ Test Suite Architecture

We implemented a comprehensive testing strategy with:

1. **Error Handling Tests** (`ErrorHandlingTest.java`)
   - Malformed inputs, SQL injection, XSS, path traversal
   - Concurrent request handling

2. **Performance Tests** (`PerformanceTest.java`)
   - Load testing, response time validation
   - Memory usage monitoring

3. **Regression Tests** (`RegressionTestSuite.java`)
   - Core functionality validation
   - Data structure consistency checks

4. **Test Data Builders** (`TestDataBuilder.java`)
   - Consistent test data creation
   - Reusable test scenarios

### 📊 Coverage and Quality Gates

Implemented JaCoCo coverage reporting with minimum thresholds:
- **Line Coverage**: 80% minimum
- **Branch Coverage**: 70% minimum

```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.11</version>
    <executions>
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

## 🔍 Summary

| Issue                              | Fix                                                     |
| ---------------------------------- | ------------------------------------------------------- |
| Missing `@EnableWebSecurity`       | Added the annotation to activate custom security config |
| Incorrect port (`5173` vs. `5175`) | Corrected to match frontend's actual port               |
| Null pointer access warnings       | Implemented null safety patterns and suppression        |
| Limited test coverage              | Added comprehensive test suites with quality gates      |
| Static analysis compliance         | Used proper null checks and variable storage            |

---

## 🥷 Ninja Takeaways

* **Spring Boot won't use your security config unless you tell it to!**
* **CORS is *strict*. One character off and you're toast.**
* **Don't trust defaults when debugging a security issue. Be explicit.**
* **Static analysis tools are your friends - embrace null safety patterns.**
* **Comprehensive testing requires multiple test categories and proper data management.**
* **Quality gates ensure code quality doesn't regress over time.**
