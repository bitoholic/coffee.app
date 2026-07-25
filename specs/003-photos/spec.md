# Feature Specification: Photos & Compact List Layout

**Feature Branch**: `feature/021-photos-spec`

**Created**: 2026-07-24

**Status**: Draft

**Input**: Add photos/images to brew entries. Compact the entry list layout.

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Add Photos to Brew Entries (Priority: P1)

As a user, I want to attach photos (of beans, equipment, brew results) to my brew entries so I can visually reference what I used.

**Why this priority**: This is the primary ask — photos add significant visual reference to brewing notes.

**Independent Test**: Add a new entry, attach a photo via gallery, save, reopen, confirm photo persists.

**Acceptance Scenarios**:

1. **Given** I am on the add/edit entry form, **When** I tap "Add Photo", **Then** I can choose between gallery picker or camera.
2. **Given** I select an image from gallery, **When** it's attached, **Then** a thumbnail preview appears on the form.
3. **Given** I take a photo with the camera, **When** it's attached, **Then** a thumbnail preview appears on the form.
4. **Given** an entry has a photo, **When** I view the detail screen, **Then** a thumbnail is displayed that I can tap to expand.
5. **Given** I tap the expanded photo, **When** viewing it fullscreen, **Then** it's not zoomable — tap again or back to dismiss.
6. **Given** I close and reopen the app, **When** viewing an entry with a photo, **Then** the photo is still there.
7. **Given** I edit an entry with a photo, **When** I tap "Remove Photo", **Then** the photo is removed.
8. **Given** an entry has no photo, **When** viewed on the list, **Then** a default placeholder icon is shown instead.

---

### User Story 2 - Compact Entry List Layout (Priority: P2)

As a user, I want the entry list to be more compact so I can see more entries at once.

**Why this priority**: Improves usability without adding new data capabilities. Quick win.

**Independent Test**: Can be tested by opening the list screen — each entry should show two lines of information in a compact layout.

**Acceptance Scenarios**:

1. **Given** the entry list is displayed, **When** I look at an entry row, **Then** the first line shows bean name on the left and origin/roast type on the right.
2. **Given** the entry list is displayed, **When** I look at an entry row, **Then** the second line shows date (short format, no "Created" text) on the left and grinder setting/weight on the right.
3. **Given** the entries have long names, **When** displayed in the compact layout, **Then** text truncates with ellipsis instead of wrapping.
4. **Given** an entry has a photo, **When** displayed in the list, **Then** a small thumbnail shows on the left.

---

### Edge Cases

- What happens when the camera/gallery permission is denied? The app shows a message explaining why the permission is needed.
- What happens when selecting a very large image? The app compresses/resizes the image to a reasonable size for storage.
- What happens to photos when an entry is deleted? The photo files are also deleted.
- What happens when storage is full? Graceful error handling.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: The app MUST allow attaching one photo per brew entry via gallery OR camera.
- **FR-002**: Photos MUST be stored as local compressed files (max 1920px, <500KB) referenced by file path.
- **FR-003**: The BrewEntry table MUST have a nullable `photoPath` column.
- **FR-004**: The photo MUST be displayed as a thumbnail on the detail screen, tappable to expand to fullscreen (not zoomable).
- **FR-005**: A small photo thumbnail MUST appear on the list row when a photo is attached. A placeholder icon for entries without photos.
- **FR-006**: The entry list row MUST show two lines: bold name + origin/roast on line 1, normal date + grinder/weight on line 2.
- **FR-007**: The date on the list MUST be in short format (e.g., "24 Jul") without the "Created" prefix.
- **FR-008**: Photo files MUST be stored in the app's private internal storage directory (`filesDir/photos/`).
- **FR-009**: When an entry is deleted, its associated photo file MUST also be deleted.
- **FR-010**: The app MUST handle permission denial for camera/storage gracefully.

### Constitution Alignment *(mandatory)*

- **Brewing-memory value**: Photos add rich visual context to brewing notes — seeing the bean bag, the grind, or the pour improves recall.
- **Local-first scope**: Photos stored as local files in app private storage. No cloud/backend involved.
- **User-safe data changes**: Photo deletion is part of entry deletion flow (already has confirmation dialog). Photo removal is explicit via a button.
- **Theme behavior**: No change — photos display in both themes.
- **Mobile assumptions**: Image picking uses platform-specific APIs (ActivityResultContract on Android). Photo storage uses app's private files directory. Large images are compressed before saving.

### Key Entities *(include if feature involves data)*

- **BrewEntry**: Add `photoPath: String?` column.
- **BrewEntryDao**: Update insert/update queries, add photo-related operations.
- **PhotoManager**: Utility class for saving, loading, resizing, and deleting photo files.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: User can attach a photo, see it on detail, and it persists across restarts.
- **SC-002**: Entry list shows 2-line compact layout with no wrapping.
- **SC-003**: Deleting an entry also removes its photo file from storage.
- **SC-004**: Large images (>5MB) are automatically compressed to <500KB.

## Assumptions

- Android image picker uses `ActivityResultContracts.PickVisualMedia` or `GetContent`.
- Photo storage directory: `context.filesDir/photos/` with UUID-based filenames.
- Image compression uses Android's `BitmapFactory` for downscaling.
- Maximum image dimension: 1920px on the longest side.
- The compact list layout uses existing `BrewEntrySummary` or directly maps from `BrewEntry`.
