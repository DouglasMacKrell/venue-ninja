# 🥷 Lessons Learned: Fixing CORS in Spring Boot with Vite Frontend

This document outlines the key lessons learned while troubleshooting a `CORS` issue between a Spring Boot backend and a Vite-powered React frontend.

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
````

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

## 🔍 Summary

| Issue                              | Fix                                                     |
| ---------------------------------- | ------------------------------------------------------- |
| Missing `@EnableWebSecurity`       | Added the annotation to activate custom security config |
| Incorrect port (`5173` vs. `5175`) | Corrected to match frontend's actual port               |

---

## 🥷 Ninja Takeaways

* **Spring Boot won't use your security config unless you tell it to!**
* **CORS is *strict*. One character off and you're toast.**
* **Don't trust defaults when debugging a security issue. Be explicit.**
