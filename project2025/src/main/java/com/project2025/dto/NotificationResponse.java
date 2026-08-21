package com.project2025.dto;

import java.time.LocalDateTime;

import com.project2025.model.Notification;

public class NotificationResponse {

    private Long id;
    private Long recipientId;
    private String message;
    private Long rideId;
    private LocalDateTime createdAt;
    private Boolean isRead;

    public static NotificationResponse from(Notification n) {
        NotificationResponse r = new NotificationResponse();
        r.id = n.getId();
        r.recipientId = n.getRecipient() != null ? n.getRecipient().getId() : null;
        r.message = n.getMessage();
        r.rideId = n.getRide() != null ? n.getRide().getId() : null;
        r.createdAt = n.getCreatedAt();
        r.isRead = n.getIsRead();
        return r;
    }

    public Long getId() {
        return id;
    }

    public Long getRecipientId() {
        return recipientId;
    }

    public String getMessage() {
        return message;
    }

    public Long getRideId() {
        return rideId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Boolean getIsRead() {
        return isRead;
    }
}