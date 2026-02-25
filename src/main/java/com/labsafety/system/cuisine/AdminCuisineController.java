package com.labsafety.system.cuisine;

import com.labsafety.system.cuisine.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminCuisineController {

    private final AdminCuisineService adminCuisineService;

    @GetMapping("/cuisines")
    public Page<CuisineResponse> list(@PageableDefault(size = 10) Pageable pageable) {
        return adminCuisineService.listCuisines(pageable);
    }

    @PostMapping("/cuisines")
    public CuisineResponse create(@Valid @RequestBody CreateCuisineRequest req) {
        return adminCuisineService.createCuisine(req);
    }

    @PutMapping("/cuisines/{id}")
    public CuisineResponse update(@PathVariable Long id, @Valid @RequestBody UpdateCuisineRequest req) {
        return adminCuisineService.updateCuisine(id, req);
    }

    @DeleteMapping("/cuisines/{id}")
    public void delete(@PathVariable Long id) {
        adminCuisineService.deleteCuisine(id);
    }

    @GetMapping("/chefs/{chefId}/cuisines")
    public List<CuisineResponse> chefCuisines(@PathVariable Long chefId) {
        return adminCuisineService.getChefCuisines(chefId);
    }

    @PutMapping("/chefs/{chefId}/cuisines")
    public void replaceChefCuisines(@PathVariable Long chefId,
                                    @Valid @RequestBody UpdateChefCuisinesRequest req) {
        adminCuisineService.replaceChefCuisines(chefId, req.getCuisineIds());
    }
}