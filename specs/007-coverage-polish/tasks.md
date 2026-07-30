# Task List: Test Coverage Fix & CI Polish

## Phase 1 — Align Metrics

| ID | Task | Status |
|----|------|--------|
| t_al_001 | Fix koverVerify `minBound` DSL to explicitly measure LINE + COVERED_PERCENTAGE | pending |
| t_al_002 | Validate comment script parses the same XML and aggregates identically to koverVerify | pending |
| t_al_003 | Verify `./gradlew koverVerify` and comment script agree on a test build | pending |

## Phase 2 — Write Tests (85% coverage)

| ID | Task | Status |
|----|------|--------|
| t_cov_001 | ViewModel tests: BrewEntryListViewModel (favourites, multi-select, starred filter, sort, search, bulk delete) | pending |
| t_cov_002 | ViewModel tests: BrewEntryFormViewModel (create, edit, validation) | pending |
| t_cov_003 | ViewModel tests: SettingsViewModel (backup, restore, overwrite, merge) | pending |
| t_cov_004 | DAO tests: BrewEntryDao (observeFavourites, deleteByUuids, updateFavourite, getAll, upsert, getById) | pending |
| t_cov_005 | DAO tests: EntryPhotoDao (insert, getPhotosForEntry) | pending |
| t_cov_006 | Repository tests: BrewEntryRepository delegates (getById, deleteByUuids, getAll, updateFavourite) | pending |
| t_cov_007 | Domain tests: SortOption, RoastType, RestoreMode, BackupException | pending |
| t_cov_008 | Backup engine tests: roundtrip create/parse, error cases (empty, corrupt, missing manifest) | pending |
| t_cov_009 | Utility tests: DateFormatUtil, ThemeMode | pending |
| t_cov_010 | Raise koverVerify threshold from 8% to 85% | pending |
| t_cov_011 | Run `./gradlew check koverXmlReport koverVerify` — must pass at 85% | pending |

## Phase 3 — Fix CI Badge

| ID | Task | Status |
|----|------|--------|
| t_badge_001 | Replace shields.io CI badge URL with GitHub Actions workflow badge | pending |
| t_badge_002 | Verify badge shows passing/failing on develop | pending |

## Phase 4 — Coverage Badge

| ID | Task | Status |
|----|------|--------|
| t_badge_003 | Add shields.io static badge for coverage percentage | pending |
| t_badge_004 | Update badge value to current coverage after Phase 2 | pending |

## Phase 5 — PR & Review

| ID | Task | Status |
|----|------|--------|
| t_pr_001 | Create PR to develop | pending |
| t_pr_002 | Wait for Master Jack's review before merging | pending |
| t_pr_003 | After approval, merge and tag v0.0.7 | pending |
