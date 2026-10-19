package com.newasteralnx.repository;

import com.newasteralnx.entity.Commodity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;

public interface CommodityRepository extends JpaRepository<Commodity, Long> {
    boolean existsByCommodityCode(String commodityCode);
    Page<Commodity> findByWarehouseId(Long warehouseId, Pageable pageable);
    boolean existsByWarehouseId(Long warehouseId);

    long countByQuantityLessThan(BigDecimal quantity);

    List<Commodity> findTop10ByQuantityLessThanOrderByQuantityAsc(BigDecimal quantity);
}
