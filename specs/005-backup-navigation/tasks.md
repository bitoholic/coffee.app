# Task List: Backup/Restore & Proper Navigation

## Phase 1 — Backup Engine

| ID | Task | Status |
|----|------|--------|
| t_backup_001 | Create `BackupModel.kt` — data classes with kotlinx-serialization for entries.json and manifest.json | ready |
| t_backup_002 | Create `BackupEngine.kt` — serialize entries to JSON, build ZIP archive with ZipOutputStream | ready |
| t_backup_003 | Create `RestoreEngine.kt` — parse ZIP, deserialize entries, handle overwrite vs merge | ready |
| t_backup_004 | Handle photo paths: copy photo files into ZIP photos/ folder | ready |
| t_backup_005 | Handle photo extraction: extract photos from ZIP to app storage | ready |
| t_backup_006 | Error handling: invalid ZIP, missing manifest, corrupt JSON, empty backup | ready |

## Phase 2 — Backup UI

| ID | Task | Status |
|----|------|--------|
| t_backup_007 | Add "Backup" and "Restore" buttons to Settings screen | ready |
| t_backup_008 | Add "Include Photos" toggle checkbox | ready |
| t_backup_009 | Implement backup flow: create ZIP → share-sheet (Intent.ACTION_SEND) | ready |
| t_backup_010 | Implement restore flow: SAF picker → parse → overwrite/merge dialog → restore | ready |
| t_backup_011 | Show progress indicator during long backup/restore operations | ready |
| t_backup_012 | Show success/error snackbar after completion | ready |

## Phase 3 — Compose Navigation Setup

| ID | Task | Status |
|----|------|--------|
| t_nav_001 | Add Compose Navigation + kotlinx-serialization dependencies to build.gradle.kts | ready |
| t_nav_002 | Define route constants and type-safe arguments for each screen | ready |
| t_nav_003 | Create NavHost with all screen routes in App.kt | ready |
| t_nav_004 | Wire correct back-stack: list→detail→form, list→settings→backup | ready |

## Phase 4 — Screen Migration

| ID | Task | Status |
|----|------|--------|
| t_nav_005 | Migrate `Screen.List` to NavHost composable route | ready |
| t_nav_006 | Migrate `Screen.Detail(entry)` to route with entry UUID arg | ready |
| t_nav_007 | Migrate `Screen.Form(entry?)` to route with optional entry UUID arg | ready |
| t_nav_008 | Migrate `Screen.Settings` to route | ready |

## Phase 5 — Polish & Testing

| ID | Task | Status |
|----|------|--------|
| t_clean_001 | Remove old `Screen` sealed class and manual navigation state | ready |
| t_clean_002 | Unit tests for backup/restore engine (commonTest) | ready |
| t_clean_003 | Full manual testing: backup, restore, navigation back-stack | ready |
| t_clean_004 | Update Kanban board, PR to develop, merge | ready |

## Commit & Merge Strategy

- Each phase is a separate commit on the feature branch
- PR to develop when all phases complete and `./gradlew check` passes
- CI must be green before merge
