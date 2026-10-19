# Functional audit, fixes & handover (honest)

**Date:** 2026-10-15  
**Environment note:** This agent environment has **no Maven and no Docker**. Automated browser E2E and full API regression could **not** be executed here. Code changes were applied and statically verified. Client must retest after `docker compose up`.

## Architecture (as implemented)
- **Frontend:** Bootstrap 5 + vanilla JS SPA (`app.html`, `app.js`, `api.js`, `auth.js`)
- **Backend:** Spring Boot modular monolith, JWT + RBAC, JPA/MySQL
- **Schema:** Hibernate `ddl-auto` (no Flyway)
- **Testing policy (project constraint):** Postman-only (no JUnit/Mockito)

## Roles retained (not 14 invented roles)
ADMIN · OPERATIONS_MANAGER · WAREHOUSE_MANAGER · DRIVER  

Frontend permission map hides unauthorized actions; **SecurityConfig** enforces API roles.

## Defects addressed in this pass

| ID | Severity | Issue | Fix |
|----|----------|-------|-----|
| BUG-01 | P1 | Search field lost focus after each character | Table shell built once; only tbody/foot refresh on search |
| BUG-02 | P1 | Create buttons only showed toast / Swagger hint | Real modals + `apiPost` for Customer, Warehouse, User, Driver |
| BUG-03 | P2 | WH menu labeled "Orders (view)" | Renamed to **Orders**; still view-only (no action buttons) |
| BUG-04 | P2 | Dashboard only 4 boxes | KPI cards + orders/shipments by status from `/api/dashboard` |

## Still incomplete / limited (do not claim done)
- Create modals not yet wired for every entity (Order, Shipment, Commodity, Invoice, Booking, etc.)
- Chart library / full executive dashboard (Phase 10 of master prompt) not fully built
- No automated Playwright/JUnit suite (project constraint + no tools in this env)
- Multi-line order/inventory ledger / reservation model remains MVP (see known limitations)
- Procurement module not in scope of current codebase

## How to verify locally
```bash
cd asteralnx && docker compose up -d --build
# http://localhost:8080
# Ctrl+F5 hard refresh
```
1. Login admin → Customers → New customer → save → appears in table  
2. Type continuously in Search — **cursor must stay** in the field  
3. Login warehouse → Orders — **no** Confirm/Cancel buttons  
4. Admin → Reports — list + filter + CSV/PDF  
5. Shipments — Dispatch / Deliver / Cancel by status  

## Readiness statement
**Not fully ready for formal client acceptance** against the entire 17-phase master prompt.  
**Ready for** continued internal demo of the logistics MVP with improved UX, working search, and real creates for core masters.

## Next recommended batch
1. Order + Shipment + Commodity create modals  
2. Server-side list filters where datasets grow  
3. Chart.js dashboard panels from real aggregates  
4. Activate/deactivate row actions on users/warehouses/commodities with confirm dialogs  
