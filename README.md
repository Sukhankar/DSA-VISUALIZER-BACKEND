# ⚡ CodeLoom Backend — The Engine Under the Grand Line

### High-Performance Algorithm Execution Engine & Async Event Stream

<div align="center">

  <a href="https://git.io/typing-svg">
    <img src="https://readme-typing-svg.demolab.com?font=Fira+Code&weight=600&size=24&pause=1000&color=6DB33F&center=true&vCenter=true&width=650&lines=Universal+Algorithm+Execution+Engine;Spring+Boot+3.4+%2B+Java+21+LTS;Apache+Kafka+Async+Event+Streaming;PostgreSQL+%2B+Flyway+Automated+Migrations" alt="Typing SVG Header" />
  </a>

  <br/>
  <br/>

  [![Java 21](https://img.shields.io/badge/Java-21_LTS-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
  [![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.4+-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
  [![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16+-4169E1?style=for-the-badge&logo=postgresql&logoColor=white)](https://www.postgresql.org/)
  [![Apache Kafka](https://img.shields.io/badge/Apache_Kafka-231F20?style=for-the-badge&logo=apachekafka&logoColor=white)](https://kafka.apache.org/)
  [![Flyway](https://img.shields.io/badge/Flyway-Migration-CC0200?style=for-the-badge&logo=flyway&logoColor=white)](https://flywaydb.org/)
  [![Docker](https://img.shields.io/badge/Docker-Ready-2496ED?style=for-the-badge&logo=docker&logoColor=white)](https://www.docker.com/)
  [![License](https://img.shields.io/badge/License-MIT-green.svg?style=for-the-badge)](LICENSE)

  <br/>

  > *Powering the CodeLoom voyage with step-by-step algorithm snapshots, secure JWT authentication, session state evaluation, and high-throughput Kafka event streaming.*

</div>

---

## 🌊 The Engine Room: Where Algorithms Come Alive

Every great ship needs a powerhouse engine under the deck.

The **CodeLoom Backend Core** is an enterprise-grade RESTful service built with **Java 21** and **Spring Boot 3.4+**. It doesn't mock execution — it dynamically runs algorithmic logic, generating step-by-step state snapshot arrays (pointers, comparisons, swaps, graph node traversals, and DP grids) sent directly to the frontend renderers.

---

## 🌟 CAPABILITIES AT A GLANCE

<table width="100%">
  <tr>
    <td width="33%" align="center">
      <h3>🎬 Universal Generators</h3>
      <p><code>⚡ SNAPSHOT ENGINE</code></p>
      <p>Strategy registry executing step-by-step state snapshot arrays across Sorting, Searching, Graphs & Smart Fallback generators.</p>
    </td>
    <td width="33%" align="center">
      <h3>📡 Event Streaming</h3>
      <p><code>📡 APACHE KAFKA BUS</code></p>
      <p>Asynchronous event processing for practice evaluation results, user streak updates & analytics telemetry.</p>
    </td>
    <td width="33%" align="center">
      <h3>🔒 Enterprise Security</h3>
      <p><code>🔑 JWT + FLYWAY DB</code></p>
      <p>Stateless Spring Security JWT authorization filter with BCrypt password hashing & Flyway idempotent schema versioning.</p>
    </td>
  </tr>
</table>

---

## 🏛️ SYSTEM ARCHITECTURE

```mermaid
graph TD
    Client[🌐 React Web Client] -->|REST / JSON + Bearer JWT| Security[🔒 Spring Security Filter Chain]
    
    subgraph Security Layer
        Security --> JWTFilter[JWT Authentication Filter]
        JWTFilter --> SecurityContext[Security Context Provider]
    end
    
    SecurityContext --> Controllers[🕹️ REST Controller Layer]
    
    subgraph Application Core Services
        Controllers --> AuthSvc[🔑 Auth Service]
        Controllers --> VisSvc[🎬 Visualization Service]
        Controllers --> PracticeSvc[🎯 Practice Arena Service]
        Controllers --> AnalyticsSvc[📊 Analytics & XP Service]
        Controllers --> ProblemSvc[💻 Problem Service]
    end

    subgraph Strategy-Based Visualization Pipeline
        VisSvc --> Registry[⚡ Generator Strategy Registry]
        Registry --> ArrayGen[📊 Array Generators - Sorting & Searching]
        Registry --> GraphGen[🕸️ Graph Generators - BFS, DFS, Dijkstra]
        Registry --> SmartFallback[🧠 Smart Fallback Pedagogical Generator Engine]
    end

    subgraph Event Streaming Infrastructure
        AnalyticsSvc & PracticeSvc -.-> Kafka[📡 Apache Kafka Broker / Topic Stream]
    end

    subgraph Data & Persistence Infrastructure
        AuthSvc & VisSvc & PracticeSvc & AnalyticsSvc & ProblemSvc --> JPA[🗄️ Spring Data JPA Repositories]
        JPA --> DB[(🐘 PostgreSQL Database)]
        Flyway[🔄 Flyway Engine V1 - V22] -->|Automated Schema & Seed Migrations| DB
    end
```

---

## ⚡ CORE ENGINE MODULES

<details open>
<summary><b>🎬 1. Universal Visualization Strategy Engine</b></summary>
<br/>

- **Universal Snapshot API**: `POST /api/v1/algorithms/{slug}/visualize` generating structured state snapshot sequences.
- **Native Generators**:
  - **Sorting**: Bubble Sort, Selection Sort, Insertion Sort, Merge Sort, Quick Sort.
  - **Searching**: Linear Search, Binary Search.
  - **Graphs**: Breadth-First Search (BFS), Depth-First Search (DFS), Dijkstra's Algorithm.
- **Smart Category Fallback**: Dynamic fallback engine providing pedagogical step states for all 218 catalog algorithms.
</details>

<details open>
<summary><b>🎯 2. Practice Arena & Session State Manager</b></summary>
<br/>

- **6 Practice Modes**: Quick Practice, Timed Sprint, Topic Focus, Streak Builder, Random Shuffle, Daily Challenge.
- **Session Lifecycle APIs**: Session initialization, active session resuming, submission evaluation, and completion validation (`/api/v1/practice/sessions/*`).
</details>

<details open>
<summary><b>📊 3. Analytics, XP Ledger & Kafka Event Bus</b></summary>
<br/>

- **Event Bus Integration**: **Apache Kafka** event streaming for practice evaluation submissions, XP events, and telemetry.
- **Gamification Engine**: XP calculation curves, streak protection mechanics, badge triggers, and GitHub-style heatmap data generation.
- **Leaderboards**: Ranked user leaderboard endpoint (`GET /api/v1/analytics/leaderboard`).
</details>

---

## 💻 TECH STACK

| Component | Technology | Version | Role & Description |
| :--- | :--- | :--- | :--- |
| **Language** | Java | 21 (LTS) | Modern Java features (Virtual Threads, Records, Sealed Classes) |
| **Framework** | Spring Boot | 3.4.3 | Enterprise REST framework |
| **Security** | Spring Security | 6.x | Stateless JWT authentication & BCrypt password hashing |
| **Messaging** | Apache Kafka | 3.x | Distributed event stream bus (`spring-boot-starter-kafka`) |
| **Database** | PostgreSQL | 16+ | Production relational persistence store |
| **Migrations** | Flyway | 10.x | Versioned database schema & seed migrations (`V1` to `V22`) |
| **Containerization** | Docker | 24+ | Multi-stage Alpine JVM container runtime |
| **Build Tool** | Apache Maven | 3.9+ | Dependency management & compilation |
| **Testing** | JUnit 5 & MockMvc | 5.x | Unit & integration testing suite |

---

## 📡 REST API REFERENCE

<details>
<summary><b>📋 View Key REST Endpoints</b></summary>
<br/>

| Endpoint | Method | Security | Description |
| :--- | :---: | :---: | :--- |
| `/api/v1/auth/register` | `POST` | Public | Register new user account |
| `/api/v1/auth/login` | `POST` | Public | Authenticate user & return JWT token |
| `/api/v1/algorithms` | `GET` | Public | Fetch algorithm catalog |
| `/api/v1/algorithms/{slug}` | `GET` | Public | Get detailed algorithm info & code snippets |
| `/api/v1/algorithms/{slug}/visualize` | `POST` | Public | Generate step-by-step snapshot animations |
| `/api/v1/practice/arena` | `GET` | User | Get user practice arena hub data |
| `/api/v1/practice/sessions` | `POST` | User | Start a new practice session |
| `/api/v1/practice/sessions/{id}/submit` | `POST` | User | Submit problem within active session |
| `/api/v1/analytics/stats` | `GET` | User | Fetch user streak, XP, heatmap & badge stats |
| `/api/v1/analytics/leaderboard` | `GET` | Public | Global user XP leaderboard ranking |
</details>

---

## ⚡ QUICK START GUIDE

### Prerequisites
- **Java 21** or higher
- **Maven 3.9+** (or included `./mvnw`)
- **PostgreSQL 16+** running on `localhost:5432`
- **Apache Kafka** running on `localhost:9092` (optional)

### 1. Database Setup
```sql
CREATE DATABASE dsa_visualizer;
```

### 2. Execution Commands
```bash
# Clone backend repository
git clone https://github.com/Sukhankar/DSA-VISUALIZER-BACKEND.git
cd DSA-VISUALIZER-BACKEND

# Configure Environment Variables
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/dsa_visualizer
export SPRING_DATASOURCE_USERNAME=postgres
export SPRING_DATASOURCE_PASSWORD=postgres
export JWT_SECRET=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
export KAFKA_SERVERS=localhost:9092

# Run Application
./mvnw clean spring-boot:run
```

Server starts on **`http://localhost:8080`**.

---

## 🐳 DOCKER DEPLOYMENT

```bash
# 1. Build Docker image
docker build -t codeloom-backend .

# 2. Run Docker container
docker run -d -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/dsa_visualizer \
  -e SPRING_DATASOURCE_USERNAME=postgres \
  -e SPRING_DATASOURCE_PASSWORD=postgres \
  -e JWT_SECRET=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970 \
  -e KAFKA_SERVERS=host.docker.internal:9092 \
  --name dsa-backend codeloom-backend
```

---

## 🧪 TESTING

```bash
./mvnw clean test
```

---

## 📜 LICENSE

Distributed under the **MIT License**.
