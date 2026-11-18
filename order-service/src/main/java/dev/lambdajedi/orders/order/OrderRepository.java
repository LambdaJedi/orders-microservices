package dev.lambdajedi.orders.order;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<CustomerOrder, Long> {

    /** Fetches items in the same query, avoiding the N+1 select problem when listing orders. */
    @EntityGraph(attributePaths = "items")
    List<CustomerOrder> findAllByOrderByCreatedAtDesc();

    @Override
    @EntityGraph(attributePaths = "items")
    Optional<CustomerOrder> findById(Long id);
}
