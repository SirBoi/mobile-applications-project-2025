package com.project2025.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.project2025.dto.ActiveTimeResponse;
import com.project2025.service.UserActivityService;

@RestController
@RequestMapping("/api/activity")
public class UserActivityController {

    private final UserActivityService userActivityService;

    public UserActivityController(UserActivityService userActivityService) {
        this.userActivityService = userActivityService;
    }

    // App calls every ~20 seconds while active
    @PostMapping("/{userId}/heartbeat")
    public ResponseEntity<Void> heartbeat(@PathVariable Long userId) {
        userActivityService.heartbeat(userId);
        return ResponseEntity.ok().build();
    }

    // Manual stop
    @PostMapping("/{userId}/stop")
    public ResponseEntity<Void> stop(@PathVariable Long userId) {
        userActivityService.stopSession(userId);
        return ResponseEntity.ok().build();
    }

    // Total active time in last 24h
    @GetMapping("/{userId}/last-24h")
    public ResponseEntity<ActiveTimeResponse> last24h(@PathVariable Long userId) {
        long seconds = userActivityService.getActiveSecondsLast24h(userId);
        return ResponseEntity.ok(new ActiveTimeResponse(userId, seconds));
    }
}
