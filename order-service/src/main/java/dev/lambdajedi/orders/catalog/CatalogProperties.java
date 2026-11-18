package dev.lambdajedi.orders.catalog;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "catalog")
public record CatalogProperties(String baseUrl, int connectTimeoutMs, int readTimeoutMs) {
}
