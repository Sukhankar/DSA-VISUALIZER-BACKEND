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

### 4. Flyway Seed Baseline (8 Categories, 18 Algorithms)
- **Categories:** Sorting, Searching, Arrays, Linked Lists, Trees, Graphs, Dynamic Programming, Greedy.
- **Algorithms:** Bubble Sort, Selection Sort, Insertion Sort, Merge Sort, Quick Sort, Linear Search, Binary Search, Two Sum, Kadane's Algorithm, Linked List Traversal, BST, Tree Traversal, BFS, DFS, Dijkstra's, Fibonacci DP, LCS, Activity Selection.

### 5. Strategy-Based Algorithm Visualization Engine
- Universal Endpoint: `POST /api/v1/algorithms/{slug}/visualize`
- **Supported Sorting Generators (Phase 5A):**
  - **Bubble Sort** (`bubble-sort`)
  - **Selection Sort** (`selection-sort`)
  - **Insertion Sort** (`insertion-sort`)
  - **Merge Sort** (`merge-sort`)
  - **Quick Sort** (`quick-sort`)
- Action Types: `INITIAL`, `SELECT`, `COMPARE`, `SWAP`, `UPDATE`, `NO_SWAP`, `COMPLETE`.
- Defensive Copying: Every step includes an immutable array snapshot.

---

## 📡 API Usage Examples

### Request Visualization (`POST /api/v1/algorithms/quick-sort/visualize`)

```http
POST /api/v1/algorithms/quick-sort/visualize
Content-Type: application/json

{
  "input": [5, 1, 4, 2, 8]
}
```

### Response Payload

```json
{
  "algorithm": "quick-sort",
  "visualizationType": "ARRAY",
  "steps": [
    {
      "step": 1,
      "action": "INITIAL",
      "indices": [],
      "array": [5, 1, 4, 2, 8],
      "message": "Initial array state"
    },
    {
      "step": 2,
      "action": "SELECT",
      "indices": [4],
      "array": [5, 1, 4, 2, 8],
      "message": "Selected pivot 8 at index 4 for subarray [0..4]"
    },
    {
      "step": 3,
      "action": "COMPARE",
      "indices": [0, 4],
      "array": [5, 1, 4, 2, 8],
      "message": "Comparing element 5 at index 0 with pivot 8 at index 4"
    },
    ...
    {
      "step": 18,
      "action": "COMPLETE",
      "indices": [],
      "array": [1, 2, 4, 5, 8],
      "message": "Quick Sort completed! Array is fully sorted."
    }
  ]
}
```

---

## 🧪 Testing & Verification

Run the comprehensive Maven test suite (46 passing tests):

```bash
./mvnw clean test
```

Expected Output:

```text
[INFO] Results:
[INFO] 
[INFO] Tests run: 46, Failures: 0, Errors: 0, Skipped: 0
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
- [x] **Phase 5A:** Sorting Algorithm Generators (Bubble, Selection, Insertion, Merge, Quick)
- [x] **Phase 5B:** Searching Generators (Linear Search, Binary Search)
- [x] **Phase 5C:** Array & DP Generators (Two Sum, Kadane's Algorithm, Fibonacci DP)
- [x] **Phase 5D:** Linked List Generators (Linked List Traversal)
- [x] **Phase 5E:** Tree & Graph Generators (BST, Tree Traversal, BFS, DFS, Dijkstra's)
- [ ] **Phase 6:** Advanced Custom Inputs & Constraints
- [ ] **Phase 7:** User Progress, Bookmarks & Execution History
- [ ] **Phase 8:** OpenAPI / Swagger Documentation
- [ ] **Phase 9:** Frontend Integration
- [ ] **Phase 10:** Docker Production & CI/CD
