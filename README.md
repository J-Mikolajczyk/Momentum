# Momentum <img src="frontend/src/assets/favicon.ico" alt="Momentum Logo" width="24" height="24" />

Momentum is a full-stack strength training tracker PWA built with Java Spring Boot, React, and MongoDB. It allows users to plan and track their training blocks, log workouts by week and day, and record exercises with detailed set data including reps and weights.

> 🚧 This project is a work in progress. Features like JWT authentication and a finalized user profile page are currently in development.

---

## ✨ Features to be Implemented

- User login and account system (JWT + Spring Security coming soon)
- Add custom days (e.g., Chest Day, Leg Day, etc.)
- Add exercises to each day
- Track detailed sets: reps, weight, and more
---
## ☁️ Deployment Platforms

- [Render](https://render.com/) for Backend (via Dockerfile)
- [Netlify](https://www.netlify.com/) for Frontend

---

## 🛠 Tech Stack

### Backend
- **Java Spring Boot**
- **REST APIs**
- **Docker**

### Frontend
- **React** with JSX
- **Tailwind CSS** for styling

### Database
- **MongoDB** via Spring Data

### In Progress
- JWT Authentication
- Spring Security

---

## 🚀 Personal Deployment

To run Momentum locally, follow the instructions for your operating system below:

### 🪟 Windows Setup

**1. Clone the Repository**

```powershell
git clone https://github.com/j-mikolajczyk/momentum.git
cd momentum
```

**2. Set Up Environment Variables**

Copy the example ```.env``` files:

```powershell
copy backend\src\main\resources\.env.example backend\src\main\resources\.env
copy frontend\.env.example copy frontend\.env  
```

Open the ```.env``` file and fill in the necessary configuration values (e.g., MongoDB URI, username, password, etc.).

**3. Run the Backend**

- Open a terminal in the ```backend``` directory.
- Run the Spring Boot application using Maven:

```powershell
cd backend
./mvnw spring-boot:run
```

**4. Run the Frontend**

- Open a new terminal window.
- Navigate to the ```frontend``` directory.
- Install depencencies and run:
  ```powershell
  cd frontend
  npm install
  npm run dev
  ```

### 🐧 Linux/MacOS Setup

**1. Clone the Repository**

```
git clone https://github.com/j-mikolajczyk/momentum.git
cd momentum
```

**2. Set Up Environment Variables**

Copy the example ```.env``` files:

```
cp backend/src/main/resources/.env.example backend/src/main/resources/.env
cp frontend/.env.example frontend/.env
```

Open the ```.env``` file and fill in the necessary configuration values (e.g., MongoDB URI, username, password, etc.).

**3. Run the Backend**

- Open a terminal in the ```backend``` directory.
- Run the Spring Boot application using Maven:

```powershell
cd backend
./mvnw spring-boot:run
```

**4. Run the Frontend**

- Open a new terminal window.
- Navigate to the ```frontend``` directory.
- Install depencencies and run:
  ```
  cd frontend
  npm install
  npm run dev
  ```
---

## 📂 Project Structure

```bash
momentum/
├── backend/                # Java Spring Boot backend
│   ├── src/
│   └── pom.xml
├── frontend/               # React frontend
│   ├── public/
│   ├── src/
│   └── package.json
└── README.md
