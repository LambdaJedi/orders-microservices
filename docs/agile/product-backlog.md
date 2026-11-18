# Product backlog

Working agreement: this is a solo project run in the style of Scrum. One person plays product owner and
developer. Each story below is a GitHub Issue, worked on a short-lived branch, and closed by a pull request
that meets the [Definition of Done](definition-of-done.md). Points are relative effort (1, 2, 3, 5, 8).

Update the **Status** column as you go, and keep it honest.

## Sprint 1: catalog and ordering walking skeleton

| ID | User story | Acceptance criteria | Points | Status |
|----|-----------|---------------------|--------|--------|
| US-1 | As a shop admin, I can add and update products so customers can order them. | POST creates a product (201 + Location). SKU must be unique (409 on duplicate). Invalid input returns 400. PUT updates name, price, stock. | 3 | Not started |
| US-2 | As a shop admin, I can see the product list and a single product. | GET list and GET by id work. Unknown id returns 404. | 1 | Not started |
| US-3 | As the order system, I can reserve stock for a product so two customers cannot buy the last item. | Reserve reduces stock. Reserving more than available returns 409 and changes nothing. Concurrent updates are detected (optimistic locking). | 3 | Not started |
| US-4 | As a customer, I can place an order for one or more products. | Order total is calculated from catalog prices. Items store the price paid at order time. Unknown product returns 422. Not enough stock returns 409. | 5 | Not started |
| US-5 | As a customer, I can view an order and my order history. | GET by id and GET list work. Unknown id returns 404. Items load without N+1 queries. | 2 | Not started |
| US-6 | As a developer, I can start both services and their databases locally. | `docker-compose up -d` starts two PostgreSQL databases. Both services start and talk to each other. README explains how. | 2 | Not started |

## Sprint 2: resilience (planned, not built)

| ID | User story | Acceptance criteria | Points |
|----|-----------|---------------------|--------|
| US-7 | As a customer, I do not lose stock when my order fails halfway. | Stock reserved for earlier lines is released if a later line or the save fails. Release failures are logged. | 3 |
| US-8 | As an operator, I can deploy schema changes safely. | Flyway migrations replace `ddl-auto: update`. | 3 |
| US-9 | As an operator, I can see why a request failed across services. | Correlation id is passed from order-service to catalog-service and appears in both logs. | 3 |
| US-10 | As a customer, an order is not stuck when catalog-service is briefly down. | Retry with backoff or circuit breaker on catalog calls. Decision recorded in an ADR. | 5 |

## Icebox

- API gateway in front of both services
- Authentication (Spring Security + JWT)
- Contract tests between order-service and catalog-service
- Outbox pattern for reliable release/compensation
- Containerise both services (Dockerfiles in docker-compose)
