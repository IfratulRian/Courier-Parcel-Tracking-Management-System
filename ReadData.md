# Courier & Parcel Tracking Management System - Complete Project Report

## 1. Project Overview

### 1.1 Main purpose

This project is a console-based Java application for managing courier and parcel operations. It allows customers to register, book parcels, make prepaid or Cash on Delivery payments, track delivery progress, and cancel eligible parcels. Administrators manage branches and agents and produce reports. Delivery agents update only the parcels assigned to them.

The project demonstrates object-oriented programming, layered application design, exception handling, collections, text-file persistence, and role-based workflows.

### 1.2 Problem solved

The application provides a single demo system for:

- Recording customer and parcel information.
- Generating tracking IDs and parcel charges.
- Controlling parcel movement through valid status stages.
- Assigning parcels to branch delivery agents.
- Recording prepaid and COD payment states.
- Maintaining chronological tracking history.
- Saving information between application runs.
- Producing basic operational reports.

It does not represent a production courier platform. It is intended for a local, educational demonstration.

### 1.3 Features

- Customer registration and login.
- Fixed administrator login with demo credentials `admin / admin123`.
- Delivery-agent login.
- Agent removal through account deactivation.
- Branch registration and activation/deactivation.
- Standard and Express parcel booking.
- Weight-, zone-, and category-based charge calculation.
- Prepaid and Cash on Delivery payments.
- Automatic payment receipts.
- Branch and agent-based parcel assignment.
- Validated parcel status transitions.
- Parcel cancellation and return handling.
- Chronological tracking history.
- Customer parcel lookup and profile updates.
- Administrative parcel, revenue, category, zone, and agent reports.
- Text-file loading and atomic saving.
- Input and stored-record validation.

### 1.4 Intended users

1. **Customers**: register, book parcels, track parcels, cancel eligible parcels, update profiles, and view receipts.
2. **Administrators**: manage agents and branches, assign parcels, view all parcels and payments, and see reports.
3. **Delivery agents**: view assigned parcels and update the status of only those parcels.
4. **Project evaluators and students**: study and demonstrate Java, OOP, collections, validation, persistence, and service design.

### 1.5 Complete workflow

1. `main.Main` starts `main.AppController`.
2. `AppController.start()` creates the managers, services, loader, and default administrator.
3. `DataLoader.loadAll()` restores users, branches, parcels, tracking records, and payments.
4. If no branch exists, `AppController` creates `Main Office`.
5. `MainMenu` waits for a main-menu choice.
6. A customer registers or logs in, and `CustomerMenu` provides customer operations.
7. The customer enters receiver details, weight, zone, origin/destination branches, category, and payment method.
8. `ParcelService.createParcel()` validates the data and creates a `StandardParcel` or `ExpressParcel`.
9. The parcel receives a tracking ID and a `BOOKED` tracking record.
10. `PaymentService.createPayment()` creates one prepaid or COD payment.
11. Prepaid payment is immediately marked paid in the demo. COD remains pending.
12. An administrator logs in and registers agents and branches or assigns a parcel to an active agent.
13. `DeliveryService.assignParcel()` validates the parcel and agent, then `DeliveryManager` stores the agent ID in the parcel.
14. The assigned agent updates the parcel using a valid transition.
15. `DeliveryService` verifies agent, branch, ownership, payment, and transition rules.
16. `TrackingService` changes the status and appends a timestamped `TrackingRecord`.
17. On delivery, prepaid payment must be paid; COD payment is marked paid.
18. The customer can cancel before pickup or transit, causing cancellation and payment reversal when applicable.
19. The user saves and exits through the main menu. `DataLoader.saveAll()` writes the current state to text files.
20. The next startup reloads the saved state.

### 1.6 What happens at startup

`Main.main(String[] args)` creates `AppController` and calls `start()`. `start()` loads all text data in this order:

1. Users.
2. Branches.
3. Agent branch references.
4. Parcels.
5. Tracking records.
6. Payments.

It verifies that loaded agents and parcels reference valid branches and entities. It creates a default branch only when the branch list is empty. Finally, `MainMenu.show()` opens the main menu.

---

## 2. Project Structure

### 2.1 Folder structure

```text
src/
|-- main/         Application entry point and top-level object wiring
|-- controller/   Console menus and user interaction
|-- model/        Domain entities and inheritance hierarchy
|   |-- branch/
|   |-- parcel/
|   |-- payment/
|   `-- user/
|-- service/      Business rules and in-memory operations
|   |-- branch/
|   |-- delivery/
|   |-- parcel/
|   |-- payment/
|   |-- report/
|   `-- user/
|-- filehandling/ Text-file loading and atomic saving
|-- utility/      IDs, dates, and input validation
`-- exception/    Project-specific checked exceptions

data/
|-- users.txt
|-- branches.txt
|-- parcels.txt
|-- payments.txt
`-- tracking.txt

docs/
|-- courier-system-uml.mmd
`-- courier-system-uml.svg
```

### 2.2 Counts

There are 43 Java source files:

- 37 concrete classes.
- 2 abstract classes: `Person` and `Parcel`.
- 1 interface: `PaymentMethod`.
- 3 enums: `ParcelCategory`, `ParcelStatus`, and `PaymentStatus`.
- 5 custom exception classes are included in the 37 concrete classes.

Package totals:

- `main`: 2 classes.
- `controller`: 4 classes.
- `model`: 16 files: 13 classes, 1 interface, and 3 enums.
- `service`: 11 classes.
- `filehandling`: 2 classes.
- `utility`: 3 classes.
- `exception`: 5 classes.

### 2.3 Responsibilities by package

#### `main`

- `Main`: executable entry point; calls `AppController.start()`.
- `AppController`: wires managers and services, loads data, creates the default branch, and coordinates login/logout.

#### `controller`

- `MainMenu`: displays administrator, customer, and agent choices.
- `AdminMenu`: branch/agent management, parcel assignment, payment lookup, and reports.
- `CustomerMenu`: booking, parcel listing/tracking/cancellation, receipt, prepaid demo, and profile updates.
- `AgentMenu`: assigned-parcel listing, status updates, and assigned tracking history.

#### `model.user`

- `Person`: abstract shared user fields and behavior.
- `Admin`: fixed administrator identity and `getRole()`.
- `Customer`: customer address and `getRole()`.
- `DeliveryAgent`: home branch ID and `getRole()`.

#### `model.branch`

- `Branch`: branch identity, contact details, active state, and text representation.

#### `model.parcel`

- `Parcel`: abstract shared parcel fields, tracking history, status, and abstract charge/category methods.
- `StandardParcel`: standard pricing and category.
- `ExpressParcel`: express pricing and category.
- `TrackingRecord`: one parcel state-change event.
- `ParcelCategory`: `STANDARD` or `EXPRESS`.
- `ParcelStatus`: all parcel lifecycle states.

#### `model.payment`

- `PaymentMethod`: payment interface.
- `Payment`: common payment fields and base payment processing.
- `PrepaidPayment`: prepaid method name.
- `CODPayment`: COD method name.
- `PaymentStatus`: pending, paid, failed, and refunded states.

#### `service.user`

- `UserManager`: stores users and searches by ID or username.
- `UserService`: registration, login, agent removal, and profile updates.

#### `service.branch`

- `BranchManager`: stores branches and searches by ID.
- `BranchService`: branch registration, lookup, listing, and activation.

#### `service.parcel`

- `ParcelManager`: stores/searches parcels and filters customer/agent/status lists.
- `ParcelService`: booking, parcel lookup, cancellation, and charge calculation.
- `TrackingService`: status-transition rules and tracking-record creation.

#### `service.delivery`

- `DeliveryManager`: low-level assignment and assigned-parcel retrieval.
- `DeliveryService`: assignment authorization, agent/branch validation, and delivery status processing.

#### `service.payment`

- `PaymentService`: payment creation, processing, cancellation/refund, delivery validation, and receipts.

#### `service.report`

- `ReportManager`: parcel totals, revenue, category/zone counts, and agent performance.

#### `filehandling`

- `DataLoader`: load/save serialization and persisted-record validation.
- `FileManager`: UTF-8 file reading and atomic temporary-file replacement.

#### `utility`

- `IDGenerator`: unique tracking, user, branch, and payment IDs.
- `DateTimeUtility`: date/time formatting and parsing.
- `InputValidator`: repeated numeric/input prompts and forbidden stored-text characters.

#### `exception`

- `DuplicateIdException`: duplicate username or branch.
- `InvalidParcelException`: invalid parcel operation or data.
- `InvalidPaymentException`: invalid payment operation or state.
- `InvalidStatusException`: invalid status transition or status operation.
- `RecordNotFoundException`: missing or unauthorized records.

### 2.4 `main()` location

The only `main()` method is in [Main.java](src/main/Main.java). It calls `new AppController().start()`.

### 2.5 Class communication

The normal communication path is:

```text
Main
  -> AppController
     -> MainMenu
        -> AdminMenu / CustomerMenu / AgentMenu
           -> Service classes
              -> Manager classes
                 -> Model objects
     -> DataLoader
        -> FileManager
        -> Model and manager objects
```

Managers contain collection-based in-memory state. Services apply business rules and coordinate managers and model objects. Controllers collect console input and display results. `AppController` creates and shares the managers and services.

### 2.6 Important dependencies

- `AppController` depends on all menus, services, managers, `DataLoader`, and the fixed `Admin`.
- `CustomerMenu` depends on `ParcelService`, `TrackingService`, `BranchService`, `PaymentService`, and `UserService`.
- `AdminMenu` depends on user, branch, parcel, delivery, payment, and report services.
- `AgentMenu` depends on `DeliveryService`.
- `ParcelService` depends on `ParcelManager`, `BranchManager`, `UserManager`, parcel subclasses, and `IDGenerator`.
- `DeliveryService` depends on `DeliveryManager`, `TrackingService`, `ParcelManager`, `UserManager`, `PaymentService`, and `BranchManager`.
- `PaymentService` depends on `ParcelManager`, payment subclasses, and `IDGenerator`.
- `TrackingService` depends on `ParcelManager`.
- `ReportManager` depends on `ParcelManager`, `PaymentService`, and `UserManager`.
- `DataLoader` depends on every manager/service needed to restore and validate state.

---

## 3. OOP Concepts

### 3.1 Encapsulation

Private fields are used throughout domain classes. Examples:

- `Person.id`, `name`, `username`, `password`, and `active`.
- `Parcel.trackingId`, `weight`, `zone`, `status`, and `trackingHistory`.
- `Payment.paymentId`, `amount`, `status`, and `receiptNumber`.
- `Branch.id`, `name`, `city`, and `active`.

Public getters and setters provide controlled access. For example, `Parcel.setAgentId()` converts `null` to an empty string, and `Person` exposes `isActive()` and `setActive()`.

### 3.2 Inheritance

- `Person <- Admin`
- `Person <- Customer`
- `Person <- DeliveryAgent`
- `Parcel <- StandardParcel`
- `Parcel <- ExpressParcel`
- `Payment <- PrepaidPayment`
- `Payment <- CODPayment`
- `Exception <- DuplicateIdException`, `InvalidParcelException`, `InvalidPaymentException`, `InvalidStatusException`, and `RecordNotFoundException`

Subclasses reuse their parent fields and constructors through `super(...)`.

### 3.3 Polymorphism

- A `Person` reference can hold `Admin`, `Customer`, or `DeliveryAgent`; `getRole()` has role-specific implementations.
- A `Parcel` reference can hold `StandardParcel` or `ExpressParcel`; `calculateCharge()` has category-specific implementations.
- A `Payment` reference can hold `PrepaidPayment` or `CODPayment`; `getMethodName()` is overridden.
- `instanceof` is used where code must distinguish concrete types.

Dynamic dispatch allows `calculateCharge()` and `getCategory()` to execute the subclass implementation when called through a `Parcel` reference.

### 3.4 Abstraction

`Person` and `Parcel` are abstract classes. They contain shared state and behavior but cannot be instantiated directly. They declare abstract methods that subclasses must implement:

- `Person.getRole()`
- `Parcel.calculateCharge()`
- `Parcel.calculateCharge(int)`
- `Parcel.getCategory()`

### 3.5 Interface usage

`PaymentMethod` declares `getMethodName()` and `processPayment(double amount)`. `Payment` implements this interface. This enforces a common payment API for base and subclass payments.

### 3.6 Abstract classes

`Person` and `Parcel` are the project abstract classes. `Person` provides shared credentials and identity fields. `Parcel` provides shared parcel fields and tracking history.

### 3.7 Overloading and overriding

Constructor overloading:

- `Person` has no-argument and parameterized constructors.
- `Parcel` has no-argument and parameterized constructors.
- `Customer`, `DeliveryAgent`, `Branch`, `Parcel`, payment, and tracking classes have constructors for different initialization needs.
- `StandardParcel` and `ExpressParcel` have default and full constructors.

Method overloading:

- `Parcel.calculateCharge()` and `Parcel.calculateCharge(int weightUnit)`.
- `StandardParcel` and `ExpressParcel` implement both overloaded methods.

Method overriding:

- `@Override getRole()` in `Admin`, `Customer`, and `DeliveryAgent`.
- `@Override calculateCharge()`, `calculateCharge(int)`, and `getCategory()` in parcel subclasses.
- `@Override getMethodName()` and `processPayment(double)` in `Payment`; subclasses override `getMethodName()`.

### 3.8 Constructors and `this`/`super`

Constructors initialize objects and set defaults. `this.field` disambiguates constructor parameters from fields. `super(...)` is used by subclasses to initialize parent fields.

Examples:

- `Parcel` calls `this()` from the parameterized constructor.
- `StandardParcel` calls `super(...)`.
- `Customer` calls `super(...)`.
- `CODPayment` calls `super(...)`.
- `Admin` passes empty phone/email values to `Person`.

### 3.9 Static variables and methods

Static members include:

- `IDGenerator.COUNTER`: shared ID counter.
- `IDGenerator.TRAILING_NUMBER`: pattern used for persisted-ID reservation.
- Static generator/reserve methods in `IDGenerator`.
- Static date formatters and `format()`/`parse()` in `DateTimeUtility`.
- Static input methods in `InputValidator`.
- Static file read/write methods in `FileManager`.
- Static constants for persistence file names in `DataLoader`.

`main(String[] args)` is static because the JVM calls it without an object.

### 3.10 Objects and classes

A class is a blueprint; an object is a runtime instance. For example, `Customer customer = new Customer(...)` creates a `Customer` object. `ParcelManager` creates and holds a list of `Parcel` objects. `AppController` creates and connects the services used by the menus.

References store object identity, while the class hierarchy determines which fields and methods are available.

---

## 4. Code Explanation

### 4.1 Every class and method

The short method inventory below identifies each class's purpose and public or important methods.

#### `main.Main`

- `main(String[])`: application entry point; creates `AppController` and starts it.

#### `main.AppController`

- `start()`: loads data, creates a default branch, and shows `MainMenu`.
- `createDefaultBranchIfEmpty()`: adds `Main Office` when no branch is loaded.
- `adminLogin()`: collects credentials and opens `AdminMenu` on success.
- `customerRegister()`: collects required fields and registers a customer.
- `customerLogin()`: authenticates a customer and opens `CustomerMenu`.
- `agentLogin()`: authenticates an agent and opens `AgentMenu`.
- `readRequired()`: repeatedly rejects empty console input.
- `exit()`: saves all data and closes the scanner.

#### `controller.MainMenu`

- Constructor: receives `Scanner` and `AppController`.
- `show()`: repeatedly prints and processes main menu choices.

#### `controller.AdminMenu`

- `show()`: processes administrator actions.
- `registerAgent()`: prompts for agent details and branch.
- `removeAgent()`: deactivates an agent.
- `viewAgents()`: prints customer-facing agent details.
- `registerBranch()`: creates a branch.
- `viewBranches()`: lists branches.
- `assignParcel()`: assigns or reassigns a parcel.
- `viewParcels()`: lists parcel summary information.
- `printParcel()`: prints one parcel's details.
- `viewPayment()`: prints a payment receipt.
- `showReports()`: prints administrative summaries.

#### `controller.CustomerMenu`

- `show()`: processes customer menu choices.
- `createParcel()`: collects parcel/payment details and creates records.
- `viewMyParcels()`: lists the logged-in customer's parcels.
- `trackParcel()`: prints status and chronological history.
- `cancelParcel()`: validates and cancels an eligible parcel and reverses payment.
- `viewPayment()`: prints the customer's payment receipt.
- `processPrepaid()`: marks a prepaid demo payment paid.
- `updateProfile()`: updates the customer's name, phone, email, and address.

#### `controller.AgentMenu`

- `show()`: processes agent menu choices.
- `viewAssigned()`: lists assigned parcels.
- `updateStatus()`: validates a status choice and calls `DeliveryService`.
- `viewHistory()`: validates ownership and prints a parcel's history.

#### `model.user.Person`

- Constructors: default and parameterized initialization.
- `getRole()`: abstract role name.
- Getters/setters: identity, credentials, and active state.

#### `model.user.Admin`

- Constructor: initializes ID/name/username/password and empty phone/email.
- `getRole()`: returns `ADMIN`.

#### `model.user.Customer`

- Constructors: default and parameterized.
- `getAddress()`/`setAddress()`: encapsulated address.
- `getRole()`: returns `CUSTOMER`.

#### `model.user.DeliveryAgent`

- Constructors: default and parameterized.
- `getBranchId()`/`setBranchId()`: encapsulated branch association.
- `getRole()`: returns `AGENT`.

#### `model.branch.Branch`

- Constructors: default and parameterized.
- Getters/setters: ID, name, address, city, phone, and active state.
- `toString()`: human-readable branch listing.

#### `model.parcel.Parcel`

- Constructors: default and parameterized.
- `calculateCharge()` and `calculateCharge(int)`: abstract overload pair.
- `getCategory()`: abstract category method.
- Getters/setters: tracking ID, sender/receiver, weight, zone, branches, agent, status, creation date, and history.
- `addTrackingRecord()`: appends a history record.

#### `model.parcel.StandardParcel`

- Constructors: default and full.
- `calculateCharge()`: standard base + weight + zone charge.
- `calculateCharge(int)`: charge using an explicit weight unit.
- `getCategory()`: returns `STANDARD`.

#### `model.parcel.ExpressParcel`

- Constructors: default and full.
- `calculateCharge()`: express base + weight + zone charge.
- `calculateCharge(int)`: charge using an explicit weight unit.
- `getCategory()`: returns `EXPRESS`.

#### `model.parcel.TrackingRecord`

- Parameterized constructor.
- Getters: tracking ID, old/new status, date/time, branch, agent, and comment.

#### `model.payment.PaymentMethod`

- `getMethodName()`: returns the implemented method name.
- `processPayment(double)`: processes a matching valid pending payment.

#### `model.payment.Payment`

- Parameterized constructor: creates common payment fields and receipt number.
- Getters/setters: payment ID, tracking ID, amount, status, date, and receipt.
- `getMethodName()`: base implementation returns `PAYMENT`.
- `processPayment(double)`: accepts a matching positive amount when pending.

#### `model.payment.PrepaidPayment`

- Constructor: calls the base payment constructor with pending status.
- `getMethodName()`: returns `PREPAID`.

#### `model.payment.CODPayment`

- Constructor: calls the base payment constructor with pending status.
- `getMethodName()`: returns `CASH_ON_DELIVERY`.

#### Managers

- `UserManager`: `addUser`, `removeUser`, list/get, `findById`, username/ID searches, and role-specific lists.
- `BranchManager`: `addBranch`, `findById`, and `getBranches`.
- `ParcelManager`: `addParcel`, tracking/ID searches, customer/agent lists, and status count.
- `DeliveryManager`: assignment and assigned-parcel retrieval.

#### Services

- `UserService`: `registerCustomer`, both role logins, `loginAdmin`, `registerAgent`, `removeAgent`, and profile update.
- `BranchService`: `registerBranch`, `getBranchById`, `getAllBranches`, and `setActive`.
- `ParcelService`: `createParcel`, `getParcelByTrackingId`, `getCustomerParcels`, `cancelParcel`, and `calculateCharge`.
- `TrackingService`: `isValidTransition`, `updateStatus`, and `getTrackingHistory`.
- `DeliveryService`: `assignParcel`, `reassignParcel`, `getAssignedParcels`, and `updateParcelStatus`.
- `PaymentService`: payment creation, prepaid/COD processing, delivery/cancellation validation, cancellation/refund, receipt generation, lookup, charge, and list access.
- `ReportManager`: total/delivered/pending/cancelled counts, revenue, category/zone counts, and printed reports.

#### Persistence and utilities

- `DataLoader.loadAll()` and `saveAll()` orchestrate persistence; private load/save methods serialize individual entities.
- `DataLoader.loadRows()` validates field count and wraps malformed row errors.
- `FileManager.readLines()` reads UTF-8 lines and `writeLines()` atomically replaces files.
- `IDGenerator` creates/reserves IDs.
- `DateTimeUtility.format()` and `parse()` convert display/storage dates.
- `InputValidator.readInt()`, `readDouble()`, and `readRequiredString()` repeat until valid.

### 4.2 Important methods line by line

#### `Main.main()`

1. Creates `AppController`.
2. Calls `start()`.
3. Execution then moves into startup loading and menu display.

#### `AppController.start()`

1. Calls `dataLoader.loadAll(...)` with all manager/service dependencies.
2. Calls `createDefaultBranchIfEmpty()`.
3. Calls `new MainMenu(...).show()`.

#### `ParcelService.createParcel()`

1. Validates every text field through `InputValidator.validateStoredText()`.
2. Finds the sender and confirms it is an active `Customer`.
3. Rejects non-finite or non-positive weight.
4. Rejects zones outside `1-5`.
5. Rejects a missing category.
6. Confirms both active branches exist.
7. Generates a tracking ID.
8. Constructs the correct parcel subclass.
9. Validates the calculated positive finite charge.
10. Adds an initial `BOOKED` tracking record.
11. Stores the parcel and returns it.

#### `TrackingService.updateStatus()`

1. Validates or supplies a tracking comment.
2. Finds the parcel.
3. Saves the old status.
4. Calls `isValidTransition()`.
5. Changes the parcel status.
6. Updates agent and current branch.
7. Creates and appends a timestamped `TrackingRecord`.

#### `DeliveryService.updateParcelStatus()`

1. Finds the parcel.
2. Confirms the agent is active and is the assigned agent.
3. Confirms the supplied branch matches the agent's branch.
4. Confirms the agent's branch is active.
5. For delivery, verifies the transition and payment.
6. Calls `TrackingService.updateStatus()`.
7. If delivered and COD, marks the COD payment paid.

#### `PaymentService.createPayment()`

1. Finds the parcel.
2. Confirms it is newly booked and has a valid charge.
3. Searches for an existing payment for the same tracking ID.
4. Creates `PrepaidPayment` or `CODPayment`.
5. Adds it to the in-memory payment list.

#### `DataLoader.loadAll()`

1. Loads users.
2. Loads branches.
3. Verifies agent branch references.
4. Loads parcels and verifies entity references.
5. Loads tracking history.
6. Loads payment records.

#### `FileManager.writeLines()`

1. Resolves the target path.
2. Creates its parent directory.
3. Creates a temporary file in the target directory.
4. Writes UTF-8 lines.
5. Attempts an atomic move.
6. Falls back to normal replacement if atomic move is unsupported.
7. Deletes a temporary file and adds suppressed cleanup errors on failure.

### 4.3 Execution flow

The console flow moves as follows:

```text
Main
 -> AppController
 -> MainMenu
 -> role-specific Menu
 -> Service
 -> Manager
 -> Model
 <- result or exception
 <- menu message
```

Exceptions are usually caught by menu `catch` blocks or `AppController` role methods and converted into console messages.

### 4.4 Object creation and initialization

- `AppController` creates one shared set of managers and services.
- `MainMenu`, role menus, and services receive existing objects through constructors.
- `createParcel()` uses the appropriate parcel constructor.
- `createPayment()` uses a prepaid or COD constructor.
- `TrackingService.updateStatus()` creates `TrackingRecord` objects.
- `BranchService.registerBranch()` creates a `Branch`.
- `UserService.registerCustomer()` and `registerAgent()` create user objects.

### 4.5 Data passing

Console data enters menus through `Scanner`. Menus pass primitive values and IDs to services. Services construct or modify model objects. Managers store objects in `ArrayList` collections and return references or filtered lists. Exceptions carry error messages back to menus.

### 4.6 Main business logic

- Parcel creation: `ParcelService.createParcel()`.
- Cancellation: `ParcelService.cancelParcel()` plus `PaymentService.cancelPayment()`.
- Status transitions: `TrackingService.isValidTransition()` and `updateStatus()`.
- Assignment/delivery authorization: `DeliveryService`.
- Payment lifecycle: `PaymentService`.
- Registration/login: `UserService`.
- Reports: `ReportManager`.
- Persistence: `DataLoader` and `FileManager`.

### 4.7 Unnecessary or duplicate methods

No method is obviously unused, but several can be called through duplicated authorization or validation layers:

- `ParcelService.cancelParcel()` and `CustomerMenu.cancelParcel()` both verify parcel ownership.
- `DeliveryService.reassignParcel()` simply calls `assignParcel()`.
- `Parcel.calculateCharge()` is called twice in `createParcel()` validation, so it could calculate once and store the result.
- `PaymentService.calculateTotalCharge()` duplicates `parcel.calculateCharge()`.
- `ParcelService.calculateCharge()` is a thin wrapper and may not be necessary.
- `AdminMenu.viewPayment()` declares broad `throws Exception` instead of the narrower `RecordNotFoundException`.
- `UserManager.removeUser()` uses case-sensitive ID matching while most lookup methods are case-insensitive.

---

## 5. Data Management

### 5.1 Storage mechanism

The project uses pipe-delimited plain text files in `data/`; it does not use a database or external persistence library.

### 5.2 Data files

`users.txt` format:

```text
ID|ROLE|NAME|PHONE|EMAIL|USERNAME|PASSWORD|ADDRESS_OR_BRANCH_ID|ACTIVE
```

`branches.txt` format:

```text
ID|NAME|ADDRESS|CITY|PHONE|ACTIVE
```

`parcels.txt` format:

```text
TRACKING_ID|CATEGORY|SENDER_ID|RECEIVER_NAME|RECEIVER_PHONE|RECEIVER_ADDRESS|WEIGHT|ZONE|ORIGIN_BRANCH_ID|DESTINATION_BRANCH_ID|CURRENT_BRANCH_ID|AGENT_ID|STATUS|CHARGE|CREATED_DATE
```

`payments.txt` format:

```text
PAYMENT_ID|TRACKING_ID|METHOD|STATUS|AMOUNT|PAYMENT_DATE|RECEIPT_NUMBER
```

`tracking.txt` format:

```text
TRACKING_ID|OLD_STATUS|NEW_STATUS|DATE_TIME|BRANCH_ID|AGENT_ID|COMMENT
```

### 5.3 Insert, retrieve, update, and delete

- Insert: manager `add...()` methods append objects to `ArrayList` collections.
- Retrieve: manager lookup methods scan lists by ID, username, customer, or status.
- Update: model setters, status changes, agent assignment, profile updates, and payment status changes mutate objects.
- Delete: `UserManager.removeUser()` removes agents; the user service actually deactivates agents instead of fully deleting them.
- Branches and parcels are not removed through normal menu operations.

### 5.4 Persistence

Normal exit through `AppController.exit()` calls `saveAll()`. It writes users, branches, parcels, payments, and tracking history. The next startup reloads them.

Data is not saved automatically after every menu operation. If the process is terminated without the save-and-exit choice, recent unsaved in-memory changes may be lost.

### 5.5 Entity relationships

- One `Customer` sender may have many parcels.
- One `Branch` can be origin/destination/current branch for many parcels.
- One `DeliveryAgent` belongs to one branch and may handle many parcels.
- One parcel belongs to one sender and may have many tracking records.
- One parcel has at most one payment record.
- One payment points to one parcel through tracking ID.

### 5.6 Collections

- `ArrayList<Person>`: users.
- `ArrayList<Branch>`: branches.
- `ArrayList<Parcel>`: parcels.
- `ArrayList<Payment>`: payments.
- `ArrayList<TrackingRecord>`: each parcel's history.
- `AtomicInteger`: thread-safe ID counter.
- `List.of(...)` and streams: immutable/filtered result handling.

`ArrayList` is suitable for this console demo because it is simple and the data size is small. A database would be needed for large, concurrent, or relational data.

---

## 6. Exception Handling and Validation

### 6.1 Exception handling locations

- Controller menus catch broad `Exception` to prevent a menu operation from terminating the loop.
- `AppController` catches `DuplicateIdException` and `RecordNotFoundException`.
- `CustomerMenu` catches payment exceptions and wrap them as parcel exceptions.
- `InputValidator` catches `NumberFormatException`.
- `FileManager` catches `IOException` and throws `UncheckedIOException`.
- `DataLoader` catches `RuntimeException` in row loading and throws `IllegalStateException` with file/line context.
- Services throw custom checked exceptions for invalid domain operations.

### 6.2 Possible exceptions

- `DuplicateIdException`: existing username or branch.
- `RecordNotFoundException`: missing customer, agent, parcel, branch, or payment.
- `InvalidParcelException`: invalid weight, zone, category, sender, or cancellation state.
- `InvalidPaymentException`: invalid method, duplicate payment, invalid delivery/cancellation state.
- `InvalidStatusException`: unknown or illegal status transition.
- `NumberFormatException`: nonnumeric console input.
- `IllegalArgumentException`: empty or forbidden stored text.
- `IllegalStateException`: malformed persisted data or broken references.
- `UncheckedIOException`: file read/write failure.
- `DateTimeParseException`/enum parsing errors may occur through malformed data and are wrapped by `DataLoader.loadRows()`.

### 6.3 Input validation

- Required console strings cannot be empty.
- Integer input repeats until `Integer.parseInt()` succeeds.
- Double input repeats until `Double.parseDouble()` succeeds.
- Weight must be finite and positive.
- Zone must be `1-5`.
- Category and payment choices must be `1/2` or valid method names.
- Stored text cannot be empty, contain `|`, or contain line breaks.
- IDs and enum values are validated by services and loader.

### 6.4 Invalid user input

The menu prints an error and returns to the menu. Numeric input helpers show the prompt again. Invalid login, missing record, illegal status, or duplicate registration are handled by catch blocks and displayed as a message.

### 6.5 Crash prevention

Repeated invalid numeric input is handled. Active branches, agents, customer ownership, and parcel/payment state are checked before operations. Persistence validates field counts and required references. However, startup can stop if persisted data is severely malformed because `DataLoader` throws `IllegalStateException`.

Broad menu catches are convenient but can hide programming errors. Narrower catches would improve maintainability and debugging.

---

## 7. Technical Understanding

### 7.1 Java concepts and libraries

- Packages and imports.
- Classes, abstract classes, interfaces, inheritance, and polymorphism.
- Private fields and getter/setter encapsulation.
- Constructors and constructor/ method overloading.
- `this` and `super`.
- Enums and switch statements.
- Custom checked exceptions.
- `ArrayList`, `List`, streams, and lambda expressions.
- `Scanner`.
- `LocalDateTime` and `DateTimeFormatter`.
- UTF-8 `NIO` file APIs.
- Atomic file moves.
- `AtomicInteger`.
- Regular expressions.
- `try/catch` and try-with-resources.
- `instanceof`.

### 7.2 Loops and conditions

- Menu loops use `while (true)`.
- Input repeat loops use `while (true)`.
- `for` loops search lists and count reports.
- `switch` statements process menu choices.
- `if/else` and ternary expressions validate roles, categories, payment methods, and status transitions.
- Streams filter active branches in `CustomerMenu`.

### 7.3 Multithreading

No application thread or `Thread`/executor is implemented. `AtomicInteger` is a thread-safe utility, but it does not mean the application implements multithreading. The counters are used to prevent duplicate sequential ID numbers, especially after loading saved IDs.

### 7.4 Execution sequence

```text
JVM calls Main.main()
  -> AppController.start()
  -> DataLoader.loadAll()
  -> create default branch if needed
  -> MainMenu.show()
  -> role menu
  -> service/manager/model method
  -> exception or success
  -> return to menu
  -> save and exit
  -> DataLoader.saveAll()
```

### 7.5 Internal feature execution

#### Registration

`AppController.customerRegister()` reads fields, calls `UserService.registerCustomer()`, checks duplicate username, creates `Customer`, adds it to `UserManager`, and reports success.

#### Parcel booking

`CustomerMenu` reads input and calls `ParcelService.createParcel()`. The service validates entities and creates a subclass. `ParcelService` adds a tracking record. `PaymentService` creates the payment.

#### Assignment

`AdminMenu` calls `DeliveryService.assignParcel()`. The service validates parcel and agent, then `DeliveryManager` writes the agent ID into the parcel.

#### Status update

`AgentMenu` calls `DeliveryService.updateParcelStatus()`. The service checks authorization and payment; `TrackingService` verifies transition rules and appends a record.

#### Reports

`ReportManager` iterates parcel and payment lists and prints totals and filtered values.

---

## 8. Viva Preparation

### 8.1 30 important viva questions

1. What is the purpose of this project?
2. How does `Main` start the application?
3. What responsibility does `AppController` have?
4. Why is the code divided into controller, service, manager, and model packages?
5. What is the role of `Person`?
6. Why is `Person` abstract?
7. What is encapsulation in this project?
8. How does inheritance appear in the user hierarchy?
9. How does inheritance appear in the parcel hierarchy?
10. What is polymorphism, and where is it used?
11. Where is abstraction implemented?
12. What does `PaymentMethod` add to the design?
13. Which classes use constructor overloading?
14. Which method is overloaded in `Parcel`?
15. Which methods are overridden?
16. Explain `this` and `super` with examples.
17. What are static variables and methods?
18. Why is `IDGenerator` not instantiated?
19. Which collections are used and why?
20. How are objects passed between controllers and services?
21. How is a parcel's charge calculated?
22. What is the difference between Standard and Express parcels?
23. How does the status transition table prevent invalid updates?
24. How does the system ensure a parcel belongs to an agent?
25. How are tracking records stored?
26. How does persistence work?
27. How are text records loaded and validated?
28. Why is file saving atomic?
29. What custom exceptions are used?
30. What are the project's major limitations and improvements?

### 8.2 Most complicated parts

1. Delivery status authorization: ownership, branch, active state, transition, and payment must all align.
2. `DataLoader`: it reconstructs subclass objects and validates cross-file references.
3. Cancellation: parcel state and prepaid/COD payment state must be reversed consistently.
4. Tracking history: every status change must append a correctly linked record.
5. Polymorphic parcel/payment charge and method behavior.
6. ID generation and reservation after loading persisted records.
7. Atomic text-file persistence.

### 8.3 Limitations

- No real database.
- Plain-text passwords.
- Fixed administrator credentials in source code.
- Simulated payments.
- No encryption or secure authentication.
- No real route calculation or address geocoding.
- Numeric zones are only distance bands.
- Data saving is explicit rather than automatic.
- The code uses broad `Exception` catches in menus.
- No multithreading or concurrent-write protection.
- No graphical interface or network API.
- File parsing may reject an entire startup if a record is malformed.
- There is no formal test suite described in the repository.

### 8.4 Future improvements

- Replace text files with SQLite or another relational database.
- Hash and salt passwords.
- Store administrator credentials securely and add configurable accounts.
- Integrate a real payment provider.
- Add database constraints and transactions.
- Add unit and integration tests.
- Add role/permission classes instead of menu-only role separation.
- Add route, branch, GPS, and distance calculations.
- Add recipient, agent, and customer notifications.
- Add search, sorting, and pagination.
- Replace console UI with a GUI or web interface.
- Implement structured JSON/XML or database serialization.
- Add automatic periodic saving and safer multi-process access.
- Narrow exception catches and centralize error handling.
- Prevent status/payment inconsistency using transactional operations.

### 8.5 Questions an OOP teacher may ask

- Why does `Parcel` have two constructors?
- Why is `Parcel` abstract rather than concrete?
- What would happen if `calculateCharge()` were not overridden?
- What is dynamic dispatch in `calculateCharge()`?
- Why does `Payment` implement an interface instead of being abstract?
- Is `PrepaidPayment` truly polymorphic if it only overrides `getMethodName()`?
- Why are setters used instead of directly editing fields?
- Could `getRole()` be implemented by fields instead of inheritance?
- How are `Person` and `Parcel` protected from direct instantiation?
- Why does `CODPayment` call `super()` and pass `PENDING`?
- Where is composition used? (`Parcel` contains `TrackingRecord` and services contain managers.)
- Where is association used? (`Parcel` stores branch and agent IDs.)
- What is the difference between aggregation and composition in this design?
- Why are exceptions classes used instead of ordinary strings?
- Which methods should be `final`, private, or static?

### 8.6 Possible mistakes or weak implementations

- Passwords are plain text and are saved directly.
- The administrator login is fixed in code.
- Payment processing is simulated and does not validate external funds.
- The menu catch blocks catch broad `Exception`, potentially hiding bugs.
- Many manager methods accept mutable objects and lists.
- Some methods only return `false` instead of throwing domain-specific exceptions.
- `removeUser()` can differ from case-insensitive lookup behavior.
- Cancellation ownership is checked in both controller and service.
- The charge is recalculated multiple times in some operations.
- `DeliveryManager` does not perform authorization; that is delegated to `DeliveryService`.
- Tracking comments permit an empty value by some menu routes.
- No formal tests, transactions, locking, or concurrent data protection exist.
- Data is saved only at explicit exit.
- The plain-text format has no escaping mechanism despite `|` validation.
- Text values and enum names could become inconsistent if files are edited manually.

---

## 9. Personal Contribution

### 9.1 Most important parts to understand

Focus on these areas for presentation:

1. `Main` and `AppController`: startup and dependency wiring.
2. `Person` hierarchy: inheritance and polymorphism.
3. `Parcel` hierarchy: abstract business model and category pricing.
4. `Payment`/`PaymentMethod`: interface and prepaid/COD inheritance.
5. `ParcelService`: booking and cancellation.
6. `TrackingService`: status transitions and history.
7. `DeliveryService`: authorization and status processing.
8. `PaymentService`: payment lifecycle.
9. `DataLoader` and `FileManager`: persistence.
10. `IDGenerator` and `InputValidator`: generated IDs and input safety.

### 9.2 Classes and methods to memorize

Memorize:

- `Main.main()`
- `AppController.start()` and `exit()`
- `ParcelService.createParcel()` and `cancelParcel()`
- `TrackingService.isValidTransition()` and `updateStatus()`
- `DeliveryService.assignParcel()` and `updateParcelStatus()`
- `PaymentService.createPayment()` and `validateForDelivery()`
- `DataLoader.loadAll()` and `saveAll()`
- `UserService.loginCustomer()` and `loginAgent()`
- `ReportManager.printSummary()`
- `Person.getRole()`
- `Parcel.calculateCharge()` and `getCategory()`

### 9.3 Complete presentation script

“Good morning/afternoon. Our project is a console-based Courier & Parcel Tracking Management System developed in Java.

The system has three users: customers, administrators, and delivery agents. A customer can register and log in, book a parcel, choose a delivery category and payment method, and track the parcel. An administrator can manage branches and delivery agents, assign parcels, view payments, and produce reports. A delivery agent can update only assigned parcels.

The application starts from `Main.main()`. `Main` creates `AppController`, which loads the saved data using `DataLoader`, creates a default branch if necessary, and opens `MainMenu`.

The project uses a layered structure. Controllers handle console input. Services implement business rules. Managers store objects in `ArrayList` collections. Models represent users, branches, parcels, tracking records, and payments.

For OOP, `Person` is an abstract base class for `Admin`, `Customer`, and `DeliveryAgent`. `Parcel` is an abstract base class for `StandardParcel` and `ExpressParcel`. Both use polymorphism and method overriding. `PaymentMethod` is an interface implemented by `Payment`.

When a customer books a parcel, `ParcelService` validates the sender, weight, zone, category, and branches. It generates a tracking ID and creates a Standard or Express parcel. The charge is calculated by the selected subclass. The initial status is `BOOKED`, and the system creates a tracking record.

The payment service creates one prepaid or COD payment. Prepaid is marked paid immediately in this demo. COD remains pending until delivery.

The administrator assigns the parcel to an active delivery agent. `DeliveryService` verifies the parcel, agent, branch, and delivery authorization. The agent can then change the status only through valid transitions such as `BOOKED -> PICKED_UP -> IN_TRANSIT -> ARRIVED_AT_HUB -> OUT_FOR_DELIVERY -> DELIVERED`.

For delivery, the system validates the payment. A prepaid parcel must already be paid, and COD is marked paid during delivery. `TrackingService` changes the parcel status and adds a timestamped history record.

Data is stored in pipe-delimited text files under `data/`. `DataLoader` loads and validates all users, branches, parcels, payments, and tracking records at startup. `FileManager` writes UTF-8 files through a temporary file and atomic replacement before normal exit.

The project demonstrates inheritance, polymorphism, abstraction, encapsulation, interfaces, constructors, overloading, overriding, `this`, `super`, static utilities, collections, exception handling, and file I/O.

Its main limitations are that it is a console demo, passwords are stored in plain text, payments are simulated, and it does not use a real database or secure authentication. Future improvements include database integration, password hashing, real payment integration, unit tests, and a graphical or web interface.”

### 9.4 Technical code questions

1. Why is `Main` not in the controller package?
2. Which class owns the `Scanner`, and why is it shared?
3. How does `AppController` share data across role menus?
4. Why is `Person` abstract?
5. Why does `Parcel` use abstract charge methods?
6. How does `instanceof` help with polymorphism?
7. Which fields are private, and how are they accessed?
8. How does `super(...)` work in `Customer` and `ExpressParcel`?
9. How would adding a third parcel category affect the project?
10. How does `StandardParcel.calculateCharge(int)` differ from `calculateCharge()`?
11. Why does `TrackingService` use a list but not a separate tracking map?
12. How are status transitions enforced?
13. How does the service prevent an agent from updating another agent's parcel?
14. Why is branch validation required during status updates?
15. What happens internally if COD delivery is attempted before a payment exists?
16. How does `PaymentService` prevent a duplicate payment?
17. How is cancellation payment reversal implemented?
18. Why is a payment state separate from the parcel state?
19. How is the current branch updated during tracking?
20. Why are custom exceptions better than only printing errors?
21. Which exceptions are checked and which are runtime exceptions?
22. How does `DataLoader` reconstruct subclasses?
23. How are duplicate IDs prevented after loading files?
24. What is the purpose of `reserve()` in `IDGenerator`?
25. How does `FileManager` reduce the chance of partial file replacement?
26. What happens if `data/` does not exist?
27. What happens if a loaded parcel references a missing customer or branch?
28. What is the difference between `CANCELLED` and `RETURNED`?
29. Which operations could benefit from a database transaction?
30. How would you add a formal test suite for this project?

## 10. What Is New or Special About This Project?

This project is not presented as a completely new courier technology. Its special feature is how it brings several practical courier workflows together in one small Java application and demonstrates the rules behind them:

- **Three role-based workflows:** customers book and track parcels, administrators manage operations, and agents update only their assigned deliveries.
- **End-to-end parcel lifecycle:** a parcel progresses through explicit states from booking to delivery, cancellation, or return. Invalid transitions are rejected.
- **Auditable tracking history:** each successful status change records the old and new states, date/time, branch, agent, and comment.
- **Two parcel pricing models:** Standard and Express parcels calculate charges differently through polymorphism.
- **Two payment lifecycles:** prepaid payments and COD follow different rules, with COD collected only after delivery and prepaid payments eligible for a refund when cancelled.
- **Connected operational data:** parcels reference customers, branches, agents, tracking records, and payments, and the loader checks those references when restoring data.
- **Persistence without a database:** the application validates and saves related data to separate text files, using temporary-file replacement to reduce partial-write risk.
- **OOP concepts applied to a working example:** abstract classes, inheritance, an interface, overriding, overloading, encapsulation, and custom exceptions support actual application behaviors.

### Short viva answer

“The special part of our project is that it connects the complete parcel workflow rather than only storing parcel details. It supports role-based customer, administrator, and agent actions; category-based pricing; prepaid and COD payment rules; validated parcel status transitions; and a timestamped tracking history. It also persists related data in text files and validates references when loading. It is an educational demonstration, not a claim of a unique or production-ready courier platform.”
