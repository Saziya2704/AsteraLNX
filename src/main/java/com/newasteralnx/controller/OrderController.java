package com.newasteralnx.controller;

import com.newasteralnx.constant.OrderStatus;
import com.newasteralnx.dto.request.OrderRequest;
import com.newasteralnx.dto.response.OrderResponse;
import com.newasteralnx.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> create(@Valid @RequestBody OrderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.create(request));
    }

    @GetMapping
    public ResponseEntity<Page<OrderResponse>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(orderService.list(PageRequest.of(Math.max(page, 0), size < 1 ? 20 : size, Sort.by("createdAt").descending())));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.get(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderResponse> update(@PathVariable Long id, @Valid @RequestBody OrderRequest request) {
        return ResponseEntity.ok(orderService.update(id, request));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderResponse> updateStatus(@PathVariable Long id, @RequestParam OrderStatus status) {
        return ResponseEntity.ok(orderService.updateStatus(id, status));
    }

    @PostMapping("/{id}/confirm")
    public ResponseEntity<OrderResponse> confirm(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.confirm(id));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<OrderResponse> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.cancel(id));
    }
}
