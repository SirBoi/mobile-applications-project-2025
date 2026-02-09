package com.example.mobile_applications_project_2025.Model;

import java.time.LocalDateTime;

public class UserActivitySession {
    public Long id;
    public RegisteredUser user;
    public LocalDateTime startedAt;
    public LocalDateTime endedAt;
    public LocalDateTime lastHeartbeatAt;
    
    public UserActivitySession() {
        
    }

    public UserActivitySession(Long id, RegisteredUser user, LocalDateTime startedAt, LocalDateTime endedAt, LocalDateTime lastHeartbeatAt) {
        this.id = id;
        this.user = user;
        this.startedAt = startedAt;
        this.endedAt = endedAt;
        this.lastHeartbeatAt = lastHeartbeatAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RegisteredUser getUser() {
        return user;
    }

    public void setUser(RegisteredUser user) {
        this.user = user;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public LocalDateTime getEndedAt() {
        return endedAt;
    }

    public void setEndedAt(LocalDateTime endedAt) {
        this.endedAt = endedAt;
    }

    public LocalDateTime getLastHeartbeatAt() {
        return lastHeartbeatAt;
    }

    public void setLastHeartbeatAt(LocalDateTime lastHeartbeatAt) {
        this.lastHeartbeatAt = lastHeartbeatAt;
    }

    @Override
    public String toString() {
        return "UserActivitySession{" +
                "id=" + id +
                ", user=" + user +
                ", startedAt=" + startedAt +
                ", endedAt=" + endedAt +
                ", lastHeartbeatAt=" + lastHeartbeatAt +
                '}';
    }
}
