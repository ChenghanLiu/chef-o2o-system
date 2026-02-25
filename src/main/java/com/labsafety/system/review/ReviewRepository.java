package com.labsafety.system.review;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByChef_IdOrderByCreatedAtDesc(Long chefId);

    boolean existsByOrderId(Long orderId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.chef.id = :chefId")
    Double calculateAverageRating(Long chefId);

    @Query("SELECT COUNT(r) FROM Review r WHERE r.chef.id = :chefId")
    Long countReviews(Long chefId);

    @Query("""
    SELECT r FROM Review r
    JOIN FETCH r.user
    WHERE r.chef.id = :chefId
    ORDER BY r.createdAt DESC
""")
    List<Review> findByChefWithUser(Long chefId);
}