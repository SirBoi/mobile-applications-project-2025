package com.project2025.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.project2025.dto.ChatResponse;
import com.project2025.dto.MessageResponse;
import com.project2025.model.Chat;
import com.project2025.service.ChatService;
import com.project2025.service.MessageService;

@RestController
@RequestMapping("/api/chats")
public class ChatController {

    private final ChatService service;
    private final MessageService messageService;

    public ChatController(ChatService service, MessageService messageService) {
        this.service = service;
        this.messageService = messageService;
    }

    @PostMapping
    public ResponseEntity<Chat> create(@RequestBody Chat entity) {
        return ResponseEntity.ok(service.create(entity));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Chat> update(@PathVariable Long id, @RequestBody Chat updated) {
        return service.update(id, updated)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Chat> getOne(@PathVariable Long id) {
        return service.findOne(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 2.11 - lista svih chat-ova (admin panel: 24/7 live support inbox).
    @GetMapping
    public ResponseEntity<List<ChatResponse>> getAll() {
        List<ChatResponse> chats = service.findAll().stream().map(ChatResponse::from).collect(Collectors.toList());
        return ResponseEntity.ok(chats);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return service.delete(id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    // 2.11 - vraca (ili kreira) chat konkretnog korisnika/vozaca sa supportom.
    @GetMapping("/user/{userId}")
    public ResponseEntity<ChatResponse> getOrCreateForUser(@PathVariable Long userId) {
        return service.getOrCreateForUser(userId)
                .map(chat -> ResponseEntity.ok(ChatResponse.from(chat)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 2.11 - istorija poruka za chat konkretnog korisnika, hronoloski.
    @GetMapping("/user/{userId}/messages")
    public ResponseEntity<List<MessageResponse>> getMessagesForUser(@PathVariable Long userId) {
        return service.getOrCreateForUser(userId)
                .map(chat -> ResponseEntity.ok(messageService.findByChatId(chat.getId())))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}