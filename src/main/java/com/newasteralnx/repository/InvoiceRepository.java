package com.newasteralnx.repository;

import com.newasteralnx.constant.InvoiceStatus;
import com.newasteralnx.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface InvoiceRepository extends JpaRepository<Invoice, Long>, JpaSpecificationExecutor<Invoice> {
    boolean existsByInvoiceNumber(String invoiceNumber);

    long countByStatus(InvoiceStatus status);
}
