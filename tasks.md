# Project Tasks

This file is the project-level backlog. Update task status when work is completed; use issues for larger work that needs discussion.

## Status key

- **Done**: implemented and verified in the current codebase.
- **Next**: useful work that can be picked up without a product decision.
- **Future**: requires a product decision, broader redesign, or external service.

## Done

- **Done** — Build the console workflows for customer, administrator, and delivery-agent roles.
- **Done** — Support parcel booking, Standard/Express pricing, tracking history, status transitions, and eligible cancellation.
- **Done** — Support simulated prepaid and Cash on Delivery payment lifecycles.
- **Done** — Add branch and agent management, parcel assignment, and agent performance/report summaries.
- **Done** — Persist users, branches, parcels, payments, and tracking records to local text files.
- **Done** — Validate persisted rows and references, resume ID sequences, and report file I/O errors.
- **Done** — Configure explicit IntelliJ compiler output and a VS Code launch configuration.

## Next

- **Next** — Add automated tests for booking validation, status transitions, role ownership, payment lifecycle, and persistence round trips.
- **Next** — Make persistence saves recoverable as a group: currently each text file is replaced atomically, but a failure between files can leave a mixed snapshot.
- **Next** — Add a clear startup error message for malformed or unreadable local data so users know which file and row to repair.
- **Next** — Validate and normalize all user-entered and persisted values consistently, including usernames, phone numbers, and dates.
- **Next** — Add a safe local backup/export workflow before overwriting persisted files.
- **Next** — Review the IDE launch and build instructions against a clean checkout on both IntelliJ IDEA and VS Code.

## Future

- **Future** — Replace plaintext password storage with a suitable password hashing approach before handling real accounts.
- **Future** — Move from local text files to a transactional database if multi-user or production use is intended.
- **Future** — Clarify how COD delivery confirmation, prepaid refunds, and parcel cancellation should work with a real payment provider.
- **Future** — Define routing, branch-to-branch movement, delivery areas, and service-level timing before implementing route optimization.
- **Future** — Define privacy, retention, access-control, and audit requirements before storing real customer information.
