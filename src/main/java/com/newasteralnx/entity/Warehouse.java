package com.newasteralnx.entity;

import com.newasteralnx.constant.WarehouseStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "warehouses",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_warehouse_code", columnNames = "warehouse_code"),
                @UniqueConstraint(name = "uk_warehouse_name", columnNames = "name")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Warehouse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "warehouse_code", nullable = false, length = 20)
    private String warehouseCode;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 255)
    private String address;

    @Column(nullable = false, length = 50)
    private String city;

    @Column(nullable = false, length = 50)
    private String state;

    @Column(nullable = false, length = 30)
    private String country;

    @Column(nullable = false, length = 20)
    private String postalCode;

    @Column(length = 50)
    private String managerName;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal capacity;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal availableCapacity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private WarehouseStatus status;

    @Column(nullable = false)
    private Boolean deleted;

    private LocalDateTime deletedAt;

    private Long deletedBy;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
