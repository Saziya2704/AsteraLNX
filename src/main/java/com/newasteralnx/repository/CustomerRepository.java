package com.newasteralnx.repository;

import com.newasteralnx.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CustomerRepository extends JpaRepository<Customer, Long>, JpaSpecificationExecutor<Customer> {

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByGstNumberIgnoreCase(String gstNumber);

    boolean existsByCustomerCode(String customerCode);

    long countByDeletedFalse();
}
