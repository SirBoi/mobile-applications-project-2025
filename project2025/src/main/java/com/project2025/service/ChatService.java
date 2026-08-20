package com.project2025.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project2025.model.Chat;
import com.project2025.model.RegisteredUser;
import com.project2025.repository.ChatRepository;
import com.project2025.repository.RegisteredUserRepository;

@Service
public class ChatService {

    private final ChatRepository chatRepository;
    private final RegisteredUserRepository userRepository;

    public ChatService(ChatRepository chatRepository, RegisteredUserRepository userRepository) {
        this.chatRepository = chatRepository;
        this.userRepository = userRepository;
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

    // 2.11 - svaki (ne-admin) korisnik ima tacno jedan chat sa supportom; kreira
    // se lenjo, prvi put kad neko pokusa da otvori/posalje poruku u njemu.
    @Transactional
    public Optional<Chat> getOrCreateForUser(Long userId) {
        Optional<Chat> existing = chatRepository.findByUserId(userId);
        if (existing.isPresent()) return existing;

        Optional<RegisteredUser> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) return Optional.empty();

        Chat chat = new Chat(userOpt.get(), new ArrayList<>(), LocalDateTime.now());
        return Optional.of(chatRepository.save(chat));
    }
}