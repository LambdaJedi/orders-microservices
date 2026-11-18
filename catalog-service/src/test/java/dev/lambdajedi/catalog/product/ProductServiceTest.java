package dev.lambdajedi.catalog.product;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository repository;

    @InjectMocks
    private ProductService service;

    @Test
    void createRejectsDuplicateSku() {
        when(repository.existsBySku("KB-001")).thenReturn(true);

        assertThrows(DuplicateSkuException.class, () -> service.create(
                new ProductRequest("KB-001", "Keyboard", new BigDecimal("49.99"), 5)));
        verify(repository, never()).save(any());
    }

    @Test
    void reserveReducesStock() {
        Product product = new Product("KB-001", "Keyboard", new BigDecimal("49.99"), 10);
        when(repository.findById(1L)).thenReturn(Optional.of(product));

        Product result = service.reserve(1L, 4);

        assertThat(result.getStockQuantity()).isEqualTo(6);
    }

    @Test
    void reserveFailsWhenStockIsTooLow() {
        Product product = new Product("KB-001", "Keyboard", new BigDecimal("49.99"), 2);
        when(repository.findById(1L)).thenReturn(Optional.of(product));

        assertThrows(InsufficientStockException.class, () -> service.reserve(1L, 3));
        assertThat(product.getStockQuantity()).isEqualTo(2);
    }

    @Test
    void releaseGivesStockBack() {
        Product product = new Product("KB-001", "Keyboard", new BigDecimal("49.99"), 2);
        when(repository.findById(1L)).thenReturn(Optional.of(product));

        service.release(1L, 3);

        assertThat(product.getStockQuantity()).isEqualTo(5);
    }

    @Test
    void getFailsForUnknownProduct() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> service.get(99L));
    }
}
