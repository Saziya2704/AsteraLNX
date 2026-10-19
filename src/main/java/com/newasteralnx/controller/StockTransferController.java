package com.newasteralnx.controller;

import com.newasteralnx.dto.request.StockTransferRequest;
import com.newasteralnx.dto.response.StockTransferResponse;
import com.newasteralnx.service.StockTransferService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stock-transfers")
@RequiredArgsConstructor
public class StockTransferController {

    private final StockTransferService stockTransferService;

    @PostMapping
    public ResponseEntity<StockTransferResponse> create(@Valid @RequestBody StockTransferRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(stockTransferService.createAndComplete(request));
    }

    @GetMapping
    public ResponseEntity<Page<StockTransferResponse>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(stockTransferService.list(
                PageRequest.of(Math.max(page, 0), size < 1 ? 20 : Math.min(size, 100),
                        Sort.by("createdAt").descending())));
    }

    @GetMapping("/{id}")
    public ResponseEntity<StockTransferResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(stockTransferService.get(id));
    }
}
