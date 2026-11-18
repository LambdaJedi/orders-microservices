package dev.lambdajedi.orders.catalog;

/** Port to catalog-service. An interface so the order logic can be unit tested without HTTP. */
public interface CatalogClient {

    ProductInfo getProduct(long productId);

    void reserve(long productId, int quantity);

    void release(long productId, int quantity);
}
