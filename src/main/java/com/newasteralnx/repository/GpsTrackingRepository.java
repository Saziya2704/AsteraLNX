package com.newasteralnx.repository;

import com.newasteralnx.entity.GpsTracking;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

import java.util.List;

public interface GpsTrackingRepository extends JpaRepository<GpsTracking, Long> {
    List<GpsTracking> findByShipmentIdOrderByTimestampAsc(Long shipmentId);
    List<GpsTracking> findTop100ByOrderByTimestampDesc();
}
