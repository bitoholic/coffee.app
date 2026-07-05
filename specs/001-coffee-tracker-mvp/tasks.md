---
description: "Task list for Coffee Brewing Settings Tracker MVP"
---

# Tasks: Coffee Brewing Settings Tracker MVP

**Input**: Design documents from `specs/001-coffee-tracker-mvp/`

**Prerequisites**: plan.md (required), spec.md (required for user stories), data-model.md, contracts/

**Tests**: Tests are REQUIRED by the coffee.app constitution. Include test tasks before implementation tasks for behavior involving data, state, validation, persistence, or important mobile flows. Tests MUST be written and confirmed failing before implementation starts.

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to
- File paths follow the iOS project structure from plan.md

## Path Conventions

- **iOS App Source**: `ios/coffee_tracker_mvp/`
- **iOS Tests**: `Tests/coffee_tracker_mvp_tests/`

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization and basic Xcode/iOS project structure

- [ ] T001 Create Xcode iOS project scaffold at `ios/` with SwiftUI app template and CoreData enabled
- [ ] T002 [P] Create `PersistenceController.swift` with CoreData stack setup at `ios/coffee_tracker_mvp/Persistence/PersistenceController.swift`
- [ ] T003 [P] Create `Assets.xcassets` with color sets for light and dark mode support at `ios/coffee_tracker_mvp/Resources/`
- [ ] T004 Add `DateFormatter+Extensions.swift` at `ios/coffee_tracker_mvp/Utilities/`

**Checkpoint**: Xcode project builds successfully on iOS 15+ simulator

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure — data model entities and persistence — that MUST be complete before ANY user story

- [ ] T005 Create CoreData data model `coffee_tracker_mvp.xcdatamodeld` with BrewEntry entity and attributes (uuid, beanName, beanOrigin, roastType, grinderSetting, portionWeight, description, createdDate, lastModifiedDate) at `ios/coffee_tracker_mvp/Models/`
- [ ] T006 [P] Create `Origin` CoreData entity with attributes (uuid, name, isCustom) and relationship to BrewEntry at `ios/coffee_tracker_mvp/Models/`
- [ ] T007 [P] Add One-to-Many relationship between Origin and BrewEntry in CoreData model
- [ ] T008 Pre-populate Origin store with 14 predefined origins (Brazil, Colombia, Ethiopia, Kenya, Guatemala, Costa Rica, Honduras, Peru, El Salvador, Panama, Indonesia, India, Vietnam, Yemen) in `PersistenceController.swift`
- [ ] T009 Create `BrewEntryFormViewModel.swift` with validation logic at `ios/coffee_tracker_mvp/ViewModels/`
- [ ] T010 Create `BrewEntryListViewModel.swift` with fetch and sort logic at `ios/coffee_tracker_mvp/ViewModels/`

**Checkpoint**: Foundation ready — models persist, origins seeded, ViewModels compiled

---

## Phase 3: User Story 1 — Record a Brew Entry (Priority: P1) 🎯 MVP

**Goal**: User can create a brew entry with bean name, origin, roast type, grinder setting, portion weight, and optional description. Validation prevents invalid saves.

**Independent Test**: Open app, tap add, fill valid fields, save, confirm entry appears in list. Restart app, confirm entry persists.

### Tests for User Story 1 (REQUIRED — write before implementation) ⚠️

- [ ] T011 [P] [US1] Create failing test `test_brewEntry_save_and_fetch` for CoreData BrewEntry creation and retrieval in `Tests/coffee_tracker_mvp_tests/Models/BrewEntryTests.swift`
- [ ] T012 [P] [US1] Create failing test `test_brewEntry_validation_beanNameRequired` for Bean Name empty/whitespace rejection in `Tests/coffee_tracker_mvp_tests/ViewModels/BrewEntryFormViewModelTests.swift`
- [ ] T013 [P] [US1] Create failing test `test_brewEntry_validation_grinderSettingRange` for Grinder Setting 1-48 range in `Tests/coffee_tracker_mvp_tests/ViewModels/BrewEntryFormViewModelTests.swift`
- [ ] T014 [P] [US1] Create failing test `test_brewEntry_validation_roastTypeRequired` for Roast Type must be Light/Medium/Dark in `Tests/coffee_tracker_mvp_tests/ViewModels/BrewEntryFormViewModelTests.swift`
- [ ] T015 [P] [US1] Create failing test `test_brewEntry_validation_portionWeightPositive` for Portion Weight positive decimal in `Tests/coffee_tracker_mvp_tests/ViewModels/BrewEntryFormViewModelTests.swift`
- [ ] T016 [P] [US1] Create failing test `test_brewEntry_validation_descriptionMaxLength` for 500-char limit in `Tests/coffee_tracker_mvp_tests/ViewModels/BrewEntryFormViewModelTests.swift`
- [ ] T017 [P] [US1] Create failing test `test_brewEntry_persistence_afterRestart` for data surviving app restart in `Tests/coffee_tracker_mvp_tests/ViewModels/BrewEntryFormViewModelTests.swift`

### Implementation for User Story 1

- [ ] T018 [P] [US1] Implement `BrewEntryFormView.swift` with all fields (Bean Name, Origin, Roast Type, Grinder Setting, Portion Weight, Description) at `ios/coffee_tracker_mvp/Views/BrewEntryForm/BrewEntryFormView.swift`
- [ ] T019 [P] [US1] Implement validation logic in `BrewEntryFormViewModel.swift` for Bean Name (required, non-empty), Grinder Setting (1-48 integer), Roast Type (required picker), Portion Weight (positive decimal), Description (max 500 chars)
- [ ] T020 [P] [US1] Implement CoreData save operation in `BrewEntryFormViewModel.swift` that sets createdDate and lastModifiedDate
- [ ] T021 [US1] Wire `BrewEntryFormView.swift` to `BrewEntryListViewModel.swift` for entry list refresh on save

**Checkpoint**: User can create a valid brew entry. Invalid entries are blocked with feedback. Entry persists after restart.

---

## Phase 4: User Story 2 — Choose and Reuse Coffee Origins (Priority: P2)

**Goal**: User can select from 14 predefined origins or create a reusable custom origin inline. Custom origins persist and are available for future entries.

**Independent Test**: Open add entry, select Brazil from list. Create custom origin "My Blend", save entry, reopen, confirm "My Blend" is selectable. Restart, confirm it's still there.

### Tests for User Story 2 (REQUIRED — write before implementation) ⚠️

- [ ] T022 [P] [US2] Create failing test `test_origin_predefinedList_complete` for all 14 origins in `Tests/coffee_tracker_mvp_tests/Models/OriginTests.swift`
- [ ] T023 [P] [US2] Create failing test `test_origin_create_customAndReusable` for custom origin creation and reuse in `Tests/coffee_tracker_mvp_tests/Models/OriginTests.swift`
- [ ] T024 [P] [US2] Create failing test `test_origin_create_duplicateCaseInsensitive` for case-insensitive duplicate prevention in `Tests/coffee_tracker_mvp_tests/Models/OriginTests.swift`
- [ ] T025 [P] [US2] Create failing test `test_origin_persistence_afterRestart` for custom origin persistence in `Tests/coffee_tracker_mvp_tests/Models/OriginTests.swift`

### Implementation for User Story 2

- [ ] T026 [P] [US2] Implement `OriginPicker.swift` with predefined origins list and "Add Custom" option at `ios/coffee_tracker_mvp/Views/BrewEntryForm/OriginPicker.swift`
- [ ] T027 [P] [US2] Implement `OriginCreateView.swift` with text input and case-insensitive duplicate check at `ios/coffee_tracker_mvp/Views/BrewEntryForm/OriginCreateView.swift`
- [ ] T028 [US2] Implement custom origin CoreData save and fetch in `BrewEntryFormViewModel.swift` that merges custom + predefined origins for picker display

**Checkpoint**: User can select predefined or create custom origins. Custom origins persist and remain reusable.

---

## Phase 5: User Story 3 — Review Brew Entries (Priority: P3)

**Goal**: User can view a concise list of brew entries and open a detail view to see all information.

**Independent Test**: Create multiple entries, confirm list shows Bean Name, Origin, Roast Type, Grinder Setting, Portion Weight, Created Date. Tap entry, confirm detail shows Description, Created Date, Last Modified Date.

### Tests for User Story 3 (REQUIRED — write before implementation) ⚠️

- [ ] T029 [P] [US3] Create failing test `test_brewEntry_list_displayFields` for list summary field display in `Tests/coffee_tracker_mvp_tests/ViewModels/BrewEntryListViewModelTests.swift`
- [ ] T030 [P] [US3] Create failing test `test_brewEntry_detail_displayAllFields` for detail view completeness in `Tests/coffee_tracker_mvp_tests/ViewModels/BrewEntryListViewModelTests.swift`

### Implementation for User Story 3

- [ ] T031 [P] [US3] Implement `BrewEntryListView.swift` with `List`/`ForEach` showing Bean Name, Origin, Roast Type, Grinder Setting, Portion Weight, Created Date at `ios/coffee_tracker_mvp/Views/BrewEntryList/BrewEntryListView.swift`
- [ ] T032 [P] [US3] Implement `BrewEntryRow.swift` as the list row component at `ios/coffee_tracker_mvp/Views/BrewEntryList/BrewEntryRow.swift`
- [ ] T033 [P] [US3] Implement `BrewEntryDetailView.swift` showing all entry info including Description, Created Date, Last Modified Date at `ios/coffee_tracker_mvp/Views/BrewEntryDetail/BrewEntryDetailView.swift`

**Checkpoint**: List and detail views display correct fields. Navigation between list and detail works.

---

## Phase 6: User Story 4 — Sort Brew Entries (Priority: P4)

**Goal**: User can sort entries by Created Date (default, newest first), Bean Name (A-Z), Origin (A-Z), or Last Modified Date.

**Independent Test**: Create entries with varied names and dates, verify default newest-first sort. Change to Bean Name A-Z, confirm alphabetical. Change to Origin A-Z, etc.

### Tests for User Story 4 (REQUIRED — write before implementation) ⚠️

- [ ] T034 [P] [US4] Create failing test `test_brewEntry_sort_defaultNewestFirst` for default sort order in `Tests/coffee_tracker_mvp_tests/ViewModels/BrewEntryListViewModelTests.swift`
- [ ] T035 [P] [US4] Create failing test `test_brewEntry_sort_beanNameAZ` for bean name sort in `Tests/coffee_tracker_mvp_tests/ViewModels/BrewEntryListViewModelTests.swift`
- [ ] T036 [P] [US4] Create failing test `test_brewEntry_sort_originAZ` for origin sort in `Tests/coffee_tracker_mvp_tests/ViewModels/BrewEntryListViewModelTests.swift`
- [ ] T037 [P] [US4] Create failing test `test_brewEntry_sort_lastModifiedDate` for last modified sort in `Tests/coffee_tracker_mvp_tests/ViewModels/BrewEntryListViewModelTests.swift`

### Implementation for User Story 4

- [ ] T038 [US4] Add sort state management to `BrewEntryListViewModel.swift` with sort descriptor toggle for Bean Name A-Z, Origin A-Z, Created Date, Last Modified Date
- [ ] T039 [US4] Add sort picker UI to `BrewEntryListView.swift` (Segmented Picker or Menu with sort options)

**Checkpoint**: List sorting works for all 4 sort modes. Default is newest-first.

---

## Phase 7: User Story 5 — Edit or Delete Brew Entries Safely (Priority: P5)

**Goal**: User can edit or delete entries without accidental data loss. Delete requires confirmation. Unsaved edits warn before discarding.

**Independent Test**: Edit an entry, save, confirm changes. Start edit, change a field, navigate back — confirm warning appears. Delete an entry, confirm dialog appears, confirm, entry disappears.

### Tests for User Story 5 (REQUIRED — write before implementation) ⚠️

- [ ] T040 [P] [US5] Create failing test `test_brewEntry_edit_savesUpdatedValues` for edit save in `Tests/coffee_tracker_mvp_tests/ViewModels/BrewEntryFormViewModelTests.swift`
- [ ] T041 [P] [US5] Create failing test `test_brewEntry_edit_updatesLastModifiedDate` for date update in `Tests/coffee_tracker_mvp_tests/ViewModels/BrewEntryFormViewModelTests.swift`
- [ ] T042 [P] [US5] Create failing test `test_brewEntry_delete_confirmationRequired` for delete confirmation in `Tests/coffee_tracker_mvp_tests/ViewModels/BrewEntryListViewModelTests.swift`
- [ ] T043 [P] [US5] Create failing test `test_brewEntry_unsavedChanges_warning` for unsaved changes detection in `Tests/coffee_tracker_mvp_tests/ViewModels/BrewEntryFormViewModelTests.swift`
- [ ] T044 [P] [US5] Create failing test `test_brewEntry_duplicateEntry_warning` for duplicate entry warning in `Tests/coffee_tracker_mvp_tests/ViewModels/BrewEntryFormViewModelTests.swift`

### Implementation for User Story 5

- [ ] T045 [US5] Add edit mode to `BrewEntryFormView.swift` — pre-populate fields from existing BrewEntry
- [ ] T046 [US5] Add CoreData update operation in `BrewEntryFormViewModel.swift` that updates lastModifiedDate
- [ ] T047 [US5] Add delete confirmation `.confirmationDialog` or `.alert` in `BrewEntryDetailView.swift` or `BrewEntryListView.swift`
- [ ] T048 [US5] Add CoreData delete operation in `BrewEntryListViewModel.swift` (requires confirmation before executing)
- [ ] T049 [US5] Add unsaved-changes detection and `.confirmationDialog` when navigating back from `BrewEntryFormView.swift` with changed fields
- [ ] T050 [US5] Add duplicate-entry warning dialog in `BrewEntryFormViewModel.swift` that compares all fields against existing entries

**Checkpoint**: Edit saves correctly, delete requires confirmation, unsaved edits trigger warning, duplicates trigger warning but allow save.

---

## Phase 8: User Story 6 — Use the App in System, Light, and Dark Themes (Priority: P6)

**Goal**: App follows system theme by default, remains readable in light and dark modes.

**Independent Test**: Toggle device theme between light and dark. Verify all screens (list, add/edit, detail, origin picker, delete confirmation) remain readable.

### Tests for User Story 6 (REQUIRED — write before implementation) ⚠️

- [ ] T051 [P] [US6] Create failing test `test_theme_colorSchemeRespectsSystem` for system theme default in `Tests/coffee_tracker_mvp_tests/ViewModels/BrewEntryListViewModelTests.swift`
- [ ] T052 [P] [US6] Create failing test `test_theme_lightMode_readable` for light mode readability in `Tests/coffee_tracker_mvp_tests/Utilities/ThemeTests.swift`

### Implementation for User Story 6

- [ ] T053 [P] [US6] Apply `@Environment(\.colorScheme)` conditional styling to all views (BrewEntryListView, BrewEntryFormView, BrewEntryDetailView, OriginPicker, OriginCreateView)
- [ ] T054 [P] [US6] Configure `Assets.xcassets` color sets with light/dark appearance variants for primary text, background, and accent colors

**Checkpoint**: App renders correctly in system, light, and dark modes on all MVP screens.

---

## Phase 9: Polish & Cross-Cutting Concerns

**Purpose**: Final quality checks, verification, and hardening

- [ ] T055 [P] Update `README.md` with setup, run, and test instructions for the Xcode project
- [ ] T056 Verify Brew Entry and custom origin persistence by running app restart test flow
- [ ] T057 Verify system/light/dark theme behavior across all MVP screens
- [ ] T058 [P] Run full XCTest suite, confirm all tests pass
- [ ] T059 Run quickstart.md validation against the running app

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies — can start immediately
- **Foundational (Phase 2)**: Depends on Setup completion — BLOCKS all user stories
- **User Stories (Phase 3–8)**: All depend on Foundational phase completion
  - Stories can proceed sequentially in priority order
  - US1 (P1) must be complete first as MVP baseline
  - US2 (P2) through US6 (P6) build on US1
- **Polish (Phase 9)**: Depends on all desired user stories being complete

### User Story Dependencies

- **US1 — Record a Brew Entry (P1)**: Core MVP — foundation for all stories. No dependencies on other stories.
- **US2 — Choose Origins (P2)**: Integrates with US1 form but independently testable.
- **US3 — Review Entries (P3)**: Needs brew entries (US1) to display. Read-only, independently testable.
- **US4 — Sort Entries (P4)**: Built on US3 list view. Independently testable sorting logic.
- **US5 — Edit/Delete (P5)**: Builds on US1 (data) and US3 (detail). Independently testable safety flows.
- **US6 — Themes (P6)**: Applies across all screens. Independently testable with theme toggle.

### Within Each User Story

- Tests MUST be written and FAIL before implementation
- CoreData operations before UI wiring
- Validation before save/update flows
- Story complete before moving to next priority

### Parallel Opportunities

- All Setup tasks marked [P] can run in parallel
- All Foundational tasks marked [P] can run in parallel
- Tests within a story marked [P] can run in parallel
- Models within a story marked [P] can run in parallel (where applicable)
- Theme work (US6) can be done in parallel with other stories since it's cross-cutting