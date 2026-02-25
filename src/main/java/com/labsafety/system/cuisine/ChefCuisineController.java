package com.labsafety.system.cuisine;

import com.labsafety.system.cuisine.dto.CuisineResponse;
import com.labsafety.system.cuisine.dto.UpdateChefCuisinesRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chef/me")
@RequiredArgsConstructor
public class ChefCuisineController {

    private final ChefCuisineService chefCuisineService;
    private final com.labsafety.system.chef.ChefProfileRepository chefProfileRepository;

    @GetMapping("/cuisines")
    public List<CuisineResponse> myCuisines(Authentication auth) {
        // You likely use username/phone as subject in JWT. We map to chef profile via account.
        // If you already have a "me" helper elsewhere, tell me and I’ll match it.
        String subject = auth.getName();

        // minimal: find chef profile by account username/phone -> needs existing repo method
        // For now, if you already have a /api/chef/me/profile endpoint, you can reuse its way.
        throw new RuntimeException("Wire chefProfileId resolution using your existing 'me' mapping");
    }

    @PutMapping("/cuisines")
    public void replaceMyCuisines(@Valid @RequestBody UpdateChefCuisinesRequest req, Authentication auth) {
        throw new RuntimeException("Wire chefProfileId resolution using your existing 'me' mapping");
    }
}