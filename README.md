# BitoCoffee ☕

A local-first mobile coffee brewing journal built with Compose Multiplatform and Room KMP. Record, view, edit, and sort your brew experiments — all branded with the bitoholic identity.

## Features

![BitoCoffee](https://img.shields.io/badge/version-0.0.3-red?style=flat&color=C8102E)
![CI](https://github.com/bitoholic/coffee.app/actions/workflows/ci.yml/badge.svg?branch=develop)

- **Add brew entries** — Record bean name, origin, roast type, grinder setting, portion weight, and description
- **14 predefined origins** — Brazil, Colombia, Ethiopia, Kenya, and more seeded out of the box
- **Custom origins** — Create and reuse your own origins with case-insensitive duplicate detection
- **Sortable list** — 8 sort options with direction arrows (Date Added ↑↓, Bean Name ↑↓, Bean Origin ↑↓, Date Modified ↑↓)
- **Compact list layout** — Two-line rows with photo thumbnails, short dates, and right-aligned details
- **Entry detail** — Clean card layout with full details, short dates, and action buttons
- **Photo attachments** — Attach one photo per entry via gallery or camera, compressed automatically
- **Photo display** — Thumbnail on list, full-width banner on detail, tap for fullscreen with pinch-to-zoom
- **Camera capture** — Snap photos directly from the app with the camera option
- **Edit & Delete** — Edit entries from the detail screen, delete with confirmation dialog
- **Unsaved changes warning** — Warns before discarding edits on the add/edit form
- **Dark/Light/System theme** — Toggle via settings screen, persists across restarts
- **Bitoholic branding** — Custom `#C8102E` colour scheme, logo TopBar with 0101/1010, coffee bean app icon
- **App name: BitoCoffee** — Released under the bitoholic brand identity
- **Persistence** — All data stored locally via Room (SQLite), survives app restarts
- **Signed release APK** — Build with `assembleRelease` for clean installation

## Tech Stack

- **Kotlin Multiplatform** — Shared business logic across Android and iOS
- **Compose Multiplatform** — Shared UI layer using Jetpack Compose
- **Room KMP** — Local SQLite persistence with reactive flows
- **Material 3** — Material Design theming with dark/light mode support
- **PhotoManager** — Local photo storage with compression (1920px max, <500KB)
- **FileProvider** — Android FileProvider for camera capture
- **Spec-driven development** — Formal specs and TDD checklists per feature

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
│   ├── core/           # Validation, date formatting, PhotoManager, BitoholicTopBar, Theme
│   ├── data/
│   │   ├── database/   # Room entities, DAOs, database definition
│   │   └── repository/ # Repository wrappers
│   ├── domain/         # Enums (RoastType, SortOption with display names)
│   ├── form/           # Add/edit brew entry form with photo picker
│   ├── list/           # Brew entry list, detail with zoom, compact row
│   ├── settings/       # Theme toggle settings screen
│   └── origin/         # Origin picker sheet and viewmodel
├── androidMain/
│   ├── AndroidManifest.xml  # FileProvider, app icon, BitoCoffee label
│   └── res/                 # App icon, adaptive icon, vector drawables
├── commonTest/         # Shared tests
└── androidUnitTest/    # Android-specific tests
```

## Roadmap

Planned future enhancements:

- **Search** — Filter brew entries by bean name, origin, or roast type
- **Photo gallery** — Multiple photos per entry instead of one
- Additional brewing parameters and recipe support
- Data export/backup

## License

See [LICENSE](LICENSE).
