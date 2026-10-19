package com.newasteralnx.controller;

import com.newasteralnx.constant.BookingStatus;
import com.newasteralnx.dto.request.BookingRequest;
import com.newasteralnx.dto.response.BookingResponse;
import com.newasteralnx.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingResponse> create(@Valid @RequestBody BookingRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookingService.create(request));
    }

    @GetMapping
    public ResponseEntity<Page<BookingResponse>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(bookingService.list(PageRequest.of(Math.max(page, 0), size < 1 ? 20 : size)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.get(id));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<BookingResponse> updateStatus(
            @PathVariable Long id,
            @RequestParam BookingStatus status) {
        return ResponseEntity.ok(bookingService.updateStatus(id, status));
    }
}
