package com.newasteralnx.service;

import com.newasteralnx.constant.TrackingStatus;
import com.newasteralnx.dto.response.GpsTrackingResponse;
import com.newasteralnx.entity.GpsTracking;
import com.newasteralnx.entity.Shipment;
import com.newasteralnx.entity.Trip;
import com.newasteralnx.repository.GpsTrackingRepository;
import com.newasteralnx.repository.TripRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TrackingService {

    private final GpsTrackingRepository gpsTrackingRepository;
    private final ShipmentService shipmentService;
    private final TripRepository tripRepository;

    public List<GpsTrackingResponse> simulate(Long shipmentId) {
        Shipment shipment = shipmentService.require(shipmentId);
        Trip trip = tripRepository.findByShipmentId(shipmentId).orElse(null);

        TrackingStatus[] path = {
                TrackingStatus.ASSIGNED,
                TrackingStatus.PICKED_UP,
                TrackingStatus.IN_TRANSIT,
                TrackingStatus.ARRIVING,
                TrackingStatus.DELIVERED
        };

        List<GpsTracking> points = new ArrayList<>();
        BigDecimal lat = new BigDecimal("13.082700");
        BigDecimal lon = new BigDecimal("80.270700");
        LocalDateTime time = LocalDateTime.now();

        for (int i = 0; i < path.length; i++) {
            GpsTracking point = GpsTracking.builder()
                    .shipment(shipment)
                    .trip(trip)
                    .latitude(lat.add(BigDecimal.valueOf(i * 0.010000)))
                    .longitude(lon.add(BigDecimal.valueOf(i * 0.008000)))
                    .timestamp(time.plusMinutes(i * 20L))
                    .speed(new BigDecimal("40.00"))
                    .status(path[i])
                    .build();
            points.add(gpsTrackingRepository.save(point));
        }
        return points.stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<GpsTrackingResponse> byShipment(Long shipmentId) {
        shipmentService.require(shipmentId);
        return gpsTrackingRepository.findByShipmentIdOrderByTimestampAsc(shipmentId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<GpsTrackingResponse> listRecent() {
        return gpsTrackingRepository.findTop100ByOrderByTimestampDesc().stream()
                .map(this::toResponse)
                .toList();
    }

    private GpsTrackingResponse toResponse(GpsTracking tracking) {
        return GpsTrackingResponse.builder()
                .id(tracking.getId())
                .shipmentId(tracking.getShipment().getId())
                .tripId(tracking.getTrip() == null ? null : tracking.getTrip().getId())
                .latitude(tracking.getLatitude())
                .longitude(tracking.getLongitude())
                .timestamp(tracking.getTimestamp())
                .speed(tracking.getSpeed())
                .status(tracking.getStatus())
                .build();
    }
}
