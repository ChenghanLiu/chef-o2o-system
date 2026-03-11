package com.labsafety.system.serviceitem;

import com.labsafety.system.chef.ChefProfile;
import com.labsafety.system.chef.ChefProfileRepository;
import com.labsafety.system.user.Role;
import com.labsafety.system.user.User;
import com.labsafety.system.user.UserRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;

@Validated
@RestController
@RequestMapping("/api/service-items")
public class ServiceItemController {

    private final ServiceItemRepository serviceItemRepository;
    private final UserRepository userRepository;
    private final ChefProfileRepository chefProfileRepository;

    public ServiceItemController(ServiceItemRepository serviceItemRepository,
                                 UserRepository userRepository,
                                 ChefProfileRepository chefProfileRepository) {
        this.serviceItemRepository = serviceItemRepository;
        this.userRepository = userRepository;
        this.chefProfileRepository = chefProfileRepository;
    }

    // USER-side: list ACTIVE items (optionally filter by chefId)
    @GetMapping
    public Page<ServiceItem> list(@RequestParam(required = false) Long chefId,
                                  @PageableDefault(size = 10) Pageable pageable) {
        if (chefId != null) {
            return serviceItemRepository.findByChefIdAndStatus(chefId, ServiceItemStatus.ACTIVE, pageable);
        }
        return serviceItemRepository.findByStatus(ServiceItemStatus.ACTIVE, pageable);
    }

    // CHEF-side: list my items (all statuses)
    @GetMapping("/my")
    public Page<ServiceItem> myItems(Principal principal,
                                     @PageableDefault(size = 10) Pageable pageable) {
        ChefProfile profile = resolveMyChefProfile(principal);
        return serviceItemRepository.findByChefId(profile.getId(), pageable);
    }

    // CHEF-side: create an item for myself
    @PostMapping("/my")
    public ResponseEntity<ServiceItem> createMyItem(Principal principal,
                                                    @Valid @RequestBody CreateServiceItemRequest req) {
        ChefProfile profile = resolveMyChefProfile(principal);

        // Only APPROVED chefs can publish ACTIVE items
        if (!"APPROVED".equalsIgnoreCase(profile.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chef not approved");
        }

        ServiceItem item = new ServiceItem();
        item.setChefId(profile.getId());
        item.setTitle(req.getTitle());
        item.setDurationMinutes(req.getDurationMinutes());
        item.setPriceCents(req.getPriceCents());
        item.setStatus(ServiceItemStatus.ACTIVE);

        return ResponseEntity.ok(serviceItemRepository.save(item));
    }

    // ✅ CHEF-side: update my service item
    @PutMapping("/my/{id}")
    public ResponseEntity<ServiceItem> updateMyItem(@PathVariable Long id,
                                                    Principal principal,
                                                    @Valid @RequestBody UpdateServiceItemRequest req) {
        ChefProfile profile = resolveMyChefProfile(principal);

        ServiceItem item = serviceItemRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Service item not found"));

        // ownership check
        if (!profile.getId().equals(item.getChefId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your service item");
        }

        // Optional: still require APPROVED to edit/publish (keeps platform safe)
        if (!"APPROVED".equalsIgnoreCase(profile.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chef not approved");
        }

        item.setTitle(req.getTitle());
        item.setDurationMinutes(req.getDurationMinutes());
        item.setPriceCents(req.getPriceCents());

        // 不让前端随便改 status（防漂移）；上下架用专用接口
        return ResponseEntity.ok(serviceItemRepository.save(item));
    }

    // ✅ CHEF-side: deactivate (down shelf)
    @PostMapping("/my/{id}/deactivate")
    public ResponseEntity<ServiceItem> deactivateMyItem(@PathVariable Long id, Principal principal) {
        ChefProfile profile = resolveMyChefProfile(principal);

        ServiceItem item = serviceItemRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Service item not found"));

        if (!profile.getId().equals(item.getChefId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your service item");
        }

        item.setStatus(ServiceItemStatus.INACTIVE);
        return ResponseEntity.ok(serviceItemRepository.save(item));
    }

    // ✅ CHEF-side: activate (up shelf again) —— 可选但强烈推荐
    @PostMapping("/my/{id}/activate")
    public ResponseEntity<ServiceItem> activateMyItem(@PathVariable Long id, Principal principal) {
        ChefProfile profile = resolveMyChefProfile(principal);

        // Only APPROVED chefs can publish ACTIVE items
        if (!"APPROVED".equalsIgnoreCase(profile.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chef not approved");
        }

        ServiceItem item = serviceItemRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Service item not found"));

        if (!profile.getId().equals(item.getChefId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your service item");
        }

        item.setStatus(ServiceItemStatus.ACTIVE);
        return ResponseEntity.ok(serviceItemRepository.save(item));
    }

    // -------- helpers --------

    private ChefProfile resolveMyChefProfile(Principal principal) {
        User me = findAccountByIdentifier(principal);
        if (me.getRole() != Role.CHEF) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only CHEF can access");
        }

        return chefProfileRepository.findByAccount_Id(me.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chef profile missing"));
    }

    private User findAccountByIdentifier(Principal principal) {
        if (principal == null || principal.getName() == null || principal.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        String identifier = principal.getName();
        return userRepository.findByUsername(identifier)
                .or(() -> userRepository.findByPhone(identifier))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized"));
    }

    // ---- request DTO as static inner class (so you don't need extra files) ----
    public static class CreateServiceItemRequest {
        @NotBlank
        private String title;

        @NotNull
        @Min(1)
        private Integer durationMinutes;

        @NotNull
        @Min(0)
        private Integer priceCents;

        public CreateServiceItemRequest() {}

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public Integer getDurationMinutes() { return durationMinutes; }
        public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }

        public Integer getPriceCents() { return priceCents; }
        public void setPriceCents(Integer priceCents) { this.priceCents = priceCents; }
    }

    public static class UpdateServiceItemRequest {
        @NotBlank
        private String title;

        @NotNull
        @Min(1)
        private Integer durationMinutes;

        @NotNull
        @Min(0)
        private Integer priceCents;

        public UpdateServiceItemRequest() {}

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public Integer getDurationMinutes() { return durationMinutes; }
        public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }

        public Integer getPriceCents() { return priceCents; }
        public void setPriceCents(Integer priceCents) { this.priceCents = priceCents; }
    }
}