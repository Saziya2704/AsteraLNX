# Postman / E2E verification guide (Phase 13)

## Setup
```bash
docker compose up -d --build
# App: http://localhost:8080
# Swagger: http://localhost:8080/swagger-ui.html
```
Import `postman/AsteraLNX.postman_collection.json`.  
Variable: `baseUrl` = `http://localhost:8080`

## Auth
1. POST `/api/auth/login` `{ "email":"admin@asteralnx.com", "password":"Astera@123" }`
2. Save `token` from response; set collection Bearer token.
3. Repeat for OPS / WH / DRIVER demo users as seeded.

## Happy path (Admin or OPS)
1. POST customer  
2. POST inquiry → quote → send → accept  
3. POST order (or convert from quote) → confirm → status PROCESSING → READY_FOR_SHIPMENT  
4. POST shipment (link order + commodity if stock test)  
5. POST dispatch → POST deliver (POD body)  
6. POST invoice → pay  
7. GET `/api/dashboard` — counts move with data  
8. GET reports export (Admin)

## Negative
| Call | Token | Expect |
|------|-------|--------|
| POST `/api/users` | OPS | 403 |
| GET `/api/reports/summary` | OPS/WH | 403 |
| POST `/api/orders/.../confirm` | WH | 403 |
| Login inactive user | — | unauthorized / inactive message |

## UI spot checks
- Search continuous typing  
- WH: Orders list without mutation buttons  
- Dashboard queues after creating ready/delayed data  

## Result template
| Case | Result | Notes |
|------|--------|-------|
| Login admin | | |
| Order→deliver | | |
| Stock deduct | | |
| 403 OPS users | | |
| Dashboard | | |

**Agent status:** Collection and guide delivered; live execution is **client-side**.
