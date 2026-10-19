package com.newasteralnx.repository;

import com.newasteralnx.entity.Receipt;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReceiptRepository extends JpaRepository<Receipt, Long> {
    boolean existsByReceiptNumber(String receiptNumber);
}
