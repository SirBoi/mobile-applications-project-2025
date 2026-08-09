package com.project2025.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.project2025.dto.RideCreateWithCriteriaRequest;
import com.project2025.dto.RideResponse;
import com.project2025.enums.RideStatus;
import com.project2025.model.Ride;
import com.project2025.service.RideService;

@RestController
@RequestMapping("/api/rides")
public class RideController {

    private final RideService service;

    public RideController(RideService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Ride> create(@RequestBody Ride entity) {
        return ResponseEntity.ok(service.create(entity));
    }

    @PostMapping("/create")
    public ResponseEntity<Ride> createWithDriverMatch(@RequestBody RideCreateWithCriteriaRequest request) {
        return service.createWithDriverMatch(request)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.CONFLICT).build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Ride> update(@PathVariable Long id, @RequestBody Ride updated) {
        return service.update(id, updated)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ride> getOne(@PathVariable Long id) {
        return service.findOne(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Ride>> getAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/passenger/{passengerId}")
    public ResponseEntity<Page<RideResponse>> getPassengerRidesPaged(
            @PathVariable Long passengerId,
            @RequestParam(required = false) List<String> statuses,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            @RequestParam(defaultValue = "false") boolean favoritesOnly,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        List<RideStatus> statusEnums = new ArrayList<>();
        if (statuses != null) {
            for (String s : statuses) {
                statusEnums.add(RideStatus.valueOf(s));
            }
        }

        LocalDateTime fromDt = (from != null && !from.isBlank()) ? LocalDateTime.parse(from) : null;
        LocalDateTime toDt = (to != null && !to.isBlank()) ? LocalDateTime.parse(to) : null;

        Page<Ride> result = service.findPassengerRidesPaged(
                passengerId, statusEnums, fromDt, toDt, favoritesOnly, PageRequest.of(page, size)
        );

        return ResponseEntity.ok(result.map(RideResponse::from));
    }

    @GetMapping("/driver/{driverId}")
    public ResponseEntity<Page<RideResponse>> getDriverRidesPaged(
            @PathVariable Long driverId,
            @RequestParam(required = false) List<String> statuses,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        List<RideStatus> statusEnums = new ArrayList<>();
        if (statuses != null) {
            for (String s : statuses) {
                statusEnums.add(RideStatus.valueOf(s));
            }
        }

        LocalDateTime fromDt = (from != null && !from.isBlank()) ? LocalDateTime.parse(from) : null;
        LocalDateTime toDt = (to != null && !to.isBlank()) ? LocalDateTime.parse(to) : null;

        Page<Ride> result = service.findDriverRidesPaged(driverId, statusEnums, fromDt, toDt, PageRequest.of(page, size));

        return ResponseEntity.ok(result.map(RideResponse::from));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return service.delete(id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}