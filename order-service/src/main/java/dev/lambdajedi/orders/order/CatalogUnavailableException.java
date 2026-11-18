package dev.lambdajedi.orders.order;

public class CatalogUnavailableException extends RuntimeException {

    public CatalogUnavailableException(Throwable cause) {
        super("Catalog service is unavailable or returned an unexpected response", cause);
    }
}
