package com.newasteralnx.controller;

import com.newasteralnx.constant.ShipmentStatus;
import com.newasteralnx.dto.request.AssignmentRequest;
import com.newasteralnx.dto.request.ShipmentRequest;
import com.newasteralnx.dto.response.ShipmentResponse;
import com.newasteralnx.service.ShipmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shipments")
@RequiredArgsConstructor
public class ShipmentController {

    private final ShipmentService shipmentService;

    @PostMapping
    public ResponseEntity<ShipmentResponse> create(@Valid @RequestBody ShipmentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(shipmentService.create(request));
    }

    @GetMapping
    public ResponseEntity<Page<ShipmentResponse>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(shipmentService.list(PageRequest.of(Math.max(page, 0), size < 1 ? 20 : size, Sort.by("createdAt").descending())));
    }

    @GetMapping("/mine")
    public ResponseEntity<Page<ShipmentResponse>> mine(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(shipmentService.mine(PageRequest.of(Math.max(page, 0), size < 1 ? 20 : size)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShipmentResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(shipmentService.get(id));
    }

    @PostMapping("/{id}/assign")
    public ResponseEntity<ShipmentResponse> assign(
            @PathVariable Long id,
            @Valid @RequestBody AssignmentRequest request) {
        return ResponseEntity.ok(shipmentService.assign(id, request.getDriverId(), request.getVehicleId()));
    }

    @PostMapping("/{id}/dispatch")
    public ResponseEntity<ShipmentResponse> dispatch(@PathVariable Long id) {
        return ResponseEntity.ok(shipmentService.dispatch(id));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<ShipmentResponse> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(shipmentService.cancel(id));
    }

    @PostMapping("/{id}/deliver")
    public ResponseEntity<ShipmentResponse> deliver(
            @PathVariable Long id,
            @RequestBody(required = false) com.newasteralnx.dto.request.ProofOfDeliveryRequest request) {
        return ResponseEntity.ok(shipmentService.deliver(id, request));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ShipmentResponse> updateStatus(
            @PathVariable Long id,
            @RequestParam ShipmentStatus status) {
        return ResponseEntity.ok(shipmentService.updateStatus(id, status));
    }
}
