package com.kdt.taskflow.controller;

import com.kdt.taskflow.dto.ReportRequest;
import com.kdt.taskflow.dto.ReportResponse;
import com.kdt.taskflow.service.ReportService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    /**
     * POST /api/report or /api/reports — Submit a new harmful user report.
     */
    @PostMapping({"/report", "/reports"})
    public ResponseEntity<ReportResponse> create(
            @Valid @RequestBody ReportRequest request,
            @AuthenticationPrincipal String currentUsername,
            UriComponentsBuilder uriBuilder) {

        ReportResponse created = reportService.create(request, currentUsername);

        URI location = uriBuilder
                .path("/api/report/{id}")
                .buildAndExpand(created.id())
                .toUri();

        return ResponseEntity.created(location).body(created);
    }

    /**
     * GET /api/report or /api/reports — List/filter reports by type, author, targetUsername, or keyword.
     */
    @GetMapping({"/report", "/reports"})
    public List<ReportResponse> search(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String targetUsername,
            @RequestParam(required = false) String keyword) {

        return reportService.search(type, author, targetUsername, keyword);
    }

    /**
     * GET /api/report/{id} or /api/reports/{id} — Get details of a single report by ID.
     */
    @GetMapping({"/report/{id}", "/reports/{id}"})
    public ReportResponse getById(@PathVariable Long id) {
        return reportService.getById(id);
    }

    /**
     * DELETE /api/report/{id} or /api/reports/{id} — Delete a report by ID.
     */
    @DeleteMapping({"/report/{id}", "/reports/{id}"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        reportService.delete(id);
    }
}
