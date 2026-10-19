package com.newasteralnx.controller;

import com.newasteralnx.dto.request.CommodityRequest;
import com.newasteralnx.dto.response.CommodityResponse;
import com.newasteralnx.service.CommodityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/commodities")
@RequiredArgsConstructor
public class CommodityController {

    private final CommodityService commodityService;

    @PostMapping
    public ResponseEntity<CommodityResponse> create(@Valid @RequestBody CommodityRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(commodityService.create(request));
    }

    @GetMapping
    public ResponseEntity<Page<CommodityResponse>> list(
            @RequestParam(required = false) Long warehouseId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(commodityService.list(warehouseId, PageRequest.of(Math.max(page, 0), size < 1 ? 20 : size)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommodityResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(commodityService.get(id));
    }
}
