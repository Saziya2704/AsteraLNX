package com.newasteralnx.dto.response;

import com.newasteralnx.constant.InquiryStatus;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class InquiryResponse {
    private Long id;
    private String inquiryNumber;
    private Long customerId;
    private String subject;
    private String origin;
    private String destination;
    private String cargoDescription;
    private BigDecimal quantity;
    private InquiryStatus status;
    private String remarks;
    private LocalDateTime createdAt;
}
