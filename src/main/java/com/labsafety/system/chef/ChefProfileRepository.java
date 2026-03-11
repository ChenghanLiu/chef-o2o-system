package com.labsafety.system.chef;


import com.labsafety.system.cuisine.CuisineCategory;
import com.labsafety.system.cuisine.dto.ChefPublicCardView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface ChefProfileRepository extends JpaRepository<ChefProfile, Long> {

    Optional<ChefProfile> findByAccount_Id(Long accountId);

    boolean existsByAccount_Id(Long accountId);

    Page<ChefProfile> findByStatus(String status, Pageable pageable);

    @Query("select c from ChefProfile c join fetch c.account where c.status = :status")
    Page<ChefProfile> findByStatusFetchAccount(@Param("status") String status, Pageable pageable);

    @Query("select c from ChefProfile c join fetch c.account where c.account.id = :accountId")
    Optional<ChefProfile> findByAccountIdFetchAccount(@Param("accountId") Long accountId);

    @Query("select c from ChefProfile c join fetch c.account where c.id = :id")
    Optional<ChefProfile> findByIdFetchAccount(@Param("id") Long id);

    @Query("""
    select c from CuisineCategory c
    join ChefCuisine cc on c.id = cc.cuisineId  
    where cc.chefId = :chefId
    """)
    List<CuisineCategory> findCuisinesByChefId(@Param("chefId") Long chefId);

    List<ChefProfile> findAllByStatus(String status);

    // Admin listing needs account fields but must avoid lazy
    @Query("""
    select cp from ChefProfile cp
    join fetch cp.account a
    where cp.status = :status
""")
    Page<ChefProfile> findByStatusFetchAccountForAdmin(@Param("status") String status, Pageable pageable);

    @Query(value = """
    SELECT cp.* FROM chef_profiles cp
    JOIN chef_cuisines cc ON cp.id = cc.chef_id
    WHERE cc.cuisine_id = :cuisineId
      AND cp.status = 'APPROVED'
    ORDER BY cp.avg_rating DESC, cp.total_orders DESC, cp.id DESC
    """,
            countQuery = """
    SELECT COUNT(*) FROM chef_profiles cp
    JOIN chef_cuisines cc ON cp.id = cc.chef_id
    WHERE cc.cuisine_id = :cuisineId
      AND cp.status = 'APPROVED'
    """,
            nativeQuery = true)
    Page<ChefProfile> findApprovedChefsByCuisine(@Param("cuisineId") Long cuisineId, Pageable pageable);

    @Query(value = """
    SELECT
      cp.id AS chefId,
      cp.account_id AS accountId,
      a.username AS username,
      a.phone AS phone,
      cp.avatar_url AS avatarUrl,
      cp.bio AS bio,
      cp.service_area AS serviceArea,
      cp.work_time_desc AS workTimeDesc,
      cp.years_experience AS yearsExperience,
      cp.base_price_cents AS basePriceCents,
      cp.avg_rating AS avgRating,
      cp.total_orders AS totalOrders
    FROM chef_profiles cp
    JOIN chef_cuisines cc ON cp.id = cc.chef_id
    JOIN accounts a ON a.id = cp.account_id
    WHERE cc.cuisine_id = :cuisineId
      AND cp.status = 'APPROVED'
    ORDER BY cp.avg_rating DESC, cp.total_orders DESC, cp.id DESC
    """,
            countQuery = """
    SELECT COUNT(*)
    FROM chef_profiles cp
    JOIN chef_cuisines cc ON cp.id = cc.chef_id
    WHERE cc.cuisine_id = :cuisineId
      AND cp.status = 'APPROVED'
    """,
            nativeQuery = true)
    Page<ChefPublicCardView> findApprovedChefCardsByCuisine(@Param("cuisineId") Long cuisineId, Pageable pageable);


}