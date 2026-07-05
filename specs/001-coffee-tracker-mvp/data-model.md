# Data Model: coffee.app MVP

## Core Entities

### BrewEntry
Represents a single recorded brewing attempt for a coffee bean. Multiple entries can exist for the same bean to track different experiments.

**Attributes**:
- `uuid`: UUID (Primary Key, automatically generated)
- `beanName`: String (Required, non-empty after trimming whitespace. MVP allows duplicate entries with a warning.)
- `beanOrigin`: String (Optional, references an Origin entity. If custom, validated for cleanliness and uniqueness against existing origins, case-insensitively.)
- `roastType`: String (Required. Must be one of "Light", "Medium", or "Dark". Enforced via picker/enum.)
- `grinderSetting`: Int16 (Required. Must be between 1 and 48 inclusive.)
- `portionWeight`: Decimal (Required. Positive decimal number representing grams. Stored in storage, displayed with 'g'.)
- `description`: String? (Optional. Maximum length 500 characters.)
- `createdDate`: Date (Auto-set when Brew Entry is first saved.)
- `lastModifiedDate`: Date (Auto-set when Brew Entry is first saved; updated on edit completion.)

**Relationships**:
- Many-to-One with Origin (a BrewEntry belongs to one Origin).

### Origin
Represents a coffee bean's origin, either predefined or custom.

**Attributes**:
- `uuid`: UUID (Primary Key)
- `name`: String (Required. Case-insensitive unique identifier. Cleaned of leading/trailing whitespace.)
- `isCustom`: Bool (True if created by user, False if predefined.)

**Relationships**:
- One-to-Many with BrewEntry (an Origin can be used in many BrewEntries).

## Predefined Origins

The following origins MUST be available for selection:
- Brazil
- Colombia
- Ethiopia
- Kenya
- Guatemala
- Costa Rica
- Honduras
- Peru
- El Salvador
- Panama
- Indonesia
- India
- Vietnam
- Yemen

## Data Validation Rules Summary

- **Bean Name**: Required; non-empty after trimming whitespace.
- **Grinder Setting**: Integer between 1 and 48 (inclusive).
- **Roast Type**: Must be one of "Light", "Medium", or "Dark".
- **Origin Name**: Unique case-insensitively. Must sanitize for leading/trailing whitespace.
- **Description**: Optional; max 500 characters.
- **Portion Weight**: Positive decimal number representing grams; displayed with 'g'.
- **Duplicate Brew Entries**: App must warn user before saving an exact duplicate Brew Entry (same bean name, origin, roast, grinder, portion weight), but allow saving if confirmed.

## Data Safety Features

- **Deletion**: Brew Entry deletion MUST require confirmation before completion.
- **Unsaved Edits**: Unsaved changes in add/edit forms MUST NOT be discarded without warning or confirmation whenever any field differs.
- **Custom Origins**: Create-only in MVP. Predefined origins are static. Existing entries using origins are safe as origins cannot be deleted or renamed in MVP.

## Persistence

- All data (Brew Entries, custom Origins) stored locally using CoreData.
- Data MUST persist after app restarts.

## Sorting

- **Default**: Created Date descending (newest first).
- **User Options**: Bean Name (A-Z), Origin (A-Z), Created Date, Last Modified Date.

## Theme Support

- App follows system theme by default.
- Supports explicit light mode and dark mode.
- UI elements remain readable and usable in all theme modes.

## Mobile Assumptions

- **Target Platform**: iOS 15+.
- **Device**: Assumes standard mobile device form factor and multi-touch interaction.
- **Date Formatting**: Respects device locale settings.
- **Navigation**: SwiftUI standard patterns (TabView, NavigationView, NavigationStack, modal presentations for forms/pickers).
- **Local Storage**: CoreData for structured data persistence.
- **Test Commands**: XCTest commands executed via `xcodebuild` or similar; actual commands TBD in tasks.md.
