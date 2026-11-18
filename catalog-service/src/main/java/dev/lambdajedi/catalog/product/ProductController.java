package dev.lambdajedi.catalog.product;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @GetMapping
    public List<ProductResponse> list() {
        return service.findAll().stream().map(ProductResponse::from).toList();
    }

    @GetMapping("/{id}")
    public ProductResponse get(@PathVariable Long id) {
        return ProductResponse.from(service.get(id));
    }

    @PostMapping
    public ResponseEntity<ProductResponse> create(@Valid @RequestBody ProductRequest request) {
        Product created = service.create(request);
        return ResponseEntity
                .created(URI.create("/api/products/" + created.getId()))
                .body(ProductResponse.from(created));
    }

    @PutMapping("/{id}")
    public ProductResponse update(@PathVariable Long id, @Valid @RequestBody ProductRequest request) {
        return ProductResponse.from(service.update(id, request));
    }

    @PostMapping("/{id}/reserve")
    public ProductResponse reserve(@PathVariable Long id, @Valid @RequestBody QuantityRequest request) {
        return ProductResponse.from(service.reserve(id, request.quantity()));
    }

    @PostMapping("/{id}/release")
    public ProductResponse release(@PathVariable Long id, @Valid @RequestBody QuantityRequest request) {
        return ProductResponse.from(service.release(id, request.quantity()));
    }
}
