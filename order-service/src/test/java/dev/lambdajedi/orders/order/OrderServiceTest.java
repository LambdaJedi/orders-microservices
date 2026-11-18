package dev.lambdajedi.orders.order;

import dev.lambdajedi.orders.catalog.CatalogClient;
import dev.lambdajedi.orders.catalog.ProductInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.doNothing;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository repository;

    @Mock
    private CatalogClient catalog;

    @InjectMocks
    private OrderService service;

    private final ProductInfo keyboard = new ProductInfo(1L, "KB-001", "Keyboard", new BigDecimal("10.00"), 100);
    private final ProductInfo mouse = new ProductInfo(2L, "MS-001", "Mouse", new BigDecimal("5.50"), 100);

    @Test
    void placeOrderReservesStockAndCalculatesTotal() {
        when(catalog.getProduct(1L)).thenReturn(keyboard);
        when(catalog.getProduct(2L)).thenReturn(mouse);
        when(repository.save(any(CustomerOrder.class))).thenAnswer(inv -> inv.getArgument(0));

        OrderResponse response = service.placeOrder(new PlaceOrderRequest(
                "buyer@example.com",
                List.of(new PlaceOrderRequest.Line(1L, 2), new PlaceOrderRequest.Line(2L, 1))));

        assertThat(response.total()).isEqualByComparingTo("25.50");
        assertThat(response.items()).hasSize(2);
        assertThat(response.status()).isEqualTo(OrderStatus.CONFIRMED);
        verify(catalog).reserve(1L, 2);
        verify(catalog).reserve(2L, 1);
    }

    @Test
    void releasesEarlierReservationsWhenALaterOneFails() {
        when(catalog.getProduct(1L)).thenReturn(keyboard);
        when(catalog.getProduct(2L)).thenReturn(mouse);
        doNothing().when(catalog).reserve(1L, 2);
        doThrow(new OutOfStockException(2L)).when(catalog).reserve(2L, 5);

        assertThrows(OutOfStockException.class, () -> service.placeOrder(new PlaceOrderRequest(
                "buyer@example.com",
                List.of(new PlaceOrderRequest.Line(1L, 2), new PlaceOrderRequest.Line(2L, 5)))));

        verify(catalog).release(1L, 2);
        verify(repository, never()).save(any());
    }

    @Test
    void unknownProductFailsBeforeAnyStockIsReserved() {
        when(catalog.getProduct(99L)).thenThrow(new ProductUnavailableException(99L));

        assertThrows(ProductUnavailableException.class, () -> service.placeOrder(new PlaceOrderRequest(
                "buyer@example.com", List.of(new PlaceOrderRequest.Line(99L, 1)))));

        verify(catalog, never()).reserve(any(Long.class), any(Integer.class));
    }

    @Test
    void releasesStockWhenSavingTheOrderFails() {
        when(catalog.getProduct(1L)).thenReturn(keyboard);
        when(repository.save(any(CustomerOrder.class))).thenThrow(new IllegalStateException("db down"));

        assertThrows(IllegalStateException.class, () -> service.placeOrder(new PlaceOrderRequest(
                "buyer@example.com", List.of(new PlaceOrderRequest.Line(1L, 3)))));

        verify(catalog).release(1L, 3);
    }
}
