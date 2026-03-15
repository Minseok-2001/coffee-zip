# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

`coffee-zip` is a Quarkus 3.32.2 application written in Kotlin, using:
- **Quarkus REST** (Jakarta REST / JAX-RS) with Jackson for JSON
- **Hibernate ORM with Panache** for persistence
- **CDI (Arc)** for dependency injection

Group: `org.coffeezip`, Java 21, Kotlin 2.3.10.

## Commands

```bash
# Run in dev mode (live reload, Dev UI at http://localhost:8080/q/dev/)
./gradlew quarkusDev

# Build
./gradlew build

# Run tests
./gradlew test

# Run a single test class
./gradlew test --tests "org.coffeezip.ExampleResourceTest"

# Build native executable
./gradlew build -Dquarkus.native.enabled=true
```

## Architecture

- `src/main/kotlin/org/coffeezip/` — application source
  - REST resources annotated with `@Path` (e.g., `ExampleResource`)
  - JPA entities annotated with `@Entity` (e.g., `MyKotlinEntity`)
- `src/test/kotlin/` — unit/integration tests using `@QuarkusTest` + RestAssured
- `src/native-test/kotlin/` — native image integration tests using `@QuarkusIntegrationTest`
- `src/main/resources/application.properties` — Quarkus configuration (currently empty)
- `src/main/resources/import.sql` — SQL seeded in dev/test mode

## Key Conventions

- `allOpen` plugin is configured to open classes annotated with `@Path`, `@ApplicationScoped`, `@Entity`, and `@QuarkusTest` — Kotlin classes don't need `open` added manually for these.
- Use `@get:` annotation prefix for JPA annotations on Kotlin properties (e.g., `@get:Id`, `@get:GeneratedValue`).
- Tests use RestAssured DSL and run against an embedded Quarkus instance via `@QuarkusTest`.
