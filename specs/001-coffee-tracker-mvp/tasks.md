---
description: "Task list for Coffee Brewing Settings Tracker MVP (Compose Multiplatform)"
---

# Tasks: Coffee Brewing Settings Tracker MVP (Compose Multiplatform)

**Input**: Design documents from `specs/001-coffee-tracker-mvp/`

**Prerequisites**: plan.md (required), spec.md (required), data-model.md, contracts/

**Tests**: Tests are REQUIRED by the coffee.app constitution. Include test tasks before implementation tasks for behavior involving data, state, validation, persistence, or important mobile flows. Tests MUST be written and confirmed failing before implementation starts.

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to
- File paths follow the Compose Multiplatform project structure from plan.md

## Path Conventions

- **Shared Code**: `composeApp/src/commonMain/kotlin/coffee/app/`
- **Android Entry Point**: `composeApp/src/androidMain/kotlin/coffee/app/`
- **iOS Entry Point**: `composeApp/src/iosMain/kotlin/coffee/app/` and `iosApp/`
- **Shared Tests**: `composeApp/src/commonTest/kotlin/coffee/app/`

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization, Gradle configuration, and KMP scaffolding

- [ ] T001 Scaffold KMP project using the official JetBrains Compose Multiplatform wizard template
- [ ] T002 Configure `gradle/libs.versions.toml` with Kotlin 2.1.x, Compose Multiplatform, Room KMP, and kotlin.test versions
- [ ] T003 [P] Create `CoffeeDatabase.kt` with Room KMP database definition at `composeApp/src/commonMain/kotlin/coffee/app/data/database/CoffeeDatabase.kt`
- [ ] T004 Create `Theme.kt` and `Color.kt` at `composeApp/src/commonMain/kotlin/coffee/app/ui/theme/` with Material 3 light/dark colour schemes
- [ ] T005 [P] Set up Android entry point at `composeApp/src/androidMain/kotlin/coffee/app/MainActivity.kt`
- [ ] T006 [P] Set up iOS entry point at `composeApp/src/iosMain/kotlin/coffee/app/MainViewController.kt` and `iosApp/iosApp/iosAppApp.swift`

**Checkpoint**: `./gradlew :composeApp:build` succeeds for both Android and iOS simulator targets.

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure — Room entities, DAOs, repositories, and ViewModel scaffolding

- [ ] T007 Create `BrewEntry.kt` entity with Room annotations at `composeApp/src/commonMain/kotlin/coffee/app/data/model/BrewEntry.kt`
- [ ] T008 [P] Create `Origin.kt` entity with Room annotations at `composeApp/src/commonMain/kotlin/coffee/app/data/model/Origin.kt`
- [ ] T009 Create `RoastType.kt` enum at `composeApp/src/commonMain/kotlin/coffee/app/data/model/RoastType.kt`
- [ ] T010 [P] Create `SortOption.kt` enum at `composeApp/src/commonMain/kotlin/coffee/app/data/model/SortOption.kt`
- [ ] T011 Create `BrewEntryDao.kt` with CRUD + sort queries at `composeApp/src/commonMain/kotlin/coffee/app/data/database/BrewEntryDao.kt`
- [ ] T012 [P] Create `OriginDao.kt` with CRUD + case-insensitive lookup at `composeApp/src/commonMain/kotlin/coffee/app/data/database/OriginDao.kt`
- [ ] T013 [P] Create `BrewEntryRepository.kt` at `composeApp/src/commonMain/kotlin/coffee/app/data/repository/BrewEntryRepository.kt`
- [ ] T014 [P] Create `OriginRepository.kt` at `composeApp/src/commonMain/kotlin/coffee/app/data/repository/OriginRepository.kt`
- [ ] T015 Seed predefined origins (14 entries) in `CoffeeDatabase.kt` callback
- [ ] T016 Create `ValidationUtil.kt` with shared validation functions at `composeApp/src/commonMain/kotlin/coffee/app/util/ValidationUtil.kt`
- [ ] T017 Create `DateFormatUtil.kt` with date formatting helpers at `composeApp/src/commonMain/kotlin/coffee/app/util/DateFormatUtil.kt`

**Checkpoint**: Room entities compile, DAO queries are valid, repository layer is wired. `./gradlew :composeApp:jvmTest` runs.

---

## Phase 3: User Story 1 — Record a Brew Entry (Priority: P1) 🎯 MVP

**Goal**: User can create a brew entry with bean name, origin, roast type, grinder setting, portion weight, and optional description. Validation prevents invalid saves.

### Tests for User Story 1 (REQUIRED — write before implementation) ⚠️

- [ ] T018 [P] [US1] Write failing test `test_brewEntry_saveAndFetch` for Room DAO insert + read in `BrewEntryDaoTest.kt`
- [ ] T019 [P] [US1] Write failing test `test_validation_beanNameRequired` for empty/whitespace rejection in `ValidationUtilTest.kt`
- [ ] T020 [P] [US1] Write failing test `test_validation_grinderSettingRange` for 1-48 range in `ValidationUtilTest.kt`
- [ ] T021 [P] [US1] Write failing test `test_validation_roastTypeRequired` for valid enum check in `ValidationUtilTest.kt`
- [ ] T022 [P] [US1] Write failing test `test_validation_portionWeightPositive` for positive decimal in `ValidationUtilTest.kt`
- [ ] T023 [P] [US1] Write failing test `test_validation_descriptionMaxLength` for 500-char limit in `ValidationUtilTest.kt`
- [ ] T024 [P] [US1] Write failing test `test_brewEntry_persistence_afterRestart` for Room in-memory DB persistence in `BrewEntryDaoTest.kt`

### Implementation for User Story 1

- [ ] T025 [US1] Implement `BrewEntryFormScreen.kt` with all input fields (Bean Name, Origin picker, Roast Type picker, Grinder Setting, Portion Weight, Description) at `composeApp/src/commonMain/kotlin/coffee/app/ui/screens/form/BrewEntryFormScreen.kt`
- [ ] T026 [US1] Implement `BrewEntryFormViewModel.kt` with validation, save logic, and date stamping at `composeApp/src/commonMain/kotlin/coffee/app/viewmodel/BrewEntryFormViewModel.kt`
- [ ] T027 [US1] Wire form save to repository and navigate back to list

**Checkpoint**: User can create a valid brew entry. Invalid entries are blocked with feedback. Entry persists after process restart (Room in-memory test passes).

---

## Phase 4: User Story 2 — Choose and Reuse Coffee Origins (Priority: P2)

**Goal**: User can select from 14 predefined origins or create a reusable custom origin inline.

### Tests for User Story 2 (REQUIRED — write before implementation) ⚠️

- [ ] T028 [P] [US2] Write failing test `test_origin_predefinedListComplete` for all 14 origins seeded in `OriginDaoTest.kt`
- [ ] T029 [P] [US2] Write failing test `test_origin_createCustomAndReusable` for custom origin insert + fetch in `OriginDaoTest.kt`
- [ ] T030 [P] [US2] Write failing test `test_origin_duplicateCaseInsensitive` for case-insensitive duplicate prevention in `OriginDaoTest.kt`
- [ ] T031 [P] [US2] Write failing test `test_origin_persistence` for origin surviving mock restart in `OriginDaoTest.kt`

### Implementation for User Story 2

- [ ] T032 [P] [US2] Implement `OriginPickerSheet.kt` with predefined origins list and "Add Custom" option at `composeApp/src/commonMain/kotlin/coffee/app/ui/screens/form/OriginPickerSheet.kt`
- [ ] T033 [P] [US2] Implement `OriginCreateDialog.kt` with text input and case-insensitive duplicate check at `composeApp/src/commonMain/kotlin/coffee/app/ui/screens/form/OriginCreateDialog.kt`
- [ ] T034 [US2] Integrate origin creation and selection in `BrewEntryFormViewModel.kt`

**Checkpoint**: User can select predefined or create custom origins. Custom origins persist and remain reusable.

---

## Phase 5: User Story 3 — Review Brew Entries (Priority: P3)

**Goal**: User can view a concise list and open a detail view.

### Tests for User Story 3 (REQUIRED — write before implementation) ⚠️

- [ ] T035 [P] [US3] Write failing test `test_brewEntry_list_displayFields` for list summary fields in `BrewEntryListViewModelTest.kt`
- [ ] T036 [P] [US3] Write failing test `test_brewEntry_detail_displayAllFields` for detail view completeness in `BrewEntryListViewModelTest.kt`

### Implementation for User Story 3

- [ ] T037 [P] [US3] Implement `BrewEntryListScreen.kt` with `LazyColumn` showing Bean Name, Origin, Roast Type, Grinder Setting, Portion Weight, Created Date at `composeApp/src/commonMain/kotlin/coffee/app/ui/screens/list/BrewEntryListScreen.kt`
- [ ] T038 [P] [US3] Implement `BrewEntryRow.kt` as the list row composable at `composeApp/src/commonMain/kotlin/coffee/app/ui/screens/list/BrewEntryRow.kt`
- [ ] T039 [P] [US3] Implement `BrewEntryDetailScreen.kt` showing all entry info at `composeApp/src/commonMain/kotlin/coffee/app/ui/screens/detail/BrewEntryDetailScreen.kt`

**Checkpoint**: List and detail views display correct fields. Navigation between list and detail works.

---

## Phase 6: User Story 4 — Sort Brew Entries (Priority: P4)

**Goal**: User can sort by Created Date (default, newest first), Bean Name (A-Z), Origin (A-Z), Last Modified Date.

### Tests for User Story 4 (REQUIRED — write before implementation) ⚠️

- [ ] T040 [P] [US4] Write failing test `test_brewEntry_sort_defaultNewestFirst` in `BrewEntryListViewModelTest.kt`
- [ ] T041 [P] [US4] Write failing test `test_brewEntry_sort_beanNameAZ` in `BrewEntryListViewModelTest.kt`
- [ ] T042 [P] [US4] Write failing test `test_brewEntry_sort_originAZ` in `BrewEntryListViewModelTest.kt`
- [ ] T043 [P] [US4] Write failing test `test_brewEntry_sort_lastModifiedDate` in `BrewEntryListViewModelTest.kt`

### Implementation for User Story 4

- [ ] T044 [US4] Add sort option state management to `BrewEntryListViewModel.kt` and pass `SortOption` enum to `BrewEntryDao`
- [ ] T045 [US4] Add sort picker UI (DropdownMenu / SegmentedButton) to `BrewEntryListScreen.kt`

**Checkpoint**: List sorting works for all 4 modes. Default is newest-first.

---

## Phase 7: User Story 5 — Edit or Delete Brew Entries Safely (Priority: P5)

**Goal**: User can edit or delete entries without accidental data loss.

### Tests for User Story 5 (REQUIRED — write before implementation) ⚠️

- [ ] T046 [P] [US5] Write failing test `test_brewEntry_edit_savesUpdatedValues` in `BrewEntryDaoTest.kt`
- [ ] T047 [P] [US5] Write failing test `test_brewEntry_edit_updatesLastModifiedDate` in `BrewEntryDaoTest.kt`
- [ ] T048 [P] [US5] Write failing test `test_brewEntry_delete_confirmationRequired` in `BrewEntryListViewModelTest.kt`
- [ ] T049 [P] [US5] Write failing test `test_brewEntry_unsavedChanges_warning` in `BrewEntryFormViewModelTest.kt`
- [ ] T050 [P] [US5] Write failing test `test_brewEntry_duplicateEntry_warning` in `BrewEntryFormViewModelTest.kt`

### Implementation for User Story 5

- [ ] T051 [US5] Add edit mode to `BrewEntryFormScreen.kt` — pre-populate fields from existing BrewEntry
- [ ] T052 [US5] Add Room update operation in `BrewEntryDao.kt` that updates `lastModifiedDate`
- [ ] T053 [US5] Add `AlertDialog` confirmation on delete in `BrewEntryListScreen.kt` or `BrewEntryDetailScreen.kt`
- [ ] T054 [US5] Add Room delete operation in `BrewEntryDao.kt` (confirmed before calling)
- [ ] T055 [US5] Add unsaved-changes detection with `AlertDialog` in `BrewEntryFormScreen.kt` when navigating back with changed fields
- [ ] T056 [US5] Add duplicate-entry warning dialog in `BrewEntryFormViewModel.kt` comparing all fields

**Checkpoint**: Edit saves correctly, delete requires confirmation, unsaved edits trigger warning, duplicates trigger warning but allow save.

---

## Phase 8: User Story 6 — Use the App in System, Light, and Dark Themes (Priority: P6)

**Goal**: App follows system theme by default, remains readable in light and dark modes.

### Tests for User Story 6 (REQUIRED — write before implementation) ⚠️

- [ ] T057 [P] [US6] Write failing test `test_theme_systemDefaultRespected` for system theme default
- [ ] T058 [P] [US6] Write failing test `test_theme_lightMode_readable` for light mode colour contrast

### Implementation for User Story 6

- [ ] T059 [P] [US6] Apply `MaterialTheme` with `isSystemInDarkTheme()` in `App.kt` entry composable
- [ ] T060 [P] [US6] Verify all screens use `MaterialTheme.colorScheme` for backgrounds, text, and accent colours

**Checkpoint**: App renders correctly in system, light, and dark modes on all MVP screens.

---

## Phase 9: Polish & Cross-Cutting Concerns

**Purpose**: Final quality checks, verification, and hardening

- [ ] T061 [P] Update `README.md` with setup, run, and test instructions for Compose Multiplatform
- [ ] T062 Verify Brew Entry and custom origin persistence by running app restart test flow
- [ ] T063 Verify system/light/dark theme behaviour across all MVP screens
- [ ] T064 [P] Run full `./gradlew :composeApp:allTests`, confirm all tests pass
- [ ] T065 Run quickstart.md validation against the running app on Android

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies — can start immediately
- **Foundational (Phase 2)**: Depends on Setup completion — BLOCKS all user stories
- **User Stories (Phase 3–8)**: All depend on Foundational phase completion
- **Polish (Phase 9)**: Depends on all desired user stories being complete

### User Story Dependencies

- **US1 — Record a Brew Entry (P1)**: Core MVP — foundation for all stories.
- **US2 — Choose Origins (P2)**: Integrates with US1 form but independently testable.
- **US3 — Review Entries (P3)**: Needs brew entries (US1). Read-only, independently testable.
- **US4 — Sort Entries (P4)**: Built on US3 list view. Independently testable.
- **US5 — Edit/Delete (P5)**: Builds on US1 and US3.
- **US6 — Themes (P6)**: Cross-cutting, independently testable with theme toggle.

### Within Each User Story

- Tests MUST be written and FAIL before implementation
- Room entities before DAOs
- DAOs before ViewModels
- ViewModels before UI screens
- Story complete before moving to next priority

### Parallel Opportunities

- All Setup tasks marked [P] can run in parallel
- All Foundational tasks marked [P] can run in parallel
- Tests within a story marked [P] can run in parallel
- UI screens within a story marked [P] can run in parallel
- Theme work (US6) can be done in parallel with other stories