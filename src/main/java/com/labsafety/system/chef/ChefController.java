package com.labsafety.system.chef;

import com.labsafety.system.chef.dto.ChefProfileResponse;
import com.labsafety.system.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/chefs")
public class ChefController {

    private final ChefProfileRepository chefProfileRepository;

    public ChefController(ChefProfileRepository chefProfileRepository) {
        this.chefProfileRepository = chefProfileRepository;
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