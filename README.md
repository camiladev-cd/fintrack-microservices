# 💰 FinTrack Microservices

![CI](https://github.com/camiladev-cd/fintrack-microservices/actions/workflows/ci.yml/badge.svg)
![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.5-brightgreen)
![Kafka](https://img.shields.io/badge/Apache%20Kafka-7.6.0-black)
![Docker](https://img.shields.io/badge/Docker-Compose-2496ED)
![Kubernetes](https://img.shields.io/badge/Kubernetes-manifests-326CE5)

> **EN:** FinTech backend built with microservices architecture — JWT auth, optimistic locking, Kafka events, Prometheus monitoring and Kubernetes deployment.
>
> **ES:** Backend FinTech construido con arquitectura de microservicios — autenticación JWT, bloqueo optimista, eventos Kafka, monitoreo con Prometheus y despliegue en Kubernetes.

---

## 🏗️ Architecture / Arquitectura

```
Client → API Gateway (8080)
              ├── Auth Service (8081)        → PostgreSQL fintrack_auth
              ├── User Service (8082)        → PostgreSQL fintrack_users
              ├── Wallet Service (8083)      → PostgreSQL fintrack_wallets
              └── Transaction Service (8084) → PostgreSQL fintrack_transactions
                            ↓
                   Kafka Topic: transaction-created
                            ↓
              Notification Service (8085)
```

---

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.2.5 |
| Gateway | Spring Cloud Gateway |
| Security | JWT (JJWT 0.12.5) |
| Messaging | Apache Kafka (Confluent 7.6.0) |
| Cache / Blacklist | Redis 7 |
| Database | PostgreSQL 15 |
| Monitoring | Prometheus + Grafana |
| Containerization | Docker + Docker Compose |
| Orchestration | Kubernetes |
| CI/CD | GitHub Actions |
| Build | Maven multi-module |

---

## 📦 Services / Servicios

### API Gateway — port 8080
**EN:** Single entry point. Validates JWT and injects `X-User-Id` and `X-User-Email` headers to downstream services.

**ES:** Punto de entrada único. Valida el JWT e inyecta los headers `X-User-Id` y `X-User-Email` a los servicios downstream.

### Auth Service — port 8081
**EN:** Register, login, refresh token and logout with Redis blacklist.

**ES:** Registro, login, refresh token y logout con blacklist en Redis.

### User Service — port 8082
**EN:** User profile management. Profile ID matches the JWT `userId` claim — no inter-service calls needed.

**ES:** Gestión de perfil de usuario. El ID del perfil coincide con el claim `userId` del JWT — sin llamadas entre servicios.

### Wallet Service — port 8083
**EN:** Wallet management with **optimistic locking** (`@Version`) to prevent concurrent balance corruption. Auto-retry with `@Retryable` on conflict.

**ES:** Gestión de billeteras con **bloqueo optimista** (`@Version`) para prevenir corrupción de saldos concurrentes. Reintento automático con `@Retryable` en conflicto.

### Transaction Service — port 8084
**EN:** Records transactions and publishes events to Kafka topic `transaction-created`. Supports pagination.

**ES:** Registra transacciones y publica eventos en el topic Kafka `transaction-created`. Soporta paginación.

---

## 🚀 Getting Started / Inicio Rápido

### Prerequisites / Requisitos
- Java 21+
- Maven 3.8+
- Docker Desktop

### Run / Ejecutar

```bash
# Clone / Clonar
git clone https://github.com/camiladev-cd/fintrack-microservices.git
cd fintrack-microservices

# Start infrastructure / Levantar infraestructura
docker compose up -d

# Run Auth Service / Ejecutar Auth Service
mvn spring-boot:run -pl auth-service
```

---

## 📡 API Endpoints

### Auth Service — `http://localhost:8081/swagger-ui/index.html`

| Method | Endpoint | Description | Auth |
|---|---|---|---|
| POST | `/api/v1/auth/register` | Register new user | ❌ |
| POST | `/api/v1/auth/login` | Login and get tokens | ❌ |
| POST | `/api/v1/auth/refresh` | Refresh access token | ❌ |
| POST | `/api/v1/auth/logout` | Logout and blacklist token | ✅ |

### Wallet Service — `http://localhost:8083/swagger-ui/index.html`

| Method | Endpoint | Description | Auth |
|---|---|---|---|
| POST | `/api/v1/wallets` | Create wallet | ✅ |
| GET | `/api/v1/wallets` | Get all wallets | ✅ |
| POST | `/api/v1/wallets/{id}/deposit` | Deposit | ✅ |
| POST | `/api/v1/wallets/{id}/withdraw` | Withdraw | ✅ |

### Transaction Service — `http://localhost:8084/swagger-ui/index.html`

| Method | Endpoint | Description | Auth |
|---|---|---|---|
| POST | `/api/v1/transactions` | Create transaction | ✅ |
| GET | `/api/v1/transactions` | Get transactions (paginated) | ✅ |
| GET | `/api/v1/transactions/{id}` | Get transaction by ID | ✅ |

---

## 🔐 Authentication / Autenticación

**EN:** All protected endpoints require a Bearer token in the `Authorization` header. The token is obtained from `/api/v1/auth/login`.

**ES:** Todos los endpoints protegidos requieren un Bearer token en el header `Authorization`. El token se obtiene de `/api/v1/auth/login`.

```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

JWT claims:
- `subject` — email
- `userId` — user ID
- `role` — USER or ADMIN
- `exp` — expiration (15 min for access token)

---

## 🐳 Infrastructure / Infraestructura

| Service | Port | URL |
|---|---|---|
| API Gateway | 8080 | `http://localhost:8080` |
| Auth Service | 8081 | `http://localhost:8081/swagger-ui/index.html` |
| Wallet Service | 8083 | `http://localhost:8083/swagger-ui/index.html` |
| Transaction Service | 8084 | `http://localhost:8084/swagger-ui/index.html` |
| PostgreSQL | 5432 | — |
| Redis | 6379 | — |
| Kafka | 9092 | — |
| Kafka UI | 8090 | `http://localhost:8090` |
| pgAdmin | 5050 | `http://localhost:5050` |
| Prometheus | 9090 | `http://localhost:9090` |
| Grafana | 3000 | `http://localhost:3000` |

---

## ✅ Key Technical Decisions / Decisiones Técnicas Clave

**EN:**
- **Optimistic locking** with `@Version` on Wallet entity prevents concurrent balance corruption without database-level locks
- **Redis blacklist** for JWT logout — tokens are invalidated server-side before expiration
- **Gateway header injection** — downstream services never touch the JWT, they receive `X-User-Id` and `X-User-Email` as trusted headers
- **Separate databases** per service — true data isolation following microservices best practices
- **Kafka events** decouple transaction recording from wallet balance updates

**ES:**
- **Bloqueo optimista** con `@Version` en la entidad Wallet previene corrupción de saldos concurrentes sin bloqueos a nivel de base de datos
- **Blacklist en Redis** para logout JWT — los tokens se invalidan del lado del servidor antes de expirar
- **Inyección de headers en Gateway** — los servicios downstream nunca tocan el JWT, reciben `X-User-Id` y `X-User-Email` como headers de confianza
- **Bases de datos separadas** por servicio — aislamiento real de datos siguiendo mejores prácticas de microservicios
- **Eventos Kafka** desacoplan el registro de transacciones de la actualización de saldos

---

## 👩‍💻 Author / Autora

**Camila Dueñas** — Backend Developer · Java · Spring Boot · FinTech

🔗 [LinkedIn](https://linkedin.com/in/camiladuenas-dev) · [GitHub](https://github.com/camiladev-cd)

---

## 📄 License

MIT License
