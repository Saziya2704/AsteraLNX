<<<<<<< HEAD
# AsteraLNX — Enterprise Logistics Management Platform

Internal logistics system for managing customers, orders, warehouses, shipments, drivers, GPS tracking, invoices and reports.

**Stack:** Java 17 · Spring Boot · Spring Security (JWT + RBAC) · Spring Data JPA · MySQL · OpenPDF · Bootstrap 5 · Leaflet maps · Docker · Swagger UI

**Testing:** Postman only (no JUnit / Mockito). Collection available in `postman/AsteraLNX.postman_collection.json`.

**Schema:** Hibernate `ddl-auto` (no Flyway).

---

## Production business rules

- **No stock → no shipment** from warehouse (commodity + quantity required; insufficient qty rejected)
- **Cancel shipment** restores stock and warehouse capacity
- **Cannot deactivate commodity** with remaining stock or open shipments
- **Cannot delete warehouse** while stock or open shipments exist
- **Stock transfer** between warehouses (source must hold qty; completes in one transaction)
- Soft delete + RBAC on all critical modules

## Features

- **4 roles:** ADMIN, OPERATIONS_MANAGER, WAREHOUSE_MANAGER, DRIVER (backend-enforced RBAC)
- Full workflow: Inquiry → Quote → Order → Booking → Warehouse/Receipt → Shipment → Trip → GPS → **POD** → Invoice
- Status transition rules for Order, Booking, Shipment
- JWT auth (30 min), BCrypt, soft deletes, audit fields, pagination
- **Stock Transfers:** inter-warehouse inventory movement; cancel shipment restores stock
- **Proof of Delivery** on deliver
- **Invoicing:** issue, overdue detection, mark paid, cancel
- **Reports:** summary KPIs + CSV/PDF export
- **GPS Tracking:** Simulated path + Leaflet map view
- Role-specific dashboard KPIs
- Professional UI (dark sidebar, responsive)
- Actuator health, Swagger UI, Docker Compose
- Docs under `docs/` · Postman under `postman/`

---

## 1. Run with Docker (recommended)

### Requirements
- Docker Desktop (Windows/Mac) or Docker Engine + Compose (Linux)

### Steps
```bash
# 1. Unzip the project
unzip ASTERA_LNX_COMPLETE.zip
cd asteralnx

# 2. Start MySQL + App
docker compose up -d --build

# 3. Wait ~60–90 seconds for MySQL health + app start
docker compose logs -f app
```

Open browser:
- **UI:** http://localhost:8080  
- **Swagger:** http://localhost:8080/swagger-ui.html  
- **Health:** http://localhost:8080/actuator/health  

**Login (seeded on first start)**
| Email | Password | Role |
|-------|----------|------|
| admin@asteralnx.com | Astera@123 | ADMIN |
| ops@asteralnx.com | Ops@12345 | OPERATIONS_MANAGER |
| warehouse@asteralnx.com | Wh@12345 | WAREHOUSE_MANAGER |
| driver@asteralnx.com | Driver@123 | DRIVER |

Stop:
```bash
docker compose down
```

---

## 2. Run locally (without Docker)

### Requirements
- JDK 17+
- Maven 3.9+
- MySQL 8

### Steps
```bash
# Create database
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS astera_lnx;"

# Edit credentials if needed
# src/main/resources/application.properties
#   spring.datasource.username=root
#   spring.datasource.password=YOUR_PASSWORD

# Build & run
cd asteralnx
mvn spring-boot:run
```

Open http://localhost:8080 and login as above.

---

## 3. How to use the UI

1. Login → Dashboard (KPIs)
2. Sidebar changes by role
3. **Reports** (Admin) → click **CSV** or **PDF** on any card → file downloads
4. **Tracking** → enter a Shipment ID → **Simulate path** → map shows route (Chennai area sample) → markers with status
5. All list pages load data from APIs with status badges

---

## 4. Main API groups

| Area | Base path | Roles |
|------|-----------|-------|
| Auth | `/api/auth/*` | Public login |
| Customers | `/api/customers` | ADMIN, OPS |
| Orders | `/api/orders` | ADMIN, OPS (+ WH read) |
| Shipments | `/api/shipments` | ADMIN, OPS |
| Tracking | `/api/tracking/shipments/{id}` | ADMIN, OPS |
| Reports | `/api/reports/{type}/csv` or `/pdf` | ADMIN |
| Warehouses | `/api/warehouses` | ADMIN, WH |
| Drivers / Trips | `/api/drivers`, `/api/trips` | ADMIN, OPS |
| My work | `/api/shipments/mine`, `/api/trips/mine` | DRIVER |

---

## 5. Testing (Postman only)

Automated unit/integration tests (JUnit, Mockito) are **not** used.

1. Import `postman/AsteraLNX.postman_collection.json` into Postman.
2. Set collection variable `baseUrl` to `http://localhost:8080` (default).
3. Run **Auth → Login** first — the token is saved automatically.
4. Execute subsequent requests; they use `Bearer {{token}}`.
5. Expand the collection with more folders as modules are stabilized.

Swagger UI remains available at http://localhost:8080/swagger-ui.html for interactive exploration.

## 6. Project layout

```
asteralnx/
├── docs/                  ← Project documentation
├── postman/               ← Postman collections
├── Dockerfile
├── docker-compose.yml
├── pom.xml
├── README.md
└── src/main/
    ├── java/com/newasteralnx/
    │   ├── config / security / constant
    │   ├── controller / service / repository
    │   ├── entity / dto / exception / util
    └── resources/
        ├── application.properties
        └── static/          ← UI (index.html, app.html, css/, js/)
```

---

## 6. Default ports

| Service | Port |
|---------|------|
| App | 8080 |
| MySQL (Docker) | 3307 (host) → 3306 (container) |

---

## 7. Documentation

See `docs/INDEX.md` for the full set. Start with:

- `docs/10-e2e-postman-verification.md` — end-to-end API walkthrough  
- `docs/11-known-limitations-and-future.md` — scope boundaries  
- `docs/04-role-permission-matrix.md` — roles  


## Password reset email (SMTP)

Forgot password sends an email with a **6-digit OTP** and a **reset link**.

| Variable | Example | Notes |
|----------|---------|--------|
| `MAIL_HOST` | `smtp.gmail.com` | Leave empty to **log email to console** (local demo) |
| `MAIL_PORT` | `587` | |
| `MAIL_USERNAME` | your@gmail.com | |
| `MAIL_PASSWORD` | app password | Use Gmail App Password, not account password |
| `MAIL_FROM` | noreply@asteralnx.com | |
| `APP_PUBLIC_BASE_URL` | `http://localhost:8080` | Used in the reset link |

Example (Gmail):

```bash
export MAIL_HOST=smtp.gmail.com
export MAIL_PORT=587
export MAIL_USERNAME=you@gmail.com
export MAIL_PASSWORD=your-app-password
export MAIL_FROM=you@gmail.com
export APP_PUBLIC_BASE_URL=http://localhost:8080
mvn spring-boot:run
```

Without SMTP configured, the OTP and link are printed in the **application log** so local testing still works.

## Troubleshooting

| Issue | Fix |
|-------|-----|
| App can't connect to DB | Wait for MySQL healthy: `docker compose ps` |
| 401 on APIs | Re-login; token expires in 30 min |
| Blank lists | Create data via Swagger or Postman |
| Map not showing | Allow unpkg.com / OpenStreetMap tiles; use Chrome |
| PDF empty | Create some shipments/orders first |
| DRIVER mine empty | Ensure user has DRIVER role and linked Driver profile |

---

Built as a portfolio / interview-ready enterprise logistics monolith.
=======
# AsteraLNX
>>>>>>> 5c20528c36abf3a2975b8ce0af4fd560fc9b574e
