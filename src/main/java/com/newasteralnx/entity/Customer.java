package com.newasteralnx.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "customers",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_customer_email", columnNames = "email"),
                @UniqueConstraint(name = "uk_customer_gst", columnNames = "gst_number"),
                @UniqueConstraint(name = "uk_customer_code", columnNames = "customer_code")
        },
        indexes = {
                @Index(name = "idx_customer_company", columnList = "company_name"),
                @Index(name = "idx_customer_status", columnList = "status")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_code", nullable = false, unique = true, length = 20)
    private String customerCode;

    @Column(name = "company_name", nullable = false, length = 50)
    private String companyName;

    @Column(name = "contact_person", nullable = false, length = 50)
    private String contactPerson;

    @Column(nullable = false, length = 20)
    private String phone;

    @Column(nullable = false, length = 50)
    private String email;

    @Column(name = "gst_number", nullable = false, length = 20)
    private String gstNumber;

    @Column(nullable = false, length = 255)
    private String address;

    @Column(nullable = false, length = 50)
    private String city;

    @Column(nullable = false, length = 50)
    private String state;

    @Column(nullable = false, length = 30)
    private String country;

    @Column(name = "postal_code", nullable = false, length = 20)
    private String postalCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CustomerStatus status;

    @Column(nullable = false)
    private Boolean deleted;

    private LocalDateTime deletedAt;

    private Long deletedBy;

    private Long createdBy;

    private Long updatedBy;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
