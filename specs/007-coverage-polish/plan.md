# Implementation Plan: Test Coverage Fix & CI Polish

**Feature**: `feature/007-coverage-polish`

**Created**: 2026-07-30

## Overview

Four independent workstreams in one release:
1. **Test coverage** — Write tests across all packages to reach 85% line coverage
2. **CI metric alignment** — Ensure comment script and koverVerify measure the same thing
3. **CI badge fix** — Replace broken shields.io badge with GitHub Actions workflow badge
4. **Coverage badge** — Add static shields.io badge, updated per release

## Phases

### Phase 1 — Align koverVerify and comment script
- Change koverVerify `minBound` to use explicit `LINE` metric with `COVERED_PERCENTAGE` aggregation using the correct Kover DSL syntax
- Update comment script to read the same kover XML report (it already does, but validate the aggregation matches koverVerify output)
- The threshold stays at 8% during this phase (raised in Phase 2)

### Phase 2 — Write tests to reach 85%
- **ViewModel tests**: BrewEntryListViewModel (favourites, multi-select, bulk delete, sort, starred filter, search), BrewEntryFormViewModel (existing, expand), SettingsViewModel (backup/restore)
- **DAO tests**: Move to androidUnitTest with Room.inMemoryDatabaseBuilder, test observeFavourites, deleteByUuids, updateFavourite, getAll, upsert
- **Repository tests**: BrewEntryRepository delegates to DAO, verify getById, deleteByUuids, getAll, observeFavourites, updateFavourite
- **Domain tests**: SortOption (display names, ordering), RoastType, RestoreMode, BackupException
- **Backup engine tests**: BackupEngine.createBackup/parseBackup roundtrip, manifest validation, error cases (empty entries, corrupt ZIP)
- **Utility tests**: DateFormatUtil, PhotoManager edge cases
- Raise threshold to 85%

### Phase 3 — Fix CI badge
- README badge URL points to GitHub Actions workflow badge instead of shields.io
- Format: `https://github.com/bitoholic/coffee.app/actions/workflows/ci.yml/badge.svg?branch=develop`

### Phase 4 — Add coverage badge
- README coverage badge using shields.io static badge
- Update manually each release with current percentage
- Format: `https://img.shields.io/badge/coverage-8%25-red` (color changes: <50% red, 50-75% yellow, 75%+ green)
