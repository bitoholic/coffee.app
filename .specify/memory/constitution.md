<!--
Sync Impact Report
Version change: template → 1.0.0
Modified principles:
- Template principle 1 placeholder → I. Focused Brewing Memory
- Template principle 2 placeholder → II. Local-First Simplicity
- Template principle 3 placeholder → III. Test-First Development (NON-NEGOTIABLE)
- Template principle 4 placeholder → IV. User-Safe Data Changes
- Template principle 5 placeholder → V. Beginner-Friendly Mobile Architecture
Added sections:
- Product Scope & Technical Constraints
- Development Workflow & Quality Gates
Removed sections:
- None; template placeholders replaced with project-specific sections
Templates requiring updates:
- ✅ .specify/templates/plan-template.md
- ✅ .specify/templates/spec-template.md
- ✅ .specify/templates/tasks-template.md
- ✅ .specify/templates/checklist-template.md (reviewed; no project-specific changes required)
- ✅ .specify/templates/commands/*.md (not present in this repository)
Follow-up TODOs:
- None
-->
# coffee.app Constitution

## Core Principles

### I. Focused Brewing Memory
coffee.app MUST remain focused on helping the user remember coffee bean brewing
settings that produced good coffee. Core product value is capturing, reviewing,
and reusing bean-specific brewing details such as grinder setting, portion
weight, and simple evaluation notes. Every feature MUST state clear user value,
acceptance criteria, and how it supports this brewing-memory purpose.

Rationale: the app exists to solve one concrete problem: forgetting what worked
when experimenting with different coffee beans. Scope discipline keeps the MVP
small and learnable.

### II. Local-First Simplicity
The MVP MUST prefer simple local-first behavior over backend services. The
project MUST NOT introduce accounts, cloud sync, payments, social features,
analytics, push notifications, or backend infrastructure unless a future
specification explicitly adds them and updates this constitution if needed.
Dependencies MUST be added only when they clearly simplify the app.

Rationale: local-first storage is enough for the initial brewing-memory use case
and avoids unnecessary production complexity for a learning project.

### III. Test-First Development (NON-NEGOTIABLE)
Development MUST follow a test-first workflow: define the expected behavior,
write failing tests, confirm they fail, then implement the smallest code change
that makes them pass. Tests MUST focus first on core data and state behavior,
including brewing-setting creation, editing, deletion/undo, validation, and
persistence. UI tests SHOULD be added where they clarify important mobile flows.

Rationale: test-first development keeps iterations small, makes the learning
process explicit, and protects the app's core brewing-memory behavior.

### IV. User-Safe Data Changes
Any operation that can lose or overwrite user-entered brewing data MUST protect
the user through confirmation, undo, or an equivalent recovery pattern. Deletion,
bulk changes, destructive resets, and irreversible edits MUST be explicitly
called out in specifications and acceptance scenarios. Data models MUST avoid
silently discarding information the user typed.

Rationale: the app is a personal memory aid; accidental loss of saved brewing
settings directly breaks trust in the product.

### V. Beginner-Friendly Mobile Architecture
Generated code MUST be readable, reviewable, and beginner-friendly for a
developer who is experienced with Python scripting but new to mobile
development. The architecture MUST favor small modules, clear names, boring and
well-documented technology, and direct data flow over clever abstractions. Every
mobile-specific assumption MUST be documented explicitly, including target
platform, storage choice, navigation model, theme behavior, and local run/test
commands.

Rationale: this is a learning project. Clarity, small iterations, and simple
architecture are more valuable than advanced patterns or premature scalability.

## Product Scope & Technical Constraints

- The MVP MUST remain a personal mobile app for tracking coffee bean brewing
  settings.
- The app MUST respect the system theme by default and support both light and
  dark modes.
- The MVP MUST be runnable locally with clear README commands for setup, tests,
  and app launch.
- Specifications MUST document mobile assumptions explicitly instead of relying
  on unstated platform defaults.
- Specifications MUST avoid future features unless they are explicitly requested.
- Backend services, remote databases, accounts, analytics, notifications,
  payments, and social features are out of scope for the MVP.
- New dependencies MUST be justified in the implementation plan with the user
  value or complexity reduction they provide.

## Development Workflow & Quality Gates

- Each feature MUST begin with a specification that includes user stories,
  independent tests, acceptance scenarios, edge cases, functional requirements,
  success criteria, and relevant assumptions.
- Each implementation plan MUST pass the Constitution Check before Phase 0
  research and again after Phase 1 design.
- Each tasks.md MUST include tests before implementation tasks for every user
  story that changes data, state, persistence, or important mobile flows.
- Implementation MUST proceed in small vertical slices that can be run and
  reviewed locally.
- The MVP MUST be kept small; complexity tracking is required for any backend,
  account system, cloud feature, broad abstraction layer, or dependency that is
  not clearly necessary for the current specification.
- Reviews MUST verify that code is readable for a mobile beginner, that README
  commands remain accurate, and that no unspecified future features were added.

## Governance

This constitution supersedes conflicting project practices, templates, and
implementation preferences. All specifications, plans, tasks, and reviews MUST
check compliance with the principles above.

Amendments require:
1. A clear reason for changing the rule.
2. An update to this constitution with a semantic version bump.
3. A Sync Impact Report describing affected templates and follow-up work.
4. Review of dependent Spec Kit templates and runtime guidance docs.

Versioning policy:
- MAJOR: backward-incompatible governance changes or removal/redefinition of a
  core principle.
- MINOR: new principles or materially expanded governance/quality guidance.
- PATCH: clarifications, wording fixes, or non-semantic refinements.

Compliance review expectations:
- Feature specs MUST identify any constitution conflicts before planning.
- Plans MUST document complexity violations and rejected simpler alternatives.
- Tasks MUST preserve test-first ordering.
- Implementation is not complete until tests and local run instructions are
  verified or any blocker is documented.

**Version**: 1.0.0 | **Ratified**: 2026-07-05 | **Last Amended**: 2026-07-05
