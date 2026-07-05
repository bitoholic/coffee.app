# Feature Specification: Coffee Brewing Settings Tracker MVP

**Feature Branch**: `develop`

**Created**: 2026-07-05

**Status**: Draft

**Input**: Combined Telegram app brief for the coffee.app MVP: build a local-first mobile brewing journal for recording, viewing, editing, deleting, sorting, and safely managing coffee bean brewing settings and reusable custom origins.

## Clarifications

### Session 2026-07-05

- Q: How should Portion Weight be represented in the MVP? → A: Grams only, positive decimal, display with g.
- Q: What should “manage reusable custom origins” include in the MVP? → A: Create custom origins only.
- Q: Should the MVP prevent users from saving exact duplicate Brew Entries? → A: Warn but allow duplicates.
- Q: Which delete safety pattern should the MVP require? → A: Confirmation before deletion.
- Q: When should the app warn about unsaved changes? → A: Any changed field.

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Record a Brew Entry (Priority: P1)

As a home coffee enthusiast experimenting with different coffee beans, I want to record the settings I used for a brew so I can remember which bean, origin, roast type, grinder setting, and portion weight produced a good result.

**Why this priority**: Recording a brew entry is the core value of coffee.app. Without this capability, the app cannot act as a simple brewing journal or memory aid.

**Independent Test**: Can be tested by starting the app locally, creating a valid brew entry, viewing it in the list, restarting the app, and confirming the same entry is still available.

**Acceptance Scenarios**:

1. **Given** the user is on the brew entry list, **When** they add an entry with Bean Name, Bean Origin, Roast Type, Grinder Setting, and Portion Weight, **Then** the entry is saved and appears in the list.
2. **Given** the user is adding a brew entry, **When** they include an optional Description of 500 characters or fewer, **Then** the description is saved with the entry.
3. **Given** the user is adding a brew entry, **When** they leave Bean Name empty or enter only whitespace, **Then** the app prevents saving and explains that Bean Name is required.
4. **Given** the user is adding a brew entry, **When** they enter a Grinder Setting below 1 or above 48, **Then** the app prevents saving and explains the valid range.
5. **Given** the user saves a brew entry, **When** the app is restarted, **Then** the brew entry remains available with its saved values.

---

### User Story 2 - Choose and Reuse Coffee Origins (Priority: P2)

As a home coffee enthusiast, I want to select a common origin or create a reusable custom origin so that each brew entry reflects the coffee bean I actually used.

**Why this priority**: Origin is part of the core Brew Entry record. Predefined origins make common entries quick, while custom origins support real-world beans that are not covered by the initial list.

**Independent Test**: Can be tested by selecting a predefined origin, creating a custom origin, using the custom origin in a brew entry, restarting the app, and confirming the custom origin remains reusable.

**Acceptance Scenarios**:

1. **Given** the user is choosing an origin, **When** the predefined origins are shown, **Then** Brazil, Colombia, Ethiopia, Kenya, Guatemala, Costa Rica, Honduras, Peru, El Salvador, Panama, Indonesia, India, Vietnam, and Yemen are available.
2. **Given** the user is choosing an origin, **When** the desired origin is not in the predefined list, **Then** the user can create a custom origin.
3. **Given** a custom origin has been created, **When** the user creates or edits another brew entry, **Then** the custom origin is available for selection.
4. **Given** a custom origin has been created, **When** the app is restarted, **Then** the custom origin remains available.
5. **Given** an origin named "Brazil" already exists, **When** the user attempts to create "brazil", **Then** the app prevents creating a duplicate origin that differs only by letter casing.

---

### User Story 3 - Review Brew Entries (Priority: P3)

As a home coffee enthusiast, I want to view a concise list of saved brew entries and open a detailed view so I can quickly remember previous brewing settings.

**Why this priority**: Saved entries are only useful if the user can easily find and review them later, especially when multiple entries exist for the same bean.

**Independent Test**: Can be tested by creating multiple entries, confirming the list summaries contain the required fields, and opening an entry to see all saved details.

**Acceptance Scenarios**:

1. **Given** one or more brew entries exist, **When** the user opens the list screen, **Then** each entry displays Bean Name, Origin, Roast Type, Grinder Setting, Portion Weight, and Created Date.
2. **Given** the user selects a brew entry from the list, **When** the detail screen opens, **Then** all entry information is displayed, including Description, Created Date, and Last Modified Date.
3. **Given** multiple entries exist for the same Bean Name, **When** the user views the list or detail screens, **Then** each entry remains a separate record with its own settings and dates.

---

### User Story 4 - Sort Brew Entries (Priority: P4)

As a home coffee enthusiast with multiple saved brew entries, I want to sort entries in useful ways so I can find a bean, origin, or recent experiment quickly.

**Why this priority**: Sorting improves the usefulness of the brewing journal as entries accumulate, while keeping the MVP focused and simple.

**Independent Test**: Can be tested by creating entries with different bean names, origins, created dates, and modified dates, then confirming each supported sort order changes the list as expected.

**Acceptance Scenarios**:

1. **Given** multiple brew entries exist, **When** the user opens the list screen, **Then** entries are sorted by Created Date descending by default, with newest entries first.
2. **Given** multiple brew entries exist, **When** the user selects Bean Name A-Z, **Then** entries are sorted alphabetically by Bean Name.
3. **Given** multiple brew entries exist, **When** the user selects Origin A-Z, **Then** entries are sorted alphabetically by Origin.
4. **Given** multiple brew entries exist, **When** the user selects Created Date, **Then** entries are sorted by Created Date.
5. **Given** multiple brew entries exist, **When** the user selects Last Modified Date, **Then** entries are sorted by Last Modified Date.

---

### User Story 5 - Edit or Delete Brew Entries Safely (Priority: P5)

As a home coffee enthusiast, I want to edit or delete brew entries without accidentally losing useful brewing information so my brewing memory stays accurate and trustworthy.

**Why this priority**: Brewing experiments change over time, but accidental data loss would undermine the app's purpose as a memory aid.

**Independent Test**: Can be tested by editing an existing entry, attempting to leave with unsaved changes, deleting an entry, restarting the app, and confirming the saved state matches only confirmed user actions.

**Acceptance Scenarios**:

1. **Given** an existing brew entry, **When** the user edits valid fields and saves, **Then** the entry is updated and Last Modified Date changes.
2. **Given** the user has changed any field in an add or edit flow, **When** they attempt to leave without saving, **Then** the app warns or asks for confirmation before discarding those changes.
3. **Given** an existing brew entry, **When** the user requests deletion, **Then** the app asks for confirmation before deleting the entry.
4. **Given** an entry has been edited or deleted, **When** the app is restarted, **Then** the persisted data reflects the completed confirmed change.

---

### User Story 6 - Use the App in System, Light, and Dark Themes (Priority: P6)

As a mobile app user, I want coffee.app to follow my system theme by default and remain usable in light and dark modes so the app feels comfortable on my device.

**Why this priority**: Theme support is required for the MVP and must be considered from the start to keep the app readable and beginner-friendly.

**Independent Test**: Can be tested by viewing the list, add/edit, detail, origin selection, sorting, and delete safety flows in system theme, light mode, and dark mode.

**Acceptance Scenarios**:

1. **Given** the device uses a system theme, **When** the app starts, **Then** the app follows the system theme by default.
2. **Given** the app is shown in light mode, **When** the user uses MVP flows, **Then** text, controls, validation messages, and destructive-action prompts remain readable and usable.
3. **Given** the app is shown in dark mode, **When** the user uses MVP flows, **Then** text, controls, validation messages, and destructive-action prompts remain readable and usable.

---

### Edge Cases

- Bean Name is empty or contains only whitespace: the app treats it as missing and prevents saving.
- Grinder Setting is blank, not a whole number, less than 1, or greater than 48: the app prevents saving and explains the valid range.
- Portion Weight is blank, zero, negative, or not a valid decimal number of grams: the app prevents saving and explains that Portion Weight must be a positive gram value.
- Roast Type is missing or not one of Light, Medium, or Dark: the app prevents saving.
- Description is omitted: the app allows saving.
- Description exceeds 500 characters: the app prevents saving or requires the text to be shortened before saving.
- Origin creation uses extra spaces or casing that matches an existing predefined or custom origin: the app prevents duplicate origins.
- Multiple Brew Entries use the same Bean Name: the app allows them as separate experiments.
- Saving an exact duplicate Brew Entry (same bean name, origin, roast, grinder, and portion weight): the app warns the user about the existing entry but allows saving if they confirm.
- Custom origin management in the MVP is create-only: users cannot rename or delete origins in the MVP.
- The user changes any field in an add or edit flow and then attempts to leave without saving: the app warns or asks for confirmation before discarding changes.
- The user initiates deletion unintentionally: deletion requires confirmation before completion.
- The app restarts after Brew Entries or custom origins are created, edited, or deleted: confirmed changes remain persisted.
- The app is used in system, light, or dark theme modes: all MVP flows remain readable and usable.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: The app MUST start successfully in a local development environment.
- **FR-002**: The app MUST allow the user to view a list of Brew Entries.
- **FR-003**: The app MUST allow the user to add a Brew Entry.
- **FR-004**: The app MUST allow the user to edit an existing Brew Entry.
- **FR-005**: The app MUST allow the user to delete an existing Brew Entry.
- **FR-006**: The app MUST allow the user to open a detailed view for a Brew Entry.
- **FR-007**: A Brew Entry MUST include Bean Name, Bean Origin, Roast Type, Grinder Setting, Portion Weight, Created Date, and Last Modified Date.
- **FR-008**: A Brew Entry MAY include Description.
- **FR-009**: The app MUST allow multiple Brew Entries for the same Bean Name.
- **FR-010**: Bean Name MUST be free text and required.
- **FR-011**: Bean Name MUST be treated as missing when it contains only whitespace.
- **FR-012**: Bean Origin MUST be selected from predefined origins or reusable custom origins.
- **FR-013**: The predefined origin list MUST include Brazil, Colombia, Ethiopia, Kenya, Guatemala, Costa Rica, Honduras, Peru, El Salvador, Panama, Indonesia, India, Vietnam, and Yemen.
- **FR-014**: The app MUST allow the user to create a custom origin when the desired origin is not available.
- **FR-015**: Custom origins MUST be stored locally.
- **FR-016**: Custom origins MUST become reusable and available for selection in future Brew Entries.
- **FR-017**: Origin names MUST NOT create duplicates that differ only by letter casing.
- **FR-018**: The app MUST allow the user to create reusable custom origins; renaming and deleting custom origins are out of scope for the MVP.
- **FR-019**: Roast Type MUST be one of Light, Medium, or Dark.
- **FR-020**: Grinder Setting MUST be a whole number from 1 to 48 inclusive.
- **FR-021**: Portion Weight MUST be recorded as a positive decimal number of grams and displayed with `g`.
- **FR-022**: Description MUST be optional.
- **FR-023**: Description MUST allow no more than 500 characters.
- **FR-024**: Created Date MUST be recorded when a Brew Entry is first saved.
- **FR-025**: Last Modified Date MUST be recorded when a Brew Entry is first saved and updated when the Brew Entry is edited.
- **FR-026**: The list screen MUST display a concise summary for each Brew Entry: Bean Name, Origin, Roast Type, Grinder Setting, Portion Weight, and Created Date.
- **FR-027**: The detail screen MUST display all Brew Entry information, including Description, Created Date, and Last Modified Date.
- **FR-028**: The default Brew Entry sort order MUST be Created Date descending, with newest entries first.
- **FR-029**: The app MUST provide additional sort options for Bean Name A-Z, Origin A-Z, Created Date, and Last Modified Date.
- **FR-030**: Brew Entries MUST persist after app restart.
- **FR-031**: Custom origins MUST persist after app restart.
- **FR-032**: Deleting a Brew Entry MUST require confirmation before deletion is completed.
- **FR-033**: Unsaved add or edit changes MUST NOT be discarded without warning or confirmation whenever any field differs from the saved entry or blank new-entry form.
- **FR-034**: The app MUST follow the system theme by default.
- **FR-035**: The app MUST support light mode.
- **FR-036**: The app MUST support dark mode.
- **FR-037**: The app MUST remain readable and usable in system, light, and dark theme modes.
- **FR-038**: Automated tests MUST cover core data validation and state behavior, including Brew Entry validation, sorting behavior, persistence expectations, custom origin reuse, and accidental-loss protections.
- **FR-039**: The README MUST explain how to run the application locally and how to run automated tests.
- **FR-040**: The MVP MUST NOT include user accounts, authentication, cloud sync, backend services, APIs, sharing entries, ratings, favourites, photos, brewing timers, brewing recipes, multiple grinder profiles, multiple brewing methods, notifications, analytics, payments, social features, or app store publishing.
- **FR-041**: The MVP MUST minimize dependencies and include only dependencies that clearly simplify the app.
- **FR-042**: Mobile platform assumptions MUST be documented during planning before implementation begins.
- **FR-043**: The app MUST warn the user when trying to save an exact duplicate Brew Entry (same bean name, origin, roast, grinder setting, and portion weight) but allow saving if they choose to proceed.

### Constitution Alignment *(mandatory)*

- **Brewing-memory value**: The feature is the core coffee.app MVP. It directly solves the user's problem of forgetting which coffee bean brewing settings produced a good result by recording Brew Entries with bean, origin, roast, grinder, portion, description, and date information.
- **Local-first scope**: The MVP stores Brew Entries and custom origins locally on the device. User accounts, authentication, cloud sync, backend services, APIs, analytics, payments, social features, and app store publishing are explicitly out of scope.
- **User-safe data changes**: Destructive Brew Entry deletion must require confirmation before deletion is completed. Unsaved add/edit changes must not be discarded without warning or confirmation whenever any field differs from the saved entry or blank new-entry form. Custom origins are create-only in the MVP, avoiding rename/delete flows that could silently affect existing Brew Entries.
- **Theme behavior**: The app follows system theme by default, supports light and dark modes, and must remain usable in all three theme contexts.
- **Mobile assumptions**: Planning must document target platform assumptions, local storage choice, navigation model, theme handling, dependency choices, and local run/test commands before implementation begins.

### Key Entities *(include if feature involves data)*

- **Brew Entry**: The primary record in coffee.app. Represents one saved brewing experiment for a coffee bean. Multiple Brew Entries may exist for the same Bean Name. Fields: Bean Name, Bean Origin, Roast Type, Grinder Setting, Portion Weight in grams, optional Description, Created Date, and Last Modified Date.
- **Origin**: A reusable coffee origin selectable for a Brew Entry. Origins include the predefined common origins and user-created custom origins. Origin names are unique without regard to letter casing.
- **Custom Origin**: A user-created Origin stored locally and made available for future Brew Entries. Custom origins are part of the reusable origin selection set and cannot be renamed or deleted in the MVP.
- **Roast Type**: A fixed Brew Entry value with exactly three allowed options: Light, Medium, and Dark.
- **Sort Option**: A user-selectable list ordering. Supported options are Created Date descending by default, Bean Name A-Z, Origin A-Z, Created Date, and Last Modified Date.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: A user can start the app locally and create a valid Brew Entry from a fresh app state in under 2 minutes.
- **SC-002**: A user can view the Brew Entry list and identify Bean Name, Origin, Roast Type, Grinder Setting, Portion Weight, and Created Date for each visible entry.
- **SC-003**: A user can open a Brew Entry detail view and see all saved information, including Description, Created Date, and Last Modified Date.
- **SC-004**: A user can edit an existing Brew Entry and see the updated values and Last Modified Date after saving.
- **SC-005**: A user can delete a Brew Entry only after confirming deletion.
- **SC-006**: 100% of invalid save attempts for missing Bean Name, invalid Grinder Setting, invalid Roast Type, duplicate Origin casing, or overlong Description are blocked with understandable feedback.
- **SC-007**: 100% of completed Brew Entry and custom origin saves remain available after app restart.
- **SC-008**: A user can create a custom origin and reuse it in a later Brew Entry after app restart.
- **SC-009**: The Brew Entry list defaults to newest-first ordering and supports all required sort options.
- **SC-010**: The app remains readable and usable in system theme, light mode, and dark mode across list, add, edit, detail, sort, origin selection, and deletion flows.
- **SC-011**: Automated tests cover core data validation and state behavior for Brew Entries, custom origins, sorting, persistence expectations, duplicate-entry warning, and accidental-loss protections.
- **SC-012**: A developer can follow README instructions to run the app and tests without needing undocumented commands.

## Assumptions

- The target user is a single home coffee enthusiast using the app on their own device.
- The MVP is a single-user local app with no accounts, identity, sharing, syncing, backend, or cross-device behavior.
- The app acts as a simple brewing journal and memory aid, not a recipe system, timer, ratings tracker, photo journal, or social product.
- Portion Weight is a required Brew Entry value saved as a positive decimal number of grams and displayed with `g`.
- Created Date is set when a Brew Entry is first saved. Last Modified Date is set when the entry is first saved and updated whenever the entry is edited.
- Created Date and Last Modified Date are displayed in a readable format appropriate for the user's device settings.
- Managing custom origins in the MVP means creating and reusing custom origins only; renaming and deleting custom origins are deferred out of scope.
- The app has no requirement to import, export, back up, or restore data in the MVP.
- Mobile platform, navigation, local storage, dependency, and test-command decisions are intentionally deferred to the planning phase and must be documented there.
