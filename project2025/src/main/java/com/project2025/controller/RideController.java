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
    public ResponseEntity<RideResponse> create(@RequestBody Ride entity) {
        return ResponseEntity.ok(RideResponse.from(service.create(entity)));
    }

    @PostMapping("/create")
    public ResponseEntity<RideResponse> createWithDriverMatch(@RequestBody RideCreateWithCriteriaRequest request) {
        return service.createWithDriverMatch(request)
                .map(ride -> ResponseEntity.ok(RideResponse.from(ride)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.CONFLICT).build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<RideResponse> update(@PathVariable Long id, @RequestBody Ride updated) {
        return service.update(id, updated)
                .map(ride -> ResponseEntity.ok(RideResponse.from(ride)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RideResponse> getOne(@PathVariable Long id) {
        return service.findOne(id)
                .map(ride -> ResponseEntity.ok(RideResponse.from(ride)))
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

    // --- 2.6.2 / 2.7 support (videti napomenu u RideService) ---
    // Napomena: vraćamo RideResponse (ne sirov Ride entitet) jer entitet ima
    // "passengers" kao listu punih Passenger objekata, dok mobilna app
    // (Model.Ride.passengers) očekuje listu mejlova (List<String>) - to je
    // izazivalo "IllegalStateException: Expected a string..." na klijentu.

    @PatchMapping("/{id}/start")
    public ResponseEntity<RideResponse> start(@PathVariable Long id) {
        return service.startRide(id)
                .map(ride -> ResponseEntity.ok(RideResponse.from(ride)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/finish")
    public ResponseEntity<RideResponse> finish(@PathVariable Long id) {
        return service.finishRide(id)
                .map(ride -> ResponseEntity.ok(RideResponse.from(ride)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<RideResponse> cancel(@PathVariable Long id) {
        return service.cancelRide(id)
                .map(ride -> ResponseEntity.ok(RideResponse.from(ride)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/{rideId}/favorite/{passengerId}")
    public ResponseEntity<Void> addFavorite(@PathVariable Long rideId, @PathVariable Long passengerId) {
        return service.addFavorite(rideId, passengerId)
                ? ResponseEntity.ok().build()
                : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{rideId}/favorite/{passengerId}")
    public ResponseEntity<Void> removeFavorite(@PathVariable Long rideId, @PathVariable Long passengerId) {
        return service.removeFavorite(rideId, passengerId)
                ? ResponseEntity.ok().build()
                : ResponseEntity.notFound().build();
    }

    @GetMapping("/passenger/{passengerId}/current")
    public ResponseEntity<RideResponse> currentForPassenger(@PathVariable Long passengerId) {
        return service.findCurrentForPassenger(passengerId)
                .map(ride -> ResponseEntity.ok(RideResponse.from(ride)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/driver/{driverId}/current")
    public ResponseEntity<RideResponse> currentForDriver(@PathVariable Long driverId) {
        return service.findCurrentForDriver(driverId)
                .map(ride -> ResponseEntity.ok(RideResponse.from(ride)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/driver/{driverId}/next")
    public ResponseEntity<RideResponse> nextForDriver(@PathVariable Long driverId) {
        return service.findNextScheduledForDriver(driverId)
                .map(ride -> ResponseEntity.ok(RideResponse.from(ride)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 2.13 - Admin: pregled vožnji koje trenutno traju, pretraga po imenu vozača.
    @GetMapping("/ongoing")
    public ResponseEntity<List<com.project2025.dto.OngoingRideResponse>> getOngoing(
            @RequestParam(required = false) String driverName
    ) {
        List<com.project2025.dto.OngoingRideResponse> result = service.findOngoingByDriverName(driverName).stream()
                .map(com.project2025.dto.OngoingRideResponse::from)
                .collect(java.util.stream.Collectors.toList());
        return ResponseEntity.ok(result);
    }
}