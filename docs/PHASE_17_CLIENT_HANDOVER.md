# Phase 17 — Client Handover Report

**Product:** AsteraLNX — Enterprise Logistics & Transportation Management System  
**Version:** 1.0 (repository snapshot)  
**Handover type:** Implementation complete (L1) · Runtime acceptance pending (L2)

---

## 1. Project status

| Status | Detail |
|--------|--------|
| **Ready for implementation review** | Yes |
| **Ready for production acceptance** | **No** until client L2 checklist passes |
| Primary reason | Live E2E and production build were not executed in the delivery agent environment |

---

## 2. Architecture summary

| Layer | Choice |
|-------|--------|
| Style | Modular monolith |
| Backend | Java 17, Spring Boot, Spring Security, Spring Data JPA, Validation, Mail |
| API docs | Swagger UI (`/swagger-ui.html`) via springdoc |
| Auth | JWT bearer, BCrypt, stateless |
| Database | MySQL 8, Hibernate `ddl-auto=update` (**no Flyway** — project rule) |
| Frontend | Bootstrap 5 SPA: `index.html` (auth), `app.html` + `app.js` (console) |
| Deploy | Docker Compose (`app` + `mysql`) |
| Testing rule | Postman only (no JUnit/Mockito) |

**Package root:** `com.newasteralnx`  
**Controllers:** Auth, User, Customer, Inquiry, Quote, Order, Booking, Shipment, Trip, Tracking, Warehouse, Commodity, Receipt, StockTransfer, Driver, Vehicle, Invoice, Dashboard, Report  

---

## 3. Modules completed / improved

| Module | Capability |
|--------|------------|
| Auth | Login, logout, remember-me storage, forgot/reset password (mail or console log) |
| Users | ADMIN CRUD, activate/deactivate, last-admin guard |
| Customers | CRUD, status, active-for-transaction checks |
| Inquiries / Quotes | Lifecycle including send/accept/reject; convert to order |
| Orders | Draft→…→Delivered/Cancelled transitions |
| Bookings | Operational booking records |
| Shipments | Create (stock ↓), dispatch, deliver + POD, cancel (stock restore) |
| Trips / Tracking | Assignment trips; tracking points + simulate |
| Warehouse / Commodity | Master data, capacity, qty movements |
| Receipts / Transfers | Inbound and inter-warehouse |
| Drivers / Vehicles | Fleet master + status |
| Invoices | Issue, pay, cancel (no payment gateway) |
| Dashboard | Role KPIs, status charts, work queues, low stock |
| Reports | Admin browse + CSV/PDF export |
| UI shell | RBAC menus, tables (search/filter/sort/page), collapse sidebar, nav filter |

**Not in product:** Procurement/PO/suppliers, multi-bin WMS, payment gateway, 14-role IAM.

---

## 4. Role and permission matrix

| Resource | ADMIN | OPERATIONS_MANAGER | WAREHOUSE_MANAGER | DRIVER |
|----------|-------|--------------------|------------------|--------|
| Users | Full | — | — | — |
| Customers | Full | Full | Read | — |
| Inquiry / Quote | Full | Full | Read | — |
| Orders | Full | Full | Read | — |
| Bookings | Full | Full | — | — |
| Shipments / Tracking / Trips | Full | Full | — | Own |
| Warehouse domain | Full | Read | Full | — |
| Drivers / Vehicles | Full | Full | — | — |
| Invoices | Full | Full | — | — |
| Reports | Full | — | — | — |

Backend: `SecurityConfig`. Frontend: `PERMS` (hide only).

**Demo users (seed):** see DataInitializer / login page hints (e.g. `admin@asteralnx.com` / `Astera@123`).

---

## 5. Business workflows and status transitions

### Happy path
Customer → Inquiry → Quote (accept) → Order (confirm → process → ready) → Shipment (stock ↓) → Dispatch → Deliver + POD (order DELIVERED) → Invoice → Paid.

### Order states
`DRAFT → CONFIRMED → PROCESSING → READY_FOR_SHIPMENT → SHIPPED → DELIVERED` (+ `CANCELLED` from allowed states).

### Shipment states
`CREATED → … → IN_TRANSIT → DELIVERED` with DELAYED/CANCELLED rules; deliver requires prior dispatch.

### Inventory rule (current)
Stock decreases on **shipment create** when commodity linked; restores on **shipment cancel**. No separate reservation at order confirm.

---

## 6. Database / config notes

- Schema managed by JPA update (no Flyway).  
- Important env: `SPRING_DATASOURCE_*`, `JWT_SECRET`, `MAIL_*`, `APP_PUBLIC_BASE_URL`.  
- Compose maps host **8080** (app), **3307→3306** (MySQL).  
- Change default JWT secret and DB passwords before any shared deployment.

---

## 7. Bug-fix summary

See `docs/14-defect-log.md`.  
P1 items fixed: missing `apiGet`, RBAC UI/backend alignment, search remount pattern.  
Open: client L2 evidence; optional reservation ledger; modal field completeness.

---

## 8. Testing report

| Type | Status |
|------|--------|
| Static JS syntax (`node --check`) | Passed in delivery environment |
| Code inspection of services/security | Done |
| Postman collection | Delivered (`postman/AsteraLNX.postman_collection.json`) |
| Live API / UI E2E | **Not executed in agent** — client must run |
| JUnit | Intentionally excluded by project rule |

---

## 9. Security report

| Control | Status |
|---------|--------|
| Password hashing | BCrypt |
| JWT access | 30 min default; configurable |
| Inactive users | Cannot login |
| Method security | Path-based role rules |
| Error bodies | No stack traces to client |
| CSRF | Disabled (stateless JWT API) |
| Remaining risk | Default secrets in compose; ddl-auto in prod; no penetration test |

---

## 10. Dashboard and reporting

- **Dashboard:** real counts, status breakdowns, recent/ready/delayed queues, low stock (qty &lt; 10).  
- **Reports:** Admin-only summary + entity browse + CSV/PDF export endpoints.  
- Charts: Chart.js doughnuts from API maps.

---

## 11. Run instructions

```bash
unzip ASTERA_LNX_COMPLETE.zip
cd asteralnx
docker compose up -d --build
```

| URL | Purpose |
|-----|---------|
| http://localhost:8080 | App (login) |
| http://localhost:8080/app.html | Console (after login) |
| http://localhost:8080/swagger-ui.html | API docs |

Hard-refresh (**Ctrl+F5**) after upgrades so static JS/CSS reload.

Optional mail for password reset:
```bash
export MAIL_HOST=smtp.example.com MAIL_PORT=587 MAIL_USERNAME=... MAIL_PASSWORD=...
docker compose up -d
```
If `MAIL_HOST` empty, reset content is logged server-side for demo.

---

## 12. Deployment readiness

| Item | Guidance |
|------|----------|
| Secrets | Override `JWT_SECRET`, DB passwords |
| HTTPS | Terminate TLS at reverse proxy |
| Backups | Backup MySQL volume `mysql_data` |
| Monitoring | Actuator `/actuator/health` |
| Migrations | Plan Flyway/Liquibase if leaving ddl-auto |
| Scale | Single-node MVP; not multi-instance session sticky (JWT is fine) |

---

## 13. Remaining limitations

1. No procurement / supplier / PO module.  
2. Inventory: no reservation-before-ship; simple qty + capacity.  
3. No Flyway; schema via Hibernate update.  
4. List UX: client-side filter on pages up to size 200.  
5. Not all enterprise layouts (kanban, split-pane everywhere).  
6. Live E2E evidence must be produced by the client.  
7. Four roles only (not 14-role IAM).

---

## 14. Client acceptance checklist

Execute and tick:

- [ ] `docker compose up -d --build` succeeds; health OK  
- [ ] Admin login; dashboard KPIs load  
- [ ] Create customer → quote accept → order → shipment → deliver  
- [ ] Commodity qty decreases after shipment; restores on cancel  
- [ ] Invoice pay path  
- [ ] WH manager: sees Orders, no mutation buttons  
- [ ] OPS: cannot open Users; POST `/api/users` → 403  
- [ ] DRIVER: only my deliveries/trips  
- [ ] Search: continuous typing without focus loss  
- [ ] Date presets on a list page  
- [ ] Reports: filter table + CSV download (Admin)  
- [ ] Forgot-password flow (console log or SMTP)  
- [ ] Hard refresh still authenticated with Remember me  

---

## 15. Document index

| Doc | Content |
|-----|---------|
| `docs/PHASE_1_TO_5_SIGNOFF.md` | Audit, RBAC, status, forms |
| `docs/PHASE_6_TO_10_SIGNOFF.md` | Search, workflows, sync, machines, dashboard |
| `docs/PHASE_11_TO_15_SIGNOFF.md` | UI, API, tests, defects, process |
| `docs/PHASE_16_DEFINITION_OF_DONE.md` | DoD evaluation |
| `docs/PHASE_17_CLIENT_HANDOVER.md` | This file |
| `docs/04-role-permission-matrix.md` | RBAC matrix |
| `docs/14-defect-log.md` | Defects |
| `docs/10-e2e-postman-verification.md` | Postman guide |
| `postman/AsteraLNX.postman_collection.json` | API tests |

---

## Formal Phase 16–17 sign-off

| Phase | Decision |
|------:|----------|
| **16** Definition of Done | **Evaluated** — L1 largely met; L2 production acceptance **not** claimed |
| **17** Client handover | **Delivered** — this report + runbook + limitations |

**Overall product status:**  
**Implementation-complete logistics MVP (L1). Client runtime acceptance (L2) outstanding.**
