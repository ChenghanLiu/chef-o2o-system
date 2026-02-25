package com.labsafety.system.cuisine;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ChefCuisineRepository extends JpaRepository<ChefCuisine, ChefCuisineId> {

    @Query(value = "SELECT cuisine_id FROM chef_cuisines WHERE chef_id = :chefId", nativeQuery = true)
    List<Long> findCuisineIdsByChefId(Long chefId);

    @Modifying
    @Query(value = "DELETE FROM chef_cuisines WHERE chef_id = :chefId", nativeQuery = true)
    void deleteAllByChefId(Long chefId);

    @Query(value = "SELECT chef_id FROM chef_cuisines WHERE cuisine_id = :cuisineId", nativeQuery = true)
    List<Long> findChefIdsByCuisineId(Long cuisineId);
}