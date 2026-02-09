package com.project2025.controller;

import java.util.List;

import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.project2025.dto.LoginRequest;
import com.project2025.model.RegisteredUser;
import com.project2025.service.RegisteredUserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class RegisteredUserController {

    private final RegisteredUserService service;

    public RegisteredUserController(RegisteredUserService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<RegisteredUser> create(@Valid @RequestBody RegisteredUser entity) {
        return ResponseEntity.ok(service.create(entity));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RegisteredUser> update(@PathVariable Long id, @RequestBody RegisteredUser updated) {
        return service.update(id, updated)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{id:\\d+}")
    public ResponseEntity<RegisteredUser> getOne(@PathVariable Long id) {
        return service.findOne(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<RegisteredUser>> getAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return service.delete(id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
    
    // Login endpoint
    @GetMapping("/login")
    public ResponseEntity<RegisteredUser> login(
            @RequestParam String mail,
            @RequestParam String password) {

        return service.findByMailAndPassword(mail, password)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    
    // Profile picture endpoints
    @PutMapping(path="/{id}/picture", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> uploadPicture(@PathVariable Long id,
                                              @RequestPart("file") MultipartFile file) {
        boolean ok = service.updateProfilePicture(id, file);
        return ok ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}/picture")
    public ResponseEntity<byte[]> getPicture(@PathVariable Long id) {
        var opt = service.loadProfilePicture(id);

        if (opt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        var img = opt.get();
        return ResponseEntity.ok()
                .contentType(img.mediaType())
                .cacheControl(CacheControl.noCache())
                .body(img.bytes());
    }
}
