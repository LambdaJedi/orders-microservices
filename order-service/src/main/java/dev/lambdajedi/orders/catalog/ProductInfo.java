package dev.lambdajedi.orders.catalog;

import java.math.BigDecimal;

/** The part of catalog-service's product representation that order-service cares about. */
public record ProductInfo(Long id, String sku, String name, BigDecimal price, int stockQuantity) {
}
