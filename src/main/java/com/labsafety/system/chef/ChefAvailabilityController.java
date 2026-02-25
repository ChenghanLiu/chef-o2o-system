package com.labsafety.system.chef;

import com.labsafety.system.chef.dto.ChefAvailabilityResponse;
import com.labsafety.system.order.Order;
import com.labsafety.system.order.OrderRepository;
import com.labsafety.system.order.OrderStatus;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/chefs")
public class ChefAvailabilityController {

    private final ChefProfileRepository chefProfileRepository;
    private final OrderRepository orderRepository;

    private static final EnumSet<OrderStatus> BLOCKING_STATUSES =
            EnumSet.of(OrderStatus.PENDING, OrderStatus.ACCEPTED, OrderStatus.IN_PROGRESS);

    // Fixed daily slots (MVP). You can evolve this later using work_time_desc parsing.
    private static final List<LocalTime> DAILY_SLOTS = List.of(
            LocalTime.of(10, 0),
            LocalTime.of(14, 0),
            LocalTime.of(18, 0)
    );

    public ChefAvailabilityController(ChefProfileRepository chefProfileRepository,
                                      OrderRepository orderRepository) {
        this.chefProfileRepository = chefProfileRepository;
        this.orderRepository = orderRepository;
    }

    @GetMapping("/{chefId}/availability")
    public ChefAvailabilityResponse availability(@PathVariable Long chefId,
                                                 @RequestParam(defaultValue = "7") int days) {
        if (days < 1) days = 1;
        if (days > 30) days = 30;

        ChefProfile chef = chefProfileRepository.findById(chefId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Chef not found"));

        if (!"APPROVED".equalsIgnoreCase(chef.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chef not available");
        }

        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.plusDays(days).atTime(23, 59, 59);

        List<Order> existing = orderRepository.findByChefIdAndScheduledAtBetweenAndStatusIn(
                chefId, start, end, BLOCKING_STATUSES
        );

        Set<LocalDateTime> blocked = existing.stream()
                .map(Order::getScheduledAt)
                .collect(Collectors.toSet());

        List<LocalDateTime> slots = new ArrayList<>();
        for (int d = 0; d < days; d++) {
            LocalDate date = today.plusDays(d);
            for (LocalTime t : DAILY_SLOTS) {
                LocalDateTime slot = date.atTime(t);
                if (!blocked.contains(slot)) {
                    slots.add(slot);
                }
            }
        }

        return new ChefAvailabilityResponse(chefId, days, slots);
    }
}