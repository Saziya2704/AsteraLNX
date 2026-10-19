# Patch notes — regression-safe UI fixes

This patch preserves the existing Spring Boot application structure, styles, routes, and existing actions. Changes are deliberately limited to the current static console layer.

## Implemented in this patch
- Added a reusable, read-only View modal to list pages built with `listPage`; Tracking keeps its dedicated tracking interface and does not get the generic View action.
- Updated Inquiry list columns to Inquiry #, Subject, Customer, Status, and Created Date.
- Changed the Warehouse table label from Available to Stock Available.
- Added Order # display to Booking and Shipment lists when the API response supplies `orderNumber` or a related `order.orderNumber`.
- Renamed the order workflow action label to Ready to Dispatch without changing the existing status transition.
- Replaced the commodity Unit text input with a controlled dropdown.
- Replaced the Inquiry Subject text input with a predefined dropdown and an Others option that reveals a validated custom subject field.

## Verification
- `node --check src/main/resources/static/js/app.js` passed.
- Maven tests/build could not be run in this environment because Maven is not installed.

## Important limitations
This is not a claim that every item in the supplied 18-section requirements list is complete. Backend-sensitive requirements such as transactional stock validation, duplicate prevention, PDF/email delivery, server-side role enforcement, database schema/data mapping, and full end-to-end workflow testing require backend/runtime/database verification. Review and test these in the target environment before client handover.


## Follow-up bug-fix pass (2026-10-17)
- Booking creation now rejects an order that already has a booking (backend rule); booking responses include the related order number and created timestamp. The booking form filters already-booked orders where those records are loaded.
- Shipment creation now requires order, warehouse, driver, vehicle, commodity, positive quantity, and expected delivery; backend verifies commodity belongs to the selected warehouse. Repeated driver/order assignment is rejected. Assignment success displays a confirmation toast.
- Tracking history now displays a readable table with point ID, trip, coordinates, speed, status, and timestamp instead of raw JSON.
- Driver My Deliveries is restricted to delivered shipments; My Trips is limited to assigned/cancelled trips and displays pickup/delivery locations. Assigned drivers can complete their own shipment; backend rejects a driver attempting to complete another driver's shipment. Delivery completion continues through the existing transactional shipment workflow so status updates are reflected in management views.
- Added quote and invoice customer-email actions. These require SMTP configuration; the application returns an error instead of pretending the email was sent when SMTP is not configured. The email body includes the quote/invoice details.
- Added a formatted invoice view that opens a print-ready invoice layout and invokes the browser's Print / Save as PDF workflow.
- Existing forgot-password, password-reset, change-password, and Remember Me flows were retained. Existing stock transfer/capacity services were retained; they still need runtime verification with a configured database.

## Verification limitations
- JavaScript syntax check passed using Node.js.
- Maven tests/build could not be run in this environment because Maven is not installed. No live database, SMTP server, or browser end-to-end session was available; do not treat email delivery, PDF printing, database persistence, stock transfer, or role workflows as end-to-end verified until tested in the target environment.
