# orders-microservices

Two small Spring Boot microservices that show JPA/Hibernate, REST communication between services, and an
Agile paper trail.

- **catalog-service** (port 8081): products and stock. JPA/Hibernate, PostgreSQL, optimistic locking.
- **order-service** (port 8082): orders with line items. Calls catalog-service to price and reserve stock,
  and releases stock if the order cannot be completed.

Stack: Java 17, Spring Boot 3.3, Spring Data JPA (Hibernate), PostgreSQL, Maven, JUnit 5, Mockito, GitHub Actions.

## Run it

Requirements: JDK 17+, Maven, Docker.

```bash
docker compose up -d                      # two PostgreSQL databases (ports 5433, 5434)
mvn -B verify                             # build and run all tests

mvn -pl catalog-service spring-boot:run   # terminal 1
mvn -pl order-service spring-boot:run     # terminal 2
```

## Try it

```bash
# create a product
curl -i -X POST localhost:8081/api/products -H 'Content-Type: application/json' \
  -d '{"sku":"KB-001","name":"Keyboard","price":49.99,"stockQuantity":10}'

# place an order for 2 of product 1
curl -i -X POST localhost:8082/api/orders -H 'Content-Type: application/json' \
  -d '{"customerEmail":"buyer@example.com","items":[{"productId":1,"quantity":2}]}'

# stock is now 8
curl localhost:8081/api/products/1

# ask for more than is in stock: 409 Conflict, stock unchanged
curl -i -X POST localhost:8082/api/orders -H 'Content-Type: application/json' \
  -d '{"customerEmail":"buyer@example.com","items":[{"productId":1,"quantity":500}]}'
```

## API

| Service | Endpoint | Purpose |
|---------|----------|---------|
| catalog | `GET /api/products`, `GET /api/products/{id}` | read products |
| catalog | `POST /api/products`, `PUT /api/products/{id}` | create, update |
| catalog | `POST /api/products/{id}/reserve`, `/release` | take or return stock |
| order | `POST /api/orders` | place an order |
| order | `GET /api/orders`, `GET /api/orders/{id}` | order history |

Errors use RFC 7807 problem details: 400 validation, 404 not found, 409 conflict or out of stock,
422 unknown product, 503 catalog-service unavailable.

## Tests

- Repository tests (`@DataJpaTest`, H2): unique constraint, optimistic-lock version, cascade save, entity graph
- Service tests (Mockito): stock reservation, compensation when an order fails halfway
- Controller tests (`@WebMvcTest`): validation and status codes

## Agile working notes

Planning lives in [`docs/agile`](docs/agile): product backlog with user stories and acceptance criteria,
Sprint 1 plan, Definition of Done. Architecture decisions and known limitations are in
[`docs/architecture.md`](docs/architecture.md).


