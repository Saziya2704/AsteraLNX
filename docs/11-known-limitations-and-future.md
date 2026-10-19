# Known Limitations & Future Enhancements

## MVP intentional simplifications
- Single commodity + quantity per Order and Shipment (no multi-line items)
- Inventory as quantity on Commodity (no separate stock-movement ledger / reservation table)
- Payment recording = mark invoice PAID (no payment allocation ledger or credit notes)
- POD is text fields only (no signature/file attachment)
- Tracking is simulated path (not live GPS hardware)
- Quote is single amount (no rate components / versioning)
- Schema managed by Hibernate `ddl-auto` (no Flyway)
- Verification via Postman only (no JUnit/Mockito)

## Operational caveats
- Human-readable codes use sequential probing (safe enough for demo load; not a distributed sequence service)
- DRIVER dashboard/mine endpoints require a linked Driver profile (auto-created when ADMIN creates a DRIVER user)
- Concurrent stock updates rely on transactional service methods; extreme concurrency may still race without DB-level locks
- Report PDF/CSV load full datasets into memory — suitable for demo volumes, not massive production extracts

## Suggested future enhancements (not in scope)
1. OrderLine / ShipmentLine / InvoiceLine multi-line model  
2. StockMovement + StockReservation with available-to-promise  
3. Payment + PaymentAllocation + credit notes  
4. POD attachments (object storage)  
5. Flyway versioned migrations  
6. Automated test suite (if policy changes)  
7. Notification / in-app alerts  
8. Advanced aging reports and date-range filters  
9. Refresh tokens / session hardening beyond 30-min JWT  

## What is intentionally solid for demo / interview
- Full Inquiry → Quote → Order → Booking → Warehouse → Shipment → Trip → Tracking → POD → Invoice flow  
- Backend RBAC + DRIVER ownership on mine endpoints  
- Status transition tables for Order, Booking, Shipment  
- Stock decrease on ship / restore on cancel  
- Role-specific dashboard KPIs  
- Swagger UI + Postman collection  
- Docker Compose local run  
