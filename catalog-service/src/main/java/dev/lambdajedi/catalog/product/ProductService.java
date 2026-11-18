package dev.lambdajedi.catalog.product;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Product create(ProductRequest request) {
        if (repository.existsBySku(request.sku())) {
            throw new DuplicateSkuException(request.sku());
        }
        return repository.save(new Product(
                request.sku(), request.name(), request.price(), request.stockQuantity()));
    }

    @Transactional(readOnly = true)
    public List<Product> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Product get(Long id) {
        return repository.findById(id).orElseThrow(() -> new ProductNotFoundException(id));
    }

    @Transactional
    public Product update(Long id, ProductRequest request) {
        Product product = get(id);
        product.update(request.name(), request.price(), request.stockQuantity());
        return product;
    }

    /** Takes stock. Dirty checking writes the change on commit; @Version guards concurrent updates. */
    @Transactional
    public Product reserve(Long id, int quantity) {
        Product product = get(id);
        product.reserve(quantity);
        return product;
    }

    /** Gives stock back, used by order-service to compensate when an order cannot be completed. */
    @Transactional
    public Product release(Long id, int quantity) {
        Product product = get(id);
        product.release(quantity);
        return product;
    }
}
