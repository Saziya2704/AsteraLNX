package com.newasteralnx.dto.response;

import com.newasteralnx.constant.QuoteStatus;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class QuoteResponse {
    private Long id;
    private String quoteNumber;
    private Long customerId;
    private Long inquiryId;
    private String origin;
    private String destination;
    private BigDecimal amount;
    private LocalDate validUntil;
    private QuoteStatus status;
    private String notes;
    private LocalDateTime createdAt;
}
