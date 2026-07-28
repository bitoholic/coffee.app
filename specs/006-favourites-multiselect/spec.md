# Feature Specification: Favourites & Multi-Select Bulk Delete

**Feature Branch**: `feature/006-favourites-multiselect`

**Created**: 2026-07-28

**Status**: Draft

**Input**: Add two features: (1) ability to "star" favourite brew entries and filter/sort by starred, (2) multi-select mode for bulk deletion of entries.

## User Stories & Testing *(mandatory)*

### User Story 1 — Star a Favourite (Priority: P1)

As a user, I want to mark my best brew entries as favourites by tapping a star icon so I can quickly find them later.

**Why this priority**: Differentiating exceptional brews from experiments is core to the journaling use case.

**Independent Test**: Open an entry → tap star icon in top bar → star fills red → go back to list → entry shows filled star. Tap again → star empties.

### User Story 2 — Filter by Starred (Priority: P1)

As a user, I want a "Starred" option in the sort selector so I can view only my favourite entries.

**Why this priority**: The filter makes the feature useful — without it, starring has no discoverability.

**Independent Test**: Tap sort → select "Starred" → list filters to show only starred entries. Select another sort option → filter clears, all entries show sorted normally.

### User Story 3 — Multi-Select Mode (Priority: P1)

As a user, I want to enter a selection mode on the list screen so I can select multiple entries at once.

**Why this priority**: Bulk operations are a natural next step after single-entry edit/delete.

**Independent Test**: Long-press an entry → selection mode activates → checkbox appears on each row → selected rows highlight. Tap entries to toggle selection. Tap close button or back → selection mode exits.

### User Story 4 — Bulk Delete (Priority: P1)

As a user, I want to delete all selected entries at once with a single confirmation so I can clean up my data quickly.

**Why this priority**: Deleting one by one is tedious — bulk delete is the payoff for multi-select.

**Independent Test**: Select 3 entries → tap delete button → confirmation dialog shows count → confirm → entries disappear, selection mode exits, snackbar shows count deleted.

### User Story 5 — Delete from Detail Screen (Priority: P2)

As a user, I want a way to delete a single entry from the detail screen using the new delete flow.

**Why this priority**: The existing detail screen delete already works — this is a nice-to-have consistency improvement.

**Independent Test**: Open entry detail → tap delete → confirmation dialog → confirm → navigated back to list, entry gone.

## Open Questions *(to resolve during clarify)*

### Q1 — Star persistence
Should starred state survive app restarts and backups?

### Q2 — Bulk delete confirmation
Simple "Delete N entries?" dialog, or also show which entries will be deleted?

### Q3 — Multi-select exit
Ways to exit selection mode: tap close X, tap back, or also auto-exit after deleting?

### Q4 — Sort/filter UX
"Starred" as a separate filter toggle (star icon button) vs added to the sort dropdown options?

### Q5 — Star icon position
Star icon in the detail screen top bar next to the entry name, or on the card itself in the list?

## Constraints

- All data stored locally in Room DB
- Favourite state must survive app restarts (persist to DB)
- Multi-select must work with search active (filtered selection)
- Sort options dropdown should not grow too long

## Test Coverage Metrics

### Target
- **Minimum line coverage**: 60%
- **Target line coverage**: 75%
- **Coverage measured across**: all `commonMain` source (not generated or test code)

### Scope
Every new feature must include tests for:
- **ViewModel** — state transitions, load/success/error flows, edge cases (empty list, single item, max items)
- **DAO queries** — new or modified Room queries (insert, read, filter, delete)
- **Domain logic** — new enums, sort/filter logic, any `when` branches
- **UI screens** — at minimum the most critical user paths (star an entry, filter list, enter/exit multi-select, delete selected)

### Exempt from coverage
- Composables (structure/positioning logic — covered by manual testing)
- Navigation routes and NavHost wiring
- Data classes with no logic
- Platform-specific entry points (MainActivity, Application class)

### Tooling
- **JVM test runner** — existing `./gradlew check` (runs `commonTest`)
- **Coverage plugin** — add `jacoco` or `kotlinx-kover` to the build for coverage reports
- **Report output**: `build/reports/kover/` or `build/reports/jacoco/` — HTML and XML
- **CI gate**: the `check` task (or a dedicated `coverage` task) must enforce the minimum threshold; a build that drops below 60% coverage should fail

### Current baseline (before v0.0.6)

Establish a baseline coverage measurement on `develop` before adding v0.0.6 code. Compare after implementation to ensure coverage hasn't regressed.

### Verification
```bash
# Run tests and generate coverage report
./gradlew check

# View HTML report
open build/reports/kover/html/index.html
```
