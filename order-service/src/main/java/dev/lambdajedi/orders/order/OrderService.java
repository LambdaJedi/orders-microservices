package dev.lambdajedi.orders.order;

import dev.lambdajedi.orders.catalog.CatalogClient;
import dev.lambdajedi.orders.catalog.ProductInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository repository;
    private final CatalogClient catalog;

    public OrderService(OrderRepository repository, CatalogClient catalog) {
        this.repository = repository;
        this.catalog = catalog;
    }

    /**
     * Places an order across two services without a distributed transaction:
     * resolve prices, reserve stock remotely, then save locally. If any step after the first
     * reservation fails, the reservations already made are released (a simple compensating action).
     * Deliberately not @Transactional so no database transaction stays open during remote calls.
     */
    public OrderResponse placeOrder(PlaceOrderRequest request) {
        List<ResolvedLine> lines = new ArrayList<>();
        for (PlaceOrderRequest.Line line : request.items()) {
            lines.add(new ResolvedLine(catalog.getProduct(line.productId()), line.quantity()));
        }

        List<ResolvedLine> reserved = new ArrayList<>();
        try {
            for (ResolvedLine line : lines) {
                catalog.reserve(line.product().id(), line.quantity());
                reserved.add(line);
            }

            CustomerOrder order = new CustomerOrder(request.customerEmail());
            for (ResolvedLine line : lines) {
                ProductInfo p = line.product();
                order.addItem(new OrderItem(p.id(), p.sku(), p.price(), line.quantity()));
            }
            return OrderResponse.from(repository.save(order));
        } catch (RuntimeException ex) {
            compensate(reserved);
            throw ex;
        }
    }

    @Transactional(readOnly = true)
    public OrderResponse get(Long id) {
        return repository.findById(id)
                .map(OrderResponse::from)
                .orElseThrow(() -> new OrderNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> list() {
        return repository.findAllByOrderByCreatedAtDesc().stream().map(OrderResponse::from).toList();
    }

    private void compensate(List<ResolvedLine> reserved) {
        for (ResolvedLine line : reserved) {
            try {
                catalog.release(line.product().id(), line.quantity());
            } catch (RuntimeException releaseFailure) {
                // Best effort: surfaced in logs so it can be reconciled (see backlog: outbox/retry).
                log.warn("Could not release {} x product {} after failed order",
                        line.quantity(), line.product().id(), releaseFailure);
            }
        }
    }

    private record ResolvedLine(ProductInfo product, int quantity) {
    }
}
