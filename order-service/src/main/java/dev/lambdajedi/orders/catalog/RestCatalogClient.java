package dev.lambdajedi.orders.catalog;

import dev.lambdajedi.orders.order.CatalogUnavailableException;
import dev.lambdajedi.orders.order.OutOfStockException;
import dev.lambdajedi.orders.order.ProductUnavailableException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Map;

@Component
public class RestCatalogClient implements CatalogClient {

    private final RestClient restClient;

    public RestCatalogClient(RestClient catalogRestClient) {
        this.restClient = catalogRestClient;
    }

    @Override
    public ProductInfo getProduct(long productId) {
        try {
            return restClient.get()
                    .uri("/api/products/{id}", productId)
                    .retrieve()
                    .body(ProductInfo.class);
        } catch (HttpClientErrorException.NotFound ex) {
            throw new ProductUnavailableException(productId);
        } catch (RestClientException ex) {
            throw new CatalogUnavailableException(ex);
        }
    }

    @Override
    public void reserve(long productId, int quantity) {
        try {
            restClient.post()
                    .uri("/api/products/{id}/reserve", productId)
                    .body(Map.of("quantity", quantity))
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpClientErrorException.NotFound ex) {
            throw new ProductUnavailableException(productId);
        } catch (HttpClientErrorException.Conflict ex) {
            throw new OutOfStockException(productId);
        } catch (RestClientException ex) {
            throw new CatalogUnavailableException(ex);
        }
    }

    @Override
    public void release(long productId, int quantity) {
        try {
            restClient.post()
                    .uri("/api/products/{id}/release", productId)
                    .body(Map.of("quantity", quantity))
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException ex) {
            throw new CatalogUnavailableException(ex);
        }
    }
}
