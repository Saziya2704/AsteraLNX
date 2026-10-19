package com.newasteralnx.service;

import com.newasteralnx.constant.InquiryStatus;
import com.newasteralnx.dto.request.InquiryRequest;
import com.newasteralnx.dto.response.InquiryResponse;
import com.newasteralnx.entity.Customer;
import com.newasteralnx.entity.Inquiry;
import com.newasteralnx.exception.BusinessException;
import com.newasteralnx.exception.ResourceNotFoundException;
import com.newasteralnx.repository.InquiryRepository;
import com.newasteralnx.util.SequentialCodes;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class InquiryService {

    private final InquiryRepository inquiryRepository;
    private final CustomerService customerService;

    public InquiryResponse create(InquiryRequest request) {
        Customer customer = request.getCustomerId() == null
                ? null
                : customerService.requireActiveForTransaction(request.getCustomerId());

        Inquiry inquiry = Inquiry.builder()
                .inquiryNumber(SequentialCodes.next("INQ", inquiryRepository.count() + 1, inquiryRepository::existsByInquiryNumber))
                .customer(customer)
                .subject(request.getSubject().trim())
                .origin(request.getOrigin().trim())
                .destination(request.getDestination().trim())
                .cargoDescription(request.getCargoDescription())
                .quantity(request.getQuantity())
                .status(InquiryStatus.OPEN)
                .remarks(request.getRemarks())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        return toResponse(inquiryRepository.save(inquiry));
    }

    @Transactional(readOnly = true)
    public Page<InquiryResponse> list(Pageable pageable) {
        return inquiryRepository.findAll(pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public InquiryResponse get(Long id) {
        return toResponse(require(id));
    }

    public InquiryResponse update(Long id, InquiryRequest request) {
        Inquiry inquiry = require(id);
        if (inquiry.getStatus() == InquiryStatus.CONVERTED || inquiry.getStatus() == InquiryStatus.CANCELLED) {
            throw new BusinessException("Converted or cancelled inquiries cannot be edited");
        }
        Customer customer = request.getCustomerId() == null
                ? null
                : customerService.requireActiveForTransaction(request.getCustomerId());
        inquiry.setCustomer(customer);
        inquiry.setSubject(request.getSubject().trim());
        inquiry.setOrigin(request.getOrigin().trim());
        inquiry.setDestination(request.getDestination().trim());
        inquiry.setCargoDescription(request.getCargoDescription());
        inquiry.setQuantity(request.getQuantity());
        inquiry.setRemarks(request.getRemarks());
        inquiry.setUpdatedAt(LocalDateTime.now());
        return toResponse(inquiryRepository.save(inquiry));
    }

    public InquiryResponse updateStatus(Long id, InquiryStatus status) {
        Inquiry inquiry = require(id);
        InquiryStatus current = inquiry.getStatus();
        if (current == InquiryStatus.CONVERTED || current == InquiryStatus.CANCELLED) {
            throw new BusinessException("Converted or cancelled inquiries cannot change status");
        }
        if (status == InquiryStatus.CONVERTED) {
            throw new BusinessException("Use quote acceptance / order conversion to mark an inquiry as converted");
        }
        if (current == InquiryStatus.CLOSED && status != InquiryStatus.CLOSED) {
            throw new BusinessException("Closed inquiry cannot be reopened");
        }
        inquiry.setStatus(status);
        inquiry.setUpdatedAt(LocalDateTime.now());
        return toResponse(inquiryRepository.save(inquiry));
    }

    public Inquiry require(Long id) {
        return inquiryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inquiry not found with id: " + id));
    }

    public void markConverted(Inquiry inquiry) {
        if (inquiry.getStatus() == InquiryStatus.CANCELLED) {
            throw new BusinessException("Cancelled inquiry cannot be converted");
        }
        inquiry.setStatus(InquiryStatus.CONVERTED);
        inquiry.setUpdatedAt(LocalDateTime.now());
    }

    private InquiryResponse toResponse(Inquiry inquiry) {
        return InquiryResponse.builder()
                .id(inquiry.getId())
                .inquiryNumber(inquiry.getInquiryNumber())
                .customerId(inquiry.getCustomer() == null ? null : inquiry.getCustomer().getId())
                .subject(inquiry.getSubject())
                .origin(inquiry.getOrigin())
                .destination(inquiry.getDestination())
                .cargoDescription(inquiry.getCargoDescription())
                .quantity(inquiry.getQuantity())
                .status(inquiry.getStatus())
                .remarks(inquiry.getRemarks())
                .createdAt(inquiry.getCreatedAt())
                .build();
    }
}
