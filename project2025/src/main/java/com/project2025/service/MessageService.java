package com.project2025.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project2025.dto.MessageResponse;
import com.project2025.dto.MessageSendRequest;
import com.project2025.model.Chat;
import com.project2025.model.Message;
import com.project2025.model.RegisteredUser;
import com.project2025.repository.ChatRepository;
import com.project2025.repository.MessageRepository;
import com.project2025.repository.RegisteredUserRepository;

@Service
public class MessageService {

    private final MessageRepository repository;
    private final ChatRepository chatRepository;
    private final RegisteredUserRepository userRepository;
    private final ChatService chatService;

    public MessageService(MessageRepository repository, ChatRepository chatRepository,
            RegisteredUserRepository userRepository, ChatService chatService) {
        this.repository = repository;
        this.chatRepository = chatRepository;
        this.userRepository = userRepository;
        this.chatService = chatService;
    }

    @Transactional
    public Message create(Message entity) {
        return repository.save(entity);
    }

    @Transactional
    public Optional<Message> update(Long id, Message updated) {
        if (!repository.existsById(id)) return Optional.empty();
        updated.setId(id);
        return Optional.of(repository.save(updated));
    }

    @Transactional(readOnly = true)
    public Optional<Message> findOne(Long id) {
        return repository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Message> findAll() {
        return repository.findAll();
    }

    @Transactional
    public boolean delete(Long id) {
        if (!repository.existsById(id)) return false;
        repository.deleteById(id);
        return true;
    }

    // 2.11 - slanje poruke u chat konkretnog korisnika (podrska). Radi i za
    // korisnika/vozaca (userId == senderId) i za admina koji odgovara
    // (senderId = admin, userId = korisnik ciji je chat).
    @Transactional
    public Optional<MessageResponse> sendMessage(MessageSendRequest request) {
        if (request.getUserId() == null || request.getSenderId() == null
                || request.getText() == null || request.getText().trim().isEmpty()) {
            return Optional.empty();
        }

        Optional<Chat> chatOpt = chatService.getOrCreateForUser(request.getUserId());
        if (chatOpt.isEmpty()) return Optional.empty();
        Chat chat = chatOpt.get();

        Optional<RegisteredUser> senderOpt = userRepository.findById(request.getSenderId());
        if (senderOpt.isEmpty()) return Optional.empty();

        LocalDateTime now = LocalDateTime.now();
        Message message = new Message(chat, senderOpt.get(), request.getText().trim(), now);
        Message saved = repository.save(message);

        chat.setLastMessageDateTime(now);
        chatRepository.save(chat);

        return Optional.of(MessageResponse.from(saved));
    }

    @Transactional(readOnly = true)
    public List<MessageResponse> findByChatId(Long chatId) {
        return repository.findByChatIdOrderByDatetimeAsc(chatId).stream()
                .map(MessageResponse::from)
                .collect(Collectors.toList());
    }
}