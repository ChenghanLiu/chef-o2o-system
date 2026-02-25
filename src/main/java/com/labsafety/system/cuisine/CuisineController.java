package com.labsafety.system.cuisine;

import com.labsafety.system.cuisine.dto.ChefPublicCardResponse;
import com.labsafety.system.cuisine.dto.CuisineResponse;
import com.labsafety.system.chef.ChefProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CuisineController {

    private final CuisineService cuisineService;
    private final CuisineCategoryRepository cuisineCategoryRepository;

    @GetMapping("/cuisines")
    public List<CuisineResponse> listCuisines() {
        return cuisineService.listPublicCuisines();
    }

    @GetMapping("/cuisines/{cuisineId}/chefs")
    public Page<ChefPublicCardResponse> listChefsByCuisine(@PathVariable Long cuisineId,
                                                           @PageableDefault(size = 10) Pageable pageable) {
        return cuisineService.listApprovedChefsByCuisine(cuisineId, pageable);
    }

    @GetMapping("/chefs/{chefId}/cuisines")
    public List<CuisineResponse> chefCuisines(@PathVariable Long chefId) {
        return cuisineCategoryRepository.findByChefId(chefId)
                .stream()
                .map(c -> CuisineResponse.builder().id(c.getId()).name(c.getName()).sortOrder(c.getSortOrder()).build())
                .toList();
    }
}