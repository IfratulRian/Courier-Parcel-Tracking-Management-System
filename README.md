# Courier & Parcel Tracking Management System

A beginner-friendly, console-based Java application for booking parcels, tracking their status, managing branches and delivery agents, recording payments, and viewing basic reports. It uses core Java, object-oriented programming, and plain text files; it does not require a database or external libraries.

> **Project status:** This is a university learning/demo project. It is not intended for real customer data or production use. Passwords are stored as plain text in local files.

## Contents

- [Features](#features)
- [Requirements](#requirements)
- [Run the application](#run-the-application)
- [First-time walkthrough](#first-time-walkthrough)
- [Administrator login](#administrator-login)
- [Project structure](#project-structure)
- [Class responsibilities](#class-responsibilities)
- [OOP concepts demonstrated](#oop-concepts-demonstrated)
- [Parcel status transitions](#parcel-status-transitions)
- [Data files and persistence](#data-files-and-persistence)
- [Project documents](#project-documents)
- [Known limitations](#known-limitations)

## Features

- Console menus for administrators, customers, and delivery agents.
- Customer registration, login, and profile updates.
- Parcel booking with generated tracking IDs, receiver details, parcel weight, delivery zone, and Standard or Express category.
- Parcel charge calculation based on category, weight, and zone.
- Parcel lookup, chronological tracking history, and cancellation before the parcel enters transit.
- Branch registration and agent registration/removal (removal deactivates the agent).
- Parcel assignment to active agents; agents can update only parcels assigned to them.
- Prepaid payments are simulated at booking; Cash on Delivery is collected when delivery is confirmed.
- Cancellation changes pending payments to failed and marks completed prepaid payments as refunded.
- Parcel, zone, service-category, revenue, and delivery-agent performance reports.
- Validation for stored text fields, finite parcel weights/charges, valid branches, and status transitions.
- Text-file persistence for users, branches, parcels, payments, and tracking history.

## Requirements

- Java Development Kit (JDK); JDK 17 or later is recommended.
- IntelliJ IDEA or another Java IDE, or a terminal.
- No third-party libraries are required.

## Run the application

### IntelliJ IDEA

1. Open the project folder in IntelliJ IDEA.
2. Set a JDK under **File → Project Structure → Project → SDK**.
3. Open `src/main/Main.java`.
4. Click the run icon beside `main`, or right-click the file and choose **Run 'Main.main()'**.
5. Use the **Run** tool window at the bottom as the console. Enter a menu number and press Enter.

The `.iml` and `.idea` files configure this project as an IntelliJ Java module. The `.vscode` files provide source and launch settings for VS Code.

### PowerShell

Run these commands from the project root:

```powershell
$files = Get-ChildItem .\src -Filter *.java -Recurse | ForEach-Object { $_.FullName }
javac -d .\out $files
java -cp .\out main.Main
```

The program is text-based: its interface appears in the IDE's Run panel or terminal, not in a separate window.

## First-time walkthrough

1. At the main menu, enter `2` to register a customer. Enter each requested value and press Enter.
2. Enter `3` to log in with the username and password you registered.
3. In the customer menu, enter `1` to book a parcel.
4. Enter receiver information, weight in kilograms, and a zone from `1` to `5`.
5. Select an origin and destination branch from the displayed branch list. A default `Main Office` branch is created if no branches are loaded.
6. Select Standard or Express, then select prepaid or Cash on Delivery. Prepaid is marked paid in the demo; COD is marked paid after an agent confirms delivery.
7. Save the displayed tracking ID. Use customer menu option `3` to look up that parcel.
8. Use option `8` in the customer menu to log out. At the main menu, choose `5` to save data and exit.

To try the administrator workflow, log in with the demo credentials below. Register an agent only after confirming that a branch exists. An agent can then log in and view parcels assigned to them.

## Administrator login

The demo administrator is created in `AppController`; it is not stored in `users.txt`.

| Username | Password |
|---|---|
| `admin` | `admin123` |

These are public demonstration credentials, not secure credentials.

## Project structure

```text
CourierManagementSystem/
├── .idea/                   IntelliJ project configuration
├── .vscode/                 VS Code Java launch/settings
├── data/                    Runtime text files (ignored by Git)
├── src/
│   ├── controller/          Console menus and user interaction
│   ├── exception/            Application-specific checked exceptions
│   ├── filehandling/         Text file reads, writes, and object loading
│   ├── main/                 Application entry point and dependency setup
│   ├── model/                User, parcel, branch, and payment objects
│   ├── service/              Business operations and in-memory managers
│   └── utility/              Input, ID, and date/time helpers
├── .gitignore
├── CourierManagementSystem.iml
└── README.md
```

## Class responsibilities

### Application entry and console controllers

| Class | Package | Responsibility |
|---|---|---|
| `Main` | `main` | Java entry point; starts `AppController`. |
| `AppController` | `main` | Creates managers and services, loads data, creates a default branch if needed, and opens the main menu. |
| `MainMenu` | `controller` | Shows the welcome instructions and routes users to administrator, customer, or agent login/registration. |
| `AdminMenu` | `controller` | Prompts an administrator to manage agents and branches, assign parcels, inspect parcels/payments, and view reports. |
| `CustomerMenu` | `controller` | Prompts a customer to book, track, view, or cancel parcels, view payments, and edit their profile. |
| `AgentMenu` | `controller` | Lets a delivery agent list assigned parcels, update statuses, and view assigned parcel histories. |

Menus collect console input and call services. They are not meant to be the source of the business rules.

### User models

| Class | Package | Responsibility |
|---|---|---|
| `Person` | `model.user` | Abstract shared user data: ID, name, contact details, login credentials, and active state. |
| `Admin` | `model.user` | Administrator identity and `ADMIN` role. |
| `Customer` | `model.user` | Customer profile, including address, and `CUSTOMER` role. |
| `DeliveryAgent` | `model.user` | Agent profile, assigned home branch, and `AGENT` role. |

### Parcel and tracking models

| Class | Package | Responsibility |
|---|---|---|
| `Parcel` | `model.parcel` | Abstract shared parcel details, status, and tracking history; declares charge and category behavior. |
| `StandardParcel` | `model.parcel` | Implements Standard parcel charge calculation. |
| `ExpressParcel` | `model.parcel` | Implements Express parcel charge calculation. |
| `ParcelCategory` | `model.parcel` | Enumerates `STANDARD` and `EXPRESS`. |
| `ParcelStatus` | `model.parcel` | Enumerates the supported parcel statuses. |
| `TrackingRecord` | `model.parcel` | Stores one status-change record with time, branch, agent, and comment. |

### Branch and payment models

| Class | Package | Responsibility |
|---|---|---|
| `Branch` | `model.branch` | Stores branch identity, location/contact information, and active state. |
| `Payment` | `model.payment` | Stores payment ID, parcel tracking ID, amount, status, date, and receipt number; provides the common payment behavior. |
| `PaymentMethod` | `model.payment` | Interface for naming and processing a payment method. |
| `PrepaidPayment` | `model.payment` | Identifies a prepaid payment and starts it as pending. |
| `CODPayment` | `model.payment` | Identifies a Cash on Delivery payment and starts it as pending. |
| `PaymentStatus` | `model.payment` | Enumerates `PENDING`, `PAID`, `FAILED`, and `REFUNDED`. |

### Services and managers

| Class | Package | Responsibility |
|---|---|---|
| `UserService` | `service.user` | Registers and authenticates customers/agents, checks the demo administrator credentials, and updates customer profiles. |
| `UserManager` | `service.user` | Holds users in memory and finds users, customers, and agents. |
| `ParcelService` | `service.parcel` | Validates parcel booking, creates the Standard/Express object, computes charges, and applies cancellation rules. |
| `ParcelManager` | `service.parcel` | Holds parcels in memory and looks them up by tracking ID, customer, agent, or status. |
| `TrackingService` | `service.parcel` | Checks allowed status transitions and adds tracking records for status updates. |
| `BranchService` | `service.branch` | Registers branches, retrieves them, and changes their active state. |
| `BranchManager` | `service.branch` | Holds branches in memory and looks them up by ID. |
| `DeliveryService` | `service.delivery` | Checks parcel and active agent before assignment; routes status updates to `TrackingService`. |
| `DeliveryManager` | `service.delivery` | Assigns parcels and lists parcels assigned to an agent. |
| `PaymentService` | `service.payment` | Creates payment records, enforces prepaid/COD lifecycle rules, handles cancellation/refunds, and builds receipts. |
| `ReportManager` | `service.report` | Summarizes statuses, zones, service categories, payment-method revenue, and agent delivery outcomes. |

### File handling, exceptions, and utilities

| Class | Package | Responsibility |
|---|---|---|
| `FileManager` | `filehandling` | Reads and writes UTF-8 text files, atomically replacing each file on save and surfacing I/O failures. |
| `DataLoader` | `filehandling` | Validates saved row shapes/references, restores model objects and ID sequence state at startup, and serializes current objects on exit. |
| `InvalidParcelException` | `exception` | Reports invalid parcel details or operations. |
| `InvalidStatusException` | `exception` | Reports missing parcels or disallowed status changes. |
| `InvalidPaymentException` | `exception` | Reports invalid or duplicate payment actions. |
| `DuplicateIdException` | `exception` | Reports duplicate user/branch registration details. |
| `RecordNotFoundException` | `exception` | Reports a missing user, branch, parcel, or payment. |
| `InputValidator` | `utility` | Reads required text and retries invalid integer/decimal input. |
| `IDGenerator` | `utility` | Generates tracking, user, branch, and payment IDs using a shared counter that resumes beyond loaded IDs. |
| `DateTimeUtility` | `utility` | Formats and parses date/time values used for display and storage. |

## OOP concepts demonstrated

| Concept | Where it appears |
|---|---|
| Encapsulation | Model fields are private and accessed through getters/setters or methods. |
| Inheritance | `Customer`, `Admin`, and `DeliveryAgent` extend `Person`; parcel types extend `Parcel`; payment types extend `Payment`. |
| Abstraction | `Person` and `Parcel` are abstract base classes that describe shared state and required behavior. |
| Polymorphism | `Parcel.calculateCharge()` and `getCategory()` resolve to the Standard or Express implementation; `getMethodName()` resolves to the payment subtype. |
| Interfaces | `PaymentMethod` defines the common payment operations implemented by `Payment` and its subclasses. |
| Method overriding | Subclasses override `getRole()`, parcel charge/category methods, and payment method naming. |
| Method overloading | Parcel charge calculation has a no-argument and a weight-unit overload. Constructors also have different parameter lists. |
| Association | Parcels store customer, branch, and delivery-agent IDs to relate the records. |
| Aggregation | Managers hold collections of independently created users, branches, parcels, and payments. |
| Composition | A parcel owns its list of `TrackingRecord` objects. |
| Exception handling | Services throw project-specific exceptions; menus catch errors and display a message. |
| File handling | `FileManager` and `DataLoader` read/write the local text files. |
| Static members | `IDGenerator` uses a shared static counter; `DateTimeUtility` exposes shared formatters and helper methods. |
| Constructors | Model objects are initialized through no-argument or parameterized constructors. |
| Getters and setters | Models expose accessors for their encapsulated fields. |

## Parcel status transitions

`TrackingService` permits the following transitions:

```text
BOOKED            -> PICKED_UP, CANCELLED
PICKED_UP          -> IN_TRANSIT, CANCELLED
IN_TRANSIT         -> ARRIVED_AT_HUB, RETURNED
ARRIVED_AT_HUB     -> OUT_FOR_DELIVERY, IN_TRANSIT
OUT_FOR_DELIVERY   -> DELIVERED, RETURNED
DELIVERED          -> no further transition
CANCELLED          -> no further transition
RETURNED           -> no further transition
```

Cancellation through the customer workflow is limited to parcels in `BOOKED` or `PICKED_UP`.

## Data files and persistence

Runtime data is stored in the `data/` directory, relative to the application's working directory:

| File | Contents |
|---|---|
| `users.txt` | Customer and delivery-agent profiles and login details. The demo admin is not stored here. |
| `branches.txt` | Branch ID, name, address, city, phone, and active state. |
| `parcels.txt` | Parcel details, status, assigned IDs, and calculated charge. |
| `tracking.txt` | Parcel status-history entries, including timestamps and comments. |
| `payments.txt` | Payment details, status, date, and receipt number. |
| `agents.txt` | Currently unused; agent records are stored in `users.txt`. |

Rows use `|` between fields, so persisted text values cannot contain that delimiter or line breaks. The application loads data at startup and saves when the user chooses **Save data and exit**. Each data file is replaced atomically; exit through the menu to save the complete current session. Malformed rows, broken parcel references, and file read/write errors are reported rather than silently ignored. The `data/*.txt` files are ignored by Git so local records are not published with source code.

## Project documents

- [`prd.md`](./prd.md) — Goals, roles, functional/non-functional requirements, scope, and acceptance criteria.
- [`architecture.md`](./architecture.md) — Package boundaries, workflows, persistence, and technical constraints.
- [`design.md`](./design.md) — Console interaction flows and experience principles.
- [`rules.md`](./rules.md) — Development and verification conventions.
- [`tasks.md`](./tasks.md) — Completed work and prioritized project backlog.
- [`memory.md`](./memory.md) — Durable project facts and working context for future contributors.

## Known limitations

- This is a local demonstration app; it has no database, encryption, or secure password storage.
- The administrator account is fixed in source code and is not configurable through the menus.
- Payment processing is simulated; there is no real payment provider or receipt document.
- Zones are simple numeric distance bands, not addresses or route calculations.
- Branch movement is represented by the parcel's current branch and tracking record; there is no routing engine.
- User and parcel data is kept in memory while the program runs and is written to text files on normal exit.
- `agents.txt` is present for the initial project layout but is not used by the current loader.
