package com.newasteralnx.entity;

import com.newasteralnx.constant.TrackingStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "gps_tracking", indexes = @Index(name = "idx_tracking_shipment", columnList = "shipment_id"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GpsTracking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "shipment_id", nullable = false)
    private Shipment shipment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id")
    private Trip trip;

    @Column(nullable = false, precision = 10, scale = 6)
    private BigDecimal latitude;

    @Column(nullable = false, precision = 10, scale = 6)
    private BigDecimal longitude;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(precision = 8, scale = 2)
    private BigDecimal speed;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TrackingStatus status;
}
