# Quickstart Guide: coffee.app MVP

This guide outlines the steps to set up, run, and test the coffee.app MVP locally.

## Prerequisites

- **Java**: JDK 17 or later
- **Android Studio**: Ladybug (2024.3) or later with KMP plugin
- **Kotlin**: Kotlin 2.1.x (bundled with Gradle wrapper)
- **Xcode**: 15.4 or later (for iOS simulator target)
- **Git**: For cloning the repository
- **Optional**: Physical Android device or iOS device for testing

## Setup

1.  **Clone the repository** (if not already cloned):
    ```bash
    git clone <repository_url>
    cd coffee.app
    ```

2.  **Open the project in Android Studio**:
    Open the root directory — it contains the Gradle wrapper, `settings.gradle.kts`, and the KMP project.

3.  **Sync Gradle**:
    Android Studio will prompt you to sync — accept. This downloads Compose Multiplatform, Room KMP, and Kotlin dependencies.

## Running the App

### Android
1.  Select the `composeApp` run configuration
2.  Choose an Android emulator (API 26+) or connected device
3.  Click **Run**

### iOS (macOS only)
1.  Ensure Xcode is installed and you have an iOS simulator available
2.  In a terminal:
    ```bash
    ./gradlew :composeApp:iosSimulatorArm64Run
    ```
    Or select the iOS simulator target from the Android Studio run configuration menu.

The app should launch, displaying the main Brew Entry list.

## Testing

Automated tests are crucial for the Test-First Development principle. Tests run on the JVM for fast feedback.

1.  **Run all shared tests**:
    ```bash
    ./gradlew :composeApp:allTests
    ```

2.  **Run JVM unit tests only** (fastest):
    ```bash
    ./gradlew :composeApp:jvmTest
    ```

3.  **Run Android instrumentation tests**:
    ```bash
    ./gradlew :composeApp:connectedAndroidTest
    ```

This executes all unit and integration tests defined in `composeApp/src/commonTest/`.

## Key Flows to Verify

These scenarios should be tested manually after running the app and also covered by automated tests:

### Brew Entry Management

- **Create Entry**: Verify all fields (Bean Name, Origin, Roast Type, Grinder Setting, Portion Weight, Description) can be filled, validated, and saved.
- **Edit Entry**: Verify an existing entry can be modified and saved.
- **Delete Entry**: Verify deletion prompts for confirmation and completes successfully.
- **View List**: Verify entries appear in the list with concise summaries, sorted by Created Date descending by default.
- **View Detail**: Verify tapping an entry opens a detail view with all information.

### Origin Management

- **Select Predefined Origin**: Verify the full list of predefined origins is selectable.
- **Create Custom Origin**: Verify a custom origin can be added and used in a Brew Entry.
- **Custom Origin Persistence**: Confirm custom origins are available after app restart.
- **Duplicate Origin Prevention**: Verify creating an origin with mismatched casing (e.g., "brazil" vs "Brazil") is disallowed.

### Sorting

- **Apply Sort Options**: Verify sorting the list by Bean Name (A-Z), Origin (A-Z), Created Date, and Last Modified Date behaves as expected.

### Safety & Themes

- **Unsaved Changes**: Test leaving Add/Edit forms with unsaved data triggers a warning.
- **Theme Support**: Verify app appearance and readability in system default, light, and dark modes.

This quickstart guide ensures the core functionality and MVP requirements are met.