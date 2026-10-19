package com.newasteralnx.repository;

import com.newasteralnx.entity.StockTransfer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockTransferRepository extends JpaRepository<StockTransfer, Long> {
    boolean existsByTransferNumber(String transferNumber);
    Page<StockTransfer> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
