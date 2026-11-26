# Project Memory

This document records durable project facts and conventions for future contributors and coding sessions. It is not a user-data store or a substitute for the product requirements.

## Project snapshot

- **Name:** Courier & Parcel Tracking Management System.
- **Purpose:** University/demo project showing end-to-end parcel booking, tracking, branch/agent operations, payments, reports, and local persistence.
- **Language/runtime:** Core Java; JDK 17+ is recommended. There are no third-party runtime dependencies.
- **Interface:** Interactive console application launched from `main.Main`.
- **Persistence:** UTF-8, pipe-delimited text files under `data/`; those files are local runtime data and ignored by Git.
- **Entry and wiring:** `src/main/Main.java` and `src/main/AppController.java`.

## Important current behavior

- A customer must be registered and active to book a parcel.
- Booking requires active origin and destination branches, a positive finite weight, zone 1–5, and Standard or Express category.
- Parcel status progression is enforced by `TrackingService`.
- An agent must be active and assigned to a parcel to update it; status updates use the agent's branch.
- Prepaid payment is simulated and marked paid at booking. COD remains pending until delivery is confirmed.
- Customers can cancel eligible parcels they own. Pending payment is failed and paid prepaid payment is marked refunded.
- The administrator account (`admin` / `admin123`) is hard-coded for demonstration only.
- Saved free-text fields must not include the pipe (`|`) delimiter or line breaks.
- `FileManager` reports I/O failures. `DataLoader` validates rows and gives file/line context for malformed records.
- `FileManager` atomically replaces an individual data file, but the set of all files is not saved as one transaction.

## Project conventions

- Keep console input and display in `controller`; put business rules in `service`; keep domain state in `model`.
- Reuse existing managers, services, checked exceptions, `InputValidator`, `IDGenerator`, and `DateTimeUtility`.
- Do not silently ignore malformed input, missing references, or persistence failures.
- Keep changes focused. Update `README.md` and the relevant project docs when behavior or setup changes.
- Keep personal runtime data, crash logs, IDE workspace metadata, and compiled output out of source commits.

## Verification

Compile from the project root in PowerShell:

```powershell
$files = Get-ChildItem .\src -Filter *.java -Recurse | ForEach-Object { $_.FullName }
javac -d .\out $files
```

For persistence tests, use a temporary working directory so the repository's `data/` files are not changed.
