package com.newasteralnx.service;

import com.newasteralnx.dto.CustomerRequest;
import com.newasteralnx.dto.CustomerResponse;
import com.newasteralnx.entity.Customer;
import com.newasteralnx.entity.CustomerStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomerService {

    CustomerResponse createCustomer(CustomerRequest request);

    Page<CustomerResponse> getAllCustomers(
            String search,
            CustomerStatus status,
            Pageable pageable
    );

    CustomerResponse getCustomerById(Long id);

    CustomerResponse updateCustomer(
            Long id,
            CustomerRequest request
    );

    void deleteCustomer(Long id);

    void updateCustomerStatus(
            Long id,
            CustomerStatus status
    );

    Customer requireActiveForTransaction(Long customerId);
}