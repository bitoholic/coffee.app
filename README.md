# BitoCoffee ☕

A local-first mobile coffee brewing journal built with Compose Multiplatform and Room KMP. Record, view, edit, sort, search, browse, and back up your brew experiments with multiple photos per entry — all branded with the bitoholic identity.

## Features

![BitoCoffee](https://img.shields.io/badge/version-0.0.5-red?style=flat&color=C8102E)
![CI](https://github.com/bitoholic/coffee.app/actions/workflows/ci.yml/badge.svg?branch=develop)

- **Add brew entries** — Record bean name, origin, roast type, grinder setting, portion weight, and description
- **14 predefined origins** — Brazil, Colombia, Ethiopia, Kenya, and more seeded out of the box
- **Custom origins** — Create and reuse your own origins with case-insensitive duplicate detection
- **Sortable list** — 8 sort options with direction arrows (Date Added ↑↓, Bean Name ↑↓, Bean Origin ↑↓, Date Modified ↑↓)
- **Real-time search** — As-you-type filtering by bean name, works alongside sorting, brand red border in dark mode
- **Compact list layout** — Two-line rows with photo thumbnails, short dates, and right-aligned details
- **Multi-photo gallery** — Up to 10 photos per entry with gallery multi-select and sequential camera capture
- **Photo thumbnails** — Horizontal LazyRow on the form with individual X remove buttons
- **Detail gallery** — Swipeable fullscreen viewer with pinch-to-zoom, pan clamping, and position indicator
- **Camera capture** — Snap photos directly from the app, added one at a time to the gallery
- **Compact search bar** — Rounded input field, inline between sort and close
- **Smooth gallery navigation** — HorizontalPager-based swipe between photos with per-page zoom state
- **Entry detail** — Clean card layout with full details, short dates, and action buttons
- **Edit & Delete** — Edit entries from the detail screen, delete with confirmation dialog
- **Unsaved changes warning** — Warns before discarding edits on the add/edit form
- **Keyboard-aware form** — Scrollable form with `imePadding` so the keyboard never obscures inputs
- **Dark/Light/System theme** — Toggle via settings screen, persists across restarts
- **Backup & Restore** — Export all entries to ZIP (`BitoCoffee-<epoch>.zip`) with optional photos, share or save via system file picker with overwrite confirmation
- **Restore with options** — Overwrite or merge existing data, photo extraction from backup archive
- **Compose Navigation** — Proper back-stack with type-safe routes via `NavHost`, replaces manual screen-state management
- **Schema metadata** — Backups include database schema version for forward compatibility
- **Android Auto Backup disabled** — Fresh installs start clean, no stale cloud data
- **Bitoholic branding** — Custom `#C8102E` colour scheme, logo TopBar with 0101/1010, coffee bean app icon
- **App name: BitoCoffee** — Released under the bitoholic brand identity
- **Persistence** — All data stored locally via Room (SQLite), survives app restarts
- **Signed release APK** — Build with `assembleRelease` for clean installation

## Tech Stack

- **Kotlin Multiplatform** — Shared business logic across Android and iOS
- **Compose Multiplatform** — Shared UI layer using Jetpack Compose
- **Room KMP** — Local SQLite persistence with reactive flows, schema migrations
- **Material 3** — Material Design theming with dark/light mode support
- **PhotoManager** — Local photo storage with compression (1920px max, <500KB)
- **FileProvider** — Android FileProvider for camera capture and backup sharing
- **HorizontalPager** — Compose Foundation pager for smooth gallery swipe navigation
- **Compose Navigation** — Type-safe navigation with `NavHost` and kotlinx-serialization args
- **ZIP + JSON** — Backup format using `java.util.zip` and manual JSON serialization (no external deps)
- **SAF** — Storage Access Framework for file picker and document creation
- **Spec-driven development** — Formal specs, plans, and task checklists per feature

## Building

```bash
# Clone
git clone https://github.com/bitoholic/coffee.app.git
cd coffee.app

# Set up Android SDK
echo "sdk.dir=$HOME/Android/Sdk" > local.properties

# Build signed release APK (recommended)
./gradlew :composeApp:assembleRelease

# Build debug APK
./gradlew :composeApp:assembleDebug

# Install on device
adb install -r composeApp/build/outputs/apk/release/composeApp-release.apk
```

Requires JDK 17+ and Android SDK with build-tools 34 and platform android-35.

## Running Tests

```bash
./gradlew check
```

## Project Structure

```
composeApp/src/
├── commonMain/kotlin/coffee/app/
│   ├── backup/         # Backup/restore engine: ZIP creation, JSON serialization, manifest
│   ├── core/           # Validation, date formatting, PhotoManager, BitoholicTopBar, Theme
│   ├── data/
│   │   ├── database/   # Room entities, DAOs, database definition, migrations
│   │   └── repository/ # Repository wrappers
│   ├── domain/         # Enums (RoastType, SortOption with display names)
│   ├── form/           # Add/edit brew entry form with multi-photo picker
│   ├── list/           # Brew entry list with search, detail with HorizontalPager gallery
│   ├── navigation/     # Routes, NavHost, screen graph with type-safe arguments
│   ├── settings/       # Theme toggle, backup/restore buttons with SAF pickers
│   └── origin/         # Origin picker sheet and viewmodel
├── androidMain/
│   ├── AndroidManifest.xml  # FileProvider, app icon, BitoCoffee label
│   └── res/                 # App icon, adaptive icon, file_paths, vector drawables
├── commonTest/         # Shared tests
└── androidUnitTest/    # Android-specific tests
```

## Roadmap

Planned future enhancements:

- **Multi-select & batch delete** — Select multiple entries in the list and delete them in one action
- Additional brewing parameters and recipe support
- Sorting by rating or custom fields

## License

See [LICENSE](LICENSE).