# Implementation Plan: coffee.app MVP

**Branch**: `001-coffee-tracker-mvp` | **Date**: 2026-07-05 | **Spec**: `specs/001-coffee-tracker-mvp/spec.md`

**Input**: Feature specification from `specs/001-coffee-tracker-mvp/spec.md`

**Note**: This plan has been updated to reflect the Compose Multiplatform (Kotlin) stack. All sections have been updated to fully resolve ambiguities and align with the specification and constitution.

## Summary
Build the MVP of coffee.app - a cross-platform mobile app for recording and managing coffee bean brewing settings, targeting both Android and iOS from a single Kotlin codebase. Solves user problem of forgetting successful settings by acting as a simple brewing journal and memory aid. Emphasizes beginner-friendliness, simple architecture, local data, no infrastructure, minimal dependencies, and documented mobile assumptions.

## Technical Context

**Language/Version**: Kotlin 2.1.x (or latest stable)
**Primary Dependencies**: Jetpack Compose Multiplatform (shared declarative UI), Room KMP (local-first persistence with SQLite)
**Storage**: Room KMP (SQLite via SQLDelight-compatible multiplatform Room)
**Testing**: kotlin.test + JVM unit tests (shared module), Compose UI tests (androidTest / iOS simulator)
**Target Platform**: Android (API 26+) and iOS 15+ via Compose Multiplatform
**Project Type**: Compose Multiplatform Mobile App
**Performance Goals**: App launch < 2s, list/detail view load < 300ms for 20 entries on both platforms.
**Constraints**: Local-only data, no backend, no accounts, no cloud sync, MVP scope strictly defined. Single codebase shared between Android and iOS.
**Mobile Assumptions**: Standard mobile device form factors (phone), multi-touch gestures, device locale for date formatting, system theme adoption per platform. Compose Multiplatform renders native on both OSes with a single UI layer.
**Scale/Scope**: Single user, local device storage, MVP focused on core brew entry and origin management.

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

- **Focused Brewing Memory**: Feature directly supports remembering coffee bean
  brewing settings; user value and acceptance criteria are explicit.
- **Local-First Simplicity**: No backend, accounts, cloud sync, payments,
  social features, analytics, or notifications unless explicitly specified and
  justified as a complexity violation. MVP uses local-only persistence, relying
  on Room KMP for local SQLite data storage.
- **Test-First Development**: Failing tests planned before implementation for
  data, state, validation, persistence, and mobile flows. Chosen technologies (Kotlin, Room, Compose)
  support this, ensuring core logic, validation, persistence, custom origins,
  duplicate warnings, and accidental-loss protections are rigorously tested.
- **User-Safe Data Changes**: Destructive actions (delete) require confirmation.
  Unsaved add/edit changes warn before discarding. Custom origins are create-only.
- **Beginner-Friendly Mobile Architecture**: Mobile assumptions, theme behavior,
  storage choice, dependencies, and local run/test commands documented. Using
  Compose Multiplatform for declarative UI and Room KMP for persistence, both with
  strong Kotlin idioms that are approachable for a developer experienced in scripting.

## Dependency Selection & Justification

- **Kotlin**: A modern, statically typed language that runs on JVM (Android) and compiles to native (iOS). Chosen for its strong ecosystem, readability, and beginner-friendly syntax that will feel familiar to a developer with Python scripting experience. Enables true code sharing across platforms.
- **Jetpack Compose Multiplatform**: Declarative UI framework from JetBrains shared across Android and iOS. Chosen for its modern paradigm, approachable learning curve, single codebase for both platforms, and strong integration with Kotlin. Aligns with keeping architecture simple and readable.
- **Room KMP**: Official JetBrains/Kotlin multiplatform persistence library wrapping SQLite. Chosen for its type-safe DAOs, compile-time query validation, built-in migration support, and native multiplatform support. Avoids external dependencies and aligns with local-first, no-infrastructure principle.
- **kotlin.test**: Standard Kotlin testing framework. Essential for enforcing the TDD principle. Runs on JVM for fast feedback and supports multiplatform test targets.

## Data Model Design

- **BrewEntry Entity**:
    - `uuid`: String (Primary Key, auto-generated UUID)
    - `beanName`: String (Required, non-empty after trimming whitespace. MVP allows duplicate entries with warning.)
    - `beanOrigin`: String (Optional, references an Origin entity. If custom, validated for cleanliness and uniqueness check against existing origins.)
    - `roastType`: String (Required. Must be one of "Light", "Medium", or "Dark". Enforced via enum.)
    - `grinderSetting`: Int (Required. Must be between 1 and 48 inclusive.)
    - `portionWeight`: Double (Required. Positive decimal number representing grams. Stored as Double, displayed with 'g'.)
    - `description`: String? (Optional. Maximum length 500 characters.)
    - `createdDate`: Long (Epoch millis auto-set on save. Used for default sorting.)
    - `lastModifiedDate`: Long (Epoch millis auto-set on save and updates on edit completion.)
- **Origin Entity**:
    - `name`: String (Primary Key. Case-insensitive unique identifier. Cleaned of leading/trailing whitespace.)
    - `isCustom`: Boolean (True if created by user, False if predefined.)

**Relationships**:
- Foreign Key: BrewEntry.beanOrigin references Origin.name (optional, nullable). An Origin can be used in many BrewEntries.

**Predefined Origins**: Brazil, Colombia, Ethiopia, Kenya, Guatemala, Costa Rica, Honduras, Peru, El Salvador, Panama, Indonesia, India, Vietnam, Yemen.

**Data Validation Rules Summary**: Bean Name (required), Grinder Setting (1-48), Roast Type (Light/Medium/Dark), Origin Name (unique case-insensitively, sanitized), Description (optional, max 500 chars), Portion Weight (positive decimal grams), Duplicate Brew Entries (warn but allow).

**Data Safety Features**: Delete confirmation, unsaved changes warning, custom origins create-only.

## Local Persistence Approach

- **Room KMP**: Used for local persistence of Brew Entries and custom Origins via SQLite.
    - `CoffeeDatabase` object defines the Room database with entities and DAOs.
    - `BrewEntryDao` handles CRUD for brew entries.
    - `OriginDao` handles CRUD for predefined and custom origins.
    - Predefined origins are seeded on first launch via a callback or migration step.
    - Adheres to local-first principle. Data persists after app restart.

## Navigation Structure

- **Compose Navigation (voyager / decompose / built-in nav)**: Using a simple navigation library or Compose Multiplatform's built-in navigate patterns. A navigation host drives screen transitions.
- **Screens**: BrewEntryList → BrewEntryDetail | BrewEntryForm (add/edit). Modals for origin picker and origin creation.
- **Platform-specific wrappers**: Main Activity (Android) / SwiftUI wrapper (iOS) host the Compose entry point.

## Form Validation Approach

- **Compose Validation**: State hoisting via `MutableState` / `StateFlow` in the ViewModel. Validation functions return `Boolean` or sealed result types. Real-time feedback using `remember` snapshots and `LaunchedEffect` for async checks. Enforces required fields, range checks, specific selections, uniqueness, and length limits.

## Theme Handling Approach

- **`MaterialTheme` / `isSystemInDarkTheme()`**: Detect system theme and apply Material 3 colour schemes.
- **Conditional Styling**: UI elements adapt to light/dark mode via Compose's built-in `MaterialTheme.colorScheme`.
- **Default to System**: App respects system theme settings by default.
- **Custom colours**: Defined in `Theme.kt` with light/dark variants.

## Sorting Approach

- **Room KMP Query Descriptors**: Applied via DAO query with ORDER BY clause (using `SimpleSQLiteQuery` or DAO method naming convention for compile-time validation).
- **Default Sort**: Created Date descending (newest first).
- **User Options**: Bean Name (A-Z), Origin (A-Z), Created Date, Last Modified Date.

## Data Safety Approach

- **Delete Confirmation**: `AlertDialog` composable before deleting a Brew Entry.
- **Unsaved Changes Warning**: State tracking in ViewModel — if any field differs from the loaded or blank entry, a "Discard changes?" dialog appears on back navigation.
- **No Undo in MVP**: Undo functionality deferred.

## Testing Strategy

- **kotlin.test** + JUnit: For unit, integration, and focused UI tests.
- **Test-First**: Failing tests written before implementation.
- **Unit Tests (shared module)**:
    - ViewModel/use-case logic for data manipulation, validation, sorting, and presentation.
    - Room DAO operations for saving, fetching, updating, and deleting Brew Entries and Origins (using Room in-memory database test rule).
    - Utility functions (e.g., date formatting, string validation).
- **Integration Tests (shared module + platform)**: ViewModel ↔ Room DAO ↔ Database interactions.
- **Compose UI Tests**: Critical flows: create/edit/delete, origin management, sorting, theme switching — tested with Compose UI test framework on Android and iOS simulator.
- **Test Case Examples**: `test_brewEntry_validation_grinderSettingRange`, `test_brewEntry_persistence_afterRestart`, `test_origin_create_uniqueCaseInsensitive`, `test_form_unsavedChanges_warning`, `test_delete_brewEntry_confirmation`.

## Dependency List & Justification

- **Kotlin**: Modern, readable language. Single codebase for both platforms.
- **Jetpack Compose Multiplatform**: Declarative UI from JetBrains, shared across Android and iOS. Approachable, modern, simple architecture.
- **Room KMP**: Official Kotlin multiplatform persistence. Type-safe, compile-time verified queries, migration support. Fulfils local-first persistence requirement.
- **kotlin.test**: Standard Kotlin testing framework. Integrated with Gradle, supports multiplatform test targets.

## Risks and Trade-offs

- **Compose Multiplatform Maturity**: While stable, iOS rendering in Compose Multiplatform is newer than SwiftUI. Mitigation: Focus on standard Material 3 components and simple layouts that work well on both platforms.
- **Kotlin Learning Curve**: Developer is experienced in Python scripting but new to Kotlin. Mitigation: Kotlin syntax is approachable for Pythonistas; focus on clear, simple patterns.
- **Room KMP Setup**: Setting up KMP with Room requires Gradle configuration. Mitigation: Leverage the official KMP wizard template for initial project setup.
- **Test-First Discipline**: Maintaining strict TDD requires commitment. Mitigation: Integrate tests into every user story and task.
- **Scope Creep**: MVP scope is strict. Avoid adding out-of-scope features. Mitigation: Adhere to explicit out-of-scope list.

## Project Structure

### Documentation (this feature)

```text
specs/001-coffee-tracker-mvp/
├── plan.md              # This file
├── research.md          # Phase 0 output
├── data-model.md        # Phase 1 output
├── quickstart.md        # Phase 1 output
├── contracts/           # Phase 1 output (N/A for this MVP)
└── tasks.md             # Phase 2 output
```

### Source Code (repository root)

```text
# Compose Multiplatform Project Structure
composeApp/
  src/
    commonMain/          # Shared Kotlin code (UI, ViewModels, Models, Persistence)
      kotlin/
        coffee/app/
          App.kt                          # Root composable entry point
          navigation/
            NavHost.kt                    # Navigation graph
          ui/
            theme/
              Theme.kt                    # Material 3 theme (light/dark)
              Color.kt                    # Color definitions
            screens/
              list/
                BrewEntryListScreen.kt    # List screen composable
                BrewEntryRow.kt           # List row composable
              detail/
                BrewEntryDetailScreen.kt  # Detail screen composable
              form/
                BrewEntryFormScreen.kt    # Add/Edit form screen composable
                OriginPickerSheet.kt      # Origin selection bottom sheet / dialog
                OriginCreateDialog.kt     # Custom origin creation dialog
          data/
            database/
              CoffeeDatabase.kt           # Room KMP database definition
              BrewEntryDao.kt             # BrewEntry DAO interface
              OriginDao.kt                # Origin DAO interface
            model/
              BrewEntry.kt                # BrewEntry entity data class
              Origin.kt                   # Origin entity data class
              RoastType.kt                # RoastType enum
              SortOption.kt               # SortOption enum
            repository/
              BrewEntryRepository.kt      # Abstraction layer over DAOs
              OriginRepository.kt         # Abstraction layer over DAOs
          viewmodel/
            BrewEntryListViewModel.kt     # List + sort + delete logic
            BrewEntryFormViewModel.kt     # Add/Edit + validation logic
          util/
            DateFormatUtil.kt             # Date formatting helpers
            ValidationUtil.kt             # Shared validation functions
          di/
            AppModule.kt                  # Simple manual DI / service locator
    androidMain/         # Android-specific code
      kotlin/
        coffee/app/
          MainActivity.kt                # Android activity hosting the Compose entry point
    iosMain/             # iOS-specific code (minimal)
      kotlin/
        coffee/app/
          MainViewController.kt          # iOS UIKit bridge for SwiftUI wrapper
    commonTest/          # Shared tests (JVM target)
      kotlin/
        coffee/app/
          data/
            database/
              BrewEntryDaoTest.kt         # In-memory Room DAO tests
              OriginDaoTest.kt            # In-memory Room DAO tests
          viewmodel/
            BrewEntryListViewModelTest.kt # ViewModel tests
            BrewEntryFormViewModelTest.kt # ViewModel tests
          util/
            ValidationUtilTest.kt         # Validation function tests
iosApp/
  iosApp/                # Thin SwiftUI wrapper
    iosAppApp.swift
    ContentView.swift
  iosApp.xcodeproj
gradle/
  libs.versions.toml     # Version catalog
build.gradle.kts         # Root build file
settings.gradle.kts      # Settings
README.md                # Project setup, run, and test instructions
```

**Structure Decision**: Compose Multiplatform project structure selected, utilizing `composeApp/src/commonMain/` for all shared code (UI, data, ViewModels, utilities) and platform-specific directories only for thin entry points. This maximizes code sharing and keeps the architecture simple for a beginner.

## Phase 0: Outline & Research (Summary)

- Research Compose Multiplatform project setup with Kotlin 2.1.x and Gradle version catalog.
- Confirm Room KMP suitability for local structured data with @Entity and @Dao annotations.
- Research Compose Multiplatform theme handling (Material 3, isSystemInDarkTheme).
- Find patterns for unsaved changes confirmation in Compose Navigation.
- Research Compose AlertDialog patterns for delete safety confirmation.

## Phase 1: Design & Contracts (Summary)

- **Data Model**: Defined BrewEntry and Origin entities with explicit attributes, types, validation rules, and relationships. Detailed in `data-model.md`.
- **Persistence**: Room KMP for local, structured SQLite data storage.
- **Navigation**: Compose Navigation (NavHost) for screen transitions, modal dialogs for forms/pickers.
- **Validation**: Kotlin validation functions with Compose real-time feedback.
- **Theme**: Material 3 with `isSystemInDarkTheme()` for system, light, dark mode support.
- **Sorting**: Room DAO query sorting via compile-time verified ORDER BY clauses.
- **Data Safety**: `AlertDialog` for delete confirmation, state tracking for unsaved changes warning.
- **File Structure**: Compose Multiplatform project under `composeApp/`, shared tests in `commonTest/`.

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

- **Focused Brewing Memory**: Feature directly supports remembering coffee bean
  brewing settings; user value and acceptance criteria are explicit.
- **Local-First Simplicity**: No backend, accounts, cloud sync, payments,
  social features, analytics, or notifications unless explicitly specified and
  justified as a complexity violation. MVP uses local-only persistence via Room KMP.
- **Test-First Development**: Failing tests planned before implementation for
  data, state, validation, persistence, and mobile flows. Chosen technologies (Kotlin, Room KMP, Compose Multiplatform)
  support this.
- **User-Safe Data Changes**: Destructive actions (delete) require confirmation.
  Unsaved add/edit changes warn before discarding. Custom origins are create-only.
- **Beginner-Friendly Mobile Architecture**: Mobile assumptions, theme behavior,
  storage choice, dependencies, and local run/test commands documented. Using
  Compose Multiplatform and Room KMP for clarity and beginner-friendliness.

## Explicitly Out-of-Scope for MVP

- User accounts, authentication
- Cloud sync, backend services, APIs
- Sharing entries, ratings, favourites, photos
- Brewing timers, recipes, multiple grinder profiles, multiple brewing methods
- Notifications, analytics, payments, social features
- App store publishing (MVP focuses on local development and testing)
- Renaming or deleting custom origins (MVP limits management to creation)
- Undo functionality for deletion (MVP uses confirmation only)
- Backend infrastructure of any kind. The app is strictly local-first.