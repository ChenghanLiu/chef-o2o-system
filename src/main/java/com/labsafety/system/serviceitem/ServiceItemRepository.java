package com.labsafety.system.serviceitem;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface ServiceItemRepository extends JpaRepository<ServiceItem, Long> {

    Page<ServiceItem> findByStatus(ServiceItemStatus status, Pageable pageable);

    Page<ServiceItem> findByChefIdAndStatus(Long chefId, ServiceItemStatus status, Pageable pageable);

    Page<ServiceItem> findByChefId(Long chefId, Pageable pageable);

    /**
     * Returns rows: [chefId, minPriceCents, activeCount]
     */
    @Query("""
           select s.chefId as chefId, min(s.priceCents) as minPrice, count(s.id) as cnt
           from ServiceItem s
           where s.status = :status and s.chefId in :chefIds
           group by s.chefId
           """)
    List<Object[]> aggMinPriceAndCountByChefIds(@Param("chefIds") Collection<Long> chefIds,
                                                @Param("status") ServiceItemStatus status);
}