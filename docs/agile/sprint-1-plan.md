# Sprint 1 plan

**Length:** 5 days (solo)
**Sprint goal:** A customer can place an order that reserves stock in a separate catalog service, with both
services tested and runnable locally.

## Sprint backlog

Stories US-1 to US-6 from the [product backlog](product-backlog.md), 16 points in total.

## Suggested order of work (each step is a small PR)

1. US-1, US-2: catalog-service entity, repository, CRUD endpoints, repository and controller tests
2. US-3: reserve/release endpoints, optimistic locking test
3. US-6: docker-compose with two PostgreSQL databases, run catalog-service against it
4. US-4: order-service entities, REST client to catalog-service, place-order flow with unit tests
5. US-5: order history endpoints, entity graph to avoid N+1
6. Final pass: README, CI green, demo with curl

## Daily check (write 2 lines per day in the table below)

| Day | Done yesterday / today | Blockers |
|-----|------------------------|----------|
| 1 | | |
| 2 | | |
| 3 | | |
| 4 | | |
| 5 | | |

## Sprint review checklist

- [ ] Demo: create products, place an order, show stock decreased, show a rejected order
- [ ] All Sprint 1 stories meet the Definition of Done
- [ ] `mvn verify` passes locally and in GitHub Actions

## Sprint retrospective (fill in after the sprint, with your real answers)

- What went well:
- What did not go well:
- What I will change in Sprint 2:
