package com.newasteralnx.controller;

import com.newasteralnx.constant.WarehouseStatus;
import com.newasteralnx.dto.request.WarehouseRequest;
import com.newasteralnx.dto.response.WarehouseResponse;
import com.newasteralnx.service.WarehouseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/warehouses")
@RequiredArgsConstructor
public class WarehouseController {

    private final WarehouseService warehouseService;

    @PostMapping
    public ResponseEntity<WarehouseResponse> create(@Valid @RequestBody WarehouseRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(warehouseService.create(request));
    }

    @GetMapping
    public ResponseEntity<Page<WarehouseResponse>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(warehouseService.list(PageRequest.of(Math.max(page, 0), size < 1 ? 20 : size, Sort.by("name").ascending())));
    }

    @GetMapping("/{id}")
    public ResponseEntity<WarehouseResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(warehouseService.get(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<WarehouseResponse> update(@PathVariable Long id, @Valid @RequestBody WarehouseRequest request) {
        return ResponseEntity.ok(warehouseService.update(id, request));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<WarehouseResponse> updateStatus(
            @PathVariable Long id,
            @RequestParam WarehouseStatus status) {
        return ResponseEntity.ok(warehouseService.updateStatus(id, status));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        warehouseService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
