package com.newasteralnx.controller;

import com.newasteralnx.constant.InquiryStatus;
import com.newasteralnx.dto.request.InquiryRequest;
import com.newasteralnx.dto.response.InquiryResponse;
import com.newasteralnx.service.InquiryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inquiries")
@RequiredArgsConstructor
public class InquiryController {

    private final InquiryService inquiryService;

    @PostMapping
    public ResponseEntity<InquiryResponse> create(@Valid @RequestBody InquiryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(inquiryService.create(request));
    }

    @GetMapping
    public ResponseEntity<Page<InquiryResponse>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(inquiryService.list(PageRequest.of(Math.max(page, 0), size < 1 ? 20 : size, Sort.by("createdAt").descending())));
    }

    @GetMapping("/{id}")
    public ResponseEntity<InquiryResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(inquiryService.get(id));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<InquiryResponse> updateStatus(
            @PathVariable Long id,
            @RequestParam InquiryStatus status) {
        return ResponseEntity.ok(inquiryService.updateStatus(id, status));
    }
}
