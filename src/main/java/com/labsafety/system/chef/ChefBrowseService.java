package com.labsafety.system.chef;

import com.labsafety.system.chef.dto.ChefCardResponse;
import com.labsafety.system.order.Order;
import com.labsafety.system.order.OrderRepository;
import com.labsafety.system.order.OrderStatus;
import com.labsafety.system.serviceitem.ServiceItemRepository;
import com.labsafety.system.serviceitem.ServiceItemStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ChefBrowseService {

    private final ChefProfileRepository chefProfileRepository;
    private final ServiceItemRepository serviceItemRepository;
    private final OrderRepository orderRepository;

    private static final EnumSet<OrderStatus> BLOCKING_STATUSES =
            EnumSet.of(OrderStatus.PENDING, OrderStatus.ACCEPTED, OrderStatus.IN_PROGRESS);

    private static final List<LocalTime> DAILY_SLOTS = List.of(
            LocalTime.of(10, 0),
            LocalTime.of(14, 0),
            LocalTime.of(18, 0)
    );

    public ChefBrowseService(ChefProfileRepository chefProfileRepository,
                             ServiceItemRepository serviceItemRepository,
                             OrderRepository orderRepository) {
        this.chefProfileRepository = chefProfileRepository;
        this.serviceItemRepository = serviceItemRepository;
        this.orderRepository = orderRepository;
    }

    public Page<ChefCardResponse> listChefCards(Pageable pageable, int days, int previewSlots) {
        int safeDays = Math.max(1, Math.min(days, 30));
        int safePreview = Math.max(0, Math.min(previewSlots, 10));

        Page<ChefProfile> page = chefProfileRepository.findByStatusFetchAccount("APPROVED", pageable);

        List<ChefProfile> chefs = page.getContent();
        if (chefs.isEmpty()) {
            return page.map(this::toCardEmpty);
        }

        List<Long> chefIds = chefs.stream().map(ChefProfile::getId).toList();

        // 1) service item aggregates
        Map<Long, Agg> aggByChef = new HashMap<>();
        for (Object[] row : serviceItemRepository.aggMinPriceAndCountByChefIds(chefIds, ServiceItemStatus.ACTIVE)) {
            Long chefId = (Long) row[0];
            Integer minPrice = (Integer) row[1];
            Long cnt = (Long) row[2];
            aggByChef.put(chefId, new Agg(minPrice, cnt == null ? 0 : cnt));
        }

        // 2) blocked slots from orders
        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.plusDays(safeDays).atTime(23, 59, 59);

        List<Order> blockedOrders = orderRepository.findByChefIdInAndScheduledAtBetweenAndStatusIn(
                chefIds, start, end, BLOCKING_STATUSES
        );

        Map<Long, Set<LocalDateTime>> blockedByChef = new HashMap<>();
        for (Order o : blockedOrders) {
            blockedByChef.computeIfAbsent(o.getChefId(), k -> new HashSet<>()).add(o.getScheduledAt());
        }

        return page.map(cp -> toCard(cp, aggByChef.get(cp.getId()), blockedByChef.getOrDefault(cp.getId(), Set.of()), safeDays, safePreview));
    }

    private ChefCardResponse toCard(ChefProfile cp, Agg agg, Set<LocalDateTime> blocked, int days, int previewSlots) {
        ChefCardResponse r = new ChefCardResponse();
        r.setChefProfileId(cp.getId());

        if (cp.getAccount() != null) {
            r.setAccountId(cp.getAccount().getId());
            r.setUsername(cp.getAccount().getUsername());
            r.setPhone(cp.getAccount().getPhone());
        }

        r.setAvatarUrl(cp.getAvatarUrl());
        r.setBio(cp.getBio());
        r.setServiceArea(cp.getServiceArea());
        r.setWorkTimeDesc(cp.getWorkTimeDesc());
        r.setYearsExperience(cp.getYearsExperience());

        r.setAvgRating(cp.getAvgRating());
        r.setTotalOrders(cp.getTotalOrders());
        r.setStatus(cp.getStatus());

        if (agg != null) {
            r.setMinPriceCents(agg.minPriceCents());
            r.setServiceItemCount(agg.count());
        } else {
            r.setMinPriceCents(null);
            r.setServiceItemCount(0);
        }

        List<LocalDateTime> next = (previewSlots <= 0) ? List.of() : genNextSlots(days, previewSlots, blocked);
        r.setNextSlots(next);

        boolean hasItems = r.getServiceItemCount() > 0;
        boolean hasSlots = !next.isEmpty();
        r.setBookable("APPROVED".equalsIgnoreCase(r.getStatus()) && hasItems && hasSlots);

        return r;
    }

    private ChefCardResponse toCardEmpty(ChefProfile cp) {
        ChefCardResponse r = new ChefCardResponse();
        r.setChefProfileId(cp.getId());
        r.setStatus(cp.getStatus());
        r.setBookable(false);
        r.setNextSlots(List.of());
        r.setServiceItemCount(0);
        return r;
    }

    private List<LocalDateTime> genNextSlots(int days, int previewSlots, Set<LocalDateTime> blocked) {
        LocalDate today = LocalDate.now();
        List<LocalDateTime> out = new ArrayList<>(previewSlots);

        for (int d = 0; d < days && out.size() < previewSlots; d++) {
            LocalDate date = today.plusDays(d);
            for (LocalTime t : DAILY_SLOTS) {
                LocalDateTime slot = date.atTime(t);
                if (!blocked.contains(slot) && slot.isAfter(LocalDateTime.now())) {
                    out.add(slot);
                    if (out.size() >= previewSlots) break;
                }
            }
        }
        return out;
    }

    private record Agg(Integer minPriceCents, long count) {}
}