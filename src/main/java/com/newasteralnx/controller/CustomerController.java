package com.newasteralnx.controller;

import com.newasteralnx.dto.CustomerRequest;
import com.newasteralnx.dto.CustomerResponse;
import com.newasteralnx.dto.CustomerStatusRequest;
import com.newasteralnx.entity.CustomerStatus;
import com.newasteralnx.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;


    // =========================================================
    // CREATE CUSTOMER
    // =========================================================

    @PostMapping
    public ResponseEntity<CustomerResponse> createCustomer(
            @Valid @RequestBody CustomerRequest request) {

        CustomerResponse response =
                customerService.createCustomer(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }


    // =========================================================
    // GET ALL CUSTOMERS
    // Pagination + Search + Sorting
    // =========================================================

    @GetMapping
    public ResponseEntity<Page<CustomerResponse>> getAllCustomers(

            @RequestParam(defaultValue = "")
            String search,

            @RequestParam(required = false)
            CustomerStatus status,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size,

            @RequestParam(defaultValue = "companyName")
            String sortBy,

            @RequestParam(defaultValue = "asc")
            String direction) {

        if (page < 0) {
            page = 0;
        }

        if (size < 1 || size > 100) {
            size = 10;
        }


        // Only allow approved fields for sorting
        String[] allowedSortFields = {
                "companyName",
                "customerCode",
                "contactPerson",
                "city",
                "country",
                "createdAt",
                "updatedAt"
        };

        boolean validSortField = false;

        for (String field : allowedSortFields) {

            if (field.equals(sortBy)) {
                validSortField = true;
                break;
            }
        }

        if (!validSortField) {
            sortBy = "companyName";
        }


        Sort.Direction sortDirection =
                direction.equalsIgnoreCase("desc")
                        ? Sort.Direction.DESC
                        : Sort.Direction.ASC;


        Pageable pageable =
                PageRequest.of(
                        page,
                        size,
                        Sort.by(sortDirection, sortBy)
                );


        return ResponseEntity.ok(
                customerService.getAllCustomers(
                        search,
                        status,
                        pageable
                )
        );
    }
    // =========================================================
    // GET CUSTOMER BY ID
    // =========================================================

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponse> getCustomerById(
            @PathVariable Long id) {

        CustomerResponse response =
                customerService.getCustomerById(id);

        return ResponseEntity.ok(response);
    }


    // =========================================================
    // UPDATE CUSTOMER
    // =========================================================

    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponse> updateCustomer(
            @PathVariable Long id,
            @Valid @RequestBody CustomerRequest request) {

        CustomerResponse response =
                customerService.updateCustomer(
                        id,
                        request
                );

        return ResponseEntity.ok(response);
    }


    // =========================================================
    // SOFT DELETE CUSTOMER
    // =========================================================

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(
            @PathVariable Long id) {

        customerService.deleteCustomer(id);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> updateCustomerStatus(
            @PathVariable Long id,
            @Valid @RequestBody CustomerStatusRequest request) {

        customerService.updateCustomerStatus(
                id,
                request.getStatus()
        );

        return ResponseEntity.noContent().build();
    }

}