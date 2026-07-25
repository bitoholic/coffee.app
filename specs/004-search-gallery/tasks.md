---

description: "Task list for image gallery and list search feature"
---

# Tasks: Image Gallery & List Search

**Input**: Spec and plan from `specs/004-search-gallery/`

## Phase 1: Data Layer (DB migration)

- [ ] T001 Create `EntryPhoto` Room entity — `id: Int` (auto), `entryUuid: String` (FK), `photoPath: String`, `sortOrder: Int`
- [ ] T002 Create `EntryPhotoDao` — CRUD operations, find by entryUuid, delete by entryUuid
- [ ] T003 Register EntryPhoto entity and DAO in CoffeeDatabase, bump to v4
- [ ] T004 Write migration from v3→v4 — migrate existing BrewEntry.photoPath to EntryPhoto table, drop old column
- [ ] T005 Update PhotoManager — support multiple photos, batch delete cascading

## Phase 2: Form — Multi-Photo Picker

- [ ] T006 Replace single photo picker with multi-select gallery (`GetMultipleContents`)
- [ ] T007 Add sequential camera capture (tap Add Photo → camera → back to form → tap again for another)
- [ ] T008 Replace single thumbnail with horizontal scrollable row (`LazyRow`) of thumbnails with X remove buttons
- [ ] T009 Wire up to 10 photo limit

## Phase 3: Detail — Photo Gallery

- [ ] T010 Replace single photo with horizontal scrollable gallery (`LazyRow`) at top of detail screen
- [ ] T011 Add swipeable fullscreen photo viewer (left/right between photos, pinch-to-zoom)
- [ ] T012 Update list screen — first photo thumbnail or stacked indicator for multiple photos
- [ ] T013 Remove old single-photo code (`photoPath` references) from list, detail, form screens

## Phase 4: Search

- [ ] T014 Add search icon next to sort button in list screen top bar area
- [ ] T015 Implement expandable search bar (inline between sort and search icon)
- [ ] T016 Add search query state to BrewEntryListViewModel — real-time filter by bean name
- [ ] T017 Wire search results to remain sortable via existing sort dropdown
- [ ] T018 Add close/X button to collapse search bar and clear filter
- [ ] T019 Show "No entries found" empty state when search returns no results

## Phase 5: Polish

- [ ] T020 Update FakeBrewEntryDao in tests with new methods
- [ ] T021 Run `./gradlew check`
- [ ] T022 Build signed release APK
- [ ] T023 Update kanban board
