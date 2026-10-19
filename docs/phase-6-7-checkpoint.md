# Phase 6 + Phase 7 Checkpoint Report

**Date:** 2026-10-14  
**Constraints:** No Flyway · Postman-only · Swagger UI · skip version topics

---

## Phase 6 — Driver, Vehicle, Booking

### Hardened
1. **Booking create**
   - Order must not be DRAFT or CANCELLED
   - Order customer must match booking customer
   - Driver / vehicle still go through `requireAssignable`

2. **Booking status transitions**
   - PENDING → CONFIRMED | ASSIGNED | CANCELLED  
   - CONFIRMED → ASSIGNED | IN_PROGRESS | CANCELLED  
   - ASSIGNED → IN_PROGRESS | COMPLETED | CANCELLED  
   - IN_PROGRESS → COMPLETED | CANCELLED  
   - COMPLETED / CANCELLED → terminal  

3. **Existing eligibility retained**
   - Driver: ACTIVE, license not expired, no conflicting ASSIGNED/STARTED trips  
   - Vehicle: ACTIVE, insurance not expired  
   - Unique license / vehicle number  

### Files
| Path | Change |
|------|--------|
| `service/BookingService.java` | Order eligibility + status transition table |

---

## Phase 7 — Shipment, Trip, Tracking, POD

### Implemented
1. **Shipment assign endpoint**  
   `POST /api/shipments/{id}/assign` (driver + vehicle via AssignmentRequest)

2. **Shipment cancel endpoint**  
   `POST /api/shipments/{id}/cancel`  
   - Restores stock + capacity  
   - Cancels linked trip if not completed  

3. **Shipment deliver + Proof of Delivery**  
   `POST /api/shipments/{id}/deliver`  
   - Body optional: `receivedBy`, `remarks`, `outcome`  
   - Creates `ProofOfDelivery` entity (one per shipment)  
   - Completes trip, marks order DELIVERED  

4. **Shipment status transition table**
   - CREATED → READY_FOR_PICKUP | PICKED_UP | IN_TRANSIT | CANCELLED  
   - READY_FOR_PICKUP → PICKED_UP | IN_TRANSIT | DELAYED | CANCELLED  
   - PICKED_UP → IN_TRANSIT | DELAYED | CANCELLED  
   - IN_TRANSIT → ARRIVING | DELAYED | DELIVERED | CANCELLED  
   - ARRIVING → DELIVERED | DELAYED | CANCELLED  
   - DELAYED → IN_TRANSIT | ARRIVING | DELIVERED | CANCELLED  
   - DELIVERED / CANCELLED → terminal  

5. **New domain**
   - Entity: `ProofOfDelivery`  
   - Repository: `ProofOfDeliveryRepository`  
   - Request DTO: `ProofOfDeliveryRequest`  

6. **Existing retained**
   - Stock decrease on create, restore on cancel  
   - Auto trip create on assign/dispatch  
   - Simulated GPS tracking  
   - DRIVER `mine` endpoints  

### Files
| Path | Action |
|------|--------|
| `entity/ProofOfDelivery.java` | Created |
| `repository/ProofOfDeliveryRepository.java` | Created |
| `dto/request/ProofOfDeliveryRequest.java` | Created |
| `service/ShipmentService.java` | Transitions, cancel, deliver, POD |
| `controller/ShipmentController.java` | assign, cancel, deliver endpoints |

---

## Postman
New folders: **Drivers**, **Vehicles**, **Bookings**, **Shipments**, **Trips**  
Variables: `driverId`, `vehicleId`, `bookingId`, `shipmentId`

Suggested flow after Order is CONFIRMED / READY_FOR_SHIPMENT and stock exists:
1. Create Driver + Vehicle  
2. Create Booking (optional)  
3. Create Shipment (with commodity + qty)  
4. Assign → Dispatch → Simulate tracking → Deliver with POD  
5. Or Cancel (stock restored)

---

## Known limitations
- POD has no file/signature attachment (text fields only)  
- No multi-stop trips  
- Tracking is simulated path (as designed)  

## Next
**Phase 8** — Invoicing  
**Phase 9** — Dashboards, reports polish  

Reply **“proceed with Phase 8 and 9”** to continue.
