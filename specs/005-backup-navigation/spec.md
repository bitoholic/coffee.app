# Feature Specification: Backup/Restore & Proper Navigation

**Feature Branch**: `feature/005-backup-navigation`

**Created**: 2026-07-26

**Status**: Approved

**Input**: Add backup/restore functionality accessible from settings, and replace manual screen-state navigation with proper Compose Navigation.

## User Stories & Testing *(mandatory)*

### User Story 1 — Backup to File (Priority: P1)

As a user, I want to export all my brew entries from the settings screen so I can save a copy of my data.

**Why this priority**: Data portability is a fundamental feature — users shouldn't lose their brew history.

**Independent Test**: Go to Settings → Backup → select "Without Images" → save file → open the exported file — contains all brew entries in readable format.

**Acceptance Scenarios**:

1. **Given** I am on the settings screen, **When** I tap "Backup", **Then** I see options to backup with or without images.
2. **Given** I choose backup without images, **When** the backup completes, **Then** a single ZIP file is saved containing `entries.json`.
3. **Given** I choose backup with images, **When** the backup completes, **Then** a single ZIP file is saved containing both `entries.json` and `photos/` folder.
4. **Given** the backup finishes, **When** I check, **Then** the share-sheet opens so I can save/share the file wherever I want.
5. **Given** I have a backup file, **When** I open it on a computer, **Then** I can read `entries.json` inside the archive.

### User Story 2 — Restore from File (Priority: P1)

As a user, I want to import a previously created backup so I can recover my data on a new device or after reinstalling.

**Why this priority**: Backup without restore is half a feature.

**Acceptance Scenarios**:

1. **Given** I have a backup file, **When** I tap "Restore" in settings and pick the file via SAF, **Then** I'm asked to choose: overwrite or merge.
2. **Given** I choose "Overwrite", **When** the restore completes, **Then** existing entries are replaced with backup data.
3. **Given** I choose "Merge", **When** the restore completes, **Then** backup entries are added alongside existing ones.
4. **Given** I restore a backup that includes images, **When** the restore completes, **Then** photos are visible on entries.
5. **Given** I try to restore an invalid file, **When** I select it, **Then** an error message is shown and no data is changed.

### User Story 3 — Proper Navigation (Priority: P2)

As a developer, I want to replace the manual screen-state management with Compose Navigation so the app is maintainable and supports back-stack correctly.

**Why this priority**: Internal quality improvement. Makes the app more robust for future features.

**Acceptance Scenarios**:

1. **Given** I navigate from list → detail → edit → save, **When** I press back, **Then** I return to detail, not the main list.
2. **Given** I navigate from list → form → save, **When** I press back, **Then** I return to the list.
3. **Given** I navigate from list → settings → backup, **When** I press back repeatedly, **Then** I return through the correct back-stack.
4. **Given** deep links are configured, **When** I open a deep link, **Then** the correct screen is shown.

## Wireframes / Mock-ups

### Settings Screen — Backup Section

```
┌─────────────────────────────┐
│      Settings               │
├─────────────────────────────┤
│ Theme                        │
│ [Dark / Light / System ▼]   │
│                              │
│ Backup & Restore             │
│ [Backup] [Restore]          │
│                              │
│ Backup without images: ☐     │
│  (when checked, photos not  │
│   included in backup)        │
└─────────────────────────────┘
```

### Backup File Format

Single `.zip` archive containing:

```
backup_2026-07-26.zip
├── entries.json        # All brew entries as JSON array
├── photos/             # Only present if images included
│   ├── photo_001.jpg
│   ├── photo_002.jpg
│   └── ...
└── manifest.json       # Version, date, count, options metadata
```

## Resolved Design Decisions

| Question | Decision |
|----------|----------|
| Backup file format | ZIP with JSON + photos folder |
| How to share backup | Share-sheet after creation (Android Intent.ACTION_SEND) |
| How to pick restore file | SAF / ActivityResultContracts.OpenDocument |
| Navigation library | Compose Navigation with type-safe args (kotlinx-serialization) |
| Restore with existing data | Dialog: ask user to overwrite or merge |
| Photo handling in backup | Optional checkbox "Include Photos" in settings |

## Success Criteria *(mandatory: revisit before calling spec done)*

- [ ] Spec has no open questions or they are resolved in this document
- [ ] Acceptance scenarios are testable end-to-end
- [ ] All team members agree on the format and boundaries
- [ ] Risk of data loss during restore is addressed
- [ ] Navigation plan accounts for all existing screens and transitions

## Risks & Mitigation

| Risk | Likelihood | Impact | Mitigation |
|------|-----------|--------|------------|
| Data loss during restore | Low | Critical | Preview dialog with overwrite/merge choice, confirm before writing |
| Large ZIP with photos | Medium | Mild | Show progress, warn about size, optional photos |
| Navigation migration breaks existing screens | Medium | High | One screen at a time, test each transition |
| Backup/restore not available on iOS | Medium | Medium | Use platform-independent format (ZIP+JSON) |
