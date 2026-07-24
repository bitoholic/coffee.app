# Feature Specification: Branding, App Icon & Settings Screen

**Feature Branch**: `feature/012-branding-settings`

**Created**: 2026-07-24

**Status**: Draft

**Input**: Brand the coffee.app MVP with the bitoholic identity — new app icon, brand colour palette, top bar with logo + gear icon, settings screen with theme toggle.

## User Scenarios & Testing *(mandatory)*

### User Story 1 - See the Bitoholic Brand (Priority: P1)

As a user, I want to see the bitoholic brand identity throughout the app so I recognise which app I'm using and feel the brand personality.

**Why this priority**: Branding is the most visible change. Without it, the app still shows the generic template look. This is the foundation all other UI changes build on.

**Independent Test**: Can be tested by launching the app and confirming the red (#C8102E) brand colour is used on the FAB button, the logo mark appears in the top bar, and the app icon (when installed) shows the steaming cup + binary design.

**Acceptance Scenarios**:

1. **Given** the app is installed, **When** I look at the home screen, **Then** the top bar shows the bitoholic logo mark (red squircle with binary) on the left.
2. **Given** the app is running, **When** I look at the FAB button, **Then** it uses the brand red (#C8102E) colour.
3. **Given** the app is installed on the home screen, **When** I look at the app icon, **Then** it shows the new brand icon (red squircle with steaming coffee cup and binary).
4. **Given** the app uses the brand colour scheme, **When** I switch between light and dark mode, **Then** the brand red primary colour is preserved in both themes.

---

### User Story 2 - Access Settings via Gear Icon (Priority: P2)

As a user, I want to access a settings screen from the top bar so I can configure app preferences.

**Why this priority**: The settings screen is the gateway to the theme toggle and future preferences. Without it, users can't change the theme.

**Independent Test**: Can be tested by tapping the gear icon and confirming a settings screen appears with a theme toggle.

**Acceptance Scenarios**:

1. **Given** I am on the brew entry list screen, **When** I tap the gear icon in the top bar, **Then** a settings screen opens.
2. **Given** the settings screen is open, **When** I tap the back arrow, **Then** I return to the brew entry list.

---

### User Story 3 - Switch Theme Between Light, Dark & System (Priority: P3)

As a user, I want to choose between light theme, dark theme, or following my system setting so the app feels comfortable in any environment.

**Why this priority**: Theme control is the first real preference users can set. It's also a foundation for storing other user preferences in the future.

**Independent Test**: Can be tested by selecting each theme option and verifying the app switches immediately, then restarting the app and confirming the preference is remembered.

**Acceptance Scenarios**:

1. **Given** the settings screen is open, **When** I select "Light", **Then** the app immediately switches to light mode regardless of system setting.
2. **Given** the settings screen is open, **When** I select "Dark", **Then** the app immediately switches to dark mode regardless of system setting.
3. **Given** the settings screen is open, **When** I select "System", **Then** the app follows the device's system theme.
4. **Given** I have selected a theme preference, **When** I restart the app, **Then** my preference is remembered.

---

### Edge Cases

- What happens when the user opens settings for the first time? System default is selected.
- What happens when the device is in dark mode and user picks light mode? App shows light, ignoring system.
- What happens after switching themes rapidly? The app transitions smoothly without flicker.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: The app MUST display the bitoholic logo mark (red squircle with binary) in the top bar of the brew entry list screen.
- **FR-002**: The app MUST use `#C8102E` as the primary brand colour in the Material 3 colour scheme for both light and dark modes.
- **FR-003**: The top bar MUST have a gear icon on the right side that opens the settings screen.
- **FR-004**: A settings screen MUST exist with a theme toggle offering three options: System, Light, Dark.
- **FR-005**: The theme selection MUST apply immediately when changed.
- **FR-006**: The theme preference MUST persist across app restarts using Room (stored in a `preferences` table).
- **FR-007**: The default theme (first launch) MUST be "System" (follow device setting).
- **FR-008**: The settings screen MUST be accessible from all main screens (list, detail, form).
- **FR-009**: The settings screen MUST have a back navigation to return to the previous screen.
- **FR-010**: Future settings MUST be addable to the settings screen without structural changes.

### Constitution Alignment *(mandatory)*

- **Brewing-memory value**: Theme settings improve usability in different environments. Branding reinforces the app's identity and trust.
- **Local-first scope**: Theme preference is stored locally via Room. No backend, accounts, or cloud involved.
- **User-safe data changes**: Theme change is instant and non-destructive. No data is at risk.
- **Theme behavior**: The app follows system theme by default. Light and dark mode overrides are available via settings.
- **Mobile assumptions**: Settings screen follows the same navigation pattern as existing screens (state-based). Preference storage uses Room, which is already in the project.

### Key Entities *(include if feature involves data)*

- **AppPreferences**: A Room entity with key-value pairs for user preferences. Keys: `theme_mode` (values: `system`, `light`, `dark`). Single-row table.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: A user can change the theme and see the result immediately in under 2 seconds.
- **SC-002**: After setting a theme preference and restarting the app, the chosen theme is active.
- **SC-003**: The app icon renders correctly on the home screen (Android launcher).
- **SC-004**: The brand red colour is consistently applied across all screens.

## Assumptions

- The app icon will be generated as an Android adaptive icon (foreground + background layers) using the chosen #4 design (steaming coffee cup + binary).
- The gear icon can use Material Icons's built-in `Icons.Default.Settings`.
- The settings screen starts simple with just the theme toggle but is designed to accommodate future additions.
- Preference storage uses a new Room entity `AppPreferences` with a single row.
