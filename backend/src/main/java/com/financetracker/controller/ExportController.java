package com.financetracker.controller;

import com.financetracker.service.ExportService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/export")
@RequiredArgsConstructor
public class ExportController {

    private final ExportService exportService;

    @GetMapping("/csv")
    public ResponseEntity<ByteArrayResource> exportCsv(Authentication auth,
                                                        @RequestParam(required = false) Integer month,
                                                        @RequestParam(required = false) Integer year) throws IOException {
        LocalDate now = LocalDate.now();
        int m = month != null ? month : now.getMonthValue();
        int y = year != null ? year : now.getYear();
        byte[] data = exportService.exportCsv(auth.getName(), m, y);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=transactions-" + y + "-" + m + ".csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(new ByteArrayResource(data));
    }

    @GetMapping("/pdf")
    public ResponseEntity<ByteArrayResource> exportPdf(Authentication auth,
                                                        @RequestParam(required = false) Integer month,
                                                        @RequestParam(required = false) Integer year) throws IOException {
        LocalDate now = LocalDate.now();
        int m = month != null ? month : now.getMonthValue();
        int y = year != null ? year : now.getYear();
        byte[] data = exportService.exportPdf(auth.getName(), m, y);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=report-" + y + "-" + m + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(new ByteArrayResource(data));
    }
}
