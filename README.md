# Personal Finance Tracker — Full Stack (Angular + Spring Boot)

A responsive personal finance tracker with JWT authentication, transaction CRUD,
budget alerts, dashboard charts, and CSV/PDF export.

## Tech Stack
- **Frontend:** Angular 18 (standalone components), ng2-charts (Chart.js)
- **Backend:** Spring Boot 3.3, Spring Security, Spring Data JPA, JJWT
- **Database:** MySQL (default) or PostgreSQL
- **Build tools:** Maven, npm/Angular CLI

## Project Structure
```
finance-tracker/
├── backend/     Spring Boot REST API
└── frontend/    Angular SPA
```

---

## 1. Prerequisites

Install these before you start:
- **Java 17+** — `java -version`
- **Maven 3.9+** — `mvn -version` (or use the included wrapper if you add one)
- **Node.js 18+ and npm** — `node -v` / `npm -v`
- **Angular CLI** — `npm install -g @angular/cli`
- **MySQL 8+** (or PostgreSQL 14+) running locally

---

## 2. Database Setup

**MySQL (default):**
```sql
CREATE DATABASE finance_tracker;
```
The app uses `spring.jpa.hibernate.ddl-auto=update`, so tables are created automatically
on first run — no manual schema needed.

Edit `backend/src/main/resources/application.properties` and set your own
MySQL username/password:
```properties
spring.datasource.username=root
spring.datasource.password=your_password_here
```

**Switching to PostgreSQL:** comment out the MySQL block in `application.properties`
and uncomment the PostgreSQL block right below it (also set
`spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect`).

---

## 3. Run the Backend

```bash
cd finance-tracker/backend
mvn clean install
mvn spring-boot:run
```

The API starts on **http://localhost:8080**.

On first startup, `DataSeeder` automatically inserts default categories
(Salary, Freelance, Food & Dining, Rent, Utilities, etc.) so the app is usable right away.

**Quick test:**
```bash
curl -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{"username":"demo","email":"demo@example.com","password":"password123"}'
```
You should get back a JSON response containing a `token`.

### Key API Endpoints
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/auth/signup` | Create account, returns JWT |
| POST | `/api/auth/login` | Login, returns JWT |
| GET | `/api/categories` | List all categories |
| GET/POST/PUT/DELETE | `/api/transactions` | Transaction CRUD (JWT required) |
| GET/POST/PUT/DELETE | `/api/budgets` | Budget CRUD (JWT required) |
| GET | `/api/dashboard?month=&year=` | Summary + budget status |
| GET | `/api/export/csv?month=&year=` | Download CSV |
| GET | `/api/export/pdf?month=&year=` | Download PDF |

All endpoints except `/api/auth/**` require an `Authorization: Bearer <token>` header.

---

## 4. Run the Frontend

Open a **new terminal** (keep the backend running):

```bash
cd finance-tracker/frontend
npm install
ng serve
```

The app opens on **http://localhost:4200** and proxies API calls to
`http://localhost:8080/api` (configured in `src/environments/environment.ts`).

If your backend runs on a different host/port, update `apiUrl` in
`src/environments/environment.ts` and `environment.prod.ts`.

---

## 5. Using the App

1. Go to `http://localhost:4200` → you'll be redirected to **/login**.
2. Click **Sign up**, create an account.
3. You'll land on the **Dashboard** — income/expense totals, category
   breakdown (doughnut chart), income vs expense (bar chart), and budget
   status bars.
4. **Transactions** page — add/edit/delete income & expense entries with
   category, amount, date, and description.
5. **Budgets** page — set a monthly limit per category; the dashboard and
   budgets page both show progress bars and flag categories that go over budget.
6. Use **Export CSV** / **Export PDF** on the dashboard to download that
   month's report.

The whole UI is responsive — the navbar collapses into a hamburger menu, grids
stack into a single column, and tables convert to stacked cards below 640px width.

---

## 6. Building for Production

**Backend:**
```bash
cd backend
mvn clean package
java -jar target/personal-finance-tracker-1.0.0.jar
```

**Frontend:**
```bash
cd frontend
ng build --configuration production
```
Output goes to `frontend/dist/finance-tracker-frontend` — deploy it to any
static host (Nginx, Netlify, S3, etc.) and point `environment.prod.ts` at your
deployed backend URL.

---

## 7. Common Issues

- **CORS errors:** confirm `app.cors.allowed-origins` in `application.properties`
  matches your frontend's URL (default `http://localhost:4200`).
- **401 on every request:** token expired (default 24h) or missing — log in again.
- **`Access denied for user 'root'@'localhost'`:** fix the MySQL credentials in
  `application.properties`.
- **`ng: command not found`:** run `npm install -g @angular/cli` or use
  `npx ng serve` instead.
- **Port 8080/4200 already in use:** stop the conflicting process, or change
  `server.port` (backend) / pass `--port` to `ng serve` (frontend).
