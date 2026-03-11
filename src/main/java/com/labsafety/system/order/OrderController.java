package com.labsafety.system.order;

import com.labsafety.system.chef.ChefProfile;
import com.labsafety.system.chef.ChefProfileRepository;
import com.labsafety.system.order.dto.CreateOrderRequest;
import com.labsafety.system.order.dto.OrderActionRequest;
import com.labsafety.system.order.dto.OrderResponse;
import com.labsafety.system.user.User;
import com.labsafety.system.user.UserRepository;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.labsafety.system.order.dto.OrderQuoteRequest;
import com.labsafety.system.order.dto.OrderQuoteResponse;
import org.springframework.security.core.Authentication;

import java.security.Principal;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final UserRepository userRepository;
    private final ChefProfileRepository chefProfileRepository;

    public OrderController(OrderService orderService, UserRepository userRepository, ChefProfileRepository chefProfileRepository) {
        this.orderService = orderService;
        this.userRepository = userRepository;
        this.chefProfileRepository = chefProfileRepository;
    }

    // USER creates order
    @PostMapping
    public ResponseEntity<OrderResponse> create(Principal principal, @Valid @RequestBody CreateOrderRequest req) {
        return ResponseEntity.ok(orderService.create(principal.getName(), req));
    }

    // USER lists my orders
    @GetMapping("/me")
    public Page<OrderResponse> myOrdersAsUser(Principal principal,
                                              @PageableDefault(size = 10) Pageable pageable) {
        return orderService.myOrdersAsUser(principal.getName(), pageable);
    }

    // CHEF lists my orders
    @GetMapping("/chef/me")
    public Page<OrderResponse> myOrdersAsChef(@RequestParam(required = false) OrderStatus status,
                                              Pageable pageable,
                                              Authentication auth) {
        return orderService.myOrdersAsChef(auth.getName(), status, pageable);
    }



    // USER action: CANCEL
    @PostMapping("/{orderId}/me/action")
    public ResponseEntity<OrderResponse> actAsUser(Principal principal,
                                                   @PathVariable Long orderId,
                                                   @Valid @RequestBody OrderActionRequest req) {
        return ResponseEntity.ok(orderService.actAsUser(principal.getName(), orderId, req));
    }

    // CHEF actions: ACCEPT/REJECT/START/COMPLETE
    @PostMapping("/{orderId}/chef/action")
    public ResponseEntity<OrderResponse> actAsChef(Principal principal,
                                                   @PathVariable Long orderId,
                                                   @Valid @RequestBody OrderActionRequest req) {
        return ResponseEntity.ok(orderService.actAsChef(principal.getName(), orderId, req));
    }

    @GetMapping("/chef/{orderId}")
    @PreAuthorize("hasRole('CHEF')")
    public Order getChefOrderDetail(@PathVariable Long orderId,
                                    Principal principal) {

        User me = userRepository.findByUsername(principal.getName())
                .or(() -> userRepository.findByPhone(principal.getName()))
                .orElseThrow(() -> new RuntimeException("Unauthorized"));

        ChefProfile chef = chefProfileRepository.findByAccount_Id(me.getId())
                .orElseThrow(() -> new RuntimeException("Chef profile not found"));

        return orderService.getChefOrderDetail(orderId, chef.getId());
    }

    @PostMapping("/quote")
    public ResponseEntity<OrderQuoteResponse> quote(Principal principal,
                                                    @Valid @RequestBody OrderQuoteRequest req) {
        return ResponseEntity.ok(orderService.quote(principal.getName(), req));
    }
}