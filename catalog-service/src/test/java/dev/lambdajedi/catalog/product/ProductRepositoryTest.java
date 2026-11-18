package dev.lambdajedi.catalog.product;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    private ProductRepository repository;

    @Test
    void findsProductBySku() {
        repository.saveAndFlush(new Product("KB-001", "Keyboard", new BigDecimal("49.99"), 10));

        assertThat(repository.findBySku("KB-001"))
                .isPresent()
                .get()
                .extracting(Product::getName)
                .isEqualTo("Keyboard");
        assertThat(repository.existsBySku("NOPE")).isFalse();
    }

    @Test
    void skuMustBeUnique() {
        repository.saveAndFlush(new Product("KB-001", "Keyboard", new BigDecimal("49.99"), 10));

        assertThrows(DataIntegrityViolationException.class,
                () -> repository.saveAndFlush(new Product("KB-001", "Other", new BigDecimal("1.00"), 1)));
    }

    @Test
    void versionIncrementsWhenStockChanges() {
        Product product = repository.saveAndFlush(new Product("KB-001", "Keyboard", new BigDecimal("49.99"), 10));
        long initialVersion = product.getVersion();

        product.reserve(3);
        repository.saveAndFlush(product);

        assertThat(product.getStockQuantity()).isEqualTo(7);
        assertThat(product.getVersion()).isGreaterThan(initialVersion);
    }
}
