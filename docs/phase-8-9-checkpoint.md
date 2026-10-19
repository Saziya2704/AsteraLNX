# Phase 8 + Phase 9 Checkpoint Report

**Date:** 2026-10-14  
**Constraints:** No Flyway · Postman-only · Swagger UI · skip version topics

---

## Phase 8 — Invoicing

### Implemented / Hardened
1. **Create invoice**
   - Order must belong to customer
   - Cancelled order blocked
   - Shipment (if provided) must belong to order and not be cancelled
   - Server-side total = subtotal + tax − discount (≥ 0)

2. **Mark paid** (`POST /api/invoices/{id}/pay`)
   - Blocked if already PAID or CANCELLED

3. **Cancel** (`POST /api/invoices/{id}/cancel`) — **new**
   - Blocked if PAID or already CANCELLED

4. **Update**
   - Blocked if PAID or CANCELLED

5. **Overdue auto-refresh**
   - ISSUED invoices with dueDate in the past become OVERDUE on list/get/create

No separate Payment entity (manual mark-paid is the MVP payment recording).

### Files
| Path | Change |
|------|--------|
| `service/InvoiceService.java` | Validation, cancel, overdue, totals |
| `controller/InvoiceController.java` | cancel endpoint |

---

## Phase 9 — Dashboards & Reports

### Dashboard (`GET /api/dashboard`)
Role-specific KPIs:

| Role | Metrics |
|------|---------|
| ADMIN / OPS | customers, orders + by status, shipments + by status, delayed, invoices (issued/overdue/paid); ADMIN also warehouses + trips |
| WAREHOUSE_MANAGER | warehouses, orders, ready-for-shipment, shipments created |
| DRIVER | assigned shipments + my trips (scoped to linked driver profile) |

### Reports
- `GET /api/reports/summary` now includes delayed/delivered shipments, overdue/paid invoices, cancelled orders
- Existing CSV/PDF exports unchanged (shipments, orders, invoices, warehouses, drivers, customers)

### Files
| Path | Change |
|------|--------|
| `service/DashboardService.java` | Rich role KPIs |
| `service/ReportService.java` | Expanded summary |

---

## Postman
Folders: **Invoices**, **Dashboard**, **Reports**  
Variable: `invoiceId`

Suggested flow after delivery:
1. Create Invoice (customer + order + optional shipment + amounts)
2. List / Get (overdue may flip if dueDate past)
3. Mark Paid **or** Cancel
4. Dashboard + Report summary

---

## Known limitations
- No multi-line invoice / credit notes  
- Payment is status flip only (no allocation ledger)  
- Dashboard DRIVER counts depend on linked Driver profile  

## Next
**Phase 10** — End-to-end Postman regression notes, packaging, final docs polish.

Reply **“proceed with Phase 10”** to finish the roadmap.
