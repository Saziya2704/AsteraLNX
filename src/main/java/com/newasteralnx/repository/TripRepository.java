package com.newasteralnx.repository;

import com.newasteralnx.constant.TripStatus;
import com.newasteralnx.entity.Trip;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Optional;

public interface TripRepository extends JpaRepository<Trip, Long> {
    boolean existsByTripNumber(String tripNumber);
    boolean existsByDriverIdAndStatusIn(Long driverId, Collection<TripStatus> statuses);
    Optional<Trip> findByShipmentId(Long shipmentId);
    Page<Trip> findByDriverId(Long driverId, Pageable pageable);
    Page<Trip> findByDriverIdAndStatusIn(Long driverId, Collection<TripStatus> statuses, Pageable pageable);

    long countByDriverId(Long driverId);
}
