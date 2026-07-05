# Constitution Alignment Checklist: coffee.app MVP

**Purpose**: Validate the technical plan and data model against the coffee.app constitution principles
**Created**: 2026-07-05
**Feature**: `specs/001-coffee-tracker-mvp/spec.md`
**Plan**: `specs/001-coffee-tracker-mvp/plan.md`
**Data Model**: `specs/001-coffee-tracker-mvp/data-model.md`

## 1. Focused Brewing Memory

- [ ] CHK001 Is the brewing-memory value of every planned approach explicitly stated and linked to a specific user problem? [Completeness, Const §I]
- [ ] CHK002 Does the plan avoid scope creep by documenting why each dependency directly serves the brewing-memory goal? [Clarity, Plan §Dependency Selection]
- [ ] CHK003 Are all technical decisions (language, framework, storage) justified in terms of the user's core problem of forgetting brewing settings? [Traceability, Plan §Technical Context]
- [ ] CHK004 Does the data model capture only brewing-relevant fields without adding speculative future data points? [Scope, Data Model §BrewEntry]
- [ ] CHK005 Are "out of scope" items explicitly mapped to the constitution's principle of staying focused on brewing memory? [Consistency, Plan §Out-of-Scope, Const §I]

## 2. Local-First Simplicity

- [ ] CHK006 Is the local-only persistence approach explicitly documented and confirmed as CoreData? [Completeness, Plan §Local Persistence, Const §II]
- [ ] CHK007 Does the plan explicitly rule out backend services, accounts, cloud sync, analytics, and remote databases? [Clarity, Plan §Constraints + §Out-of-Scope, Const §II]
- [ ] CHK008 Are all dependencies justified with reasoning that they "clearly simplify the app" (the constitution's threshold)? [Gap, Plan §Dependency Justification, Const §II]
- [ ] CHK009 Does the plan confirm that no network calls, API endpoints, or remote storage are introduced? [Coverage, Plan §Technical Context + §Constraints]
- [ ] CHK010 Is the local-storage choice (CoreData) documented with consideration of alternative simpler options? [Completeness, Plan §Dependency Justification, Const §II]

## 3. Test-First Development (NON-NEGOTIABLE)

- [ ] CHK011 Does the plan include failing tests before implementation tasks for core data behavior (creation, editing, deletion, validation, persistence)? [Completeness, Plan §Testing Strategy, Const §III]
- [ ] CHK012 Are test case examples provided that cover the full range of validation rules (Bean Name, Grinder Setting, Roast Type, Origin uniqueness, Description length)? [Coverage, Plan §Testing Strategy]
- [ ] CHK013 Does the plan specify test coverage for persistence (data survives app restart) and state behavior (sorting, custom origin reuse)? [Coverage, Plan §Testing Strategy, Const §III]
- [ ] CHK014 Are test case examples provided for accidental-loss protections (delete confirmation, unsaved changes warning, duplicate-entry warning)? [Coverage, Plan §Testing Strategy, Const §III + §IV]
- [ ] CHK015 Does the plan document the testing framework (XCTest) and confirm it supports the required test-first workflow? [Clarity, Plan §Testing Strategy + §Dependency Justification]

## 4. User-Safe Data Changes

- [ ] CHK016 Is the delete safety pattern specified as "confirmation before deletion" (consistent with the approved clarification)? [Consistency, Plan §Data Safety, Spec §Clarifications]
- [ ] CHK017 Are unsaved-edits protections documented with the correct trigger ("any changed field") matching the clarified decision? [Consistency, Plan §Data Safety, Spec §Clarifications]
- [ ] CHK018 Is the create-only custom origin management scope explicitly stated, confirming rename/delete are deferred? [Clarity, Plan §Data Safety, Spec §Clarifications]
- [ ] CHK019 Does the data model specify consequences for data safety around custom origins (existing Brew Entries are not affected)? [Completeness, Data Model §Data Safety Features]
- [ ] CHK020 Does the plan confirm that no destructive or overwriting operations can proceed without user confirmation? [Coverage, Plan §Data Safety, Const §IV]

## 5. Beginner-Friendly Mobile Architecture

- [ ] CHK021 Are all mobile-specific assumptions documented (target iOS version, device form factor, locale formatting, navigation model, storage)? [Completeness, Plan §Mobile Assumptions, Data Model §Mobile Assumptions, Const §V]
- [ ] CHK022 Is the technology choice (SwiftUI + CoreData) justified as beginner-friendly, readable, and simple? [Clarity, Plan §Dependency Justification, Const §V]
- [ ] CHK023 Are README run and test commands documented and expected to be accurate for a beginner? [Completeness, Plan §Phase 1 + Quickstart, Const §V]
- [ ] CHK024 Is the theme approach documented (system default, light/dark support) and consistent with the constitution's requirement? [Consistency, Plan §Theme Handling, Const §V + Spec §FR-034–037]
- [ ] CHK025 Does the file structure in the plan present a clear, reviewable layout that a mobile beginner can navigate? [Clarity, Plan §Project Structure, Const §V]

## 6. Development Workflow & Quality Gates

- [ ] CHK026 Does the plan include a Constitution Check section that passes before Phase 0 and re-checks after Phase 1? [Completeness, Plan §Constitution Check, Const §Workflow]
- [ ] CHK027 Does the plan reference the spec's acceptance scenarios, edge cases, and functional requirements as inputs? [Traceability, Plan §Input + §Summary]
- [ ] CHK028 Does the plan document complexity tracking for any potential violations (or confirm none exist)? [Coverage, Plan §Complexity Tracking / Omitted Section, Const §Workflow]
- [ ] CHK029 Are risks and trade-offs documented, including mitigation strategies? [Completeness, Plan §Risks and Trade-offs]

## 7. Product Scope & Technical Constraints

- [ ] CHK030 Does the plan explicitly confirm that the MVP excludes accounts, authentication, cloud sync, backend services, APIs, sharing, ratings, favourites, photos, brewing timers, recipes, multiple grinder profiles, multiple methods, notifications, analytics, payments, social features, and app store publishing? [Completeness, Plan §Explicitly Out-of-Scope, Const §Product Scope]
- [ ] CHK031 Are future features that are out of scope clearly separated and not accidentally introduced in the plan's architecture? [Scope, Plan §Out-of-Scope, Spec §FR-040]
- [ ] CHK032 Does the plan confirm the app respects system theme by default and supports light and dark modes? [Consistency, Plan §Theme Handling, Const §Product Scope, Spec §FR-034–037]
- [ ] CHK033 Is the plan's scale appropriate for a single-user, local-device MVP (no multi-user, no cross-device)? [Scope, Plan §Technical Context, Spec §Assumptions]

## Notes

- This checklist tests the QUALITY of the plan and data model against the constitution — not the correctness of any implementation.
- Items marked `[Gap]` indicate potential missing documentation that should be reviewed by the spec author.
- The checklist follows the 5 constitutional principles plus workflow and scope constraints.
- All items should be re-checked after any plan amendments.