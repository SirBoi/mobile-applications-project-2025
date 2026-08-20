package com.project2025.dto;

// 2.11 - Live podrska. "userId" je vlasnik chat-a (obican korisnik/vozac ciji je
// to chat sa supportom), "senderId" je stvarni posiljalac poruke (moze biti isti
// kao userId, ili bilo koji admin - svaki admin ima pristup istom chatu).
public class MessageSendRequest {
    private Long userId;
    private Long senderId;
    private String text;

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getSenderId() { return senderId; }
    public void setSenderId(Long senderId) { this.senderId = senderId; }
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
}