# Development Guide

This document explains how to build, test, and contribute to MTG Desktop Companion.

## Prerequisites

- Java 23+
- Maven 3.9+
- Git

## Local setup

```bash
git clone https://github.com/nicho92/MtgDesktopCompanion.git
cd MtgDesktopCompanion
mvn -DskipTests clean install
```

For full verification:

```bash
mvn clean test
```

## Useful Maven commands

- Full build: `mvn clean install`
- Skip tests: `mvn -DskipTests clean install`
- Tests only: `mvn test`

## Code organization

- `src/main/java/org/magic/api`
  - Pluggable provider interfaces and implementations (cache, dashboards, notifiers, shops, tracking, etc.)
- `src/main/java/org/magic/services`
  - Core service layer, managers, jobs, adapters, tools, and network utilities
- `src/main/java/org/magic/servers`
  - Embedded server components and timers
- `src/main/java/org/magic/game`
  - Game components and logics
- `src/test/java`
  - Test suites for APIs, providers, analysis, and networking

## Contribution workflow

1. Create a dedicated branch from `main`.
2. Keep changes scoped and atomic.
3. Add or update tests when behavior changes.
4. Run `mvn test` before opening a PR.
5. Document user-visible changes in `CHANGELOG.md` under `Unreleased`.

## Documentation expectations

When adding features, update:

- `README.md` for discoverability and quick start impact
- `docs/USER_GUIDE.md` for end-user behavior
- `docs/DEVELOPMENT.md` for developer workflow changes
- `CHANGELOG.md` for release notes
