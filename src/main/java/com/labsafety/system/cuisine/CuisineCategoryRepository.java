package com.labsafety.system.cuisine;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CuisineCategoryRepository extends JpaRepository<CuisineCategory, Long> {

    boolean existsByNameIgnoreCase(String name);

    @Query("select c from CuisineCategory c order by c.sortOrder asc, c.name asc")
    List<CuisineCategory> findAllForPublic();

    @Query("select c from CuisineCategory c order by c.sortOrder asc, c.name asc")
    Page<CuisineCategory> findAllForAdmin(Pageable pageable);

    @Query(value = """
        SELECT c.* FROM cuisine_categories c
        JOIN chef_cuisines cc ON c.id = cc.cuisine_id
        WHERE cc.chef_id = :chefId
        ORDER BY c.sort_order ASC, c.name ASC
        """, nativeQuery = true)
    List<CuisineCategory> findByChefId(Long chefId);
}