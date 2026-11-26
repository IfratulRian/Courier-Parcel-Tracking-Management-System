# Product Requirements Document

## 1. Product summary

The Courier & Parcel Tracking Management System is a local, console-based learning application that demonstrates the core parcel lifecycle, from booking through delivery or cancellation. It gives customers, administrators, and delivery agents role-specific workflows and persists records between normal runs.

## 2. Goals

1. Manage the parcel lifecycle from booking through processing, dispatch, delivery, cancellation, or return.
2. Organize operations around branches and delivery-agent assignment.
3. Give users a chronological parcel tracking history.
4. Calculate charges consistently from parcel weight, delivery zone, and service category.
5. Record prepaid and Cash on Delivery payment states and provide basic receipts.
6. Summarize operational activity and collected revenue.
7. Protect data integrity through validation, allowed state transitions, and persistent storage.
8. Support customer profiles, Standard/Express service, and eligible cancellation.

## 3. Users and permissions

### Customer

- Register and authenticate.
- Update profile details.
- Book a parcel with receiver information, weight, zone, origin/destination branches, and service category.
- Choose prepaid or Cash on Delivery.
- View own parcels, tracking history, and payment receipt.
- Cancel own parcel while its status is `BOOKED` or `PICKED_UP`, subject to payment-reversal rules.

### Administrator

- Authenticate using the demo administrator account.
- Register branches and delivery agents; deactivate agents.
- View branches, agents, parcels, and payment receipts.
- Assign parcels to active agents.
- View parcel and agent reports.

### Delivery agent

- Authenticate while active.
- View parcels assigned to the agent.
- Update an assigned parcel only through a permitted status transition.
- View assigned parcel tracking history.

## 4. Functional requirements

- **Booking:** Require an active customer, active origin/destination branches, receiver details, finite positive weight, zone 1–5, and valid service category.
- **Pricing:** Calculate a deterministic charge using service category, weight, and zone. Current formula is in `StandardParcel` and `ExpressParcel`.
- **Tracking:** Record each valid status transition with time, branch, agent, and comment.
- **Status lifecycle:** Enforce the transition table documented in `README.md`; terminal states cannot progress.
- **Assignment:** Assign only to an active agent whose home branch exists and is active. Prevent assignment of terminal parcels.
- **Payment:** Permit one payment per parcel. Prepaid is simulated at booking; COD is collected only after delivery. Report refunds distinctly from collected revenue.
- **Cancellation:** Allow cancellation only for the owning customer and only in eligible statuses. Record the cancellation in tracking and reflect payment reversal.
- **Reports:** Show status counts, parcel counts by category and zone, collected revenue by payment method, and agent delivery outcomes.
- **Persistence:** Load and save users, branches, parcels, payments, and tracking history. Invalid records and I/O errors must be visible to the operator.

## 5. Non-functional requirements

- Use standard Java and avoid external dependencies unless a requirement justifies them.
- Keep the code understandable for students learning object-oriented programming.
- Keep business logic out of console menu classes.
- Preserve data on normal save/exit and do not silently convert failures into successful-looking behavior.
- Do not claim production security: current passwords are plaintext and payment processing is simulated.

## 6. Out of scope

- Real payment gateway or bank integrations.
- GPS/live courier location and route optimization.
- Multi-user network service, web/mobile frontend, and concurrent access.
- Production-grade identity, encryption, or regulatory compliance.
- Database migration until the intended deployment and data volume are defined.

## 7. Acceptance criteria

- An active customer can book valid Standard and Express parcels and see a charge and tracking ID.
- Invalid weights, zones, categories, customers, or inactive/missing branches are rejected before creating a parcel.
- An unassigned/inactive agent cannot update a parcel; invalid transitions do not modify status or history.
- Prepaid and COD records follow the payment rules above; duplicate payments are rejected.
- Customers cannot view payment details or cancel parcels owned by another customer.
- Operational reports agree with the in-memory parcel/payment records.
- A save/reload round trip preserves model state and tracking history; malformed rows identify the file and row.
- The code compiles with the documented JDK/PowerShell commands.
