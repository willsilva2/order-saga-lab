# Order Saga Lab

An event-driven order processing lab built to practice and demonstrate modern backend engineering: hexagonal architecture, microservices, Kafka and RabbitMQ messaging, sagas, CQRS and event sourcing, on top of Kotlin, Java and Spring Boot, with CI/CD, containers and cloud infrastructure as code.

> **Status: work in progress.** This is a learning lab, not production-ready software. This README only describes what already exists in the repository; planned work is tracked in the [Status](#status) section.

## Overview

The domain is intentionally simple (order → payment → inventory → notification), so the focus stays on architecture and engineering practices rather than business rules. Each service is independently buildable and deployable, even though everything lives in a single repository (see [ADR-0001](DECISIONS.md)).

## Target architecture (planned)

| Service | Stack | Role |
|---|---|---|
| `order-service` | Kotlin, Spring Boot | Hexagonal architecture, Postgres, publishes events to Kafka, event-sourced `Order` aggregate |
| `payment-service` | Kotlin, Ktor, Exposed, Koin | Consumes `OrderCreated`, processes payment, publishes the result |
| `inventory-service` | Java 17, Spring Boot | Reserves stock, uses RabbitMQ with dead-letter queues |
| `order-query-service` | Kotlin or Java | CQRS read model built from events |
| `bff-web` | Spring Boot or Ktor | Backend for frontend, aggregates data for a fictional client |
| `gateway` | Spring Cloud Gateway | Single entry point, JWT validation, rate limiting |
| `notifier` | Go | Lightweight event consumer that "sends" notifications |

Choreographed saga, with compensation when a step fails:

```mermaid
sequenceDiagram
    participant O as order-service
    participant P as payment-service
    participant I as inventory-service
    O->>P: OrderCreated
    P->>I: PaymentApproved
    I-->>P: StockFailed
    P->>P: RefundPayment (compensation)
    P-->>O: PaymentRefunded
    O->>O: OrderCancelled
```

## Tech stack (planned)

- **Languages and frameworks:** Kotlin, Java 17, Spring Boot, Spring Cloud, Ktor, Exposed, Koin, Go
- **Messaging:** Apache Kafka, RabbitMQ
- **Data:** PostgreSQL
- **Testing:** JUnit 5, Mockito/MockK, Testcontainers, TDD
- **Containers and orchestration:** Docker, Kubernetes (kind)
- **CI/CD:** Jenkins, Maven
- **Cloud and IaC:** AWS (VPC, IAM, S3, RDS, ECS, Lambda, API Gateway), Terraform
- **Observability and security:** Prometheus, Grafana, ELK, Keycloak, OWASP Dependency-Check

## Getting started

Nothing to run yet. Instructions will be added as each service lands, starting with `order-service`.

## Repository structure

```text
order-saga-lab/
├── README.md
├── DECISIONS.md      # architecture decision records (ADRs)
├── LICENSE
└── .gitignore
```

Service folders, `infra/` and `docs/` will be added as the lab progresses.

## Status

- [ ] Phase 1: Foundation and hexagonal architecture
- [ ] Phase 2: Tests (TDD, JUnit, Testcontainers)
- [ ] Phase 3: Ktor, Exposed, Koin and Java 17
- [ ] Phase 4: Messaging and event-driven patterns
- [ ] Phase 5: Spring Cloud, BFF and resilience
- [ ] Phase 6: Docker and Kubernetes
- [ ] Phase 7: CI/CD with Jenkins
- [ ] Phase 8: AWS and Terraform
- [ ] Phase 9: Observability and security
- [ ] Phase 10: Go notifier

## Architecture decisions

Decisions and their trade-offs are recorded in [DECISIONS.md](DECISIONS.md).

## License

Released under the [MIT License](LICENSE).