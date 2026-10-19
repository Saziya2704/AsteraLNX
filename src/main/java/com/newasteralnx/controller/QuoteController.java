package com.newasteralnx.controller;

import com.newasteralnx.dto.request.QuoteRequest;
import com.newasteralnx.dto.response.OrderResponse;
import com.newasteralnx.dto.response.QuoteResponse;
import com.newasteralnx.service.OrderService;
import com.newasteralnx.service.QuoteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/quotes")
@RequiredArgsConstructor
public class QuoteController {

    private final QuoteService quoteService;
    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<QuoteResponse> create(@Valid @RequestBody QuoteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(quoteService.create(request));
    }

    @GetMapping
    public ResponseEntity<Page<QuoteResponse>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(quoteService.list(PageRequest.of(Math.max(page, 0), size < 1 ? 20 : size, Sort.by("createdAt").descending())));
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuoteResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(quoteService.get(id));
    }

    @PostMapping("/{id}/send")
    public ResponseEntity<QuoteResponse> send(@PathVariable Long id) {
        return ResponseEntity.ok(quoteService.send(id));
    }

    @PostMapping("/{id}/accept")
    public ResponseEntity<QuoteResponse> accept(@PathVariable Long id) {
        return ResponseEntity.ok(quoteService.accept(id));
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<QuoteResponse> reject(@PathVariable Long id) {
        return ResponseEntity.ok(quoteService.reject(id));
    }

    @PostMapping("/{id}/convert-to-order")
    public ResponseEntity<OrderResponse> convertToOrder(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.convertFromQuote(id));
    }
}
