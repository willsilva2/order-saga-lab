# Architecture Decision Records

This file records the significant decisions made in this lab, the context behind them and their trade-offs. Each decision is an ADR (Architecture Decision Record). Accepted ADRs are never rewritten: if a decision changes, a new ADR supersedes the old one.

## Index

| ADR | Title | Status |
|---|---|---|
| [ADR-0001](#adr-0001-monorepo-for-all-services) | Monorepo for all services | Accepted |
| [ADR-0002](#adr-0002-repository-naming-language-and-package-layout) | Repository naming, language and package layout | Accepted |
| [ADR-0003](#adr-0003-java-spring-boot-and-build-tooling) | Java, Spring Boot and build tooling | Accepted |
| [ADR-0004](#adr-0004-commit-branch-and-pull-request-conventions) | Commit, branch and pull request conventions | Accepted |

## Template

```markdown
## ADR-XXXX: Title

- Status: Proposed | Accepted | Superseded by ADR-YYYY
- Date: YYYY-MM-DD

### Context
Why this decision is needed. Forces and constraints at play.

### Decision
What was chosen.

### Alternatives considered
Other options and why they were not chosen.

### Consequences
What becomes easier, what becomes harder.
```

---

## ADR-0001: Monorepo for all services

- Status: Accepted
- Date: 2026-11-06

### Context

The lab is made of several services (order, payment, inventory, order query, BFF, gateway and a notifier), plus infrastructure code (Terraform, Kubernetes manifests) and documentation. It is built by a single person and published as one portfolio project. We need a repository layout that keeps the whole system easy to navigate without turning the services into a monolith.

### Decision

Keep everything in a single repository (`order-saga-lab`), with one folder per service plus `infra/`, `contracts/` and `docs/`.

Rules that keep the services independent:

- Each service is self-contained: its own `pom.xml` (or build file), its own `Dockerfile` and its own tests. There is no single Maven reactor that builds everything together.
- No shared code modules between services. Event contracts are documented in `contracts/` (JSON schemas) and each service keeps its own DTOs.
- The CI pipeline builds only what changed (path filters, for example `changeset "order-service/**"` in Jenkins).

### Alternatives considered

- **Polyrepo (one repository per service).** Gives fully independent deploy cycles and permissions, which is common in larger organisations. Rejected here because the overhead (several repositories, coordinated pull requests, scattered documentation) outweighs the benefits for a single-author lab.

### Consequences

- Easier: atomic changes across services (a contract change and its consumer in one pull request), a single place for documentation and diagrams, simpler onboarding for anyone reading the portfolio.
- Harder: without path filters the pipeline would rebuild everything on each commit; repository history and size grow over time; per-folder permissions are not possible without extra tooling.
- Discipline required: resisting shared libraries between services to avoid hidden coupling.
- Revisit if the project grows to multiple contributors or needs independent release cycles per service.

---

## ADR-0002: Repository naming, language and package layout

- Status: Accepted
- Date: 2026-11-06

### Context

The repository is public and meant to work as a portfolio. Its name should describe its purpose, and its content should be readable by an international audience.

### Decision

- The repository is named `order-saga-lab`, which states that it is a lab about sagas and event-driven order processing.
- Everything that lives in the repository is written in English: README, this file, commit messages, branch names, event names (for example `OrderCreated`) and log messages.
- The base package is `io.github.willsilva2.ordersagalab`, matching the repository name so that the same name is used everywhere. Below it there is one sub-package per bounded context (`order`, `payment`, `inventory`) and, inside each, the hexagonal layers (`domain`, `application`, `adapter`).
- The namespace is based on the GitHub account (`io.github.willsilva2`) because the project has no domain of its own. It is the format Maven Central accepts for GitHub-based namespaces, so it would also work if a library were ever published.
- Service folders and Maven `artifactId` values use kebab-case (`order-service`, `payment-service`). Java and Kotlin packages are lowercase without hyphens.

### Alternatives considered

- Naming the repository `orderflow` or `mini-orders`: shorter, but they say less about the purpose of the project.
- Using `io.github.willsilva2.orderflow` as the base package: shorter in every import, but a second name for the same project invites confusion. The repository name and the package do not need to match, yet keeping them aligned is simpler.
- Using `com.willsilva2.ordersagalab` as the namespace: shorter, but it suggests ownership of a `willsilva2.com` domain that does not exist.

### Consequences

- Easier: a self-explanatory portfolio entry and a single name used for the repository, the packages and the documentation.
- Harder: longer imports than a shorter package name would give. Renaming the base package after the first service exists would touch every file, so it should be decided before generating the first service.

---

## ADR-0003: Java, Spring Boot and build tooling

- Status: Accepted
- Date: 2026-11-06

### Context

The first service (`order-service`) needs a language level, a Spring Boot line and a build tool. The choice affects every other service, so it is recorded once. The development environment is Ubuntu on WSL, with OpenJDK 25 already installed and no system-wide Maven.

### Decision

- **Java 25 (LTS)** as the JDK for all JVM services.
- **Spring Boot 4.1.x**, using the latest stable release provided by Spring Initializr (4.1.2 at the time of writing).
- **Kotlin** for `order-service`, using the version managed by the Spring Boot build.
- **Maven**, always through the **Maven Wrapper** (`./mvnw`) committed in each service, so the Maven version is pinned in the repository and the CI uses the same command as local development.
- Services are generated through the Spring Initializr API (`start.spring.io`), which makes the generation reproducible.

Verified on 2026-11-06: `order-service` generated with these choices starts in about two seconds, `/actuator/health` returns `UP` and `./mvnw verify` finishes with `BUILD SUCCESS`.

### Alternatives considered

- **Java 21 (LTS):** the most conservative choice and equally supported, but it would mean installing another JDK for no functional gain, and Java 25 is also an LTS release.
- **Java 17:** still the Spring Boot 4 minimum and common in companies, but older. Modern language features can be practised on 25, and nothing in the lab requires restricting it to 17.
- **Spring Boot 4.0.x:** free support ends on 2026-12-31, shortly after the lab starts.
- **Spring Boot 3.5.x:** free support ended on 2026-06-30.
- **Gradle:** a good tool, but Maven matches the target job description and the planned Jenkins pipeline.
- **A system-wide Maven instead of the wrapper:** works, but the version would depend on each machine.

### Consequences

- Easier: nothing to install besides the JDK, and a reproducible build on any machine and in CI.
- Harder: some tools may lag behind the newest Java release (container base images, plugins). Fall back to Java 21 if one blocks the lab.
- To verify before Phase 5: which Spring Cloud release train is compatible with the chosen Spring Boot line, before adding Eureka, Config Server and Gateway.
- The legacy Spring Cloud components named in the job description (Zuul, Hystrix) are deprecated, so the lab uses their modern equivalents (Spring Cloud Gateway and Resilience4j).

---

## ADR-0004: Commit, branch and pull request conventions

- Status: Accepted
- Date: 2026-11-06

### Context

The repository is public and is also a portfolio, so its history is part of what is being shown. Although there is a single author, the lab is meant to practise a team workflow: short-lived branches, pull requests and technical review. A consistent history also allows automated changelogs and versioning later on.

### Decision

- **Commits** follow [Conventional Commits](https://www.conventionalcommits.org): `type(scope): description`, in English, imperative mood, lowercase, up to about 72 characters. Common types are `feat`, `fix`, `docs`, `test`, `refactor`, `build`, `ci` and `chore`. The scope is the service or area (`order-service`, `infra`). The body, when present, explains why and what was verified.
- **Branches** are short-lived and named `type/JIRA-KEY-short-description` in kebab-case, for example `feat/LJMP-10-order-service-skeleton`. Work is never committed directly to `main`.
- **Pull requests** are opened for every change, even by a single author, and reviewed in the *Files changed* tab as if written by someone else. The PR title follows the same Conventional Commits format as a commit. The description has three parts: *What*, *How to verify* and *Notes* (including the Jira key).
- **Merging** is done with **squash and merge** only. The PR title becomes the commit subject on `main`, so it must follow the convention. The branch is deleted after the merge.
- Planned: a `.github/pull_request_template.md` with the three description sections, and repository settings that allow squash merges only, use the PR title and description as the default squash message and delete merged branches automatically.

### Alternatives considered

- **Merge commit:** preserves every commit and the branch topology, but adds merge commits that clutter a small project's history.
- **Rebase and merge:** keeps each commit and a linear history, but relies on every intermediate commit being clean and well named, which is too strict for work-in-progress commits.
- **Committing directly to `main`:** faster, but it skips the review practice that this lab intends to demonstrate.
- **Free-form commit messages:** no rules to learn, but the history becomes harder to read and cannot feed changelog tools.

### Consequences

- Easier: a readable, uniform history on `main` with one commit per change, simple reverts and a base for automatic changelogs and versioning (for example release-please).
- Harder: the individual commits of a PR disappear from `main` after the squash, so the separation between, for instance, a `feat` commit and a `docs` commit only helps during review. It requires the small discipline of writing a good PR title.
- Revisit if the project gets more contributors or needs to keep detailed commit history.