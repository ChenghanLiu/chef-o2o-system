package com.labsafety.system.review;

import com.labsafety.system.review.dto.ChefReviewResponse;
import com.labsafety.system.review.dto.CreateReviewRequest;
import com.labsafety.system.review.dto.ReviewResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public void createReview(@Valid @RequestBody CreateReviewRequest request,
                             Authentication authentication) {
        reviewService.createReview(authentication.getName(), request);
    }

    // ✅ NEW: USER delete my own review
    @DeleteMapping("/{reviewId}")
    @PreAuthorize("hasRole('USER')")
    public void deleteMyReview(@PathVariable Long reviewId, Authentication authentication) {
        reviewService.deleteMyReview(authentication.getName(), reviewId);
    }

    @GetMapping("/chef/{chefId}")
    public List<ReviewResponse> getChefReviews(@PathVariable Long chefId) {
        return reviewService.getChefReviews(chefId);
    }

    @GetMapping("/chef/me")
    public List<ChefReviewResponse> myReviewsAsChef(Authentication auth) {
        return reviewService.myReviewsAsChef(auth.getName());
    }
}