package com.labsafety.system.review;

import com.labsafety.system.order.Order;
import com.labsafety.system.order.OrderRepository;
import com.labsafety.system.order.OrderStatus;
import com.labsafety.system.review.dto.ChefReviewResponse;
import com.labsafety.system.review.dto.CreateReviewRequest;
import com.labsafety.system.review.dto.ReviewResponse;
import com.labsafety.system.user.Role;
import com.labsafety.system.user.User;
import com.labsafety.system.user.UserRepository;
import com.labsafety.system.chef.ChefProfile;
import com.labsafety.system.chef.ChefProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ChefProfileRepository chefProfileRepository;

    @Transactional
    public void createReview(String username, CreateReviewRequest request) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getUserAccountId().equals(user.getId())) {
            throw new RuntimeException("You cannot review this order");
        }

        if (order.getStatus() != OrderStatus.COMPLETED) {
            throw new RuntimeException("Order not completed yet");
        }

        if (reviewRepository.existsByOrderId(order.getId())) {
            throw new RuntimeException("Order already reviewed");
        }

        ChefProfile chef = chefProfileRepository.findById(order.getChefId())
                .orElseThrow(() -> new RuntimeException("Chef not found"));

        Review review = Review.builder()
                .orderId(order.getId())
                .user(user)
                .chef(chef)
                .rating(request.getRating())
                .comment(request.getComment())
                .createdAt(Instant.now())
                .build();

        reviewRepository.save(review);

        // Recalculate rating
        Double avg = reviewRepository.calculateAverageRating(chef.getId());
        Long count = reviewRepository.countReviews(chef.getId());

        chef.setAvgRating(avg == null ? 0.0 : avg);
        chef.setTotalOrders(count.intValue());

        chefProfileRepository.save(chef);
    }

    @Transactional(readOnly = true)
    public List<ChefReviewResponse> myReviewsAsChef(String identifier) {

        User me = userRepository.findByUsername(identifier)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (me.getRole() != Role.CHEF) {
            throw new RuntimeException("Only CHEF can view chef reviews");
        }

        ChefProfile profile = chefProfileRepository.findByAccount_Id(me.getId())
                .orElseThrow(() -> new RuntimeException("Chef profile missing"));

        List<Review> reviews = reviewRepository.findByChefWithUser(profile.getId());

        return reviews.stream().map(r -> {
            ChefReviewResponse dto = new ChefReviewResponse();
            dto.setId(r.getId());
            dto.setRating(r.getRating());
            dto.setComment(r.getComment());
            dto.setUsername(r.getUser().getUsername());
            dto.setCreatedAt(r.getCreatedAt());
            return dto;
        }).toList();
    }

    public List<ReviewResponse> getChefReviews(Long chefId) {
        return reviewRepository.findByChefWithUser(chefId)
                .stream()
                .map(r -> ReviewResponse.builder()
                        .id(r.getId())
                        .orderId(r.getOrderId())
                        .username(r.getUser().getUsername())
                        .rating(r.getRating())
                        .comment(r.getComment())
                        .createdAt(r.getCreatedAt())
                        .build())
                .toList();
    }
    @Transactional
    public void deleteMyReview(String identifier, Long reviewId) {
        User me = userRepository.findByUsername(identifier)
                .or(() -> userRepository.findByPhone(identifier))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized"));

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Review not found"));

        // ✅ ownership check
        if (review.getUser() == null || review.getUser().getId() == null) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Review user missing");
        }
        if (!me.getId().equals(review.getUser().getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your review");
        }

        // ✅ optional: keep chef id before delete if you want to recompute stats
        Long chefProfileId = (review.getChef() != null ? review.getChef().getId() : null);

        reviewRepository.delete(review);


    }


}