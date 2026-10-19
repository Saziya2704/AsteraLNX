package com.newasteralnx.controller;

import com.newasteralnx.dto.response.TripResponse;
import com.newasteralnx.service.TripService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/trips")
@RequiredArgsConstructor
public class TripController {

    private final TripService tripService;

    @GetMapping
    public ResponseEntity<Page<TripResponse>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(tripService.list(PageRequest.of(Math.max(page, 0), size < 1 ? 20 : size)));
    }

    @GetMapping("/mine")
    public ResponseEntity<Page<TripResponse>> mine(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(tripService.mine(PageRequest.of(Math.max(page, 0), size < 1 ? 20 : size)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TripResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(tripService.get(id));
    }
}
