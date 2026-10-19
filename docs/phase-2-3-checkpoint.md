# Phase 2 + Phase 3 Checkpoint Report

**Date:** 2026-10-14  
**Constraints still in force:** No Flyway · Postman-only testing · Swagger UI · skip version topics

## Phase 2 — Auth & User Administration

### Implemented / Hardened
1. **Last-ADMIN protection**  
   - `UserServiceImpl.updateActive` and `deleteUser` now refuse to deactivate or soft-delete the last remaining active ADMIN.  
   - New repository method: `countByRoleAndDeletedFalseAndActiveTrue(Role)`.

2. **Existing strengths retained**
   - JWT login with inactive/deleted account rejection
   - Password change with strength rules (`PasswordRules`)
   - Forgot / reset password token flow (30 min, one-time)
   - User create auto-creates linked Driver profile when role = DRIVER
   - Soft-delete + cannot delete own account
   - `/api/users/**` restricted to ADMIN in SecurityConfig
   - Password validation already present on Create / Change / Reset DTOs

### Files changed (Phase 2)
| Path | Change |
|------|--------|
| `repository/UserRepository.java` | Added `countByRoleAndDeletedFalseAndActiveTrue` |
| `service/impl/UserServiceImpl.java` | Last-ADMIN guard on deactivate & delete |

## Phase 3 — Customer, Inquiry, Quote

### Implemented / Hardened
1. **Customer GSTIN validation**  
   - `CustomerRequest.gstNumber` now requires exactly 15 characters and a standard GSTIN pattern.

2. **Customer code generation**  
   - Switched to shared `SequentialCodes` (with attempt limit from Phase 1).

3. **Inquiry status transitions**  
   - Converted / Cancelled inquiries cannot change status.  
   - Direct transition to CONVERTED is blocked (must go through quote acceptance / order conversion).  
   - Closed inquiry cannot be reopened.

4. **Existing Quote strengths retained**
   - DRAFT → SENT → ACCEPTED / REJECTED  
   - Expired quotes auto-detected and cannot be accepted/converted  
   - Accept only from SENT; convert only from ACCEPTED  
   - Converting a quote also marks linked inquiry as CONVERTED

### Files changed (Phase 3)
| Path | Change |
|------|--------|
| `dto/CustomerRequest.java` | GSTIN length + pattern validation |
| `service/impl/CustomerServiceImpl.java` | Use SequentialCodes; removed unused import |
| `service/InquiryService.java` | Restricted status transitions |

## Postman
`postman/AsteraLNX.postman_collection.json` fully expanded with:
- Auth (Login, Me, Change Password, Forgot, Logout)
- Users (List, Create OPS, Create DRIVER, Get, Deactivate)
- Customers (List, Create, Get, Invalid GST negative case)
- Inquiries (Create, List, Status → IN_PROGRESS)
- Quotes (Create, Send, Accept, List)
- Health & Swagger

Variables auto-captured: `token`, `customerId`, `inquiryId`, `quoteId`, `userId`.

## Business rules now enforced (summary)
- Cannot remove the last active ADMIN
- GSTIN must be valid format on customer create/update
- Inactive customers blocked from new transactions (`requireActiveForTransaction`)
- Inquiry status machine prevents illegal transitions
- Quote lifecycle + expiry already solid

## How to verify (Postman)
1. Start app.
2. Import the collection.
3. Run **Auth → Login**.
4. Run **Users → Create User (OPS)** then **Create User (DRIVER)**.
5. Run **Customers → Create Customer** (valid GST) and the invalid-GST request (expect 400).
6. Run **Inquiries → Create Inquiry** → **Update Status**.
7. Run **Quotes → Create → Send → Accept**.

## Known limitations
- No automated tests (by design).
- Customer soft-delete does not yet check for open orders/shipments (can be added in a later phase if required).
- Quote remains single-amount (no line items) per MVP decision.

## Next
**Phase 4 — Order lifecycle** (status machine, create from accepted quote or direct, cancellation rules).

Reply **“proceed with Phase 4”** (or further instructions) to continue.
