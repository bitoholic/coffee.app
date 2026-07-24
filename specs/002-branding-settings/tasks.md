---

description: "Task list for branding, app icon and settings screen feature"
---

# Tasks: Branding, App Icon & Settings Screen

**Input**: Design documents from `specs/002-branding-settings/`

**Prerequisites**: plan.md (approved), spec.md

**Tests**: Tests are REQUIRED by the coffee.app constitution. Include test tasks before implementation tasks for behavior involving data, state, validation, persistence, or important mobile flows.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (US1 = branding, US2 = settings access, US3 = theme toggle)
- Include exact file paths in descriptions

## Phase 1: Foundational (Shared Infrastructure)

**Purpose**: Brand colours and shared UI components that all stories depend on

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

- [ ] T001 [P] Create brand colour constants and custom Material 3 colour schemes in `composeApp/src/commonMain/kotlin/coffee/app/core/Theme.kt` — primary `#C8102E`, dark/light schemes
- [ ] T002 Create shared `TopBar` composable in `composeApp/src/commonMain/kotlin/coffee/app/core/TopBar.kt` — squircle logo + "bitoholic" text on left, optional gear icon on right
- [ ] T003 Create `AppPreferences` Room entity and `AppPreferencesDao` in `composeApp/src/commonMain/kotlin/coffee/app/data/database/AppPreferences.kt` and `AppPreferencesDao.kt` — key-value structure for future extendability
- [ ] T004 Register `AppPreferences` entity and DAO in `CoffeeDatabase.kt`

**Checkpoint**: Foundation ready — user story implementation can begin

---

## Phase 2: User Story 1 — See the Bitoholic Brand (Priority: P1) 🎯 MVP

**Goal**: Brand colours, logo mark in top bar on all screens, new app icon

**Independent Test**: Launch app and verify red brand colour on FAB, logo mark in top bar, correct app icon on home screen

### Tests for User Story 1

- [ ] T005 [US1] Test that brand colour `#C8102E` is applied to primary Material 3 colour scheme in both light and dark modes
- [ ] T006 [US1] Test that TopBar renders squircle logo and "bitoholic" text

### Implementation for User Story 1

- [ ] T007 [P] [US1] Update `App.kt` to use custom colour schemes from `Theme.kt`
- [ ] T008 [P] [US1] Add `TopBar` to all screens (list, detail, form, settings) in their respective composables
- [ ] T009 [P] [US1] Add release signing config to `composeApp/build.gradle.kts` (already done — verify)
- [ ] T010 [US1] Generate Android adaptive icon assets (steaming cup + binary below) in `composeApp/src/androidMain/res/mipmap-*/`

**Checkpoint**: Brand identity visible throughout the app

---

## Phase 3: User Story 2 — Access Settings via Gear Icon (Priority: P2)

**Goal**: Gear icon on main list screen top bar opens settings screen with back navigation

**Independent Test**: Tap gear icon → settings screen opens → tap back → return to list

### Tests for User Story 2

- [ ] T011 [US2] Test that gear icon is visible on main list screen TopBar
- [ ] T012 [US2] Test that gear icon is NOT visible on detail, form, and settings screens
- [ ] T013 [US2] Test that tapping gear icon navigates to settings screen
- [ ] T014 [US2] Test that back navigation returns to previous screen

### Implementation for User Story 2

- [ ] T015 [US2] Add gear icon to TopBar on list screen only (pass `showSettings=true` prop)
- [ ] T016 [US2] Create settings screen state management in `App.kt` (new `Screen.Settings` sealed class case)
- [ ] T017 [US2] Wire gear icon tap → navigate to Settings, back button → return to list

**Checkpoint**: Settings screen accessible and navigable

---

## Phase 4: User Story 3 — Switch Theme Between Light, Dark & System (Priority: P3)

**Goal**: Segmented button toggle for System / Light / Dark, persists via Room

**Independent Test**: Select each theme → app switches immediately → restart app → preference remembered

### Tests for User Story 3

- [ ] T018 [P] [US3] Test that preference DAO saves and retrieves theme mode correctly
- [ ] T019 [P] [US3] Test that default theme mode is "system" on first launch
- [ ] T020 [P] [US3] Test that selecting Light applies light colours immediately
- [ ] T021 [P] [US3] Test that selecting Dark applies dark colours immediately
- [ ] T022 [P] [US3] Test that selecting System follows device setting
- [ ] T023 [US3] Test that theme preference persists after app restart

### Implementation for User Story 3

- [ ] T024 [P] [US3] Create `SettingsViewModel` in `composeApp/src/commonMain/kotlin/coffee/app/settings/SettingsViewModel.kt` — reads/writes theme preference via DAO
- [ ] T025 [US3] Create `SettingsScreen` in `composeApp/src/commonMain/kotlin/coffee/app/settings/SettingsScreen.kt` — segmented button with System / Light / Dark options
- [ ] T026 [US3] Wire theme state into `App.kt` — read stored preference on launch, apply colour scheme reactively, override `isSystemInDarkTheme()` when Light or Dark is selected

**Checkpoint**: Theme toggle fully functional with persistence

---

## Phase 5: Polish & Cross-Cutting Concerns

- [ ] T027 [P] Run `./gradlew check` — verify all tests pass
- [ ] T028 Build signed release APK via `./gradlew :composeApp:assembleRelease`
- [ ] T029 Verify README commands are accurate
- [ ] T030 Update kanban board with completed tasks

---

## Dependencies & Execution Order

### Phase Dependencies

- **Foundational (Phase 1)**: No dependencies — BLOCKS all user stories
- **US1 — Branding (Phase 2)**: Depends on Phase 1 (Theme.kt, TopBar.kt)
- **US2 — Settings Access (Phase 3)**: Depends on Phase 1 (TopBar.kt)
- **US3 — Theme Toggle (Phase 4)**: Depends on Phase 1 (AppPreferences, colour schemes) + Phase 3 (settings screen exists)
- **Polish (Phase 5)**: Depends on all user stories complete

### Execution Order

- Phase 1 → Phase 2 → Phase 3 → Phase 4 → Phase 5 (sequential, single developer)
- Within each phase: tests first, then implementation
