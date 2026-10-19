# Remaining phases progress (2026-10-15)

## Implemented in this batch
1. **Create modals (API-backed)** for: Customer, Warehouse, User, Driver, Order, Shipment, Commodity, Vehicle, Inquiry, Quote, Booking, Invoice, Receipt, Stock transfer.
2. **Activate / Deactivate** for Users (`/users/{id}/status?active=`) and Warehouses (`/warehouses/{id}/status?status=`).
3. **Dashboard charts** (Chart.js doughnut) for order and shipment status mixes when `/api/dashboard` returns breakdown maps.
4. **Order status transitions** use `PATCH /orders/{id}/status?status=` (aligned with controller).
5. **Search focus-safe tables** retained (toolbar not remounted).
6. **apiPatch** helper in `api.js`.

## Still out of scope / limited
- Full multi-line inventory reservation ledger
- Procurement module (not in original codebase)
- Automated Playwright/JUnit (environment + project constraints)
- Every entity activate/deactivate (vehicles/drivers/commodities endpoints exist for some — wire next if needed)
- Client acceptance testing must be run by the operator after Docker start

## Verify
```bash
docker compose up -d --build
# Ctrl+F5
```
Create each master entity via UI; confirm list refresh; test order confirm → shipment path; toggle warehouse/user status.
