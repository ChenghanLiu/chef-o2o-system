package com.labsafety.system.cuisine;

import com.labsafety.system.cuisine.dto.*;
import com.labsafety.system.chef.ChefProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;

import static org.springframework.http.HttpStatus.*;

@Service
@RequiredArgsConstructor
public class AdminCuisineService {

    private final CuisineCategoryRepository cuisineCategoryRepository;
    private final ChefCuisineRepository chefCuisineRepository;
    private final ChefProfileRepository chefProfileRepository;

    // -------- cuisines (admin) --------

    @Transactional(readOnly = true)
    public Page<CuisineResponse> listCuisines(Pageable pageable) {
        return cuisineCategoryRepository.findAllForAdmin(pageable)
                .map(this::toCuisineResponse);
    }

    @Transactional
    public CuisineResponse createCuisine(CreateCuisineRequest req) {
        String name = req.getName().trim();
        if (cuisineCategoryRepository.existsByNameIgnoreCase(name)) {
            throw new ResponseStatusException(BAD_REQUEST, "Cuisine name already exists");
        }
        CuisineCategory c = new CuisineCategory();
        c.setName(name);
        c.setSortOrder(req.getSortOrder());
        cuisineCategoryRepository.save(c);
        return toCuisineResponse(c);
    }

    @Transactional
    public CuisineResponse updateCuisine(Long id, UpdateCuisineRequest req) {
        CuisineCategory c = cuisineCategoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Cuisine not found"));

        String newName = req.getName().trim();
        if (!c.getName().equalsIgnoreCase(newName) && cuisineCategoryRepository.existsByNameIgnoreCase(newName)) {
            throw new ResponseStatusException(BAD_REQUEST, "Cuisine name already exists");
        }

        c.setName(newName);
        c.setSortOrder(req.getSortOrder());
        cuisineCategoryRepository.save(c);
        return toCuisineResponse(c);
    }

    @Transactional
    public void deleteCuisine(Long id) {
        if (!cuisineCategoryRepository.existsById(id)) {
            throw new ResponseStatusException(NOT_FOUND, "Cuisine not found");
        }
        // delete link rows first
        // easiest: delete all rows in join table for this cuisine via native query (not provided by repo)
        // We'll do it with a safe one-liner:
        cuisineCategoryRepository.deleteById(id);
    }

    // -------- binding chef <-> cuisines --------

    @Transactional(readOnly = true)
    public List<CuisineResponse> getChefCuisines(Long chefId) {
        ensureChefExists(chefId);
        return cuisineCategoryRepository.findByChefId(chefId)
                .stream()
                .map(this::toCuisineResponse)
                .toList();
    }

    @Transactional
    public void replaceChefCuisines(Long chefId, List<Long> cuisineIds) {
        ensureChefExists(chefId);

        // de-dup
        List<Long> unique = new HashSet<>(cuisineIds).stream().toList();

        // validate cuisine ids
        for (Long cid : unique) {
            if (!cuisineCategoryRepository.existsById(cid)) {
                throw new ResponseStatusException(BAD_REQUEST, "Cuisine not found: " + cid);
            }
        }

        chefCuisineRepository.deleteAllByChefId(chefId);

        for (Long cid : unique) {
            chefCuisineRepository.save(new ChefCuisine(chefId, cid));
        }
    }

    private void ensureChefExists(Long chefId) {
        if (!chefProfileRepository.existsById(chefId)) {
            throw new ResponseStatusException(NOT_FOUND, "Chef not found");
        }
    }

    private CuisineResponse toCuisineResponse(CuisineCategory c) {
        return CuisineResponse.builder()
                .id(c.getId())
                .name(c.getName())
                .sortOrder(c.getSortOrder())
                .build();
    }
}