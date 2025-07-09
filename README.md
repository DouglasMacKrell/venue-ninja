# Venue Ninja 🎟️🗡️

A Java + Spring Boot REST API that delivers smart seat recommendations for iconic venues like Madison Square Garden, Yankee Stadium, and more. Built as a showcase project to demonstrate full-stack engineering skills, rapid deployment practices, and product empathy.

**🌐 Live demo**: [https://venue-ninja.onrender.com/swagger-ui/index.html](https://venue-ninja.onrender.com/swagger-ui/index.html)

---

##  QUICKLINKS

| Link | Description |
| :--- | :--- |
| 🚀 [Deployment Notes](./docs/deployment-notes.md) | How to deploy with Docker + Render |
| 📝 [Interview Pitch](./docs/interview-pitch.md) | Talking points for interviews |
| 🧠 [Lessons Learned](./docs/lessons-learned.md) | How I fixed CORS and other issues |
| ⚠️ [Known Issues](./docs/known-issues.md) | Current limitations and future work |
| 📘 [Swagger Quickstart](./docs/swagger-quickstart.md) | How to use the API documentation |

---

## 🔥 What It Does

This project exposes RESTful endpoints that:

* List supported venues
* Return hardcoded seat recommendations per venue (categorized by value, section, and tips)

It’s powered by Spring Boot, designed for Swagger visibility, and deployed on Render with mock data that mirrors real-world ticketing logic.

---

## 💡 Why This Project?

* ✅ Built from scratch in 48 hours
* 🎟️ Reflects real-world ticketing UX patterns
* ⚡ Zero DB dependencies = fast load and deploy
* 📘 Swagger-ready for demo & test
* 🧠 Highlights engineering + product thinking
* 🛰️ Deployed live with Docker and Render

---

## 🧱 Tech Stack

* Java 17
* Spring Boot
* In-memory mock data
* Swagger UI via springdoc-openapi
* GitHub + Render for deployment

---

## 🔗 API Endpoints

### `GET /venues`

Returns a list of all supported venues with ID + name.

### `GET /venues/{id}`

Returns a list of recommended seats with:

* Section
* Category (e.g., "Budget", "Premium")
* Reason
* Estimated Price
* Tip or Warning

Example response:

```json
{
  "name": "Madison Square Garden",
  "id": "msg",
  "recommendations": [
    {
      "section": "104",
      "category": "Lower Bowl",
      "reason": "Best resale value & view of stage",
      "estimated_price": "$250",
      "tip": "Avoid row 20+ due to rigging obstruction"
    }
  ]
}
```

---

## 🧪 Local Setup

```bash
# Clone and enter project
$ git clone https://github.com/DouglasMacKrell/venue-ninja.git
$ cd venue-ninja

# Run with Maven
$ ./mvnw spring-boot:run
```

Visit:

* Swagger UI: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
* API: [http://localhost:8080/venues](http://localhost:8080/venues)

---

## 🚀 Deployment

This project is deployed live via [Render](https://render.com):

▶️ [https://venue-ninja.onrender.com/swagger-ui/index.html](https://venue-ninja.onrender.com/swagger-ui/index.html)

Full deployment instructions (Dockerfile, build command, render.yaml) live in:

* `/docs/deployment-notes.md`

---

## 📚 Documentation

* Full Swagger UI enabled (see Swagger Quickstart)
* Additional docs live in `/docs/`:

  * `swagger-quickstart.md`
  * `deployment-notes.md`
  * `interview-pitch.md`
  * `known-issues.md`

---

## 💬 Interview Highlights

* Designed with real-world user experience in mind
* Clear separation of concerns (controller, service, model)
* Deployed in under 48 hours
* Spring Boot + Swagger integration from scratch
* Uses hardcoded mock data to simulate real-world ticketing recommendations
* Swagger docs serve as live demo interface during interviews

---

## 🧠 Lessons Learned

See `/docs/interview-pitch.md` for a reflection on the design process, technical choices, and value-driven tradeoffs.

---

## 👤 Author

**Douglas MacKrell**
📍 NYC / EST
🔗 [linkedin.com/in/douglasmackrell](https://linkedin.com/in/douglasmackrell)

---

## 🥷 Final Word

A ninja strikes fast, adapts wisely, and leaves behind clean code. Thanks for checking out Venue Ninja!
