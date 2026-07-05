# Quickstart Guide: coffee.app MVP

This guide outlines the steps to set up, run, and test the coffee.app MVP locally.

## Prerequisites

- **macOS**: Xcode installed
- **Swift**: Swift 5.9 or later (typically comes with Xcode)
- **Git**: For cloning the repository
- **Optional**: Device configured for local development (physical device or simulator)

## Setup

1.  **Clone the repository** (if not already cloned):
    ```bash
    git clone <repository_url>
    cd coffee.app
    ```

2.  **Open the project**:
    Navigate to the `ios/` directory and open the `coffee_tracker_mvp.xcodeproj` file in Xcode.

3.  **Review dependencies**:
    The project uses SwiftUI for the UI and CoreData for local persistence. XCTest is included for testing. No external dependencies requiring specific installation beyond Xcode are expected for this MVP.

## Running the App

1.  **Open `ios/coffee_tracker_mvp.xcodeproj` in Xcode.**
2.  **Select a target device**: Choose a physical iOS device or an iOS Simulator.
3.  **Build and Run**: Click the "Run" button (play icon) in Xcode.

The app should launch, displaying the main Brew Entry list.

## Testing

Automated tests are crucial for the Test-First Development principle.

1.  **Open `ios/coffee_tracker_mvp.xcodeproj` in Xcode.**
2.  **Select the 'coffee_tracker_mvp_tests' scheme.**
3.  **Run Tests**: Click the "Test" button (play icon next to the test scheme) in Xcode.

This will execute all unit and integration tests defined in `Tests/coffee_tracker_mvp_tests/`.

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
