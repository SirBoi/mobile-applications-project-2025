package com.example.mobile_applications_project_2025.Model;

import java.time.LocalDateTime;
import java.util.List;

public class Chat {
    public Long id;
    public RegisteredUser user;
    public List<Message> messages;
    public LocalDateTime lastMessageDateTime;

    public Chat() {

    }

    public Chat(Long id, RegisteredUser user, List<Message> messages, LocalDateTime lastMessageDateTime) {
        this.id = id;
        this.user = user;
        this.messages = messages;
        this.lastMessageDateTime = lastMessageDateTime;
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

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public LocalDateTime getLastMessageDateTime() {
        return lastMessageDateTime;
    }

    public void setLastMessageDateTime(LocalDateTime lastMessageDateTime) {
        this.lastMessageDateTime = lastMessageDateTime;
    }

    @Override
    public String toString() {
        return "Chat{" +
                "id=" + id +
                ", user=" + user +
                ", messages=" + messages +
                ", lastMessageDateTime=" + lastMessageDateTime +
                '}';
    }
}
