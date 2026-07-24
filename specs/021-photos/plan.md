# Implementation Plan: Photos & Compact List Layout

**Branch**: `feature/021-photos` | **Date**: 2026-07-24 | **Spec**: `specs/021-photos/spec.md`

## Summary

Add photo attachment to brew entries (gallery + camera), compress locally, display as thumbnail with expand-to-fullscreen. Compact the entry list to two-line layout with photo thumbnails.

## Technical Context

**Language/Version**: Kotlin 2.1.10, Compose Multiplatform

**Primary Dependencies**: Room KMP (data), Compose (UI), Android Activity Result API (image picking), BitmapFactory (compression)

**Storage**: Room + local file system (`context.filesDir/photos/`)

**Testing**: `./gradlew check`

**Target Platform**: Android API 26+ (camera/gallery APIs)

**Project Type**: Mobile app (Compose Multiplatform)

**Constraints**:
- One photo per entry max
- Gallery AND camera picker options
- Photos compressed to max 1920px, <500KB
- No zoom on expanded photo (simple fullscreen overlay)
- Default placeholder icon for entries without photos
- Compact list: line 1 bold (name + origin/roast), line 2 normal (date + grinder/weight)

## Constitution Check

*GATE: Must pass.*

- **Focused Brewing Memory**: Photos add visual context to brewing notes — seeing the bean bag, grind, or pour
- **Local-First Simplicity**: Photos stored as local files, no cloud/backend
- **Test-First Development**: Tests for photo path persistence, list layout formatting
- **User-Safe Data Changes**: Photo deletion tied to entry deletion (already has confirmation dialog)
- **Beginner-Friendly Mobile Architecture**: Uses standard Android APIs for image picking and compression

## Implementation Order

1. **BrewEntry entity update** — Add `photoPath: String?` column, bump DB version
2. **PhotoManager** — Utility class for saving, compressing, loading, deleting photo files
3. **Form screen — Photo picker** — "Add Photo" button with gallery/camera chooser, thumbnail preview, remove option
4. **Detail screen — Photo display** — Thumbnail with tap-to-expand fullscreen overlay
5. **List screen — Compact layout** — Two-line row with photo thumbnail or placeholder icon
6. **App icon placeholder** — Default placeholder drawable for entries without photos
7. **Tests & Polish** — Unit tests, verify build, signed APK

**Next step**: Generate tasks from this plan.
