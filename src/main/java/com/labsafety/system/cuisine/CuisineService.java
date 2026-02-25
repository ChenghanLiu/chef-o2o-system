package com.labsafety.system.cuisine;

import com.labsafety.system.cuisine.dto.ChefPublicCardResponse;
import com.labsafety.system.cuisine.dto.ChefPublicCardView;
import com.labsafety.system.cuisine.dto.CuisineResponse;
import com.labsafety.system.chef.ChefProfile;
import com.labsafety.system.chef.ChefProfileRepository;
import com.labsafety.system.chef.dto.ChefProfileResponse; // if you already have a response DTO; otherwise see note below
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import com.labsafety.system.cuisine.dto.ChefPublicCardResponse;
import com.labsafety.system.cuisine.dto.ChefPublicCardView;

import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
public class CuisineService {

    private final CuisineCategoryRepository cuisineCategoryRepository;
    private final ChefProfileRepository chefProfileRepository;

    @Transactional(readOnly = true)
    public List<CuisineResponse> listPublicCuisines() {
        return cuisineCategoryRepository.findAllForPublic()
                .stream()
                .map(c -> CuisineResponse.builder()
                        .id(c.getId())
                        .name(c.getName())
                        .sortOrder(c.getSortOrder())
                        .build())
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<ChefPublicCardResponse> listApprovedChefsByCuisine(Long cuisineId, Pageable pageable) {
        if (!cuisineCategoryRepository.existsById(cuisineId)) {
            throw new ResponseStatusException(NOT_FOUND, "Cuisine not found");
        }

        Page<ChefPublicCardView> page = chefProfileRepository.findApprovedChefCardsByCuisine(cuisineId, pageable);
        return page.map(v -> ChefPublicCardResponse.builder()
                .chefId(v.getChefId())
                .accountId(v.getAccountId())
                .username(v.getUsername())
                .phone(v.getPhone())
                .avatarUrl(v.getAvatarUrl())
                .bio(v.getBio())
                .serviceArea(v.getServiceArea())
                .workTimeDesc(v.getWorkTimeDesc())
                .yearsExperience(v.getYearsExperience())
                .basePriceCents(v.getBasePriceCents())
                .avgRating(v.getAvgRating() == null ? 0.0 : v.getAvgRating())
                .totalOrders(v.getTotalOrders() == null ? 0 : v.getTotalOrders())
                .build());
    }
}