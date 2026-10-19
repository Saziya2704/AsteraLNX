package com.newasteralnx.service;

import com.newasteralnx.dto.response.TripResponse;
import com.newasteralnx.entity.Driver;
import com.newasteralnx.entity.Trip;
import com.newasteralnx.exception.ResourceNotFoundException;
import com.newasteralnx.repository.TripRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TripService {

    private final TripRepository tripRepository;
    private final DriverService driverService;

    public Page<TripResponse> list(Pageable pageable) {
        return tripRepository.findAll(pageable).map(this::toResponse);
    }

    public TripResponse get(Long id) {
        return toResponse(tripRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found with id: " + id)));
    }

    public Page<TripResponse> mine(Pageable pageable) {
        Driver driver = driverService.requireCurrentDriverProfile();
        return tripRepository.findByDriverIdAndStatusIn(driver.getId(), java.util.List.of(com.newasteralnx.constant.TripStatus.ASSIGNED, com.newasteralnx.constant.TripStatus.CANCELLED), pageable).map(this::toResponse);
    }

    private TripResponse toResponse(Trip trip) {
        return TripResponse.builder()
                .id(trip.getId())
                .tripNumber(trip.getTripNumber())
                .shipmentId(trip.getShipment().getId())
                .driverId(trip.getDriver().getId())
                .vehicleId(trip.getVehicle().getId())
                .origin(trip.getOrigin())
                .destination(trip.getDestination())
                .startTime(trip.getStartTime())
                .endTime(trip.getEndTime())
                .durationMinutes(trip.getDurationMinutes())
                .distance(trip.getDistance())
                .status(trip.getStatus())
                .build();
    }
}
