package com.newasteralnx.dto.response;

import com.newasteralnx.constant.WarehouseStatus;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class WarehouseResponse {
    private Long id;
    private String warehouseCode;
    private String name;
    private String address;
    private String city;
    private String state;
    private String country;
    private String postalCode;
    private String managerName;
    private BigDecimal capacity;
    private BigDecimal availableCapacity;
    private BigDecimal occupiedCapacity;
    private WarehouseStatus status;
    private LocalDateTime createdAt;
}
