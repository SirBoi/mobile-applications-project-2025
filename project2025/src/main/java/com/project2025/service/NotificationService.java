package com.project2025.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project2025.dto.NotificationResponse;
import com.project2025.model.Notification;
import com.project2025.model.RegisteredUser;
import com.project2025.model.Ride;
import com.project2025.repository.NotificationRepository;

@Service
public class NotificationService {

    private final NotificationRepository repository;

    public NotificationService(NotificationRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Notification create(RegisteredUser recipient, String message, Ride ride) {
        return repository.save(new Notification(recipient, message, ride));
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> findForUser(Long userId) {
        return repository.findByRecipientIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(NotificationResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public boolean markAsRead(Long notificationId) {
        return repository.findById(notificationId).map(n -> {
            n.setIsRead(true);
            repository.save(n);
            return true;
        }).orElse(false);
    }
}