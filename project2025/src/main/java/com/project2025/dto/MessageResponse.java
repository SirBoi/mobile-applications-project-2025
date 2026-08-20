package com.project2025.dto;

import java.time.LocalDateTime;

import com.project2025.model.Message;

public class MessageResponse {
    private Long id;
    private Long chatId;
    private Long senderId;
    private String senderName;
    private boolean senderIsAdmin;
    private String text;
    private LocalDateTime datetime;

    public static MessageResponse from(Message m) {
        MessageResponse dto = new MessageResponse();
        dto.id = m.getId();
        dto.chatId = m.getChat() != null ? m.getChat().getId() : null;
        if (m.getSender() != null) {
            dto.senderId = m.getSender().getId();
            dto.senderName = (safe(m.getSender().getFirstName()) + " " + safe(m.getSender().getLastName())).trim();
            dto.senderIsAdmin = m.getSender() instanceof com.project2025.model.Admin;
        }
        dto.text = m.getText();
        dto.datetime = m.getDatetime();
        return dto;
    }

    private static String safe(String s) { return s == null ? "" : s; }

    public Long getId() { return id; }
    public Long getChatId() { return chatId; }
    public Long getSenderId() { return senderId; }
    public String getSenderName() { return senderName; }
    public boolean isSenderIsAdmin() { return senderIsAdmin; }
    public String getText() { return text; }
    public LocalDateTime getDatetime() { return datetime; }
}