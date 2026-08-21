package com.project2025.dto;

import java.time.LocalDateTime;

import com.project2025.model.Chat;

public class ChatResponse {
    private Long id;
    private Long userId;
    private String userName;
    private LocalDateTime lastMessageDateTime;

    public static ChatResponse from(Chat c) {
        ChatResponse dto = new ChatResponse();
        dto.id = c.getId();
        if (c.getUser() != null) {
            dto.userId = c.getUser().getId();
            String fn = c.getUser().getFirstName() != null ? c.getUser().getFirstName() : "";
            String ln = c.getUser().getLastName() != null ? c.getUser().getLastName() : "";
            dto.userName = (fn + " " + ln).trim();
        }
        dto.lastMessageDateTime = c.getLastMessageDateTime();
        return dto;
    }

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public String getUserName() { return userName; }
    public LocalDateTime getLastMessageDateTime() { return lastMessageDateTime; }
}