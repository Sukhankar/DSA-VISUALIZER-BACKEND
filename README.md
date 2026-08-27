# CodeLoom DSA Visualizer — Backend Service

> *"Understand Algorithms. See Every Step. Master the Logic."*

High-performance, RESTful Spring Boot service powering the **CodeLoom DSA Visualizer**. Provides secure user authentication, algorithm catalog management, role-based administration, versioned Flyway migrations, and step-by-step algorithm visualization engine.

---

## 🛠️ Technology Stack

- **Language & Runtime:** Java 21 (LTS)
- **Framework:** Spring Boot 3.4+ / 4.x
- **Security:** Spring Security, JWT (JSON Web Tokens), BCrypt Password Hashing
- **Database:** PostgreSQL 16
- **Database Migrations:** Flyway (Versioned & Idempotent)
- **Cache & Message Broker:** Redis, Apache Kafka (via Docker Compose)
- **Build System:** Apache Maven 3.9+
- **Testing:** JUnit 5, MockMvc, AssertJ

---

## 🚀 Key Features Implemented

### 1. Security & Authentication Engine
- User Registration (`POST /api/v1/auth/register`) with username & email uniqueness checks.
- User Authentication (`POST /api/v1/auth/login`) issuing JWT tokens.
- Stateless JWT Authentication Filter intercepting protected requests.
- Role-Based Access Control (`ROLE_USER`, `ROLE_ADMIN`).
- Global Exception Handler returning standardized JSON error payloads.

### 2. Algorithm Catalog & Public Domain APIs
- Public Categories Endpoint (`GET /api/v1/categories`).
- Public Algorithms Endpoint (`GET /api/v1/algorithms`) supporting:
  - Full-text search (`?search=sort`)
  - Category filtering (`?category=sorting`)
  - Difficulty filtering (`?difficulty=EASY`)
  - Dynamic pagination & sorting (`?page=0&size=10&sortBy=name`)
- Single Algorithm lookup by slug (`GET /api/v1/algorithms/{slug}`).

### 3. Admin Management Subsystem
- Secured via `@PreAuthorize("hasRole('ADMIN')")`.
- Category CRUD (`POST`, `PUT`, `DELETE /api/v1/admin/categories`).
- Safe deletion validation preventing category deletion while referenced by algorithms.
- Algorithm CRUD (`POST`, `PUT`, `DELETE /api/v1/admin/algorithms`).

### 4. Seeded Catalog
- **Categories:** Sorting, Searching, Data Structures, Trees, Graphs, Dynamic Programming.
- **Algorithms:** Bubble Sort, Selection Sort, Insertion Sort, Merge Sort, Quick Sort, Linear Search, Binary Search, Two Sum, Kadane's Algorithm, Linked List Traversal, BST, Tree Traversal, BFS, DFS, Dijkstra's Algorithm, Fibonacci DP.

### 5. Strategy-Based Algorithm Visualization Engine
- Universal Endpoint: `POST /api/v1/algorithms/{slug}/visualize`
- **Supported Sorting Generators:** Bubble Sort, Selection Sort, Insertion Sort, Merge Sort, Quick Sort.
- **Supported Searching Generators:** Linear Search (requires target), Binary Search (requires ascending sorted input & target).
- **Supported Graph Generators:** Breadth-First Search, Depth-First Search (supports Graph DTO payload with `nodes`, `edges`, `startNode`).
- **Action Types:** `INITIAL`, `SELECT`, `COMPARE`, `SWAP`, `UPDATE`, `NO_SWAP`, `VISIT`, `INSERT`, `FOUND`, `NOT_FOUND`, `COMPLETE`.

---

## 📡 API Usage Examples

### 1. Sorting Request (`POST /api/v1/algorithms/quick-sort/visualize`)

```json
{
  "input": [5, 1, 4, 2, 8]
}
```

### 2. Searching Request (`POST /api/v1/algorithms/binary-search/visualize`)

```json
{
  "input": [1, 3, 5, 7, 9, 11],
  "target": 7
}
```

### 3. Graph Traversal Request (`POST /api/v1/algorithms/breadth-first-search/visualize`)

```json
{
  "graph": {
    "nodes": ["A", "B", "C", "D"],
    "edges": [
      { "from": "A", "to": "B" },
      { "from": "A", "to": "C" },
      { "from": "B", "to": "D" }
    ],
    "startNode": "A"
  }
}
```

---

## 🧪 Testing & Verification

Run the comprehensive Maven test suite:

```bash
./mvnw clean test
```

Expected Output:

```text
[INFO] Results:
[INFO] 
[INFO] Tests run: 45, Failures: 0, Errors: 0, Skipped: 0
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```

---

## 🗺️ Project Roadmap

- [x] **Phase 1:** Backend Foundation, Auth & Security
- [x] **Phase 2:** Public Category & Algorithm Domain
- [x] **Phase 3:** Admin Management & CRUD APIs
- [x] **Phase 3B:** Seed Baseline DSA Data (Flyway Migrations)
- [x] **Phase 4:** Visualization Strategy Architecture
- [x] **Phase 5:** Core Visualization Strategy Generators (Sorting, DP, Trees, Lists)
- [x] **Phase 6:** Searching, Graph Visualization Architecture, BFS, DFS & Validation
- [ ] **Phase 7:** User Progress, Bookmarks & Execution History
- [ ] **Phase 8:** OpenAPI / Swagger Documentation
- [ ] **Phase 9:** Frontend Integration
- [ ] **Phase 10:** Docker Production & CI/CD
