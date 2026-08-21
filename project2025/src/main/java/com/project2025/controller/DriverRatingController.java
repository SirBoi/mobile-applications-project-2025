package com.project2025.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.project2025.dto.DriverRatingCreateRequest;
import com.project2025.dto.DriverRatingResponse;
import com.project2025.service.DriverRatingService;
import com.project2025.service.DriverRatingService.CreateResult;

@RestController
@RequestMapping("/api/driverratings")
public class DriverRatingController {

    private final DriverRatingService service;

    public DriverRatingController(DriverRatingService service) {
        this.service = service;
    }

    // 2.8 - putnik ostavlja ocenu (odmah nakon voznje ili naknadno iz istorije).
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody DriverRatingCreateRequest request) {
        CreateResult result = service.createFromRequest(request);
        switch (result.status) {
            case OK:
                return ResponseEntity.ok(result.response);
            case RIDE_NOT_FOUND:
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Ride not found.");
            case RIDE_NOT_FINISHED:
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ride is not finished yet.");
            case ALREADY_RATED:
                return ResponseEntity.status(HttpStatus.CONFLICT).body("This ride has already been rated.");
            case DEADLINE_EXPIRED:
                return ResponseEntity.status(HttpStatus.GONE).body("Rating deadline (3 days) has expired.");
            case INVALID_RATING:
            default:
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ratings must be between 1 and 5.");
        }
    }

    @GetMapping("/ride/{rideId}")
    public ResponseEntity<DriverRatingResponse> getByRide(@PathVariable Long rideId) {
        return service.findByRide(rideId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/driver/{driverId}")
    public ResponseEntity<List<DriverRatingResponse>> getByDriver(@PathVariable Long driverId) {
        return ResponseEntity.ok(service.findByDriver(driverId));
    }

    @GetMapping
    public ResponseEntity<List<DriverRatingResponse>> getAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return service.delete(id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}