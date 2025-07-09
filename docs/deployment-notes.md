# Deployment Notes for Venue Ninja 🚀

These instructions walk through deploying the Venue Ninja Spring Boot backend to **Render** and (optionally) a static frontend to **Railway**.

---

## ✅ Backend Deployment – Render

### 1. Create a GitHub Repo

Push your local project to GitHub if you haven’t already:

```bash
git remote add origin https://github.com/YOUR_USERNAME/venue-ninja.git
git push -u origin main
```

### 2. Log Into Render

Go to: [https://render.com](https://render.com)

Create a new **Web Service** and connect your GitHub repo.

### 3. Configure Render

* **Name:** venue-ninja
* **Environment:** Docker
* **Dockerfile Location:** at repo root
* **Instance Type:** Free or Starter is fine
* **Region:** US (if targeting US interviews)

Render will build your container using the provided Dockerfile and Maven commands. The backend will be accessible at a URL like:

```
https://venue-ninja.onrender.com
```

### 4. Test Public API

After the build completes and logs show “Started VenueNinjaApplication,” visit:

* `GET /venues` → [https://venue-ninja.onrender.com/venues](https://venue-ninja.onrender.com/venues)
* `GET /venues/{id}` → try msg, citi, mcg
* Swagger UI: [https://venue-ninja.onrender.com/swagger-ui/index.html](https://venue-ninja.onrender.com/swagger-ui/index.html)

If these load successfully, your deployment was a total win 🥷✅

---

## 🎨 Optional: Frontend Deployment – Railway

Use Railway to deploy a small static site that consumes your Render API.

### 1. Create Your Frontend Folder

Build with:

* HTML + JS
* Or React + Vite (fast!)

### 2. Connect to Railway

* Login: [https://railway.app](https://railway.app)
* Create **New Project → Deploy from GitHub**
* Set root directory if needed (e.g., `/frontend`)
* Confirm build command if using Vite (`npm run build`)

### 3. Example Frontend Features

* Dropdown to select venue ID
* Display mock seat data
* Fetch from: `https://venue-ninja.onrender.com/venues/{id}`

---

## 💡 Deployment Tips

* Render supports auto-deploy from GitHub on commit
* Keep `application.properties` empty or minimal for container setups
* Test endpoints before interview using cURL or Swagger UI
* If frontend uses fetch, ensure CORS is handled if deployed separately

---

## 🧼 Cleanup / Next Steps

* Add logging via Spring Boot Actuator (optional)
* Add favicon & SEO metadata if doing frontend
* Use a custom domain if desired
* Document `.env` usage if securing future endpoints

---

Need help? DM @DouglasMacKrell on LinkedIn or whisper your service URL into the void. A ninja will answer. 🥷💨
