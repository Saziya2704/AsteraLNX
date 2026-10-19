# Phase 4 + Phase 5 Checkpoint Report

**Date:** 2026-10-14  
**Constraints:** No Flyway · Postman-only · Swagger UI · skip version topics

---

## Phase 4 — Order Lifecycle

### Implemented
1. **Formal status transition table** enforced in `OrderService.assertAllowedTransition`:
   - DRAFT → CONFIRMED | CANCELLED  
   - CONFIRMED → PROCESSING | READY_FOR_SHIPMENT | CANCELLED  
   - PROCESSING → READY_FOR_SHIPMENT | CANCELLED  
   - READY_FOR_SHIPMENT → SHIPPED | CANCELLED  
   - SHIPPED → DELIVERED  
   - DELIVERED / CANCELLED → terminal  

2. **Dedicated cancel endpoint**  
   - `POST /api/orders/{id}/cancel`  
   - Allowed only before SHIPPED / DELIVERED  
   - Clear error if already cancelled or already shipped/delivered  

3. **Confirm** restricted to DRAFT only  

4. **requireShippable** tightened: blocks DRAFT, CANCELLED, DELIVERED  

5. Existing behaviour retained:
   - Create direct order or from accepted quote  
   - Quote marked CONVERTED on order create  
   - Cannot edit after shipment begins / delivered  

### Files changed
| Path | Change |
|------|--------|
| `service/OrderService.java` | Transition table, cancel(), tighter confirm/shippable |
| `controller/OrderController.java` | `POST /{id}/cancel` |

---

## Phase 5 — Warehouse & Inventory

### Status
Already strong. Targeted polish only:

1. **Receipt**  
   - Quantity must be > 0  
   - Capacity consumption uses `CommodityService.occupyAmount` (volume preferred, else quantity) for consistency with create/transfer  

2. **Existing rules retained (verified)**
   - No stock → no shipment (`decreaseQuantity`)  
   - Cancel shipment restores stock + capacity  
   - Cannot deactivate commodity with remaining stock or open shipments  
   - Cannot delete warehouse while stock or open shipments exist  
   - Stock transfer: source/dest must differ, sufficient qty, transactional deduct + receive  
   - Capacity cannot be reduced below occupied  

### Files changed
| Path | Change |
|------|--------|
| `service/ReceiptService.java` | Positive qty check + consistent occupy calculation |

No new movement-ledger entity introduced (per MVP decision to keep Commodity.quantity model).

---

## Postman
Collection expanded with folders:
- **Orders** — Create direct, Create from quote, Confirm, Status → READY_FOR_SHIPMENT, Cancel, List, Invalid transition (expect 400)
- **Warehouses** — Create, List
- **Commodities** — Create, List
- **Receipts** — Create inbound, List
- **Stock Transfers** — Create, List

New variables: `orderId`, `warehouseId`, `commodityId`

---

## How to verify (Postman sequence)
1. Login  
2. Create Customer → Create Inquiry → Create/Send/Accept Quote (or skip and create direct order)  
3. **Orders → Create Order** → **Confirm** → **Update Status to READY_FOR_SHIPMENT**  
4. **Warehouses → Create** → **Commodities → Create**  
5. **Receipts → Create** (inbound stock)  
6. Optional: **Stock Transfers**  
7. Try **Cancel** on a DRAFT/CONFIRMED order; try invalid status jump to DELIVERED (expect 400)

---

## Known limitations
- Single commodity/quantity per order (MVP decision)  
- No dedicated stock-movement ledger table  
- Receipt still linked to an order (inbound model as currently designed)  

## Next
**Phase 6** — Driver, Vehicle, Booking eligibility  
**Phase 7** — Shipment, Trip, Tracking, POD  

Reply **“proceed with Phase 6 and 7”** (or adjustments) to continue.
