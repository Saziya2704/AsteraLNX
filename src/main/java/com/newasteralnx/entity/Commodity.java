package com.newasteralnx.entity;

import com.newasteralnx.constant.CommodityStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "commodities", uniqueConstraints = @UniqueConstraint(name = "uk_commodity_code", columnNames = "commodity_code"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Commodity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "commodity_code", nullable = false, length = 20)
    private String commodityCode;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 50)
    private String category;

    @Column(length = 255)
    private String description;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal quantity;

    @Column(nullable = false, length = 20)
    private String unit;

    @Column(precision = 12, scale = 2)
    private BigDecimal weight;

    @Column(precision = 12, scale = 2)
    private BigDecimal volume;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    @Column(length = 50)
    private String storageLocation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CommodityStatus status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
