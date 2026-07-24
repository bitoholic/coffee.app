# BitoCoffee ☕

A local-first mobile coffee brewing journal built with Compose Multiplatform and Room KMP. Record, view, edit, and sort your brew experiments — all branded with the bitoholic identity.

## Features

![BitoCoffee](https://img.shields.io/badge/version-0.0.2-red?style=flat&color=C8102E)

- **Add brew entries** — Record bean name, origin, roast type, grinder setting, portion weight, and description
- **14 predefined origins** — Brazil, Colombia, Ethiopia, Kenya, and more seeded out of the box
- **Custom origins** — Create and reuse your own origins with case-insensitive duplicate detection
- **Sortable list** — Sort by created date, bean name, origin, or last modified date (5 sort options)
- **Entry detail** — Full detail view with all saved fields
- **Edit & Delete** — Edit entries, delete with confirmation dialog
- **Unsaved changes warning** — Warns before discarding edits on the add/edit form
- **Dark/Light/System theme** — Toggle via settings screen, persists across restarts
- **Bitoholic branding** — Custom `#C8102E` colour scheme, logo TopBar, coffee bean app icon
- **Persistence** — All data stored locally via Room (SQLite), survives app restarts
- **Signed release APK** — Build with `assembleRelease` for clean installation

## Tech Stack

- **Kotlin Multiplatform** — Shared business logic across Android and iOS
- **Compose Multiplatform** — Shared UI layer using Jetpack Compose
- **Room KMP** — Local SQLite persistence with reactive flows
- **Material 3** — Material Design theming with dark/light mode support
- **Spec-driven development** — Formal specs and TDD checklists per feature

## Building

```bash
# Clone
git clone https://github.com/bitoholic/coffee.app.git
cd coffee.app

# Set up Android SDK
echo "sdk.dir=$HOME/Android/Sdk" > local.properties
# Or point to your SDK path

# Build debug APK
./gradlew :composeApp:assembleDebug

# Install on device
adb install -r composeApp/build/outputs/apk/debug/composeApp-debug.apk
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
│   ├── core/           # Validation, date formatting, workflow state
│   ├── data/
│   │   ├── database/   # Room entities, DAOs, database definition
│   │   └── repository/ # Repository wrappers
│   ├── domain/         # Enums (RoastType, SortOption)
│   ├── form/           # Add/edit brew entry form
│   ├── list/           # Brew entry list, detail, row composables
│   └── origin/         # Origin picker sheet and viewmodel
├── androidMain/        # Android-specific code (MainActivity)
├── iosMain/            # iOS-specific code (MainViewController)
├── commonTest/         # Shared tests
└── androidUnitTest/    # Android-specific tests
```

## Roadmap

Planned future enhancements:

- **Photos** — Attach photos of brew equipment, beans, and results to entries
- Additional brewing parameters and recipe support
- Data export/backup

## License

See [LICENSE](LICENSE).
