---

description: "Task list for photos and compact list layout feature"
---

# Tasks: Photos & Compact List Layout

**Input**: Spec and plan from `specs/021-photos/`

## Phase 1: Data Layer

- [ ] T001 Add `photoPath: String?` column to BrewEntry entity, bump DB version to 3
- [ ] T002 Update BrewEntryDao — insert/update query includes photoPath
- [ ] T003 Create PhotoManager in `composeApp/src/commonMain/kotlin/coffee/app/core/PhotoManager.kt` — save, load, compress, delete photo files

## Phase 2: Compact List Layout

- [ ] T004 Redesign list row composable — two-line layout: bold name + origin/roast on line 1, normal short date + grinder/weight on line 2
- [ ] T005 Add photo thumbnail to list row (placeholder icon if no photo)
- [ ] T006 Update detail screen — show photo thumbnail, tap to expand fullscreen overlay (not zoomable)

## Phase 3: Photo Picker (Form Screen)

- [ ] T007 Add "Add Photo" button to form — opens chooser (gallery or camera)
- [ ] T008 Implement image compression on selection (max 1920px, <500KB)
- [ ] T009 Show thumbnail preview on form after selection
- [ ] T010 Add "Remove Photo" button
- [ ] T011 Wire photoPath persistence — save path on form submit, load on edit

## Phase 4: Polish

- [ ] T012 Create default placeholder icon drawable for entries without photos
- [ ] T013 Handle camera/storage permissions gracefully
- [ ] T014 Run `./gradlew check` — verify all tests pass
- [ ] T015 Build signed release APK
- [ ] T016 Update kanban board
