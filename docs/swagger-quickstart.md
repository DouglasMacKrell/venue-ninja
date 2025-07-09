# Swagger Quickstart for Venue Ninja 📘

This guide helps you get Swagger/OpenAPI up and running in the Venue Ninja Spring Boot project using **springdoc-openapi**.

---

## ✅ What You Get

* Auto-generated OpenAPI docs from Spring annotations
* Interactive Swagger UI at `/swagger-ui/index.html`
* Zero manual spec writing needed
* 🚀 Deployed live at [https://venue-ninja.onrender.com/swagger-ui/index.html](https://venue-ninja.onrender.com/swagger-ui/index.html)

---

## 🧱 Dependencies

Make sure your `pom.xml` includes the following:

```xml
<dependency>
  <groupId>org.springdoc</groupId>
  <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
  <version>2.5.0</version>
</dependency>
```

Then run:

```bash
./mvnw clean install
```

---

## 🧠 How It Works

The `springdoc-openapi` library auto-scans your controller and model classes.
It creates an OpenAPI spec behind the scenes and exposes it at these endpoints:

* `/v3/api-docs` → raw JSON spec
* `/swagger-ui/index.html` → visual documentation
* ✅ Live demo: [https://venue-ninja.onrender.com/swagger-ui/index.html](https://venue-ninja.onrender.com/swagger-ui/index.html)

---

## ✍️ Annotating Your Code

Use annotations from the `io.swagger.v3.oas.annotations.*` package.

### Example – Controller

```java
@Tag(name = "Venue API", description = "Operations related to venue information")
@RestController
@RequestMapping("/venues")
public class VenueController {

    @Operation(summary = "Get all venues")
    @ApiResponse(responseCode = "200", description = "List of venues")
    @GetMapping
    public List<Venue> getAllVenues() { ... }

    @Operation(summary = "Get venue by ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Venue found"),
        @ApiResponse(responseCode = "404", description = "Venue not found")
    })
    @GetMapping("/{id}")
    public Venue getVenueById(@PathVariable String id) { ... }
}
```

### Example – Model

```java
@Schema(description = "Recommended seating at a venue")
public class SeatRecommendation {

    @Schema(description = "Section number", example = "104")
    private String section;

    @Schema(description = "Seating category", example = "Lower Bowl")
    private String category;
    ...
}
```

---

## 🚀 Pro Tip

Swagger is auto-refreshed with every Spring Boot restart. No extra config needed unless you’re customizing servers, auth, or versions.

Use Swagger in interviews as a live interface for demoing your API. It shows polish and forethought.

---

## 🧼 Cleaning Up

You can hide endpoints or add detailed `@Schema` descriptions per field to impress interviewers. Swagger is both a dev tool and a storytelling tool!

---

## 📍 Example Links

* Local: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
* Live: [https://venue-ninja.onrender.com/swagger-ui/index.html](https://venue-ninja.onrender.com/swagger-ui/index.html)

---

For questions or debugging, consult the [springdoc-openapi GitHub](https://github.com/springdoc/springdoc-openapi).
