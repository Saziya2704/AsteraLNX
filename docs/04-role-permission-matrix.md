# Role / Permission Matrix (authoritative)

## Roles (product)
- **ADMIN** — full system oversight and user administration
- **OPERATIONS_MANAGER** — commercial and operational modules
- **WAREHOUSE_MANAGER** — warehouse, stock, receipts, transfers; commercial **read**
- **DRIVER** — only own assigned trips and shipments

## Matrix

| Resource / Action | ADMIN | OPS_MANAGER | WH_MANAGER | DRIVER |
|-------------------|-------|-------------|------------|--------|
| User administration | Full | — | — | — |
| Customer | Full | Full | Read | — |
| Inquiry / Quote | Full | Full | Read | — |
| Order | Full | Full | Read | — |
| Booking / Assignment | Full | Full | — | — |
| Warehouse / Commodity | Full | Read | Full | — |
| Goods Receipt / Stock Transfer | Full | Read | Full | — |
| Shipment create / dispatch / cancel | Full | Full | — | — |
| Trip / Tracking | Full | Full | — | Own only |
| Proof of Delivery | Full | Full | — | Own only |
| Invoice | Full | Full | — | — |
| Reports / Export | Full | — | — | — |
| Dashboard | Full | Role view | Role view | Own view |

Backend enforces via Spring Security (`SecurityConfig`) and service rules. Frontend `PERMS` only hides controls.
