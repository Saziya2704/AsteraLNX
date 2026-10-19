# Phases 11–15 — Formal Sign-off Report

**Product:** AsteraLNX  
**Prerequisites:** Phases 1–10 L1 sign-off  
**Levels:** L1 code-complete · L2 live client verification · Blocked = no agent runtime

---

## PHASE 11 — Professional Corporate UI/UX

### Status: **SIGNED OFF (L1)**

### Design system (in force)
| Token / element | Implementation |
|-----------------|----------------|
| Typography | IBM Plex Sans |
| Shell | Fixed dark sidebar + sticky topbar + work surface `#eef1f5` |
| Density | Compact tables (12.5px), 3px panel radii, muted badges |
| Navigation | Role-filtered menu, section headers, active state |
| Collapse | Desktop collapse to icon rail; preference in `localStorage` |
| Nav filter | Topbar “Filter menu…” filters sidebar links client-side |
| Focus | `:focus-visible` rings on controls and nav |
| Loading | Skeleton rows instead of spinner-only empty page |
| Responsive | Overlay drawer &lt; 992px |

### Layout patterns used
- KPI grids (dashboard)
- Advanced data tables (search, status, date, sort, page size)
- Report split nav + table
- Modal forms for create
- Status badge chips
- Role-aware primary actions

### Explicitly not built (scope control)
Kanban boards, split-pane master-detail for every entity, column-visibility picker, full notification centre.

### Phase 11 sign-off
**Accepted at L1** as a consistent operations-console UI. Not a design-system library productization.

---

## PHASE 12 — Database and API Quality

### Status: **SIGNED OFF (L1)** with documented residual risks

### API conventions
| Topic | Practice |
|-------|----------|
| Base path | `/api/**` |
| Auth | Bearer JWT; public auth + static + Swagger only |
| Validation | `@Valid` + Bean Validation on request DTOs |
| Errors | `GlobalExceptionHandler` → structured `ErrorResponse` (code, message, path, timestamp) |
| Business rules | `BusinessException` → 400 `BUSINESS_RULE_VIOLATION` |
| Not found | 404 `NOT_FOUND` |
| AuthZ | 401 / 403 via entry point & access denied handler |
| Integrity | `DataIntegrityViolationException` mapped |
| Stack traces | Not exposed (`server.error.include-message=never`) |

### Database
| Topic | Practice |
|-------|----------|
| Engine | MySQL via JPA |
| Schema | `ddl-auto=update` (no Flyway — project constraint) |
| Soft delete | `deleted` flags where applicable |
| Codes | Sequential human-readable numbers with uniqueness checks |
| Open-in-view | `false` |

### Residual risks (honest)
- No Flyway versioned migrations → environment drift possible.
- Not every list endpoint is fully server-side filtered at scale (UI loads up to 200 then client-filters).
- Index audit is incomplete for high-volume production.

### Secrets
- JWT secret and DB password via env vars; demo defaults documented for local only.

### Phase 12 sign-off
**Accepted at L1** for consistent API error handling and security wiring. Production hardening (migrations, indexes, server-side filter everywhere) remains backlog.

---

## PHASE 13 — End-to-End Testing

### Status: **PARTIAL (L1 artifacts) / L2 Blocked in agent**

### Constraints
- Project rule: **Postman only** (no JUnit/Mockito).
- Agent environment: **no live Docker/Maven** for this session → cannot claim observed pass/fail of HTTP calls.

### Artifacts provided
| Artifact | Path |
|----------|------|
| Postman collection | `postman/AsteraLNX.postman_collection.json` |
| Verification guide | `docs/10-e2e-postman-verification.md` |
| Client runtime checklist | Phases 1–10 sign-off docs |

### Required client execution (to reach L2)
1. Import Postman collection; set `baseUrl=http://localhost:8080`.
2. Login as each role; capture tokens.
3. Run workflow: customer → quote → order → shipment → deliver → invoice.
4. Negative: OPS → POST `/api/users` → 403; inactive user login → 401/business error.
5. UI checklist: search focus, RBAC buttons, dashboard queues.

### Phase 13 sign-off
**L1 artifacts accepted.** **L2 not signed** until client records pass results.

---

## PHASE 14 — Bug Management and Priority

### Status: **SIGNED OFF (L1)** for formal defect log below

### Defect log (project history)

| ID | Sev | Module | Issue | Root cause | Fix | Status |
|----|-----|--------|-------|------------|-----|--------|
| BUG-001 | P1 | Frontend API | `apiGet is not defined` | Helpers missing in `api.js` | Added `apiGet/Post/Put/Delete` | Fixed |
| BUG-002 | P2 | UI brand | AsteraLNX underline | Default link underline | `text-decoration: none` | Fixed |
| BUG-003 | P3 | Sidebar | Visible scrollbar noise | Default overflow | Hidden scrollbar CSS | Fixed |
| BUG-004 | P3 | Header | “SSystem Admin” appearance | Avatar letter + name adjacency | Layout spacing / truncate | Fixed |
| BUG-005 | P1 | RBAC UI | View-only roles saw mutation buttons | No PERMS gating | `can(mod,'w')` gates actions | Fixed |
| BUG-006 | P1 | Security | Matrix vs SecurityConfig | Missing GET for WH/OPS | Split GET/write matchers | Fixed |
| BUG-007 | P2 | Reports | Download-only UX | No list view | Browse + filter + export | Fixed |
| BUG-008 | P2 | Search | Risk of focus loss | Full remount pattern | Shell + `updateTableBody` | Fixed |
| BUG-009 | P2 | Create UX | Dead create buttons | Toast-only stubs | Modals wired to APIs | Fixed (core modules) |

### Open / accepted residual

| ID | Sev | Issue | Disposition |
|----|-----|-------|-------------|
| OPEN-001 | P2 | Not all create modals cover every edge field | Accept for MVP; API/Swagger complete |
| OPEN-002 | P2 | No stock reservation before shipment | Documented business rule: deduct on ship create |
| OPEN-003 | P3 | No Flyway migrations | Project constraint |
| OPEN-004 | P1* | Live E2E not executed in agent | *Client must run L2 |

No P0 auth-bypass or silent success without persistence is known in the current code review.

### Phase 14 sign-off
**Accepted at L1.** Critical/high UI/security defects above are fixed in repo.

---

## PHASE 15 — Implementation and Execution Rules

### Status: **SIGNED OFF (L1)** for process compliance on this engagement

### Rules followed
1. Worked on **existing** codebase (no greenfield rewrite).
2. Preserved project name, JWT model, 4 roles, Postman-only testing, Swagger, no Flyway.
3. Batch changes by phase with written sign-offs.
4. Did **not** claim L2 passes without runtime.
5. Frontend hide ≠ security; backend `SecurityConfig` remains authority.
6. No payment gateway invented.
7. Procurement not fabricated as complete.

### Rules partially applicable
- Automated regression suite: limited by Postman-only + no agent runtime.
- Browser automation (Playwright): not used (constraint + environment).

### Phase 15 sign-off
**Accepted at L1** as the working method for Phases 1–15.

---

## Aggregate status after Phase 15

| Phases | L1 | L2 |
|--------|----|----|
| 1–5 | Signed | Client |
| 6–10 | Signed | Client |
| 11–12, 14–15 | Signed | Client |
| 13 | Artifacts only | **Open** |
| 16–17 | Pending | — |

### Client acceptance still requires
- Docker runtime checklist from Phase 1–10 docs  
- Postman workflow + negative authZ tests  
- Explicit record of pass/fail (Phase 13 L2)

_Next:_ Phases **16–17** (Definition of Done evaluation + Client Handover report).
