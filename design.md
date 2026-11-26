# Design Notes

## Product experience

The application is a guided, text-only workflow. Menus display numbered choices; prompts request one value at a time; successful actions show the generated ID, current status, charge, or receipt. Errors should be explicit and actionable, then return the user to the relevant menu when possible.

## Role navigation

### Main menu

- Admin login.
- Customer registration and login.
- Delivery-agent login.
- Save data and exit.
- Show a short first-time-use walkthrough and identify the demo administrator as demonstration-only.

### Customer menu

- Book a parcel.
- List own parcels.
- Track a parcel and inspect its history.
- Cancel an eligible parcel owned by the signed-in customer.
- View own payment receipt or complete a pending prepaid payment if applicable.
- Update profile and log out.

### Administrator menu

- Register/deactivate agents and register/list branches.
- Assign parcels.
- Inspect parcels and payment receipts.
- View operational and agent reports.
- Log out.

### Agent menu

- List assigned parcels.
- Update status using only a permitted next status.
- View history for an assigned parcel.
- Log out.

## Interaction principles

- State units and valid ranges in prompts (weight in kilograms; zone 1–5).
- Display active branches and explain when none are available.
- Do not ask for a payment choice until required booking details have been validated.
- After booking, show the tracking ID, charge, payment method, and resulting payment state clearly.
- For status updates, request a comment and show the successful new state.
- Distinguish invalid user input, missing records, and persistence errors; do not print success after failure.
- Avoid exposing another customer's parcel or payment information.

## Pricing and status

- Standard and Express formulas are implemented in their parcel model classes. Treat those formulas as authoritative until the product owner approves a change.
- The status lifecycle is defined by `TrackingService.isValidTransition` and summarized in `README.md`.
- Cancellation is available only in the documented eligible statuses; payment outcome follows the payment service rules.

## Accessibility and maintainability

- Use plain language, consistent labels, and clear menu ranges.
- Keep output readable in a standard terminal; do not rely on color, mouse input, or a specific terminal width.
- Keep presentation in controller classes and business decisions in services.
- When a menu changes, update the README walkthrough and this design document together.

## Current UI limitations

- The interface is English-only and console-only.
- There is no pagination, search/filter UI, confirmation prompt for destructive operations, or route map.
- These are potential enhancements, not assumed requirements.
