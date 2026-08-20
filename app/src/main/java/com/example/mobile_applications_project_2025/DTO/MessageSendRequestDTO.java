package com.example.mobile_applications_project_2025.DTO;

public class MessageSendRequestDTO {
    public Long userId;
    public Long senderId;
    public String text;

    public MessageSendRequestDTO() {}

    public MessageSendRequestDTO(Long userId, Long senderId, String text) {
        this.userId = userId;
        this.senderId = senderId;
        this.text = text;
    }
}