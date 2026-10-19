# Phases 1–5 — Formal Sign-off Report

**Product:** AsteraLNX (Central Logistics Management System)  
**Scope of this document:** Master-prompt Phases 1 through 5 only  
**Architecture:** Spring Boot 4.x modular monolith · JWT · MySQL · Bootstrap SPA (`app.html` + `app.js`)  
**Roles in product (not expanded to 14):** `ADMIN`, `OPERATIONS_MANAGER`, `WAREHOUSE_MANAGER`, `DRIVER`  
**Testing constraint:** Postman / manual only (no JUnit/Mockito per project rule)

**Sign-off levels used**
| Level | Meaning |
|-------|---------|
| **L1 Code-complete** | Implemented in repository; static inspection + syntax check |
| **L2 Runtime-verified** | Exercised on a running instance with observed result |
| **Blocked** | Cannot verify in current agent environment (no live Docker/Maven runtime) |

---

## PHASE 1 — Full Application Discovery and Codebase Audit

### Status: **SIGNED OFF (L1)**

### 1.1 Stack inventory

| Layer | Technology |
|-------|------------|
| Backend | Java 17, Spring Boot, Spring Security, Spring Data JPA, Spring Validation, Spring Mail |
| API docs | springdoc Swagger UI (`/swagger-ui.html`) |
| DB | MySQL (`mysql-connector-j`), Hibernate `ddl-auto` (no Flyway) |
| Auth | JWT bearer, BCrypt, stateless session |
| Frontend | Static HTML/CSS/JS, Bootstrap 5, Bootstrap Icons, IBM Plex Sans |
| Packaging | Maven, Docker Compose |

### 1.2 Package / module map

```
com.newasteralnx
  controller/   Auth, User, Customer, Inquiry, Quote, Order, Booking,
                Shipment, Trip, Tracking, Warehouse, Commodity, Receipt,
                StockTransfer, Driver, Vehicle, Invoice, Dashboard, Report
  service(+impl)
  entity/       18+ domain entities
  repository/
  dto/request|response
  security/     JWT filter, SecurityConfig, entry/denied handlers
  constant/     *Status enums per lifecycle
  exception/
  config/
```

### 1.3 Route inventory (frontend SPA hash routes)

| Hash | Module | Primary roles |
|------|--------|---------------|
| `#/dashboard` | KPIs / charts | All |
| `#/customers` | Customer master | ADMIN, OPS (w); WH (r) |
| `#/inquiries` | Inquiries | ADMIN, OPS (w); WH (r) |
| `#/quotes` | Quotes | ADMIN, OPS (w); WH (r) |
| `#/orders` | Orders | ADMIN, OPS (w); WH (r) |
| `#/bookings` | Bookings | ADMIN, OPS |
| `#/shipments` | Shipments | ADMIN, OPS |
| `#/tracking` | Tracking | ADMIN, OPS |
| `#/trips` | Trips | ADMIN, OPS |
| `#/warehouses` | Warehouses | ADMIN, WH (w); OPS (r) |
| `#/commodities` | Commodities | ADMIN, WH (w); OPS (r) |
| `#/receipts` | Receipts | ADMIN, WH (w); OPS (r) |
| `#/stock-transfers` | Transfers | ADMIN, WH (w); OPS (r) |
| `#/drivers` | Drivers | ADMIN, OPS |
| `#/vehicles` | Vehicles | ADMIN, OPS |
| `#/invoices` | Invoices | ADMIN, OPS |
| `#/reports` | Reports | ADMIN |
| `#/users` | Users | ADMIN |
| `#/my-deliveries` | Driver shipments | DRIVER |
| `#/my-trips` | Driver trips | DRIVER |

### 1.4 API surface (controllers)

19 REST controllers under `/api/**`. Public: login, forgot-password, reset-password, static assets, Swagger.

### 1.5 Source of truth

Database tables via JPA entities. Frontend lists call APIs and do client-side filter/sort/page on loaded pages (size up to 200). JWT is sole session mechanism.

### 1.6 Seed / demo accounts (DataInitializer)

| Email | Role | Typical password (seed) |
|-------|------|-------------------------|
| admin@asteralnx.com | ADMIN | Astera@123 |
| ops / warehouse / driver seeds | respective roles | as seeded |

### 1.7 Known gaps (documented, not Phase 1 blockers)

- No procurement / supplier module (out of MVP scope).
- Stock reservation ledger is not full double-entry WMS.
- Live E2E in agent environment is **Blocked**.

### Phase 1 sign-off

**Accepted at L1.** Architecture, modules, routes, entities, and auth model are inventoried and consistent with the existing product. No unjustified rewrite required.

---

## PHASE 2 — Complete Functionality Audit

### Status: **SIGNED OFF (L1)** — runtime click-through **client responsibility (L2)**

### 2.1 Coverage matrix (implementation-level)

| Module | Create | List/Search | Filter/Sort/Page | Status actions | Notes |
|--------|--------|-------------|------------------|----------------|-------|
| Users | API + UI create path | Yes | Yes | Activate/deactivate API | ADMIN only |
| Customers | Modal / API | Yes | Yes | Activate/deactivate | |
| Inquiries | Modal / API | Yes | Yes | Status via API | |
| Quotes | Modal / API | Yes | Yes | Send/Accept/Reject | |
| Orders | Modal / API | Yes | Yes | Confirm/Process/Ready/Cancel | |
| Bookings | Modal / API | Yes | Yes | | |
| Shipments | Modal / API | Yes | Yes | Dispatch/Deliver/Cancel | |
| Tracking | N/A | Yes | Yes | View / Simulate | |
| Warehouses | Modal / API | Yes | Yes | Status patch | |
| Commodities | Modal / API | Yes | Yes | | |
| Receipts | Modal / API | Yes | Yes | | |
| Transfers | Modal / API | Yes | Yes | | |
| Drivers | Modal / API | Yes | Yes | Status patch | |
| Vehicles | Modal / API | Yes | Yes | Status patch | |
| Invoices | Modal / API | Yes | Yes | Pay/Cancel | |
| Reports | N/A | Browse + export | Yes | CSV/PDF | ADMIN |
| Auth | Login / logout / remember / forgot-reset | | | Inactive blocked | |

### 2.2 Defects addressed in recent work

| ID | Severity | Issue | Fix |
|----|----------|-------|-----|
| UX-01 | P1 | `apiGet is not defined` | Helpers in `api.js` |
| UX-02 | P2 | Dead create buttons → toast only | Modals wired for core entities |
| UX-03 | P2 | View-only roles still saw mutation UI | PERMS + `rowActions` gated |
| UX-04 | P2 | Reports download-only | List view + filters + export |
| SEC-01 | P1 | Matrix vs SecurityConfig mismatch | GET/write split for WH/OPS |

### 2.3 Intentionally incomplete (not hidden as “working”)

- Full multi-step wizards for every entity.
- Bulk multi-select actions.
- Browser automation suite (project: Postman only).

### Phase 2 sign-off

**Accepted at L1.** Functional coverage matrix is maintained; enabled UI actions call real APIs. **L2** requires client checklist on `docker compose up`.

---

## PHASE 3 — Strict Role-Based Access Control

### Status: **SIGNED OFF (L1)** for 4-role model

### 3.1 Design decision

The master prompt suggests up to 14 roles. **Product retains four roles** aligned to MVP logistics operations. Expanding to 14 without business ownership would add unused surface area. Granular `module.view|create|edit` style is approximated as **r / w** per module in the SPA plus Spring Security path rules.

### 3.2 Final permission matrix

| Resource | ADMIN | OPERATIONS_MANAGER | WAREHOUSE_MANAGER | DRIVER |
|----------|-------|--------------------|------------------|--------|
| Users | Full | — | — | — |
| Customers | Full | Full | **Read** | — |
| Inquiries / Quotes | Full | Full | **Read** | — |
| Orders | Full | Full | **Read** | — |
| Bookings | Full | Full | — | — |
| Shipments / Tracking / Trips | Full | Full | — | Own (`/mine`) |
| Warehouses / Commodities / Receipts / Transfers | Full | **Read** | Full | — |
| Drivers / Vehicles | Full | Full | — | — |
| Invoices | Full | Full | — | — |
| Reports | Full | — | — | — |
| Dashboard | Full | Role view | Role view | Own view |

### 3.3 Enforcement

| Layer | Mechanism |
|-------|-----------|
| Backend | `SecurityConfig` method-specific matchers; 403 via `RestAccessDeniedHandler` |
| Backend | Service rules (e.g. last ADMIN, inactive login) |
| Frontend | `PERMS` + menu map; no create/row actions when `can(mod,'w')` is false |
| Frontend | Natural labels (“Orders”), not “Orders View” as a fake action |

### 3.4 Changes in this sign-off cycle

- `SecurityConfig` updated: WH **GET** customers/inquiries/quotes; OPS **GET** warehouse domain.
- SPA `PERMS` and WH menu updated to match.

### Phase 3 sign-off

**Accepted at L1.** Backend is the security boundary; UI hides unauthorized controls. 14-role model **explicitly not adopted**.

---

## PHASE 4 — Active / Inactive Status Management

### Status: **SIGNED OFF (L1)** for entities that have status in domain model

### 4.1 Status models (distinct lifecycles)

| Entity | Status concept | Storage | Activate / Deactivate |
|--------|----------------|---------|------------------------|
| User | `active` boolean | DB | `PATCH /api/users/{id}/status` |
| Customer | `CustomerStatus` enum | DB | `PATCH /api/customers/{id}/status` |
| Warehouse | `WarehouseStatus` | DB | `PATCH /api/warehouses/{id}/status` |
| Driver | `DriverStatus` | DB | `PATCH /api/drivers/{id}/status` |
| Vehicle | `VehicleStatus` | DB | `PATCH /api/vehicles/{id}/status` |
| Commodity | `CommodityStatus` | DB | via update flows |
| Order / Shipment / Invoice / Booking / Quote | **Operational** enums | DB | Workflow transitions (not generic active/inactive) |

### 4.2 Rules implemented

- Inactive users **cannot log in** (`AuthServiceImpl`).
- Soft-delete / inactive customers constrained in customer service.
- Last active ADMIN cannot be deactivated/deleted.
- UI status badges + status filters on list pages.
- Soft-delete flags where entities use `deleted`.

### 4.3 Residual limits

- Not every deactivation path blocks all downstream dependencies with a full dependency graph (e.g. warehouse with open stock) — documented as future hardening.
- Operational statuses (order/shipment) are **not** conflated with account active/inactive.

### Phase 4 sign-off

**Accepted at L1** for account and master-data active/inactive. Operational state machines continue under later phases.

---

## PHASE 5 — Forms, Validations, and Date Filters

### Status: **SIGNED OFF (L1)**

### 5.1 Server-side validation

- DTOs under `dto/request` use `@NotBlank`, `@NotNull`, `@Email`, `@Size`, `@Pattern`, `@Valid` on controllers.
- Password complexity via `PasswordRules`.
- Business exceptions for duplicates / invalid transitions.

### 5.2 Client-side forms

- Create modals for core modules (customers, inquiries, quotes, orders, bookings, warehouse domain, fleet, etc.) present in `app.js`.
- HTML5 `required` / type constraints on modal fields.
- Double-submit mitigated by async handlers + reload after success.
- Errors surfaced via toast from API `message`.

### 5.3 Date filters (list engine)

Reusable table chrome supports:

- From / To date inputs  
- Presets: **Today**, **Last 7 days**, **Last 30 days**, **This month**  
- Combined with search + status filter + sort + page size (10/25/50/100)  
- Reset control  

Date fields used where entity has meaningful dates (`createdAt`, `orderDate`, `invoiceDate`, etc.).

### 5.4 Search focus architecture (related stability)

Table **shell** is mounted once; body updates on search debounce so the search input is **not remounted** on each keystroke (addresses cursor-loss class of bugs).

### Phase 5 sign-off

**Accepted at L1.** Forms, Bean Validation, and date filter tooling are in place for in-scope modules.

---

## Cross-phase evidence checklist

| Check | Evidence |
|-------|----------|
| Inventory exists | This document §1 |
| Functional matrix | §2 |
| RBAC backend + frontend aligned | `SecurityConfig.java`, `app.js` PERMS |
| Inactive login blocked | `AuthServiceImpl` |
| Status APIs | User/Customer/Warehouse/Driver/Vehicle controllers |
| Date presets | `app.js` table chrome |
| JS syntax | `node --check` on `app.js` / `api.js` |
| Live E2E | **Blocked** in agent; client checklist below |

---

## Client L2 verification checklist (required for production acceptance)

```bash
cd asteralnx && docker compose up -d --build
# http://localhost:8080 — hard refresh Ctrl+F5
```

1. Login as ADMIN → all menus; create customer; list filters and date presets work.  
2. Deactivate a non-admin user → that user cannot log in.  
3. Login as WAREHOUSE_MANAGER → Customers/Orders **visible**, **no** create/confirm/cancel buttons; warehouse create allowed.  
4. Login as OPERATIONS_MANAGER → warehouse lists **read-only** (no create).  
5. Login as DRIVER → only my deliveries/trips.  
6. Unauthorized: call `POST /api/users` with OPS token → **403**.  
7. Type continuously in Search without re-clicking field.  

---

## Formal sign-off statement

| Phase | Title | Decision |
|------:|-------|----------|
| 1 | Discovery & audit | **SIGNED OFF (L1)** |
| 2 | Functional audit | **SIGNED OFF (L1)**; L2 pending client runtime |
| 3 | RBAC | **SIGNED OFF (L1)** — 4-role model |
| 4 | Active / inactive | **SIGNED OFF (L1)** |
| 5 | Forms & date filters | **SIGNED OFF (L1)** |

**Not claimed:** L2 production acceptance, Phases 6–17 complete, or 14-role enterprise IAM.

**Next recommended work:** Phases 6–10 (search regression, workflow depth, cross-module sync, state machines, corporate dashboard) with the same L1/L2 discipline.

_Signed off at implementation level on the repository state accompanying this document._
