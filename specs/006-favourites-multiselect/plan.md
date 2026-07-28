# Implementation Plan: Favourites & Multi-Select Bulk Delete

**Feature**: `feature/006-favourites-multiselect`

**Created**: 2026-07-28

## Overview

Two interconnected features delivered in one release because they share changes to the list screen and data layer:

1. **Favourites** — Add `isFavourite` field to BrewEntry, star icon on list cards and detail top bar, "Starred" sort/filter option
2. **Multi-Select Bulk Delete** — Long-press enters selection mode, checkboxes on rows, bottom delete bar, confirmation dialog

The features share: ViewModel state management, DAO queries, Room DB changes.

## Phases

### Phase 1 — Data Layer
- Add `isFavourite` (INTEGER, default 0) to BrewEntry entity
- Bump DB version to 5 with migration v4→v5
- Add DAO queries:
  - `getFavourites()` — Flow of only starred entries (for "Starred" filter)
  - `deleteByUuids(uuids: List<String>)` — bulk delete
  - `updateFavourite(uuid: String, isFavourite: Boolean)` — toggle star
- Update BackupEngine: add `isFavourite` to BackupEntry serialization
- Update BackupModel.BackupEntry: add `isFavourite` field

### Phase 2 — Favourites UI
- Add star icon to list row cards (filled/empty star, BrandRed when favourited)
- Add star icon to detail screen top bar (next to entry name)
- Wire star toggle through ViewModel → DAO
- Add "Starred" option to SortOption enum and sort dropdown
- When "Starred" selected: show only favourited entries (no secondary sort needed)

### Phase 3 — Multi-Select Mode
- Add selection state to ViewModel: `selectedIds: Set<String>`, `isSelectionMode: Boolean`
- Long-press on list row activates selection mode
- Checkbox overlay on each row when in selection mode
- Selected rows get visual highlight (background tint)
- Close X button in top bar exits selection mode
- System back exits selection mode
- Navigation to any other screen exits selection mode

### Phase 4 — Bulk Delete
- Bottom bar appears when in selection mode showing count ("3 selected") + delete button
- Delete button shows confirmation dialog: "Delete N entries?"
- Confirm calls `deleteByUuids()` then exits selection mode
- Success snackbar: "Deleted N entries"
- Selection resets after delete

### Phase 5 — Coverage & Polish
- Write ViewModel unit tests for:
  - `toggleFavourite()` state change
  - `setSortOption(SortOption.STARRED)` filter
  - `toggleSelection()`, `clearSelection()`, `deleteSelected()`
  - Edge cases: empty selection, all selected, none favourited
- Write DAO tests (or verify via Room in-memory test) for new queries
- Write domain tests for SortOption enum and filter logic
- Raise koverVerify threshold to match new coverage

### Phase 6 — CI & Release
- Push updated `.github/workflows/ci.yml` with Kover steps
- PR and merge to develop
- Wait for CI green
- Fast-forward release PR to main
- Tag v0.0.6, build signed APK, attach to release
