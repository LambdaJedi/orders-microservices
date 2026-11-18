package dev.lambdajedi.catalog.product;

import jakarta.validation.constraints.Min;

public record QuantityRequest(@Min(1) int quantity) {
}
