![Venue Ninja Header](public/images/venue-ninja-headder.png)

# Venue Ninja

[![CI/CD Pipeline](https://github.com/DouglasMacKrell/venue-ninja/workflows/CI%2FCD%20Pipeline/badge.svg)](https://github.com/DouglasMacKrell/venue-ninja/actions)
[![Java](https://img.shields.io/badge/Java-17-orange?logo=java)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.5-brightgreen?logo=spring)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-12+-blue?logo=postgresql)](https://www.postgresql.org/)
[![Docker](https://img.shields.io/badge/Docker-Ready-blue?logo=docker)](https://www.docker.com/)

Venue Ninja helps users explore **seat recommendations** for well-known venues (sections, categories, price hints, and tips). The **public portfolio demo** is a separate [React/Vite frontend](https://venueninja.netlify.app) that now **bundles the venue dataset client-side** so the live demo stays reliable without hosted database infrastructure.

**This repository** is the **original Java/Spring Boot/PostgreSQL backend**: a read-only REST API that loads the same canonical seed data from PostgreSQL. It remains here as an inspectable example of the full-stack architecture and backend engineering.

---

## Original architecture

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│  React/Vite     │    │  Spring Boot    │    │  PostgreSQL     │
│  frontend       │───►│  REST API       │───►│  (seed data)    │
│  (separate repo)│    │  (this repo)    │    │                 │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

The hosted frontend **no longer calls this API** for the portfolio demo. The backend implementation is preserved to demonstrate controllers, services, JPA, Hibernate, PostgreSQL integration, tests, and Docker packaging.

---

## What this backend demonstrates

- **Spring Boot 3.5.5** on **Java 17** with layered design (controller → service → repository)
- **Spring Data JPA** and **Hibernate** against **PostgreSQL** (HikariCP connection pool in the `production` profile)
- **Read-only REST API** — no write endpoints; dataset is seeded, not user-generated
- **Spring Security** with CORS (frontend origins) and permissive read access for the demo API
- **springdoc OpenAPI** — Swagger UI when running locally
- **Spring Boot Actuator** — health and metrics endpoints
- **Automated tests** (H2 in the `test` profile) plus **Checkstyle**, **SpotBugs**, and **JaCoCo** in CI
- **Multi-stage Dockerfile** for containerized runs with the `production` profile

---

## API endpoints

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/venues` | All venues with nested seat recommendations |
| `GET` | `/venues/{id}` | One venue by string id (e.g. `msg`) |

Additional endpoints useful when running locally:

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/swagger-ui/index.html` | Interactive API docs |
| `GET` | `/actuator/health` | Actuator health (includes DB when configured) |
| `GET` | `/health`, `/health/ready`, `/health/live` | Custom health-style endpoints |

The API exposes **only GET** handlers for venue data. There are no endpoints that persist user changes in production use.

**Note:** A missing venue id currently surfaces as a `RuntimeException` handled as **HTTP 500**, not 404. That behavior is unchanged in this repository; see [Known issues](./docs/known-issues.md) for other limitations.

### Example response shape

JSON uses camelCase field names (Jackson). Recommendations match the seed data in [`data.sql`](src/main/resources/data.sql):

```json
{
  "id": "msg",
  "name": "Madison Square Garden",
  "recommendations": [
    {
      "section": "104",
      "category": "Lower Bowl",
      "reason": "Best resale value & view of stage",
      "estimatedPrice": "$250",
      "tip": "Avoid row 20+ due to rigging obstruction"
    }
  ]
}
```

Each recommendation may also include a numeric `id` assigned by the database on insert.

---

## Domain model

- **`Venue`** — `@Entity` with string `id` (primary key) and `name`.
- **`SeatRecommendation`** — `@Entity` with generated `Long` id, plus `section`, `category`, `reason`, `estimatedPrice`, and `tip`.
- **Relationship** — `Venue` `@OneToMany` → `SeatRecommendation` via `venue_id` (`@JoinColumn` on the venue side).

There is **no separate DTO layer**; entities are returned directly as JSON.

---

## Data and database initialization

**Canonical dataset:** [`src/main/resources/data.sql`](src/main/resources/data.sql) — 10 venues, 3 seat recommendations each. This file is the source of truth for seed content in this backend.

**Schema and seeding (default and `production` profiles):**

1. `spring.jpa.hibernate.ddl-auto=create` — Hibernate creates the schema from entities on startup (data is not preserved across restarts).
2. `spring.jpa.defer-datasource-initialization=true` and `spring.sql.init.mode=always` — Spring runs `data.sql` after schema creation to insert seed rows.

There is **no Flyway/Liquibase** (or other migration tool) in this repository; schema lifecycle is Hibernate `create` plus SQL init.

**Tests** use the `test` profile with **H2** in-memory (`ddl-auto=create-drop`) and the same `data.sql` init pattern; test classes typically clear or replace data in setup.

---

## Local development

### Prerequisites

- Java 17+
- Maven (wrapper included)
- **PostgreSQL** for running the API locally (the default application profile does not configure an embedded database)

### Run with PostgreSQL

```bash
git clone https://github.com/DouglasMacKrell/venue-ninja.git
cd venue-ninja

export DB_HOST=localhost
export DB_PORT=5432
export DB_NAME=venueninja
export DB_USER=postgres
export DB_PASSWORD=your_password

./mvnw spring-boot:run -Dspring.profiles.active=production
```

Create an empty database (e.g. `venueninja`) before starting; Hibernate and `data.sql` will populate it on each run.

### Local URLs

- API: http://localhost:8080/venues
- Swagger UI: http://localhost:8080/swagger-ui/index.html
- Actuator health: http://localhost:8080/actuator/health

### Environment variables (`production` profile)

| Variable | Purpose |
|----------|---------|
| `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD` | PostgreSQL connection (used by Render blueprint) |
| `DATABASE_URL` | Optional full JDBC URL override (default template includes `sslmode=require`) |
| `PORT` | HTTP port (default `8080`) |
| `SPRING_PROFILES_ACTIVE=production` | Loads [`application-production.properties`](src/main/resources/application-production.properties) |

Do not commit real passwords; use environment variables or local exports only.

---

## Docker

```bash
docker build -t venue-ninja .

docker run -p 8080:8080 \
  -e DB_HOST=your_host \
  -e DB_PORT=5432 \
  -e DB_NAME=venueninja \
  -e DB_USER=your_user \
  -e DB_PASSWORD=your_password \
  venue-ninja
```

The image starts the app with `-Dspring.profiles.active=production` and expects a reachable PostgreSQL instance.

---

## Tests and quality tooling

```bash
# Unit, integration, API, performance, error-handling, and regression tests (H2)
./mvnw clean test

# Coverage report (JaCoCo)
./mvnw test jacoco:report

# Style and static analysis (also run in CI)
./mvnw checkstyle:check
./mvnw spotbugs:check
```

Test classes live under `src/test/java/com/venueninja/` (e.g. `VenueServiceTest`, `VenueRepositoryTest`, `VenueControllerTest`, `ErrorHandlingTest`, `PerformanceTest`, `RegressionTestSuite`).

CI runs tests, JaCoCo, package build, SpotBugs, and Checkstyle on pushes and pull requests to `main` — see [CI/CD pipeline](./docs/ci-cd-pipeline.md).

---

## Portfolio demo vs. this repository

The original application used **React → Spring Boot → PostgreSQL**. Because the demo dataset is **small and read-only**, the **current hosted frontend** loads that data **statically** so the portfolio does not depend on a long-lived Render database or API.

This repo **keeps the original backend** so reviewers can read the Java implementation, run it against PostgreSQL locally, and see how the API and persistence layer were built.

Historical Render deployment notes and the [`render.yaml`](render.yaml) blueprint are **reference only** — not required for the static public demo. See [Deployment notes](./docs/deployment-notes.md).

---

## Documentation

| Document | Description |
|----------|-------------|
| [Deployment notes](./docs/deployment-notes.md) | Historical Render + PostgreSQL deployment (reference) |
| [Database architecture](./docs/database-architecture.md) | Schema, connection settings, init behavior |
| [Testing strategy](./docs/testing-strategy.md) | Test layers and execution |
| [CI/CD pipeline](./docs/ci-cd-pipeline.md) | GitHub Actions workflow |
| [Known issues](./docs/known-issues.md) | Limitations and possible future work |
| [Swagger quickstart](./docs/swagger-quickstart.md) | Local API documentation |

---

## Author

**Douglas MacKrell**  
[LinkedIn](https://linkedin.com/in/douglasmackrell) · [GitHub](https://github.com/DouglasMacKrell)

---

## Additional resources

- [Spring Boot](https://spring.io/projects/spring-boot)
- [PostgreSQL documentation](https://www.postgresql.org/docs/)
- [Docker documentation](https://docs.docker.com/)
