package com.labsafety.system.order;

import com.labsafety.system.order.dto.CreateOrderRequest;
import com.labsafety.system.order.dto.OrderActionRequest;
import com.labsafety.system.order.dto.OrderResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.labsafety.system.order.dto.OrderQuoteRequest;
import com.labsafety.system.order.dto.OrderQuoteResponse;

import java.security.Principal;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
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
    public Page<OrderResponse> myOrdersAsChef(Principal principal,
                                              @PageableDefault(size = 10) Pageable pageable) {
        return orderService.myOrdersAsChef(principal.getName(), pageable);
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

    @PostMapping("/quote")
    public ResponseEntity<OrderQuoteResponse> quote(Principal principal,
                                                    @Valid @RequestBody OrderQuoteRequest req) {
        return ResponseEntity.ok(orderService.quote(principal.getName(), req));
    }
}