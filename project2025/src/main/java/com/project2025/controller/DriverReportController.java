package com.project2025.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.project2025.dto.DriverReportCreateRequest;
import com.project2025.model.DriverReport;
import com.project2025.service.DriverReportService;

@RestController
@RequestMapping("/api/driverreports")
public class DriverReportController {

    private final DriverReportService service;

    public DriverReportController(DriverReportService service) {
        this.service = service;
    }

    // 2.6.2 - putnik iz mobilne app-e šalje prijavu preko rideId/driverId/passengerId.
    @PostMapping("/create")
    public ResponseEntity<DriverReport> createFromRequest(@RequestBody DriverReportCreateRequest request) {
        return service.createFromRequest(request)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @GetMapping("/ride/{rideId}")
    public ResponseEntity<List<DriverReport>> getByRide(@PathVariable Long rideId) {
        return ResponseEntity.ok(service.findByRide(rideId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DriverReport> update(@PathVariable Long id, @RequestBody DriverReport updated) {
        return service.update(id, updated)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DriverReport> getOne(@PathVariable Long id) {
        return service.findOne(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<DriverReport>> getAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return service.delete(id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}