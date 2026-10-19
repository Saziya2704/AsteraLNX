package com.newasteralnx.service;

import com.newasteralnx.constant.QuoteStatus;
import com.newasteralnx.dto.request.QuoteRequest;
import com.newasteralnx.dto.response.QuoteResponse;
import com.newasteralnx.entity.Customer;
import com.newasteralnx.entity.Inquiry;
import com.newasteralnx.entity.Quote;
import com.newasteralnx.exception.BusinessException;
import com.newasteralnx.exception.ResourceNotFoundException;
import com.newasteralnx.repository.QuoteRepository;
import com.newasteralnx.util.SequentialCodes;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class QuoteService {

    private final QuoteRepository quoteRepository;
    private final CustomerService customerService;
    private final InquiryService inquiryService;
    private final MailService mailService;

    public QuoteResponse create(QuoteRequest request) {
        Customer customer = customerService.requireActiveForTransaction(request.getCustomerId());
        Inquiry inquiry = request.getInquiryId() == null ? null : inquiryService.require(request.getInquiryId());

        Quote quote = Quote.builder()
                .quoteNumber(SequentialCodes.next("QTE", quoteRepository.count() + 1, quoteRepository::existsByQuoteNumber))
                .customer(customer)
                .inquiry(inquiry)
                .origin(request.getOrigin().trim())
                .destination(request.getDestination().trim())
                .amount(request.getAmount())
                .validUntil(request.getValidUntil())
                .status(QuoteStatus.DRAFT)
                .notes(request.getNotes())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        return toResponse(quoteRepository.save(quote));
    }

    public Page<QuoteResponse> list(Pageable pageable) {
        return quoteRepository.findAll(pageable).map(this::refreshExpired).map(this::toResponse);
    }

    public QuoteResponse get(Long id) {
        return toResponse(refreshExpired(require(id)));
    }

    public QuoteResponse update(Long id, QuoteRequest request) {
        Quote quote = refreshExpired(require(id));
        if (quote.getStatus() != QuoteStatus.DRAFT) {
            throw new BusinessException("Only draft quotes can be edited");
        }
        Customer customer = customerService.requireActiveForTransaction(request.getCustomerId());
        Inquiry inquiry = request.getInquiryId() == null ? null : inquiryService.require(request.getInquiryId());
        quote.setCustomer(customer);
        quote.setInquiry(inquiry);
        quote.setOrigin(request.getOrigin().trim());
        quote.setDestination(request.getDestination().trim());
        quote.setAmount(request.getAmount());
        quote.setValidUntil(request.getValidUntil());
        quote.setNotes(request.getNotes());
        quote.setUpdatedAt(LocalDateTime.now());
        return toResponse(quoteRepository.save(quote));
    }

    public QuoteResponse send(Long id) {
        Quote quote = require(id);
        if (quote.getStatus() != QuoteStatus.DRAFT) {
            throw new BusinessException("Only draft quotes can be sent");
        }
        Customer customer = quote.getCustomer();
        String html = "<div style=\"font-family:Arial,sans-serif;max-width:640px;margin:auto;color:#172033\">"
                + "<h2 style=\"color:#1d4ed8\">AsteraLNX — Quotation " + quote.getQuoteNumber() + "</h2>"
                + "<p>Dear " + customer.getContactPerson() + ",</p><p>Please find your quotation details below.</p>"
                + "<table style=\"border-collapse:collapse;width:100%\"><tr><th style=\"text-align:left;padding:8px;border:1px solid #ddd\">Route</th><td style=\"padding:8px;border:1px solid #ddd\">" + quote.getOrigin() + " → " + quote.getDestination() + "</td></tr>"
                + "<tr><th style=\"text-align:left;padding:8px;border:1px solid #ddd\">Amount</th><td style=\"padding:8px;border:1px solid #ddd\">₹" + quote.getAmount() + "</td></tr>"
                + "<tr><th style=\"text-align:left;padding:8px;border:1px solid #ddd\">Valid until</th><td style=\"padding:8px;border:1px solid #ddd\">" + (quote.getValidUntil() == null ? "—" : quote.getValidUntil()) + "</td></tr></table>"
                + "<p>Regards,<br><strong>AsteraLNX Logistics</strong></p></div>";
        mailService.sendBusinessEmail(customer.getEmail(), "Quotation " + quote.getQuoteNumber() + " — AsteraLNX", html);
        quote.setStatus(QuoteStatus.SENT);
        quote.setUpdatedAt(LocalDateTime.now());
        return toResponse(quoteRepository.save(quote));
    }

    public QuoteResponse accept(Long id) {
        Quote quote = refreshExpired(require(id));
        if (quote.getStatus() == QuoteStatus.EXPIRED) {
            throw new BusinessException("Expired quote cannot be accepted");
        }
        if (quote.getStatus() != QuoteStatus.SENT) {
            throw new BusinessException("Only sent quotes can be accepted");
        }
        quote.setStatus(QuoteStatus.ACCEPTED);
        quote.setUpdatedAt(LocalDateTime.now());
        return toResponse(quoteRepository.save(quote));
    }

    public QuoteResponse reject(Long id) {
        Quote quote = require(id);
        if (quote.getStatus() != QuoteStatus.SENT) {
            throw new BusinessException("Only sent quotes can be rejected");
        }
        quote.setStatus(QuoteStatus.REJECTED);
        quote.setUpdatedAt(LocalDateTime.now());
        return toResponse(quoteRepository.save(quote));
    }

    public Quote requireAccepted(Long id) {
        Quote quote = refreshExpired(require(id));
        if (quote.getStatus() == QuoteStatus.REJECTED) {
            throw new BusinessException("Rejected quote cannot be converted");
        }
        if (quote.getStatus() == QuoteStatus.EXPIRED) {
            throw new BusinessException("Expired quote cannot be converted");
        }
        if (quote.getStatus() != QuoteStatus.ACCEPTED) {
            throw new BusinessException("Only accepted quotes can be converted to an order");
        }
        return quote;
    }

    public void markConverted(Quote quote) {
        quote.setStatus(QuoteStatus.CONVERTED);
        quote.setUpdatedAt(LocalDateTime.now());
        if (quote.getInquiry() != null) {
            inquiryService.markConverted(quote.getInquiry());
        }
    }

    public Quote require(Long id) {
        return quoteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quote not found with id: " + id));
    }

    private Quote refreshExpired(Quote quote) {
        if (quote.getValidUntil() != null
                && quote.getValidUntil().isBefore(LocalDate.now())
                && (quote.getStatus() == QuoteStatus.DRAFT || quote.getStatus() == QuoteStatus.SENT)) {
            quote.setStatus(QuoteStatus.EXPIRED);
            quote.setUpdatedAt(LocalDateTime.now());
            quoteRepository.save(quote);
        }
        return quote;
    }

    private QuoteResponse toResponse(Quote quote) {
        return QuoteResponse.builder()
                .id(quote.getId())
                .quoteNumber(quote.getQuoteNumber())
                .customerId(quote.getCustomer().getId())
                .inquiryId(quote.getInquiry() == null ? null : quote.getInquiry().getId())
                .origin(quote.getOrigin())
                .destination(quote.getDestination())
                .amount(quote.getAmount())
                .validUntil(quote.getValidUntil())
                .status(quote.getStatus())
                .notes(quote.getNotes())
                .createdAt(quote.getCreatedAt())
                .build();
    }
}
