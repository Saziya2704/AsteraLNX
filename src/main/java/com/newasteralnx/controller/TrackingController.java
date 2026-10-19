package com.newasteralnx.controller;

import com.newasteralnx.dto.response.GpsTrackingResponse;
import com.newasteralnx.service.TrackingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tracking")
@RequiredArgsConstructor
public class TrackingController {

    private final TrackingService trackingService;

    @GetMapping
    public ResponseEntity<List<GpsTrackingResponse>> list() {
        return ResponseEntity.ok(trackingService.listRecent());
    }

    @PostMapping("/shipments/{shipmentId}/simulate")
    public ResponseEntity<List<GpsTrackingResponse>> simulate(@PathVariable Long shipmentId) {
        return ResponseEntity.ok(trackingService.simulate(shipmentId));
    }

    @GetMapping("/shipments/{shipmentId}")
    public ResponseEntity<List<GpsTrackingResponse>> byShipment(@PathVariable Long shipmentId) {
        return ResponseEntity.ok(trackingService.byShipment(shipmentId));
    }
}
