# Architecture Decision Records

This file records the significant decisions made in this lab, the context behind them and their trade-offs. Each decision is an ADR (Architecture Decision Record). Accepted ADRs are never rewritten: if a decision changes, a new ADR supersedes the old one.

## Index

| ADR | Title | Status |
|---|---|---|
| [ADR-0001](#adr-0001-monorepo-for-all-services) | Monorepo for all services | Accepted |
| [ADR-0002](#adr-0002-repository-naming-language-and-package-layout) | Repository naming, language and package layout | Accepted |

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