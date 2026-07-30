# Feature Specification: Test Coverage Fix & CI Polish

**Feature Branch**: `feature/007-coverage-polish`

**Created**: 2026-07-30

**Status**: Draft

**Input**: Fix the test coverage gap, align CI metrics, fix README badges, and add a coverage badge.

## User Stories & Testing *(mandatory)*

### User Story 1 — Consistent Coverage Metric (Priority: P1)

As a developer, I want koverVerify and the PR comment to measure the same metric and value so the CI gate and the comment never disagree.

**Why this priority**: The previous release had a PR that passed CI (instruction coverage ≥9%) but showed ❌ on the comment (line coverage 8.6%). This eroded trust in the CI system.

**Independent Test**: Run `./gradlew koverVerify` and the XML parser script on the same build — both report the same percentage for the same metric (line coverage). Changing the threshold in `build.gradle.kts` updates both.

### User Story 2 — Coverage at 30% (Priority: P1)

As a developer, I want the project to have at least 30% line coverage (excl. navigation, origin picker, generated resources) so that core logic is meaningfully tested.

**Why this priority**: Current coverage is ~18% on testable code. UI composables (~35k lines) are exempt per spec. Realistic target for this release is 30%, with incremental raises in subsequent releases toward 85%.

**Independent Test**: `./gradlew test koverXmlReport koverVerify` passes. PR shows coverage percentage with current threshold (18%).

### User Story 3 — CI Badge Works (Priority: P2)

As a developer, I want the CI badge in the README to show the current build status (passing/failing) instead of "no status".

**Why this priority**: The badge is the first thing visitors see and currently looks broken.

**Independent Test**: Open README on GitHub → badge shows ✅ passing or ❌ failing based on latest develop build.

### User Story 4 — Coverage Badge (Priority: P2)

As a developer, I want a badge in the README showing the current line coverage percentage so I can see coverage at a glance.

**Why this priority**: Coverage is a key quality metric and should be visible without downloading artifacts.

**Independent Test**: Open README on GitHub → badge shows "coverage: X%" with the current line coverage.

## Open Questions *(for clarify)*

### Q1 — Coverage target
Should the spec target 85% across the entire codebase or only for specific packages (e.g., ViewModel + DAO + Domain, excluding navigation and composables)?

### Q2 — Badge service
Which service to use for coverage badges? Shields.io static badge, or a dynamic badge from a coverage service (Codecov, Coveralls, SonarCloud)?

### Q3 — CI fix approach
The CI badge shows "no status" because the workflow is named "CI" but the badge URL might point to an incorrect branch or workflow name. What's the preferred fix?

### Q4 — Comment script vs koverVerify alignment
Should the comment script read koverVerify's output directly, or should both use the same XML report parsing?

## Constraints

- Must not introduce new features or change existing functionality
- Existing tests must continue to pass
- `koverVerify` must be the single source of truth for the threshold
- Coverage must be measured as line coverage percentage
