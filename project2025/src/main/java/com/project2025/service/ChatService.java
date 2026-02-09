package com.project2025.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project2025.model.Chat;
import com.project2025.repository.ChatRepository;

@Service
public class ChatService {

    private final ChatRepository chatRepository;

    public ChatService(ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }

    @Transactional
    public Chat create(Chat chat) {
        return chatRepository.save(chat);
    }

    @Transactional
    public Optional<Chat> update(Long id, Chat updated) {
        return chatRepository.findById(id).map(existing -> {
            existing.setUser(updated.getUser());
            existing.setMessages(updated.getMessages());
            existing.setLastMessageDateTime(updated.getLastMessageDateTime());
            return chatRepository.save(existing);
        });
    }

    @Transactional(readOnly = true)
    public Optional<Chat> findOne(Long id) {
        return chatRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Chat> findAll() {
        return chatRepository.findAll();
    }

    @Transactional
    public boolean delete(Long id) {
        if (!chatRepository.existsById(id)) return false;
        chatRepository.deleteById(id);
        return true;
    }
}
