package com.labsafety.system.admin;

import com.labsafety.system.admin.dto.*;
import com.labsafety.system.chef.ChefProfile;
import com.labsafety.system.chef.ChefProfileRepository;
import com.labsafety.system.order.Order;
import com.labsafety.system.order.OrderRepository;
import com.labsafety.system.order.OrderStatus;
import com.labsafety.system.user.User;
import com.labsafety.system.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.*;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final ChefProfileRepository chefProfileRepository;
    private final OrderRepository orderRepository;

    // ---------------- USERS ----------------

    @Transactional(readOnly = true)
    public Page<AdminUserResponse> listUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(this::toUserResponse);
    }

    @Transactional
    public void disableUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "User not found"));
        user.setStatus("DISABLED");
        userRepository.save(user);
    }

    @Transactional
    public void enableUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "User not found"));
        user.setStatus("ACTIVE");
        userRepository.save(user);
    }

    // ---------------- CHEFS ----------------

    @Transactional(readOnly = true)
    public Page<AdminChefResponse> listChefsByStatus(String status, Pageable pageable) {
        // Use fetch-account pageable query to avoid lazy in mapping username/phone
        Page<ChefProfile> page = chefProfileRepository.findByStatusFetchAccountForAdmin(status, pageable);
        return page.map(this::toChefResponse);
    }

    @Transactional
    public void approveChef(Long chefProfileId) {
        ChefProfile chef = chefProfileRepository.findById(chefProfileId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Chef not found"));
        chef.setStatus("APPROVED");
        chefProfileRepository.save(chef);
    }

    @Transactional
    public void rejectChef(Long chefProfileId) {
        ChefProfile chef = chefProfileRepository.findById(chefProfileId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Chef not found"));
        chef.setStatus("REJECTED");
        chefProfileRepository.save(chef);
    }

    // ---------------- ORDERS ----------------

    @Transactional(readOnly = true)
    public Page<AdminOrderResponse> listOrders(String status, Pageable pageable) {
        Page<Order> page;

        if (status == null || status.isBlank()) {
            page = orderRepository.findAll(pageable);
        } else {
            OrderStatus st = parseOrderStatus(status);
            page = orderRepository.findByStatus(st, pageable);
        }
        return page.map(this::toOrderResponse);

    }

    @Transactional
    public void updateOrderStatus(Long orderId, String status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Order not found"));

        OrderStatus newStatus = parseOrderStatus(status);
        order.setStatus(newStatus);
        orderRepository.save(order);
    }

    // ---------------- helpers ----------------

    private OrderStatus parseOrderStatus(String raw) {
        try {
            return OrderStatus.valueOf(raw.trim().toUpperCase());
        } catch (Exception e) {
            throw new ResponseStatusException(BAD_REQUEST, "Invalid order status: " + raw);
        }
    }

    private AdminUserResponse toUserResponse(User u) {
        // Role might be enum in your entity; String.valueOf is safe either way
        return AdminUserResponse.builder()
                .id(u.getId())
                .username(u.getUsername())
                .phone(u.getPhone())
                .role(String.valueOf(u.getRole()))
                .status(u.getStatus())
                .createdAt(u.getCreatedAt())
                .updatedAt(u.getUpdatedAt())
                .build();
    }

    private AdminChefResponse toChefResponse(ChefProfile cp) {
        String username = null;
        String phone = null;
        Long accountId = null;

        if (cp.getAccount() != null) {
            accountId = cp.getAccount().getId();
            username = cp.getAccount().getUsername();
            phone = cp.getAccount().getPhone();
        }

        return AdminChefResponse.builder()
                .chefProfileId(cp.getId())
                .accountId(accountId)
                .username(username)
                .phone(phone)
                .status(cp.getStatus())
                .avatarUrl(cp.getAvatarUrl())
                .bio(cp.getBio())
                .serviceArea(cp.getServiceArea())
                .workTimeDesc(cp.getWorkTimeDesc())
                .yearsExperience(cp.getYearsExperience())
                .avgRating(cp.getAvgRating())
                .totalOrders(cp.getTotalOrders())
                .createdAt(cp.getCreatedAt())
                .updatedAt(cp.getUpdatedAt())
                .build();
    }

    private AdminOrderResponse toOrderResponse(Order o) {
        return AdminOrderResponse.builder()
                .id(o.getId())
                .userAccountId(o.getUserAccountId())
                .chefId(o.getChefId())
                .serviceItemId(o.getServiceItemId())
                .scheduledAt(o.getScheduledAt())
                .address(o.getAddress())
                .note(o.getNote())
                .status(o.getStatus() == null ? null : o.getStatus().name())
                .totalPriceCents(o.getTotalPriceCents())
                .createdAt(o.getCreatedAt())
                .updatedAt(o.getUpdatedAt())
                .build();
    }


}