package com.thebrian31.coworking.controller;

import com.thebrian31.coworking.dto.OccupancyReportResponse;
import com.thebrian31.coworking.service.OccupancyService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final OccupancyService occupancyService;

    public ReportController(OccupancyService occupancyService) {
        this.occupancyService = occupancyService;
    }

    @GetMapping("/occupancy")
    @PreAuthorize("hasRole('ADMIN')")
    public OccupancyReportResponse occupancy(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime to
    ) {
        return occupancyService.report(
                from, to);
    }
}
