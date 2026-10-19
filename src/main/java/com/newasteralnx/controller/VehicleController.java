package com.newasteralnx.controller;

import com.newasteralnx.constant.VehicleStatus;
import com.newasteralnx.dto.request.VehicleRequest;
import com.newasteralnx.dto.response.VehicleResponse;
import com.newasteralnx.service.VehicleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/vehicles")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;

    @PostMapping
    public ResponseEntity<VehicleResponse> create(@Valid @RequestBody VehicleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(vehicleService.create(request));
    }

    @GetMapping
    public ResponseEntity<Page<VehicleResponse>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(vehicleService.list(PageRequest.of(Math.max(page, 0), size < 1 ? 20 : size)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<VehicleResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(vehicleService.get(id));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<VehicleResponse> updateStatus(
            @PathVariable Long id,
            @RequestParam VehicleStatus status) {
        return ResponseEntity.ok(vehicleService.updateStatus(id, status));
    }
}
