package com.labsafety.system.cuisine;

import com.labsafety.system.cuisine.dto.CuisineResponse;
import com.labsafety.system.cuisine.dto.UpdateChefCuisinesRequest;
import com.labsafety.system.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;

import static org.springframework.http.HttpStatus.*;

@Service
@RequiredArgsConstructor
public class ChefCuisineService {

    private final CuisineCategoryRepository cuisineCategoryRepository;
    private final ChefCuisineRepository chefCuisineRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<CuisineResponse> getMyCuisines(Long chefProfileId) {
        return cuisineCategoryRepository.findByChefId(chefProfileId)
                .stream()
                .map(c -> CuisineResponse.builder().id(c.getId()).name(c.getName()).sortOrder(c.getSortOrder()).build())
                .toList();
    }

    @Transactional
    public void replaceMyCuisines(Long chefProfileId, UpdateChefCuisinesRequest req) {
        List<Long> unique = new HashSet<>(req.getCuisineIds()).stream().toList();

        for (Long cid : unique) {
            if (!cuisineCategoryRepository.existsById(cid)) {
                throw new ResponseStatusException(BAD_REQUEST, "Cuisine not found: " + cid);
            }
        }

        chefCuisineRepository.deleteAllByChefId(chefProfileId);
        for (Long cid : unique) {
            chefCuisineRepository.save(new ChefCuisine(chefProfileId, cid));
        }
    }
}