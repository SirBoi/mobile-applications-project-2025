package com.example.mobile_applications_project_2025.Model;

import java.time.LocalDateTime;

public class Message {
    public Long id;
    public Chat chat;
    public RegisteredUser sender;
    public String text;
    public LocalDateTime datetime;
    
    public Message() {
        
    }

    public Message(Long id, Chat chat, RegisteredUser sender, String text, LocalDateTime datetime) {
        this.id = id;
        this.chat = chat;
        this.sender = sender;
        this.text = text;
        this.datetime = datetime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Chat getChat() {
        return chat;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }

    public RegisteredUser getSender() {
        return sender;
    }

    public void setSender(RegisteredUser sender) {
        this.sender = sender;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public LocalDateTime getDatetime() {
        return datetime;
    }

    public void setDatetime(LocalDateTime datetime) {
        this.datetime = datetime;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", chat=" + chat +
                ", sender=" + sender +
                ", text='" + text + '\'' +
                ", datetime=" + datetime +
                '}';
    }
}
