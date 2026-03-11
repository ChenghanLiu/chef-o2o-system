package com.labsafety.system.chef;

import com.labsafety.system.chef.dto.ChefProfileResponse;
import com.labsafety.system.cuisine.CuisineCategory;
import com.labsafety.system.user.Role;
import com.labsafety.system.user.User;
import com.labsafety.system.user.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;



import java.util.List;



import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/chefs")
public class ChefController {

    private final ChefProfileRepository chefProfileRepository;
    private final UserRepository userRepository;
    private final JdbcTemplate jdbcTemplate;

    public ChefController(ChefProfileRepository chefProfileRepository,
                          UserRepository userRepository, JdbcTemplate jdbcTemplate) {
        this.chefProfileRepository = chefProfileRepository;
        this.userRepository = userRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * User-side browse: only show APPROVED chefs
     * GET /api/chefs?page=0&size=10
     */
    @GetMapping
    public Page<ChefProfileResponse> listApprovedChefs(
            @PageableDefault(size = 10) Pageable pageable) {

        return chefProfileRepository
                .findByStatusFetchAccount("APPROVED", pageable)
                .map(ChefController::toResponse);
    }

    @GetMapping("/{chefProfileId}")
    public ChefProfileResponse getApprovedChef(@PathVariable Long chefProfileId) {

        ChefProfile chef = chefProfileRepository
                .findByIdFetchAccount(chefProfileId)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Chef not found"));

        if (!"APPROVED".equalsIgnoreCase(chef.getStatus())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Chef not found");
        }

        return toResponse(chef);
    }

    /**
     * Chef-side: get my own chef profile
     * GET /api/chefs/me
     */
    @GetMapping("/me")
    @PreAuthorize("hasRole('CHEF')")
    public ChefProfileResponse getMyChefProfile(Principal principal) {
        if (principal == null || principal.getName() == null || principal.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        String identifier = principal.getName();

        User me = userRepository.findByUsername(identifier)
                .or(() -> userRepository.findByPhone(identifier))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized"));

        if (me.getRole() != Role.CHEF) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only CHEF can access");
        }

        ChefProfile chef = chefProfileRepository
                .findByAccountIdFetchAccount(me.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chef profile missing"));
        // 这里不强制 APPROVED：厨师自己可以看到自己的状态（PENDING/REJECTED/APPROVED）
        return toResponse(chef);
    }
    @GetMapping("/me/cuisines")
    @PreAuthorize("hasRole('CHEF')")
    public List<CuisineCategory> myCuisines(Principal principal) {
        User me = resolveUser(principal);

        ChefProfile chef = chefProfileRepository.findByAccount_Id(me.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chef profile missing"));

        return chefProfileRepository.findCuisinesByChefId(chef.getId());
    }

    @PutMapping("/me/cuisines")
    @PreAuthorize("hasRole('CHEF')")
    public void updateMyCuisines(@RequestBody List<Long> cuisineIds,
                                 Principal principal) {

        User me = resolveUser(principal);

        ChefProfile chef = chefProfileRepository.findByAccount_Id(me.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chef profile missing"));

        // 先删旧的
        jdbcTemplate.update("delete from chef_cuisines where chef_id = ?", chef.getId());

        // 再插新的
        for (Long cuisineId : cuisineIds) {
            jdbcTemplate.update(
                    "insert into chef_cuisines (chef_id, cuisine_id) values (?, ?)",
                    chef.getId(), cuisineId
            );
        }
    }

    private User resolveUser(Principal principal) {
        if (principal == null || principal.getName() == null || principal.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        String identifier = principal.getName();

        return userRepository.findByUsername(identifier)
                .or(() -> userRepository.findByPhone(identifier))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized"));
    }

    private static ChefProfileResponse toResponse(ChefProfile chef) {
        ChefProfileResponse r = new ChefProfileResponse();
        r.setId(chef.getId());

        User account = chef.getAccount();
        if (account != null) {
            r.setAccountId(account.getId());
            r.setUsername(account.getUsername());
            r.setPhone(account.getPhone());
        }

        r.setAvatarUrl(chef.getAvatarUrl());
        r.setBio(chef.getBio());
        r.setServiceArea(chef.getServiceArea());
        r.setWorkTimeDesc(chef.getWorkTimeDesc());
        r.setYearsExperience(chef.getYearsExperience());
        r.setBasePriceCents(chef.getBasePriceCents());
        r.setAvgRating(chef.getAvgRating());
        r.setTotalOrders(chef.getTotalOrders());
        r.setStatus(chef.getStatus());
        return r;
    }
}