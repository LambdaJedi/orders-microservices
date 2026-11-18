# Architecture notes

```
   client
     |
     |  POST /api/orders
     v
+---------------+   REST (RestClient)   +------------------+
| order-service | --------------------> | catalog-service  |
|  :8082        |  GET product          |  :8081           |
|  JPA/Hibernate|  POST reserve/release |  JPA/Hibernate   |
+-------+-------+                       +--------+---------+
        |                                        |
   orders-db (PostgreSQL :5434)           catalog-db (PostgreSQL :5433)
```

## Decisions

1. **Database per service.** Each service owns its schema. `order_items.product_id` is a plain column, not a
   foreign key, because the product lives in another database.
2. **Prices are copied into the order.** Later catalog price changes must not change past orders.
3. **No distributed transaction.** Placing an order resolves prices, reserves stock over HTTP, then saves locally.
   If a later step fails, earlier reservations are released (compensation). Release is best effort and logged on
   failure. A production system would add an outbox or retry (backlog US-10).
4. **Optimistic locking on products** (`@Version`) so concurrent reservations cannot both win on stale stock.
5. **`CatalogClient` interface.** Order logic is tested with a mock, with no HTTP involved.
6. **Entity graph on order queries** so listing orders loads items in one query instead of one query per order.
7. **`ddl-auto: update` is for local development only.** Flyway is planned (backlog US-8).

## Known limitations

- No authentication
- Compensation can fail silently if catalog-service is down at release time
- No retry or circuit breaker on remote calls
- No service discovery or gateway (URLs come from configuration)
