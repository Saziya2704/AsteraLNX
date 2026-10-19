package com.newasteralnx.service.impl;

import com.newasteralnx.dto.CustomerRequest;
import com.newasteralnx.dto.CustomerResponse;
import com.newasteralnx.entity.Customer;
import com.newasteralnx.entity.CustomerStatus;
import com.newasteralnx.exception.BusinessException;
import com.newasteralnx.exception.ResourceNotFoundException;
import com.newasteralnx.repository.CustomerRepository;
import com.newasteralnx.service.CustomerService;
import com.newasteralnx.specification.CustomerSpecifications;
import com.newasteralnx.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    @Override
    public CustomerResponse createCustomer(CustomerRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        String gstNumber = request.getGstNumber().trim().toUpperCase();

        if (customerRepository.existsByEmailIgnoreCase(email)) {
            throw new BusinessException("Customer with this email already exists");
        }
        if (customerRepository.existsByGstNumberIgnoreCase(gstNumber)) {
            throw new BusinessException("GST number already exists");
        }

        Long userId = SecurityUtils.requireCurrentUserId();
        LocalDateTime now = LocalDateTime.now();

        Customer customer = Customer.builder()
                .customerCode(generateCustomerCode())
                .companyName(request.getCompanyName().trim())
                .contactPerson(request.getContactPerson().trim())
                .phone(request.getPhone().trim())
                .email(email)
                .gstNumber(gstNumber)
                .address(request.getAddress().trim())
                .city(request.getCity().trim())
                .state(request.getState().trim())
                .country(request.getCountry().trim())
                .postalCode(request.getPostalCode().trim())
                .status(CustomerStatus.ACTIVE)
                .deleted(false)
                .createdBy(userId)
                .updatedBy(userId)
                .createdAt(now)
                .updatedAt(now)
                .build();

        return mapToResponse(customerRepository.save(customer));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CustomerResponse> getAllCustomers(String search, CustomerStatus status, Pageable pageable) {
        return customerRepository.findAll(CustomerSpecifications.from(search, status), pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerResponse getCustomerById(Long id) {
        return mapToResponse(requireExisting(id));
    }

    @Override
    public CustomerResponse updateCustomer(Long id, CustomerRequest request) {
        Customer customer = requireExisting(id);

        String email = request.getEmail().trim().toLowerCase();
        String gstNumber = request.getGstNumber().trim().toUpperCase();

        if (!customer.getEmail().equalsIgnoreCase(email)
                && customerRepository.existsByEmailIgnoreCase(email)) {
            throw new BusinessException("Customer with this email already exists");
        }
        if (!customer.getGstNumber().equalsIgnoreCase(gstNumber)
                && customerRepository.existsByGstNumberIgnoreCase(gstNumber)) {
            throw new BusinessException("GST number already exists");
        }

        customer.setCompanyName(request.getCompanyName().trim());
        customer.setContactPerson(request.getContactPerson().trim());
        customer.setPhone(request.getPhone().trim());
        customer.setEmail(email);
        customer.setGstNumber(gstNumber);
        customer.setAddress(request.getAddress().trim());
        customer.setCity(request.getCity().trim());
        customer.setState(request.getState().trim());
        customer.setCountry(request.getCountry().trim());
        customer.setPostalCode(request.getPostalCode().trim());
        customer.setUpdatedBy(SecurityUtils.requireCurrentUserId());
        customer.setUpdatedAt(LocalDateTime.now());

        return mapToResponse(customerRepository.save(customer));
    }

    @Override
    public void updateCustomerStatus(Long id, CustomerStatus status) {
        Customer customer = requireExisting(id);
        if (customer.getStatus() == status) {
            return;
        }
        customer.setStatus(status);
        customer.setUpdatedBy(SecurityUtils.requireCurrentUserId());
        customer.setUpdatedAt(LocalDateTime.now());
        customerRepository.save(customer);
    }

    @Override
    public void deleteCustomer(Long id) {
        Customer customer = requireExisting(id);
        customer.setDeleted(true);
        customer.setStatus(CustomerStatus.INACTIVE);
        customer.setDeletedAt(LocalDateTime.now());
        customer.setDeletedBy(SecurityUtils.requireCurrentUserId());
        customer.setUpdatedAt(LocalDateTime.now());
        customerRepository.save(customer);
    }

    @Override
    public Customer requireActiveForTransaction(Long customerId) {
        Customer customer = requireExisting(customerId);
        if (customer.getStatus() != CustomerStatus.ACTIVE) {
            throw new BusinessException("Inactive customer cannot be used for new transactions");
        }
        return customer;
    }

    private Customer requireExisting(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
        if (Boolean.TRUE.equals(customer.getDeleted())) {
            throw new ResourceNotFoundException("Customer not found with id: " + id);
        }
        return customer;
    }

    private String generateCustomerCode() {
        return com.newasteralnx.util.SequentialCodes.next(
                "CUS",
                customerRepository.count() + 1,
                customerRepository::existsByCustomerCode);
    }

    private CustomerResponse mapToResponse(Customer customer) {
        return CustomerResponse.builder()
                .id(customer.getId())
                .customerCode(customer.getCustomerCode())
                .companyName(customer.getCompanyName())
                .contactPerson(customer.getContactPerson())
                .phone(customer.getPhone())
                .email(customer.getEmail())
                .gstNumber(customer.getGstNumber())
                .address(customer.getAddress())
                .city(customer.getCity())
                .state(customer.getState())
                .country(customer.getCountry())
                .postalCode(customer.getPostalCode())
                .status(customer.getStatus())
                .createdAt(customer.getCreatedAt())
                .updatedAt(customer.getUpdatedAt())
                .build();
    }
}
