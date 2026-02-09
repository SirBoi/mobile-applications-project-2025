package com.project2025.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.project2025.model.Config;
import com.project2025.service.ConfigService;

@RestController
@RequestMapping("/api/config")
public class ConfigController {

    private final ConfigService service;

    public ConfigController(ConfigService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<Config> get() {
        return service.get().map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Config> upsert(@RequestBody Config config) {
        return ResponseEntity.ok(service.upsert(config));
    }

    @PutMapping
    public ResponseEntity<Config> update(@RequestBody Config config) {
        return ResponseEntity.ok(service.upsert(config));
    }

    @DeleteMapping
    public ResponseEntity<Void> delete() {
        service.delete();
        return ResponseEntity.noContent().build();
    }
}
