# Implementation Plan: Backup/Restore & Proper Navigation

**Feature**: `feature/005-backup-navigation`

**Created**: 2026-07-26

## Overview

Two major features delivered together because they touch overlapping areas (settings screen, App.kt, platform-specific code):

1. **Backup/Restore** — Export entries (with/without photos) to a ZIP file, import via SAF with overwrite/merge dialog
2. **Compose Navigation** — Replace manual `currentScreen: Screen` state with Compose Navigation + type-safe args

## Phases

### Phase 1 — Backup Engine (platform-agnostic)

**Goal**: Data model serialization, ZIP creation/parsing, manifest generation.

**Files**: New `coffee.app.backup` package.

**Tasks**:
- `t_backup_001` — Create `BackupModel.kt`: data classes for `entries.json` and `manifest.json` with kotlinx-serialization
- `t_backup_002` — Create `BackupEngine.kt`: core logic to serialize entries to JSON, build ZIP archive
- `t_backup_003` — Create `RestoreEngine.kt`: parse ZIP, deserialize entries, handle overwrite vs merge logic
- `t_backup_004` — Handle photo paths during backup: copy photo files into ZIP photos/ folder
- `t_backup_005` — Handle photo extraction during restore: extract photos from ZIP to app storage
- `t_backup_006` — Error handling: invalid ZIP, missing manifest, corrupt JSON

### Phase 2 — Backup UI (Android-specific)

**Goal**: Settings screen backup/restore buttons, SAF picker, share-sheet, progress indicator.

**Files**: Settings screen, new backup UI composables.

**Tasks**:
- `t_backup_007` — Add "Backup" and "Restore" buttons to Settings screen
- `t_backup_008` — Add "Include Photos" toggle checkbox to Settings
- `t_backup_009` — Implement backup flow: generate ZIP → share-sheet (Intent.ACTION_SEND)
- `t_backup_010` — Implement restore flow: SAF picker → parse ZIP → overwrite/merge dialog → restore
- `t_backup_011` — Show progress indicator during backup/restore (long operations)
- `t_backup_012` — Show success/error snackbar after completion

### Phase 3 — Compose Navigation Setup

**Goal**: Add Compose Navigation dependency, define routes, set up NavHost.

**Files**: `build.gradle.kts`, new `Navigation.kt`.

**Tasks**:
- `t_nav_001` — Add Compose Navigation and kotlinx-serialization dependencies to build.gradle.kts
- `t_nav_002` — Define route constants and type-safe arguments for each screen
- `t_nav_003` — Create `NavHost` with all screen routes in App.kt, replacing `currentScreen`
- `t_nav_004` — Wire list → detail → form → back navigation correctly

### Phase 4 — Screen Migration

**Goal**: Migrate each screen from manual state to NavHost composable.

**Tasks**:
- `t_nav_005` — Migrate `Screen.List` to `NavHost` composable route
- `t_nav_006` — Migrate `Screen.Detail(entry)` to route with entry UUID arg
- `t_nav_007` — Migrate `Screen.Form(entry?)` to route with optional entry UUID arg
- `t_nav_008` — Migrate `Screen.Settings` to route

### Phase 5 — Polish & Testing

**Tasks**:
- `t_clean_001` — Remove old `Screen` sealed class and manual state navigation helpers
- `t_clean_002` — Add backup/restore unit tests (commonTest)
- `t_clean_003` — Full manual testing: backup, restore, navigation edge cases
- `t_clean_004` — Update Kanban, PR, merge to develop

## Dependencies

- `org.jetbrains.kotlinx:kotlinx-serialization-json` (probably already present)
- `androidx.navigation:navigation-compose` (Compose Navigation)
- `org.jetbrains.kotlinx:kotlinx-serialization` (type-safe nav args)
- Java ZIP API (`java.util.zip.ZipOutputStream`/`ZipInputStream`) — available in KMP via `okio` or platform-specific

## Backward Compatibility

- ZIP format is self-contained — no migration needed
- Navigation migration replaces `currentScreen` but keeps all ViewModel/screen composables same
- Settings screen gets new UI elements, existing theme toggle unchanged
