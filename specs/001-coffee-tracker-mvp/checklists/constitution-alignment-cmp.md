# Constitution Alignment Checklist: Compose Multiplatform Plan Audit

**Purpose**: Validate the Compose Multiplatform plan.md and data-model.md against the coffee.app constitution principles
**Created**: 2026-07-05
**Audit Target**: `specs/001-coffee-tracker-mvp/plan.md` (Compose Multiplatform revision)
**Data Model**: `specs/001-coffee-tracker-mvp/data-model.md`
**Constitution**: `.specify/memory/constitution.md`

---

## 1. Focused Brewing Memory (Constitution §I)

- [ ] CHK001 Is the brewing-memory value explicitly stated in the plan summary and connected to the user's core problem of forgetting successful settings? [Completeness, Plan §Summary, Const §I]
- [ ] CHK002 Does each dependency choice (Kotlin, Compose, Room KMP, kotlin.test) include a rationale that ties back to serving the brewing-memory goal rather than speculative future needs? [Clarity, Plan §Dependency Selection, Const §I]
- [ ] CHK003 Is the out-of-scope section complete and aligned with keeping the app focused on brewing memory, explicitly excluding features that would distract from the core purpose? [Completeness, Plan §Explicitly Out-of-Scope, Const §I]
- [ ] CHK004 Does the data model capture only brewing-relevant fields (bean name, origin, roast, grinder, portion, description, dates) without adding speculative data points? [Scope, Data Model §BrewEntry, Const §I]
- [ ] CHK005 Does the plan avoid introducing features not in the spec, such as analytics, sharing, or advanced filtering? [Consistency, Plan §Constraints + §Out-of-Scope, Spec §FR-040]

## 2. Local-First Simplicity (Constitution §II)

- [ ] CHK006 Is the local-only persistence approach explicitly confirmed as Room KMP (SQLite) with no backend, remote database, or cloud dependency? [Completeness, Plan §Local Persistence, Const §II]
- [ ] CHK007 Does the Technical Context section explicitly rule out network calls, API endpoints, remote storage, accounts, and cloud sync? [Clarity, Plan §Technical Context, Const §II]
- [ ] CHK008 Are all four dependencies (Kotlin, Compose Multiplatform, Room KMP, kotlin.test) individually justified with reasoning that meets the constitution's "clearly simplify the app" threshold? [Completeness, Plan §Dependency Selection, Const §II]
- [ ] CHK009 Is the Gradle/version-catalog approach documented in a way that avoids introducing unnecessary build complexity? [Clarity, Plan §Project Structure, Const §II]
- [ ] CHK010 Does the plan confirm that the navigation library choice does not introduce a network-enabled dependency? [Coverage, Plan §Navigation Structure, Const §II]
  - ⚠️ NOTE: The plan lists "voyager / decompose / built-in nav" as options without committing to one. This ambiguity should be resolved before implementation.

## 3. Test-First Development — NON-NEGOTIABLE (Constitution §III)

- [ ] CHK011 Are failing tests planned before implementation for core data behavior (Brew Entry creation, editing, deletion, validation, and persistence)? [Completeness, Plan §Testing Strategy, Const §III]
- [ ] CHK012 Do the test case examples cover the full range of validation rules: Bean Name (required), Grinder Setting (1-48), Roast Type (enum check), Origin uniqueness (case-insensitive), Description (max 500 chars), and Portion Weight (positive decimal)? [Coverage, Plan §Testing Strategy, Const §III]
- [ ] CHK013 Are persistence tests specified using Room's in-memory database test rule to verify data survives app restart? [Coverage, Plan §Testing Strategy, Const §III]
- [ ] CHK014 Are test case examples provided for accidental-loss protections: delete confirmation, unsaved changes warning, and duplicate-entry warning? [Coverage, Plan §Testing Strategy, Const §III + §IV]
- [ ] CHK015 Does the plan confirm that kotlin.test is used for JVM-fast test feedback and that Compose UI tests cover critical mobile flows? [Clarity, Plan §Testing Strategy, Const §III]
- [ ] CHK016 Does the tasks.md preserve test-first ordering with test tasks (T01X) appearing before implementation tasks (T02X) in each user story phase? [Consistency, Tasks §Phase 3–8, Const §III]

## 4. User-Safe Data Changes (Constitution §IV)

- [ ] CHK017 Is the delete safety pattern specified as `AlertDialog` confirmation before deletion (consistent with the approved clarification)? [Consistency, Plan §Data Safety, Spec §Clarifications]
- [ ] CHK018 Are unsaved-edits protections documented with the correct trigger — "any field differs from the loaded or blank entry" — matching the clarified decision? [Consistency, Plan §Data Safety, Spec §Clarifications]
- [ ] CHK019 Is the create-only custom-origin scope explicitly stated in both the plan and data model, confirming rename/delete are deferred? [Clarity, Plan §Data Safety + Data Model §Custom Origins, Spec §Clarifications]
- [ ] CHK020 Does the plan confirm that no destructive or overwriting operations can proceed without user confirmation via a dialog? [Coverage, Plan §Data Safety, Const §IV]
- [ ] CHK021 Does the data model specify that removing or modifying a custom Origin must not silently break existing Brew Entries that reference it? [Completeness, Data Model §Data Safety, Const §IV]

## 5. Beginner-Friendly Mobile Architecture (Constitution §V)

- [ ] CHK022 Are all mobile-specific assumptions documented: target platforms (Android API 26+, iOS 15+), device form factor (phone), date formatting approach, navigation model, and theme handling? [Completeness, Plan §Mobile Assumptions + Data Model §Mobile Assumptions, Const §V]
- [ ] CHK023 Is the choice of Compose Multiplatform justified as beginner-friendly for a developer experienced in Python scripting, with specific mention of Kotlin's approachable syntax? [Clarity, Plan §Dependency Selection, Const §V]
- [ ] CHK024 Are the README run/test commands documented in quickstart.md with actual Gradle commands (`./gradlew :composeApp:allTests`) rather than vague references? [Completeness, Quickstart §Testing, Const §V]
- [ ] CHK025 Does the project structure present a clear, reviewable layout that a mobile beginner can navigate, with commonMain housing all shared logic? [Clarity, Plan §Project Structure, Const §V]
- [ ] CHK026 Is the navigation approach documented without ambiguity — one specific library choice rather than a list of alternatives? [Clarity, Plan §Navigation Structure, Const §V]
  - ⚠️ ISSUE: Plan lists "voyager / decompose / built-in nav" as options. This should be narrowed to a single recommendation.

## 6. Development Workflow & Quality Gates (Constitution §Workflow)

- [ ] CHK027 Does the plan include a Constitution Check section at the top (pre-research) and does it re-check after Phase 1 design? [Completeness, Plan §Constitution Check ×2, Const §Workflow]
- [ ] CHK028 Does the plan reference the spec's user stories, acceptance scenarios, edge cases, and functional requirements as inputs? [Traceability, Plan §Input + §Summary, Const §Workflow]
- [ ] CHK029 Is complexity tracking documented, or is a clear statement made that no complexity violations exist? [Coverage, Plan §Risks and Trade-offs, Const §Workflow]
- [ ] CHK030 Are risks and trade-offs documented with mitigation strategies for each (Compose maturity, Kotlin learning curve, Room KMP setup, TDD discipline, scope creep)? [Completeness, Plan §Risks and Trade-offs, Const §Workflow]

## 7. Product Scope & Technical Constraints (Constitution §Scope)

- [ ] CHK031 Does the plan explicitly enumerate all out-of-scope items from FR-040 (accounts, authentication, cloud sync, backend services, APIs, sharing, ratings, favourites, photos, brewing timers, recipes, multiple grinder profiles, multiple methods, notifications, analytics, payments, social features, app store publishing)? [Completeness, Plan §Explicitly Out-of-Scope, Spec §FR-040]
- [ ] CHK032 Is the app confirmed to follow system theme by default with explicit light and dark mode support via `isSystemInDarkTheme()` and Material 3 colour schemes? [Consistency, Plan §Theme Handling, Const §Scope, Spec §FR-034–037]
- [ ] CHK033 Is the plan's scale appropriate for a single-user, local-device MVP with no multi-user, multi-device, or cross-platform sync? [Scope, Plan §Technical Context, Spec §Assumptions]

## 8. Data Model Integrity

- [ ] CHK034 Does the data model use Kotlin-appropriate types: `String` for UUIDs, `Int` for grinder setting, `Double` for portion weight, `Long` (epoch millis) for dates? [Consistency, Data Model §BrewEntry, Plan §Data Model]
- [ ] CHK035 Is the Origin-to-BrewEntry relationship correctly documented as a foreign key (`beanOrigin` referencing `Origin.name`)? [Clarity, Data Model §Relationships]
- [ ] CHK036 Does the data model address the case-insensitive origin uniqueness requirement, and is the FK constraint consistent with case-sensitive/lowered matching? [Completeness, Data Model §Origin + §Validation, Spec §FR-017]
  - ⚠️ NOTE: `Origin.name` is a String PK which is case-sensitive by default in SQLite/Room. The plan must specify how case-insensitive lookups are enforced (e.g., storing lowercased version or using COLLATE NOCASE).
- [ ] CHK037 Are both `RoastType` and `SortOption` properly defined as enums with all valid values enumerated? [Completeness, Data Model §Enums]
- [ ] CHK038 Does the plan explain how predefined origins are seeded (callback or migration) and how they are distinguished from custom origins via `isCustom` Boolean? [Clarity, Plan §Local Persistence, Data Model §Origin]

## Notes

- This checklist tests the QUALITY of the updated Compose Multiplatform plan and data model against the constitution — not the correctness of any implementation.
- Items marked `⚠️` indicate potential gaps or ambiguities found during the audit that should be resolved before implementation begins.
- Items marked `[Gap]` indicate missing documentation that should be reviewed by the spec author.
- This is a fresh audit; the previous iOS checklist (constitution-alignment.md) is superseded.