# Known Issues and Future Improvements – Venue Ninja 🐛🔧

While the Venue Ninja API is battle-ready for interview demos and lightweight use, here are a few known tradeoffs and areas where the system could grow or be hardened.

---

## ⚠️ Known Issues

### 1. ❌ No Persistence Layer

* Currently uses in-memory mock data
* Data resets on app restart
* No database or file persistence

### 2. 📉 No Validation/Error Coverage on Input

* No input format validation on `GET /venues/{id}`
* Invalid/malformed IDs return 404, but not with typed validation

### 3. 🌐 No CORS/Frontend Headers Configured

* Local frontends calling from a different port may hit CORS issues
* This can be resolved via Spring Boot Web MVC configuration

### 4. 🧪 No Automated Tests Yet

* Has basic Spring Boot test scaffold, but no written tests
* Could use JUnit + Mockito for service/controller testing

### 5. 🧵 Static Data is Hardcoded

* Expanding or maintaining venues/recommendations means manually editing Java files
* A database or JSON loader would make future updates cleaner

### 6. 📦 Swagger Not Versioned

* Swagger spec uses default config
* Could benefit from custom grouping, versioning, or tag filtering for larger apps

---

## 🧠 Future Improvements

* Add persistent layer: H2 for local, Postgres for prod
* Implement service tests + input validation (e.g. `@Valid`)
* Externalize mock data into a JSON file or lightweight DB seed
* Add logging (e.g. via Spring Boot Actuator)
* Build minimal frontend that uses fetch API
* Add API key or basic token auth for limited access

---

These issues don’t block the project from being demoed or used live—they’re just the next steps on your ninja growth path. 🥷✨
