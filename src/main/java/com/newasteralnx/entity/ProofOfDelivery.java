package com.newasteralnx.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "proof_of_delivery", uniqueConstraints = {
        @UniqueConstraint(name = "uk_pod_shipment", columnNames = "shipment_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProofOfDelivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "shipment_id", nullable = false)
    private Shipment shipment;

    @Column(name = "received_by", length = 100)
    private String receivedBy;

    @Column(length = 500)
    private String remarks;

    @Column(length = 30)
    private String outcome;

    @Column(name = "delivered_at", nullable = false)
    private LocalDateTime deliveredAt;

    @Column(name = "recorded_by")
    private Long recordedBy;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
