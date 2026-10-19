package com.newasteralnx.entity;

import com.newasteralnx.constant.StockTransferStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "stock_transfers", uniqueConstraints = {
        @UniqueConstraint(name = "uk_stock_transfer_number", columnNames = "transfer_number")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockTransfer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "transfer_number", nullable = false, length = 30)
    private String transferNumber;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "from_warehouse_id", nullable = false)
    private Warehouse fromWarehouse;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "to_warehouse_id", nullable = false)
    private Warehouse toWarehouse;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "commodity_id", nullable = false)
    private Commodity commodity;

    @Column(nullable = false, precision = 18, scale = 3)
    private BigDecimal quantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StockTransferStatus status;

    @Column(length = 500)
    private String remarks;

    private Long createdBy;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    private LocalDateTime completedAt;
}
