package com.project2025.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class UserActivityScheduler {

    private final UserActivityService userActivityService;

    public UserActivityScheduler(UserActivityService userActivityService) {
        this.userActivityService = userActivityService;
    }

    // Runs every 20 seconds; closes sessions that missed heartbeats for 60 seconds
    @Scheduled(fixedDelay = 20000)
    public void closeStaleSessions() {
        userActivityService.closeStaleSessions();
    }
}
