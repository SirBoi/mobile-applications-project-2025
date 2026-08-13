package com.project2025.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project2025.dto.ActiveDriverResponse;
import com.project2025.service.DriverService;

@RestController
@RequestMapping("/api/drivers")
public class DriverController {

    private final DriverService service;

    public DriverController(DriverService service) {
        this.service = service;
    }

    // 2.1.1 - list of currently active vehicles (busy or free) for the map.
    @GetMapping("/active")
    public ResponseEntity<List<ActiveDriverResponse>> getActiveDrivers() {
        return ResponseEntity.ok(service.findActiveDrivers());
    }
}