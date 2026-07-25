# Feature Specification: Image Gallery & List Search

**Feature Branch**: `feature/022-search-gallery`

**Created**: 2026-07-25

**Status**: Draft

**Input**: Add multiple photo support per entry (gallery) and search/filter the brew entry list.

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Multiple Photos Per Entry (Priority: P1)

As a user, I want to attach and view multiple photos per brew entry so I can document the beans, the grind, the brew process, and the result all in one place.

**Why this priority**: The current single-photo limitation was a starting point. Multiple photos add significantly more value.

**Independent Test**: Add an entry, attach 3 photos, save, reopen entry — all 3 photos visible. Tap each to expand.

**Acceptance Scenarios**:

1. **Given** I am on the add/edit form, **When** I tap "Add Photo", **Then** I can select multiple images from the gallery (or take multiple camera shots).
2. **Given** I have selected multiple images, **When** they're attached, **Then** thumbnails appear in a horizontal scrollable row on the form.
3. **Given** an entry has multiple photos, **When** viewed on the detail screen, **Then** a horizontal scrollable gallery appears at the top.
4. **Given** I tap a photo in the gallery, **When** viewing it fullscreen, **Then** I can swipe left/right between photos.
5. **Given** I am editing an entry, **When** I tap the X on any photo thumbnail, **Then** that photo is removed.
6. **Given** I close and reopen the app, **When** viewing an entry with multiple photos, **Then** all photos persist.
7. **Given** an entry has photos, **When** viewed in the list, **Then** the first photo shows as the thumbnail or a stacked indicator for multiple.

---

### User Story 2 - Search Brew Entries (Priority: P2)

As a user, I want to search my brew entries by name, origin, or notes so I can quickly find specific brews.

**Why this priority**: As the entry list grows, search becomes essential for finding past entries.

**Independent Test**: Add entries with different beans and origins, type a search term, confirm only matching entries show.

**Acceptance Scenarios**:

1. **Given** I am on the main list screen, **When** I tap the search icon, **Then** a search bar appears at the top.
2. **Given** the search bar is open, **When** I type text, **Then** the list filters in real-time to show only entries where bean name, origin, or description contains the search text (case-insensitive).
3. **Given** the search bar has text, **When** I clear it, **Then** the full list is restored.
4. **Given** the search bar is open, **When** I tap the back/X button, **Then** the search bar closes and the full list shows.

---

### Edge Cases

- What happens when a user selects 20+ photos? Limit to 10 photos per entry for performance.
- What happens when searching with no results? Show "No entries found" message.
- What happens to photos when an entry is deleted? All associated photos are deleted.
- What happens during search with special characters? Normal text search, no regex needed.
- What happens when storage is full during photo selection? Graceful error handling.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: The app MUST support up to 10 photos per brew entry.
- **FR-002**: Photos MUST be stored as individual files in `filesDir/photos/` with UUID filenames, referenced by a new `entry_photos` Room table.
- **FR-003**: A new `EntryPhoto` entity MUST store photo UUID, file path, and display order per entry.
- **FR-004**: The form MUST have an Add Photo button that supports multiple gallery selection or sequential camera captures.
- **FR-005**: The form MUST show a horizontal scrollable row of photo thumbnails (64dp) with X remove buttons.
- **FR-006**: The detail screen MUST show a horizontal scrollable gallery at the top with tap-to-expand and swipe navigation.
- **FR-007**: The list screen MUST show the first photo as a thumbnail, or a stacked indicator for multiple photos.
- **FR-008**: A search bar MUST be accessible from the list screen top bar (search icon).
- **FR-009**: Search MUST filter entries in real-time by bean name, origin, and description (case-insensitive).
- **FR-010**: Search MUST run client-side on the existing data (no backend needed).

### Constitution Alignment *(mandatory)*

- **Brewing-memory value**: Multiple photos capture the full brew story. Search finds past entries quickly.
- **Local-first scope**: All photos local, search runs on-device. No backend.
- **User-safe data changes**: Photo removal is explicit (X button). Entry deletion cascades to all photos.
- **Theme behavior**: No change — gallery and search work in both themes.
- **Mobile assumptions**: Gallery uses Room's `@Relation` for one-to-many. Search uses simple string matching on the in-memory list. Horizontal scrolling uses `LazyRow`.

### Key Entities *(include if feature involves data)*

- **EntryPhoto**: New Room entity — `id: Int` (auto), `entryUuid: String` (FK to BrewEntry), `photoPath: String`, `sortOrder: Int`.
- **BrewEntry**: Remove `photoPath: String?` field (migrate to EntryPhoto table).
- **EntryPhotoDao**: New DAO — CRUD operations for entry photos.
- **PhotoManager**: Update to handle multiple photos, deletion cascading.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: User can add 10 photos to one entry, all save and reload correctly.
- **SC-002**: Tapping a photo opens fullscreen with swipeable gallery.
- **SC-003**: Typing in search bar filters the list in under 200ms.
- **SC-004**: Deleting an entry removes all its photos from storage.
- **SC-005**: Searching with no results shows a clear "no entries" state.

## Assumptions

- The `BrewEntry.photoPath` field will be replaced by the `EntryPhoto` table. Old single-photo entries will lose photo association — acceptable for dev phase.
- Search filters the in-memory list via Kotlin's `filter {}` — fast enough for hundreds of entries.
- Gallery multi-select uses `ActivityResultContracts.GetMultipleContents()`.
- Horizontal photo gallery on detail uses `LazyRow` with `HorizontalPager` or similar for fullscreen swipe.
- Database version bumped to 4 with migration handling.
