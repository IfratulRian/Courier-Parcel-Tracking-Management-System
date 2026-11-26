# Development Rules

These are project-specific contribution rules, not runtime configuration.

## Code organization

- Follow the existing package responsibilities: menus in `controller`, business rules in `service`, data/state in `model`.
- Keep controllers focused on prompting, displaying results, and translating service errors into useful messages.
- Reuse current service/manager patterns and project exception types. Avoid adding abstractions without a concrete need.
- Use standard Java APIs and the existing JDK baseline; do not add dependencies without documenting why.
- Match existing formatting and naming conventions. Prefer clear names and small, focused methods.
- Add comments only to explain non-obvious intent.

## Correctness and data integrity

- Validate at the service boundary; menu input validation alone is insufficient.
- Preserve parcel status transition rules and verify parcel ownership/assignment at the service layer.
- Do not allow duplicate IDs/payments or references to missing/inactive records where active records are required.
- Treat persisted input as untrusted. Do not silently skip malformed rows or swallow read/write errors.
- Preserve backward compatibility of the pipe-delimited file formats unless a migration is included.
- Avoid partial success: validate prerequisites before mutating parcel, tracking, or payment state.
- Keep tracking records consistent with each accepted status change.

## Changes and verification

- Make surgical changes and avoid unrelated formatting or cleanup.
- Update `README.md` and the relevant product/design/architecture docs when behavior or setup changes.
- Compile all Java sources after code changes:

  ```powershell
  $files = Get-ChildItem .\src -Filter *.java -Recurse | ForEach-Object { $_.FullName }
  javac -d .\out $files
  ```

- Test the changed business flow, including a failure/invalid-input case when applicable.
- Use a temporary working directory for persistence tests; do not overwrite local `data/` files.
- Do not commit `out/`, local data files, JVM crash dumps, or personal IDE workspace files.
