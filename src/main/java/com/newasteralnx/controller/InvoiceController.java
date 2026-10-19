package com.newasteralnx.controller;

import com.newasteralnx.dto.request.InvoiceRequest;
import com.newasteralnx.dto.response.InvoiceResponse;
import com.newasteralnx.service.InvoiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;

    @PostMapping
    public ResponseEntity<InvoiceResponse> create(@Valid @RequestBody InvoiceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(invoiceService.create(request));
    }

    @GetMapping
    public ResponseEntity<Page<InvoiceResponse>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(invoiceService.list(PageRequest.of(Math.max(page, 0), size < 1 ? 20 : size, Sort.by("createdAt").descending())));
    }

    @GetMapping("/{id}")
    public ResponseEntity<InvoiceResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(invoiceService.get(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<InvoiceResponse> update(@PathVariable Long id, @Valid @RequestBody InvoiceRequest request) {
        return ResponseEntity.ok(invoiceService.update(id, request));
    }

    @PostMapping("/{id}/send")
    public ResponseEntity<InvoiceResponse> send(@PathVariable Long id) {
        return ResponseEntity.ok(invoiceService.send(id));
    }

    @PostMapping("/{id}/pay")
    public ResponseEntity<InvoiceResponse> markPaid(@PathVariable Long id) {
        return ResponseEntity.ok(invoiceService.markPaid(id));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<InvoiceResponse> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(invoiceService.cancel(id));
    }
}
