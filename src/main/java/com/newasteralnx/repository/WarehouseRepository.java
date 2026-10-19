package com.newasteralnx.repository;

import com.newasteralnx.entity.Warehouse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface WarehouseRepository extends JpaRepository<Warehouse, Long>, JpaSpecificationExecutor<Warehouse> {
    boolean existsByWarehouseCode(String warehouseCode);
    boolean existsByNameIgnoreCaseAndDeletedFalse(String name);
    boolean existsByNameIgnoreCaseAndDeletedFalseAndIdNot(String name, Long id);
    Page<Warehouse> findByDeletedFalse(Pageable pageable);

    long countByDeletedFalse();
}
