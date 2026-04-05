# Finance Data Processing and Access Control Backend

A backend system for a finance dashboard supporting role-based access control, financial record management, and summary analytics. Built with Spring Boot 3, PostgreSQL, and JWT authentication.

> **Assignment note:** This project was originally developed as a Payment Gateway Simulation and directly maps to the Finance Data Processing assignment requirements — it covers user/role management, financial record CRUD, dashboard summary APIs, JWT-based access control, validation, and Docker-based deployment.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.4.4 |
| Security | Spring Security + JWT (jjwt 0.11.5) |
| Database | PostgreSQL |
| ORM | Spring Data JPA / Hibernate |
| Build | Maven |
| Containerization | Docker |
| Utilities | Lombok |

---

## Features

### 1. User and Role Management
- Register and manage users with assigned roles
- Three roles supported: `ADMIN`, `ANALYST`, `VIEWER`
- Activate or deactivate users
- Role-based restrictions enforced at the API layer via Spring Security

### 2. Financial Records Management
- Full CRUD for financial entries (transactions)
- Each record includes: amount, type (INCOME/EXPENSE), category, date, notes
- Filter records by date range, category, and type
- Soft-delete support to preserve audit history

### 3. Dashboard Summary APIs
- Total income and total expenses
- Net balance calculation
- Category-wise breakdown
- Monthly trends
- Recent activity feed

### 4. Access Control
| Action | VIEWER | ANALYST | ADMIN |
|---|---|---|---|
| View records | ✓ | ✓ | ✓ |
| View dashboard summary | ✓ | ✓ | ✓ |
| Filter and search records | ✗ | ✓ | ✓ |
| Create / update records | ✗ | ✗ | ✓ |
| Delete records | ✗ | ✗ | ✓ |
| Manage users | ✗ | ✗ | ✓ |

Access control is enforced using Spring Security method-level annotations and JWT claims.

### 5. Validation and Error Handling
- Bean Validation (`@Valid`) on all request bodies
- Global exception handler returns consistent JSON error responses
- Appropriate HTTP status codes (400, 401, 403, 404, 409, 500)

---

## Project Structure

```
src/main/java/com/example/
├── controller/
│   ├── AuthController.java
│   ├── UserController.java
│   ├── TransactionController.java
│   └── DashboardController.java
├── service/
│   ├── AuthService.java
│   ├── UserService.java
│   ├── TransactionService.java
│   └── DashboardService.java
├── repository/
│   ├── UserRepository.java
│   └── TransactionRepository.java
├── model/
│   ├── User.java
│   ├── Transaction.java
│   └── enums/
│       ├── Role.java
│       └── TransactionType.java
├── dto/
│   ├── request/
│   └── response/
├── security/
│   ├── JwtUtil.java
│   ├── JwtFilter.java
│   └── SecurityConfig.java
└── exception/
    └── GlobalExceptionHandler.java
```

---

## API Endpoints

### Authentication
| Method | Endpoint | Access | Description |
|---|---|---|---|
| POST | `/api/auth/register` | Public | Register a new user |
| POST | `/api/auth/login` | Public | Login and receive JWT token |

### Users
| Method | Endpoint | Access | Description |
|---|---|---|---|
| GET | `/api/users` | ADMIN | List all users |
| PUT | `/api/users/{id}/role` | ADMIN | Update user role |
| PUT | `/api/users/{id}/status` | ADMIN | Activate/deactivate user |

### Financial Records
| Method | Endpoint | Access | Description |
|---|---|---|---|
| POST | `/api/transactions` | ADMIN | Create a new record |
| GET | `/api/transactions` | ALL | View all records (paginated) |
| GET | `/api/transactions/{id}` | ALL | View a single record |
| PUT | `/api/transactions/{id}` | ADMIN | Update a record |
| DELETE | `/api/transactions/{id}` | ADMIN | Delete a record |
| GET | `/api/transactions/filter` | ANALYST, ADMIN | Filter by date/category/type |

### Dashboard Summary
| Method | Endpoint | Access | Description |
|---|---|---|---|
| GET | `/api/dashboard/summary` | ALL | Total income, expenses, net balance |
| GET | `/api/dashboard/category-breakdown` | ALL | Category-wise totals |
| GET | `/api/dashboard/monthly-trends` | ALL | Monthly income vs expense |
| GET | `/api/dashboard/recent` | ALL | Recent 10 transactions |

---

## Getting Started

### Prerequisites
- Java 17+
- Maven 3.8+
- PostgreSQL (or use Docker Compose)

### Option 1: Run with Docker

```bash
git clone https://github.com/Minu1kumari2/payment-gateway-springboot.git
cd payment-gateway-springboot
docker-compose up --build
```

The API will be available at `http://localhost:8080`.

### Option 2: Run locally

1. Create a PostgreSQL database named `financedb`
2. Update `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/financedb
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
app.jwt.secret=your_jwt_secret_key
app.jwt.expiration=86400000
```

3. Build and run:

```bash
mvn clean install
mvn spring-boot:run
```

---

## Sample Requests

### Register a user
```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "adminuser",
  "email": "admin@example.com",
  "password": "Admin@123",
  "role": "ADMIN"
}
```

### Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "admin@example.com",
  "password": "Admin@123"
}
```
Response includes a `token` field. Use it as `Authorization: Bearer <token>` on subsequent requests.

### Create a financial record
```http
POST /api/transactions
Authorization: Bearer <token>
Content-Type: application/json

{
  "amount": 15000.00,
  "type": "INCOME",
  "category": "Salary",
  "date": "2026-04-01",
  "notes": "Monthly salary"
}
```

### Get dashboard summary
```http
GET /api/dashboard/summary
Authorization: Bearer <token>
```
```json
{
  "totalIncome": 45000.00,
  "totalExpenses": 18500.00,
  "netBalance": 26500.00
}
```

---

## Assumptions Made

- Roles are assigned at registration; an ADMIN can update roles afterward.
- All monetary amounts are stored as `DECIMAL(15,2)` in the database.
- Soft delete is used for transactions — deleted records are flagged but not removed from the database.
- JWT tokens expire after 24 hours. Refresh tokens are out of scope for this version.
- The `ANALYST` role can filter and view records but cannot modify them.
- Date filtering uses ISO format (`yyyy-MM-dd`).

## Tradeoffs

- PostgreSQL was chosen over MySQL for better support of window functions used in monthly trend queries.
- No refresh token mechanism is implemented to keep authentication simple and focused on role-based logic.
- Pagination is implemented on record listing but not on dashboard summary endpoints since they return aggregated data.

---

## Optional Enhancements Implemented

- [x] JWT Authentication
- [x] Pagination on record listing
- [x] Filter by date range, category, type
- [x] Soft delete
- [x] Docker support
- [ ] Rate limiting (not implemented)
- [ ] Unit tests (not implemented in this version)

---

## Author

**Minu Kumari**  
B.Tech CSE — Noida Institute of Engineering & Technology (2026)  
minu1kumari2@gmail.com
