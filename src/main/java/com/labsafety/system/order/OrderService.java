package com.labsafety.system.order;

import com.labsafety.system.chef.ChefProfile;
import com.labsafety.system.chef.ChefProfileRepository;
import com.labsafety.system.order.dto.CreateOrderRequest;
import com.labsafety.system.order.dto.OrderActionRequest;
import com.labsafety.system.order.dto.OrderResponse;
import com.labsafety.system.serviceitem.ServiceItem;
import com.labsafety.system.serviceitem.ServiceItemService;
import com.labsafety.system.user.Role;
import com.labsafety.system.user.User;
import com.labsafety.system.user.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import com.labsafety.system.order.dto.OrderQuoteRequest;
import com.labsafety.system.order.dto.OrderQuoteResponse;

import java.util.EnumSet;

@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ChefProfileRepository chefProfileRepository;
    private final ServiceItemService serviceItemService;

    public OrderService(OrderRepository orderRepository,
                        UserRepository userRepository,
                        ChefProfileRepository chefProfileRepository,
                        ServiceItemService serviceItemService) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.chefProfileRepository = chefProfileRepository;
        this.serviceItemService = serviceItemService;
    }

    public OrderResponse create(String identifier, CreateOrderRequest req) {
        User me = findAccountByIdentifier(identifier);

        if (me.getRole() != Role.USER && me.getRole() != Role.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only USER can create orders");
        }

        // Validate chef exists and is APPROVED
        ChefProfile chef = chefProfileRepository.findById(req.getChefId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Chef not found"));

        if (!"APPROVED".equalsIgnoreCase(chef.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chef not available");
        }

        // Validate service item exists, ACTIVE, belongs to chef
        ServiceItem item = serviceItemService.mustFindActiveById(req.getServiceItemId());
        if (!item.getChefId().equals(req.getChefId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Service item does not belong to this chef");
        }

        // Price is server-side truth
        int totalPriceCents = item.getPriceCents();

        Order o = new Order();
        o.setUserAccountId(me.getId());
        o.setChefId(req.getChefId());
        o.setServiceItemId(req.getServiceItemId());
        o.setScheduledAt(req.getScheduledAt());
        o.setAddress(req.getAddress());
        o.setNote(req.getNote());
        o.setTotalPriceCents(totalPriceCents);
        o.setStatus(OrderStatus.PENDING);

        boolean conflict = orderRepository.existsByChefIdAndScheduledAtAndStatusIn(
                req.getChefId(), req.getScheduledAt(), BLOCKING_STATUSES
        );
        if (conflict) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Time slot is not available");
        }

        o = orderRepository.save(o);
        return toResponse(o);
    }

    @Transactional(readOnly = true)
    public Page<OrderResponse> myOrdersAsUser(String identifier, Pageable pageable) {
        User me = findAccountByIdentifier(identifier);
        return orderRepository.findByUserAccountId(me.getId(), pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<OrderResponse> myOrdersAsChef(String identifier, Pageable pageable) {
        User me = findAccountByIdentifier(identifier);
        if (me.getRole() != Role.CHEF) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only CHEF can view chef orders");
        }

        ChefProfile profile = chefProfileRepository.findByAccount_Id(me.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chef profile missing"));

        return orderRepository.findByChefId(profile.getId(), pageable).map(this::toResponse);
    }

    public OrderResponse actAsUser(String identifier, Long orderId, OrderActionRequest req) {
        User me = findAccountByIdentifier(identifier);

        Order order = orderRepository.findByIdAndUserAccountId(orderId, me.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

        String action = req.getAction().trim().toUpperCase();
        if (!"CANCEL".equals(action)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported action");
        }

        if (order.getStatus() == OrderStatus.IN_PROGRESS || order.getStatus() == OrderStatus.COMPLETED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot cancel after service started");
        }

        if (order.getStatus() != OrderStatus.CANCELLED) {
            order.setStatus(OrderStatus.CANCELLED);
            order = orderRepository.save(order);
        }

        return toResponse(order);
    }

    public OrderResponse actAsChef(String identifier, Long orderId, OrderActionRequest req) {
        User me = findAccountByIdentifier(identifier);
        if (me.getRole() != Role.CHEF) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only CHEF can operate orders");
        }

        ChefProfile profile = chefProfileRepository.findByAccount_Id(me.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chef profile missing"));

        Order order = orderRepository.findByIdAndChefId(orderId, profile.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

        String action = req.getAction().trim().toUpperCase();

        switch (action) {
            case "ACCEPT" -> {
                if (order.getStatus() != OrderStatus.PENDING) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only PENDING can be accepted");
                }
                order.setStatus(OrderStatus.ACCEPTED);
            }
            case "REJECT" -> {
                if (order.getStatus() != OrderStatus.PENDING) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only PENDING can be rejected");
                }
                order.setStatus(OrderStatus.REJECTED);
            }
            case "START" -> {
                if (order.getStatus() != OrderStatus.ACCEPTED) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only ACCEPTED can start");
                }
                order.setStatus(OrderStatus.IN_PROGRESS);
            }
            case "COMPLETE" -> {
                if (order.getStatus() != OrderStatus.IN_PROGRESS) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only IN_PROGRESS can complete");
                }
                order.setStatus(OrderStatus.COMPLETED);
            }
            default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported action");
        }

        order = orderRepository.save(order);
        return toResponse(order);
    }

    private User findAccountByIdentifier(String identifier) {
        return userRepository.findByUsername(identifier)
                .or(() -> userRepository.findByPhone(identifier))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized"));
    }

    private OrderResponse toResponse(Order o) {
        OrderResponse r = new OrderResponse();
        r.setId(o.getId());
        r.setUserAccountId(o.getUserAccountId());
        r.setChefId(o.getChefId());
        r.setServiceItemId(o.getServiceItemId());
        r.setScheduledAt(o.getScheduledAt());
        r.setAddress(o.getAddress());
        r.setNote(o.getNote());
        r.setStatus(o.getStatus());
        r.setTotalPriceCents(o.getTotalPriceCents());
        r.setCreatedAt(o.getCreatedAt());
        r.setUpdatedAt(o.getUpdatedAt());
        return r;
    }

    private static final EnumSet<OrderStatus> BLOCKING_STATUSES =
            EnumSet.of(OrderStatus.PENDING, OrderStatus.ACCEPTED, OrderStatus.IN_PROGRESS);
    @Transactional(readOnly = true)
    public OrderQuoteResponse quote(String identifier, OrderQuoteRequest req) {
        // Only logged-in users can quote (keeps it simple; you can open it later)
        User me = findAccountByIdentifier(identifier);

        // Chef must exist & approved
        ChefProfile chef = chefProfileRepository.findById(req.getChefId())
                .orElse(null);
        if (chef == null) {
            return OrderQuoteResponse.fail(req.getChefId(), req.getServiceItemId(), req.getScheduledAt(), "Chef not found");
        }
        if (!"APPROVED".equalsIgnoreCase(chef.getStatus())) {
            return OrderQuoteResponse.fail(req.getChefId(), req.getServiceItemId(), req.getScheduledAt(), "Chef not available");
        }

        // Service item must exist, active, and belong to chef
        ServiceItem item;
        try {
            item = serviceItemService.mustFindActiveById(req.getServiceItemId());
        } catch (Exception e) {
            return OrderQuoteResponse.fail(req.getChefId(), req.getServiceItemId(), req.getScheduledAt(), "Service item not available");
        }
        if (!item.getChefId().equals(req.getChefId())) {
            return OrderQuoteResponse.fail(req.getChefId(), req.getServiceItemId(), req.getScheduledAt(), "Service item does not belong to this chef");
        }

        // Time conflict check
        boolean conflict = orderRepository.existsByChefIdAndScheduledAtAndStatusIn(
                req.getChefId(), req.getScheduledAt(), BLOCKING_STATUSES
        );
        if (conflict) {
            return OrderQuoteResponse.fail(req.getChefId(), req.getServiceItemId(), req.getScheduledAt(), "Time slot is not available");
        }

        // Price is server truth
        return OrderQuoteResponse.ok(req.getChefId(), req.getServiceItemId(), req.getScheduledAt(), item.getPriceCents());
    }
}