package com.newasteralnx.dto.response;

import com.newasteralnx.constant.BookingStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class BookingResponse {
    private Long id;
    private String bookingNumber;
    private Long customerId;
    private Long orderId;
    private String orderNumber;
    private LocalDateTime createdAt;
    private Long warehouseId;
    private String pickup;
    private String destination;
    private Long vehicleId;
    private Long driverId;
    private LocalDate scheduledDate;
    private BookingStatus status;
}
