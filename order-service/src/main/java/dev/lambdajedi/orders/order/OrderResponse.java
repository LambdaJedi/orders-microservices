package dev.lambdajedi.orders.order;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrderResponse(
        Long id,
        String customerEmail,
        OrderStatus status,
        BigDecimal total,
        Instant createdAt,
        List<Item> items) {

    public record Item(Long productId, String sku, BigDecimal unitPrice, int quantity) {
    }

    public static OrderResponse from(CustomerOrder order) {
        List<Item> items = order.getItems().stream()
                .map(i -> new Item(i.getProductId(), i.getSku(), i.getUnitPrice(), i.getQuantity()))
                .toList();
        return new OrderResponse(
                order.getId(),
                order.getCustomerEmail(),
                order.getStatus(),
                order.getTotal(),
                order.getCreatedAt(),
                items);
    }
}
