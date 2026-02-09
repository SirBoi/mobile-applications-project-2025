package com.project2025.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.project2025.model.UserActivitySession;
import com.project2025.service.UserActivitySessionService;

@RestController
@RequestMapping("/api/useractivitysessions")
public class UserActivitySessionController {

    private final UserActivitySessionService service;

    public UserActivitySessionController(UserActivitySessionService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<UserActivitySession> create(@RequestBody UserActivitySession entity) {
        return ResponseEntity.ok(service.create(entity));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserActivitySession> update(@PathVariable Long id, @RequestBody UserActivitySession updated) {
        return service.update(id, updated)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserActivitySession> getOne(@PathVariable Long id) {
        return service.findOne(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<UserActivitySession>> getAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return service.delete(id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
