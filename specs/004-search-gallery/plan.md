# Implementation Plan: Image Gallery & List Search

**Branch**: `feature/004-search-gallery` | **Date**: 2026-07-25 | **Spec**: `specs/004-search-gallery/spec.md`

## Summary

Replace single-photo attachment with a multi-photo gallery (up to 10 per entry, gallery multi-select + sequential camera). Add real-time search by bean name on the list screen.

## Technical Context

**Language/Version**: Kotlin 2.1.10, Compose Multiplatform

**Primary Dependencies**: Room KMP (new EntryPhoto entity), Compose (LazyRow, swipe), ActivityResultContracts.GetMultipleContents

**Storage**: Room + local files (`filesDir/photos/`)

**Testing**: `./gradlew check`

**Target Platform**: Android API 26+

**Constraints**:
- Max 10 photos per entry
- Gallery multi-select + sequential camera captures
- Search filters by bean name only, as-you-type, no button needed
- Search results sortable via existing sort dropdown
- Existing single photos migrated to new table
- Horizontal scrollable thumbnail row on form and detail
- Swipeable fullscreen gallery view

## Constitution Check

*GATE: Must pass.*

- **Focused Brewing Memory**: Gallery captures full visual record. Search finds past brews fast.
- **Local-First Simplicity**: All local. No backend.
- **Test-First Development**: Tests for EntryPhoto CRUD, search filtering, photo migration.
- **User-Safe Data Changes**: Photo removal explicit (X button). Entry deletion cascades.
- **Beginner-Friendly Mobile Architecture**: Uses established patterns (Room DAOs, Compose LazyRow, existing sort).

## Implementation Order

1. **EntryPhoto entity + DAO** — New Room table, FK to BrewEntry, CRUD operations
2. **DB migration** — Bump to v4, migrate existing BrewEntry.photoPath to EntryPhoto table, remove old column
3. **Update PhotoManager** — Support multiple photos, batch delete, move to androidMain if needed
4. **Form — multi-photo picker** — Multiple gallery selection + sequential camera, horizontal thumbnail row with remove buttons
5. **Detail — photo gallery** — Horizontal scrollable gallery, swipeable fullscreen view
6. **List — photo indicator** — First photo thumbnail or stacked indicator for multiple
7. **Search UI** — Search icon next to sort, expandable inline bar, close/X button
8. **Search logic** — Real-time filter by bean name in ViewModel, sortable results
9. **Tests & Polish** — Unit tests, final build

**Next step**: Generate tasks from this plan.
