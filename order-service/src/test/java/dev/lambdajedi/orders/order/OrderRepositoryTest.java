package dev.lambdajedi.orders.order;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class OrderRepositoryTest {

    @Autowired
    private OrderRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void savesOrderWithItemsThroughCascade() {
        CustomerOrder order = new CustomerOrder("buyer@example.com");
        order.addItem(new OrderItem(1L, "KB-001", new BigDecimal("10.00"), 2));
        order.addItem(new OrderItem(2L, "MS-001", new BigDecimal("5.50"), 1));

        Long id = repository.saveAndFlush(order).getId();
        entityManager.clear();

        CustomerOrder loaded = repository.findById(id).orElseThrow();
        assertThat(loaded.getItems()).hasSize(2);
        assertThat(loaded.getTotal()).isEqualByComparingTo("25.50");
        assertThat(loaded.getStatus()).isEqualTo(OrderStatus.CONFIRMED);
    }

    @Test
    void listsOrdersNewestFirstWithItems() {
        CustomerOrder first = new CustomerOrder("first@example.com");
        first.addItem(new OrderItem(1L, "KB-001", new BigDecimal("10.00"), 1));
        repository.saveAndFlush(first);

        CustomerOrder second = new CustomerOrder("second@example.com");
        second.addItem(new OrderItem(2L, "MS-001", new BigDecimal("5.50"), 1));
        repository.saveAndFlush(second);
        entityManager.clear();

        assertThat(repository.findAllByOrderByCreatedAtDesc())
                .hasSize(2)
                .allSatisfy(o -> assertThat(o.getItems()).hasSize(1));
    }
}
