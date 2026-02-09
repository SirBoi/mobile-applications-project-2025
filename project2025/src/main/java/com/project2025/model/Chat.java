package com.project2025.model;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;

@Entity
public class Chat {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@OneToOne
    @JoinColumn(name = "user_id", nullable = false)
	private RegisteredUser user;
	
	@OneToMany(mappedBy = "chat", cascade = CascadeType.ALL)
	private List<Message> messages;
	
	@Column(name="lastMessageDateTime")
	private LocalDateTime lastMessageDateTime;
	
	public Chat() {
		super();
	}
	
	public Chat(RegisteredUser user, List<Message> messages,
			LocalDateTime lastMessageDateTime) {
		super();
		this.user = user;
		this.messages = messages;
		this.lastMessageDateTime = lastMessageDateTime;
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
		return "Chat [user=" + user + ", messages=" + messages
				+ ", lastMessageDateTime=" + lastMessageDateTime + "]";
	}
}
