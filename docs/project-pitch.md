# Pitch: Why This Project Matters

Welcome! This document provides insight into the design, architecture, and product thinking behind **Venue Ninja**.

---

## 🎯 The 30-Second Pitch

Venue Ninja is a Spring Boot REST API that provides curated seat recommendations for iconic venues. It was built from scratch in under 48 hours to showcase proficiency in backend architecture, rapid deployment, and product-centric thinking.

The API is fully documented with Swagger and deployed on Render, demonstrating an end-to-end development cycle from concept to live service.

---

## 🧠 Product & Design Philosophy

The core idea was to model a real-world user problem: "Which seats are *actually* good?" Drawing from my experience in the ticketing industry, I designed a system that provides opinionated, value-driven recommendations—like a trusted expert—without relying on complex, real-time data.

The project was intentionally kept focused to highlight three key strengths:
1.  **Speed**: From zero to a deployed, documented API in under 48 hours.
2.  **Structure**: Clean, scalable, and testable code following best practices.
3.  **Product Empathy**: A solution built around a clear user need.

---

## 🏗️ Technical Architecture

The API follows a classic, clean separation of concerns:

-   **Controller (`VenueController`)**: Exposes the REST endpoints and handles HTTP requests/responses. It knows nothing about where the data comes from.
-   **Service (`VenueService`)**: Contains the business logic. It fetches and organizes the venue data, abstracting the data source from the controller.
-   **Model (`Venue`, `SeatRecommendation`)**: Plain Java Objects (POJOs) that provide strong typing and a clear data structure for the API responses.

### Key Decisions:
*   **In-Memory Data**: I used a hardcoded, in-memory data store to keep the project lightweight and focused on the API's structure. For a production environment, this service layer could be easily swapped to use a database like PostgreSQL with JPA.
*   **Swagger for Documentation**: API documentation is auto-generated from the controller's annotations using `springdoc-openapi`. This ensures the docs are always in sync with the code and provides a live, interactive testing environment.
*   **Containerized Deployment**: The application is containerized with Docker and deployed on Render, demonstrating modern DevOps practices and ensuring consistent, portable deployments.

---

## 🚀 How I Would Expand This Project

This MVP serves as a strong foundation. Given more time, I would:
*   **Add a Persistence Layer**: Integrate a database to allow for dynamic, user-specific, or admin-curated recommendations.
*   **Implement Authentication**: Secure the API with tokens (e.g., JWT) or OAuth2 to manage access for different clients.
*   **Build a Frontend**: Develop a simple React or Vue.js frontend to consume the API and provide a rich user interface.
*   **Integrate Third-Party Data**: Connect to live stadium APIs or scrape open datasets to provide more dynamic and accurate recommendations.

---

## 💬 In Summary

Venue Ninja, though simple in scope, is a comprehensive showcase of my ability to deliver a well-architected, documented, and deployed application quickly and thoughtfully. It reflects my commitment to writing clean code, solving user problems, and bringing a touch of creativity to my work.

Thank you for taking the time to review it.
