package com.example.mobile_applications_project_2025.DTO;

public class NotificationDTO {
    public Long id;
    public Long recipientId;
    public String message;
    public Long rideId;
    public String createdAt;
    public Boolean isRead;

    public Long getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public Long getRideId() {
        return rideId;
    }

    public Boolean getIsRead() {
        return isRead;
    }
}