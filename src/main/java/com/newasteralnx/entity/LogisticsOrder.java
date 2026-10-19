package com.newasteralnx.entity;

import com.newasteralnx.constant.OrderPriority;
import com.newasteralnx.constant.OrderStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "orders",
        uniqueConstraints = @UniqueConstraint(name = "uk_order_number", columnNames = "order_number"),
        indexes = @Index(name = "idx_order_status", columnList = "status")
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LogisticsOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_number", nullable = false, length = 20)
    private String orderNumber;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quote_id")
    private Quote quote;

    @Column(nullable = false, length = 100)
    private String origin;

    @Column(nullable = false, length = 100)
    private String destination;

    @Column(nullable = false)
    private LocalDate orderDate;

    @Column(nullable = false, length = 100)
    private String commodityName;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal quantity;

    @Column(precision = 12, scale = 2)
    private BigDecimal weight;

    @Column(precision = 12, scale = 2)
    private BigDecimal volume;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private OrderStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrderPriority priority;

    private Long createdBy;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
