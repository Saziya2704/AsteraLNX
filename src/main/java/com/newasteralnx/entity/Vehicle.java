package com.newasteralnx.entity;

import com.newasteralnx.constant.VehicleStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "vehicles", uniqueConstraints = @UniqueConstraint(name = "uk_vehicle_number", columnNames = "vehicle_number"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "vehicle_number", nullable = false, length = 30)
    private String vehicleNumber;

    @Column(nullable = false, length = 50)
    private String vehicleType;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal capacity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private VehicleStatus status;

    @Column(length = 50)
    private String registrationNumber;

    private LocalDate insuranceExpiry;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
