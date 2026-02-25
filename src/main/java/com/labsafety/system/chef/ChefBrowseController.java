package com.labsafety.system.chef;

import com.labsafety.system.chef.dto.ChefCardResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chefs")
public class ChefBrowseController {

    private final ChefBrowseService chefBrowseService;

    public ChefBrowseController(ChefBrowseService chefBrowseService) {
        this.chefBrowseService = chefBrowseService;
    }

    /**
     * Front-end chef list cards:
     * - minPriceCents + serviceItemCount
     * - nextSlots preview
     * - bookable flag
     */
    @GetMapping("/cards")
    public Page<ChefCardResponse> cards(@PageableDefault(size = 10) Pageable pageable,
                                        @RequestParam(defaultValue = "7") int days,
                                        @RequestParam(defaultValue = "3") int previewSlots) {
        return chefBrowseService.listChefCards(pageable, days, previewSlots);
    }
}