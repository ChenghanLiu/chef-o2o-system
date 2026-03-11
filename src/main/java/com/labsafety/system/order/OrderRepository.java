package com.labsafety.system.order;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Page<Order> findByUserAccountId(Long userAccountId, Pageable pageable);

    Page<Order> findByChefId(Long chefId, Pageable pageable);

    Page<Order> findByStatus(OrderStatus status, Pageable pageable);

    Optional<Order> findByIdAndUserAccountId(Long id, Long userAccountId);

    Optional<Order> findByIdAndChefId(Long id, Long chefId);

    boolean existsByChefIdAndScheduledAtAndStatusIn(Long chefId, LocalDateTime scheduledAt, Collection<OrderStatus> statuses);

    List<Order> findByChefIdAndScheduledAtBetweenAndStatusIn(Long chefId,
                                                             LocalDateTime start,
                                                             LocalDateTime end,
                                                             Collection<OrderStatus> statuses);

    // NEW: batch fetch for many chefs (for list-page availability preview)
    List<Order> findByChefIdInAndScheduledAtBetweenAndStatusIn(Collection<Long> chefIds,
                                                               LocalDateTime start,
                                                               LocalDateTime end,
                                                               Collection<OrderStatus> statuses);

    Page<Order> findByChefIdAndStatus(Long chefId, OrderStatus status, Pageable pageable);

}