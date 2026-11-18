package dev.lambdajedi.orders.order;

public class ProductUnavailableException extends RuntimeException {

    public ProductUnavailableException(long productId) {
        super("Product " + productId + " does not exist in the catalog");
    }
}
