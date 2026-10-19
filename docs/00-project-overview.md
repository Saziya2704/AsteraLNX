# AsteraLNX — Project Overview

**AsteraLNX** is an internal Enterprise Logistics and Transportation Management System (modular monolith).

## Purpose
Support the full operational lifecycle:

Inquiry → Quote → Order → Booking → Warehouse / Inventory → Shipment → Driver/Vehicle Assignment → Trip → Tracking → Proof of Delivery → Invoice → Reporting

## Technology (locked by project decisions)
- Java 17
- Spring Boot (existing parent version)
- Spring MVC + REST
- Spring Data JPA + Hibernate (`ddl-auto`)
- MySQL 8
- Spring Security + JWT + BCrypt
- Swagger UI (springdoc)
- Bootstrap 5 + vanilla JavaScript frontend
- Maven
- Docker Compose
- **Testing: Postman only** (no JUnit / Mockito)
- **No Flyway**

## Roles
- ADMIN
- OPERATIONS_MANAGER
- WAREHOUSE_MANAGER
- DRIVER

## Key Constraints (user-directed)
- No Flyway migrations
- No automated unit/integration tests (JUnit, Mockito, etc.)
- Verification exclusively via Postman collections
- Swagger UI for API documentation
- Do not address version compatibility or vulnerability topics

## Repository Layout
```
asteralnx/
├── docs/                  ← Project documentation
├── postman/               ← Postman collections
├── src/main/java/com/newasteralnx/
│   ├── config/
│   ├── constant/
│   ├── controller/
│   ├── dto/
│   ├── entity/
│   ├── exception/
│   ├── repository/
│   ├── security/
│   ├── service/
│   ├── specification/
│   └── util/
├── src/main/resources/
│   ├── application.properties
│   └── static/            ← Frontend (index.html, app.html, css/, js/)
├── Dockerfile
├── docker-compose.yml
├── pom.xml
└── README.md
```

## Current Status
Phase 0 audit completed. Phase 1 (project hygiene + documentation + Postman foundation) in progress under the constraints above.
