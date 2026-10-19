package com.newasteralnx.entity;

import com.newasteralnx.constant.DriverStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "drivers",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_driver_code", columnNames = "driver_code"),
                @UniqueConstraint(name = "uk_driver_license", columnNames = "license_number")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Driver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "driver_code", nullable = false, length = 20)
    private String driverCode;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 20)
    private String phone;

    @Column(length = 50)
    private String email;

    @Column(name = "license_number", nullable = false, length = 40)
    private String licenseNumber;

    @Column(nullable = false)
    private LocalDate licenseExpiry;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DriverStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_vehicle_id")
    private Vehicle assignedVehicle;

    @Column(nullable = false)
    private Boolean deleted;

    private LocalDateTime deletedAt;

    private Long deletedBy;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
