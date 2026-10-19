package com.newasteralnx.controller;

import com.newasteralnx.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> summary() {
        return ResponseEntity.ok(reportService.summary());
    }

    @GetMapping("/{type}/csv")
    public ResponseEntity<byte[]> exportCsv(@PathVariable String type) {
        byte[] data = reportService.exportCsv(type);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + type + "-report.csv\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(data);
    }

    @GetMapping("/{type}/pdf")
    public ResponseEntity<byte[]> exportPdf(@PathVariable String type) {
        byte[] data = reportService.exportPdf(type);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + type + "-report.pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(data);
    }
}
