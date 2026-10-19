package com.newasteralnx.controller;

import com.newasteralnx.dto.request.ReceiptRequest;
import com.newasteralnx.dto.response.ReceiptResponse;
import com.newasteralnx.service.ReceiptService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/receipts")
@RequiredArgsConstructor
public class ReceiptController {

    private final ReceiptService receiptService;

    @PostMapping
    public ResponseEntity<ReceiptResponse> create(@Valid @RequestBody ReceiptRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(receiptService.create(request));
    }

    @GetMapping
    public ResponseEntity<Page<ReceiptResponse>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(receiptService.list(PageRequest.of(Math.max(page, 0), size < 1 ? 20 : size, Sort.by("createdAt").descending())));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReceiptResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(receiptService.get(id));
    }
}
