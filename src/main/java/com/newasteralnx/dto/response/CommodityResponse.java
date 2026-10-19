package com.newasteralnx.dto.response;

import com.newasteralnx.constant.CommodityStatus;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class CommodityResponse {
    private Long id;
    private String commodityCode;
    private String name;
    private String category;
    private String description;
    private BigDecimal quantity;
    private String unit;
    private BigDecimal weight;
    private BigDecimal volume;
    private Long warehouseId;
    private String storageLocation;
    private CommodityStatus status;
}
