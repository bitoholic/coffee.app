# Task List: Favourites & Multi-Select Bulk Delete

## Phase 1 — Data Layer

| ID | Task | Status |
|----|------|--------|
| t_dl_001 | Add `isFavourite` field (INTEGER, default 0) to BrewEntry entity | pending |
| t_dl_002 | Bump DB version to 5, write v4→v5 migration (ALTER TABLE brew_entries ADD COLUMN isFavourite INTEGER NOT NULL DEFAULT 0) | pending |
| t_dl_003 | Add `getFavourites(): Flow<List<BrewEntry>>` to BrewEntryDao | pending |
| t_dl_004 | Add `deleteByUuids(uuids: List<String>)` to BrewEntryDao | pending |
| t_dl_005 | Add `updateFavourite(uuid: String, isFavourite: Boolean)` to BrewEntryDao | pending |
| t_dl_006 | Add `isFavourite` to BackupEntry data class and BackupEngine serialization | pending |
| t_dl_007 | Update BrewEntryListViewModel to expose favourite toggle method | pending |
| t_dl_008 | Run `./gradlew check` — must pass | pending |

## Phase 2 — Favourites UI

| ID | Task | Status |
|----|------|--------|
| t_fav_001 | Add star icon to list row cards (filled/empty star, BrandRed when favourited) | pending |
| t_fav_002 | Add star icon to detail screen top bar | pending |
| t_fav_003 | Wire star toggle: icon tap → ViewModel.toggleFavourite(uuid) → DAO update | pending |
| t_fav_004 | Add `STARRED` to SortOption enum | pending |
| t_fav_005 | Add "Starred" to sort dropdown display list | pending |
| t_fav_006 | Wire STARRED filter: when selected, call `observeAllBySort(SortOption.STARRED)` or equivalent | pending |
| t_fav_007 | Run `./gradlew check` — must pass | pending |

## Phase 3 — Multi-Select Mode

| ID | Task | Status |
|----|------|--------|
| t_ms_001 | Add selection state to ViewModel: `selectedIds: Set<String>`, `isSelectionMode: Boolean` | pending |
| t_ms_002 | Long-press on list row enters selection mode, toggles that row selected | pending |
| t_ms_003 | Add checkbox overlay on each row when in selection mode | pending |
| t_ms_004 | Highlight selected rows with background tint | pending |
| t_ms_005 | Close X button in top bar exits selection mode | pending |
| t_ms_006 | System back exits selection mode | pending |
| t_ms_007 | Navigation to detail/form exits selection mode | pending |
| t_ms_008 | Run `./gradlew check` — must pass | pending |

## Phase 4 — Bulk Delete

| ID | Task | Status |
|----|------|--------|
| t_bd_001 | Add bottom persistent bar showing count ("3 selected") + delete button | pending |
| t_bd_002 | Delete button shows dialog: "Delete N entries?" with Cancel/Delete | pending |
| t_bd_003 | Confirm calls `brewEntryDao.deleteByUuids(selectedIds.toList())` | pending |
| t_bd_004 | After delete: exit selection mode, show snackbar "Deleted N entries" | pending |
| t_bd_005 | Run `./gradlew check` — must pass | pending |

## Phase 5 — Coverage & Polish

| ID | Task | Status |
|----|------|--------|
| t_cov_001 | Write ViewModel tests: toggleFavourite, sort by starred, toggle selection, clear selection, delete selected | pending |
| t_cov_002 | Write DAO tests (Room in-memory) for new queries | pending |
| t_cov_003 | Write domain tests for SortOption enum and STARRED logic | pending |
| t_cov_004 | Raise koverVerify threshold to match new coverage | pending |
| t_cov_005 | Run `./gradlew check koverXmlReport koverVerify` — must pass | pending |

## Phase 6 — CI & Release

| ID | Task | Status |
|----|------|--------|
| t_ci_001 | Push updated `.github/workflows/ci.yml` with Kover steps | pending |
| t_ci_002 | PR → develop, wait for CI green | pending |
| t_ci_003 | Fast-forward release PR → main | pending |
| t_ci_004 | Tag v0.0.6, build signed APK, attach to release | pending |
| t_ci_005 | Clean up branches, update README, update kanban | pending |
