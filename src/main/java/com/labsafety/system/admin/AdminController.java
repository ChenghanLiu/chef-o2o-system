package com.labsafety.system.admin;

import com.labsafety.system.admin.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    // ---------------- USERS ----------------

    @GetMapping("/users")
    public Page<AdminUserResponse> users(@PageableDefault(size = 10) Pageable pageable) {
        return adminService.listUsers(pageable);
    }

    @PutMapping("/users/{id}/disable")
    public void disableUser(@PathVariable Long id) {
        adminService.disableUser(id);
    }

    @PutMapping("/users/{id}/enable")
    public void enableUser(@PathVariable Long id) {
        adminService.enableUser(id);
    }

    // ---------------- CHEFS ----------------

    @GetMapping("/chefs")
    public Page<AdminChefResponse> chefs(@RequestParam(defaultValue = "PENDING") String status,
                                         @PageableDefault(size = 10) Pageable pageable) {
        return adminService.listChefsByStatus(status.trim().toUpperCase(), pageable);
    }

    @PutMapping("/chefs/{id}/approve")
    public void approveChef(@PathVariable Long id) {
        adminService.approveChef(id);
    }

    @PutMapping("/chefs/{id}/reject")
    public void rejectChef(@PathVariable Long id) {
        adminService.rejectChef(id);
    }

    // ---------------- ORDERS ----------------

    @GetMapping("/orders")
    public Page<AdminOrderResponse> orders(@RequestParam(required = false) String status,
                                           @PageableDefault(size = 10) Pageable pageable) {
        System.out.println("ADMIN /orders status param = [" + status + "]");
        return adminService.listOrders(status, pageable);
    }

    @PutMapping("/orders/{id}/status")
    public void updateOrderStatus(@PathVariable Long id,
                                  @Valid @RequestBody UpdateOrderStatusRequest req) {
        adminService.updateOrderStatus(id, req.getStatus());
    }
}