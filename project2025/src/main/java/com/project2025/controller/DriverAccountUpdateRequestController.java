package com.project2025.controller;

import com.project2025.dto.UpdateDriver;
import com.project2025.model.DriverAccountUpdateRequest;
import com.project2025.service.DriverAccountUpdateRequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class DriverAccountUpdateRequestController {

    private final DriverAccountUpdateRequestService driverAccountUpdateRequestService;

    public DriverAccountUpdateRequestController(DriverAccountUpdateRequestService driverAccountUpdateRequestService) {
        this.driverAccountUpdateRequestService = driverAccountUpdateRequestService;
    }

    // Create or override the pending update request for this driver
    @PostMapping("/drivers/{driverId}/account-update-request")
    public ResponseEntity<Void> saveOrReplace(@PathVariable Long driverId,
                                              @RequestBody UpdateDriver dto) {
        driverAccountUpdateRequestService.saveOrReplace(driverId, dto);
        return ResponseEntity.ok().build();
    }

    // Check if a pending request exists for this driver
    @GetMapping("/drivers/{driverId}/account-update-request/exists")
    public ResponseEntity<Boolean> exists(@PathVariable Long driverId) {
        return ResponseEntity.ok(driverAccountUpdateRequestService.hasPendingRequest(driverId));
    }

    // List all pending requests
    @GetMapping("/admin/driver-account-update-requests")
    public ResponseEntity<List<DriverAccountUpdateRequest>> findAll() {
        return ResponseEntity.ok(driverAccountUpdateRequestService.findAll());
    }

    // Approve a specific driver's request
    @PostMapping("/admin/driver-account-update-requests/{driverId}/approve")
    public ResponseEntity<Void> approve(@PathVariable Long driverId) {
        driverAccountUpdateRequestService.approve(driverId);
        return ResponseEntity.ok().build();
    }

    // Reject a specific driver's request
    @PostMapping("/admin/driver-account-update-requests/{driverId}/reject")
    public ResponseEntity<Void> reject(@PathVariable Long driverId) {
        driverAccountUpdateRequestService.reject(driverId);
        return ResponseEntity.ok().build();
    }
}
