# CodeLoom DSA Visualizer — Backend Service

> *"Understand Algorithms. See Every Step. Master the Logic."*

High-performance, RESTful Spring Boot service powering the **CodeLoom DSA Visualizer**. Provides secure user authentication, algorithm catalog management, interactive algorithm visualization engines, problem practice arena, analytics & gamification, daily challenges, and practice session tracking.

---

## 🛠️ Technology Stack

- **Language & Runtime:** Java 21 (LTS)
- **Framework:** Spring Boot 3.4+
- **Security:** Spring Security, JWT (JSON Web Tokens), BCrypt Password Hashing
- **Database:** PostgreSQL 16 / 17
- **Database Migrations:** Flyway (Versioned & Idempotent Migrations `V1` to `V10`)
- **Cache & Infrastructure:** Redis, Docker & Docker Compose
- **Build System:** Apache Maven 3.9+
- **Testing:** JUnit 5, MockMvc, AssertJ, Spring Security Test

---

## 🚀 Key Features Implemented

### 1. Security & Authentication Engine
- User Registration (`POST /api/v1/auth/register`) with username & email uniqueness checks.
- User Authentication (`POST /api/v1/auth/login`) issuing JWT tokens.
- Stateless JWT Authentication Filter intercepting protected requests.
- Role-Based Access Control (`ROLE_USER`, `ROLE_ADMIN`).

### 2. Strategy-Based Algorithm Visualization Engine
- Universal Endpoint: `POST /api/v1/algorithms/{slug}/visualize`
- **Supported Sorting Generators:** Bubble Sort, Selection Sort, Insertion Sort, Merge Sort, Quick Sort.
- **Supported Searching Generators:** Linear Search, Binary Search.
- **Supported Graph Generators:** Breadth-First Search (BFS), Depth-First Search (DFS), Dijkstra.

### 3. Practice Arena & Daily Challenges (Phase 15)
- **Practice Modes:** Daily Challenge, Quick Practice, Topic Focus, Random Shuffle, Timed Sprint, Streak Builder.
- **Practice Arena Hub Endpoint:** `GET /api/v1/practice/arena` (returns active session, daily challenge, streak, XP & session history).
- **Session Lifecycle APIs:**
  - Create Session: `POST /api/v1/practice/sessions`
  - Fetch Session: `GET /api/v1/practice/sessions/{id}`
  - Submit Problem in Session: `POST /api/v1/practice/sessions/{id}/submit`
  - Abandon Session: `POST /api/v1/practice/sessions/{id}/abandon`
  - Session History: `GET /api/v1/practice/history`

### 4. Analytics, XP, Streaks & Gamification
- Daily activity tracking & GitHub-style heatmap stats.
- Streak & longest streak calculation.
- XP Ledger, level progression, and badge definitions.
- Dynamic leaderboard foundation (`GET /api/v1/analytics/leaderboard`).

---

## 🧪 Testing & Verification

Run the full Maven backend test suite:

```bash
./mvnw clean test
```

Expected Output:

```text
[INFO] Results:
[INFO] 
[INFO] Tests run: 74, Failures: 0, Errors: 0, Skipped: 0
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```

---

## 🗺️ Project Roadmap Status

- [x] **Phase 1:** Backend Foundation, Auth & JWT Security
- [x] **Phase 2:** Public Category & Algorithm Domain
- [x] **Phase 3:** Admin Management & Seed Data
- [x] **Phase 4:** Generic Visualization Architecture
- [x] **Phase 5:** Sorting Algorithm Generators
- [x] **Phase 6:** Searching & Graph Visualization
- [x] **Phase 7:** User Progress & Favorites
- [x] **Phase 8:** OpenAPI & Swagger Documentation
- [x] **Phase 9:** Frontend Application & Visualization Player
- [x] **Phase 10:** Production Readiness & Deployment
- [x] **Phase 11:** Rich Algorithm Learning Content
- [x] **Phase 12:** LeetCode-Style Problem Practice Engine
- [x] **Phase 14:** Learning Analytics, Streaks, XP & Badges
- [x] **Phase 15:** Practice Arena Infrastructure & Session Runner
