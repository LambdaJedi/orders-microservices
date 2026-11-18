package dev.lambdajedi.orders.order;

public class OutOfStockException extends RuntimeException {

    public OutOfStockException(long productId) {
        super("Not enough stock for product " + productId);
    }
}
