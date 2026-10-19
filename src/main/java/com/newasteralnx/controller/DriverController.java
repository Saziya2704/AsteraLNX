package com.newasteralnx.controller;

import com.newasteralnx.constant.DriverStatus;
import com.newasteralnx.dto.request.DriverRequest;
import com.newasteralnx.dto.response.DriverResponse;
import com.newasteralnx.service.DriverService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/drivers")
@RequiredArgsConstructor
public class DriverController {

    private final DriverService driverService;

    @PostMapping
    public ResponseEntity<DriverResponse> create(@Valid @RequestBody DriverRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(driverService.create(request));
    }

    @GetMapping
    public ResponseEntity<Page<DriverResponse>> list(
            @RequestParam(required = false) DriverStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(driverService.list(status, PageRequest.of(Math.max(page, 0), size < 1 ? 20 : size)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DriverResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(driverService.get(id));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<DriverResponse> updateStatus(
            @PathVariable Long id,
            @RequestParam DriverStatus status) {
        return ResponseEntity.ok(driverService.updateStatus(id, status));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        driverService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
