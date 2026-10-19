# Architecture & Project Decisions

## User-Directed Constraints (locked)
1. **No Flyway** — schema managed by Hibernate `ddl-auto=update` (or validate in higher environments).
2. **Testing** — Postman collections only. No JUnit, Mockito, Spring Boot Test, or Testcontainers.
3. **API Documentation** — Swagger UI (existing springdoc dependency).
4. **Version / Compatibility / Vulnerability topics** — skipped per instruction.

## Business Defaults Adopted for MVP
| Decision | Default | Rationale |
|----------|---------|-----------|
| Inventory model | Keep current Commodity.quantity + decrease/restore on shipment create/cancel. StockTransfer already present. | Minimal change; Phase 5 can introduce movement ledger if needed. |
| Order / Shipment structure | Single commodity + quantity per Order and Shipment | Matches existing entities; multi-line deferred. |
| Human-readable codes | SequentialCodes (prefix + 6-digit number) with existence check | Already used throughout; small safety guard added in Phase 1. |
| Capacity unit | Existing warehouse capacity logic (volume-oriented) | Leave as implemented. |
| Token lifetime | 30 minutes (existing) | Unchanged. |
| Frontend | Existing Bootstrap + vanilla JS | No rewrite. |

## Non-Goals for MVP
- Microservices, Kafka, Redis, Kubernetes
- Real GPS hardware or payment gateways
- Complex multi-line order/quote/invoice structures
- Automated test suite
- Flyway / Liquibase
- Refresh tokens

## Status Transition Philosophy
All stateful entities enforce allowed transitions inside the corresponding Service class. Invalid transitions throw `BusinessException`.
