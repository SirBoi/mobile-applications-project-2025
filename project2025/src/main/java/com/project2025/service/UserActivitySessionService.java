package com.project2025.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project2025.model.UserActivitySession;
import com.project2025.repository.UserActivitySessionRepository;

@Service
public class UserActivitySessionService {

    private final UserActivitySessionRepository repository;

    public UserActivitySessionService(UserActivitySessionRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public UserActivitySession create(UserActivitySession entity) {
        return repository.save(entity);
    }

    @Transactional
    public Optional<UserActivitySession> update(Long id, UserActivitySession updated) {
        if (!repository.existsById(id)) return Optional.empty();
        updated.setId(id);
        return Optional.of(repository.save(updated));
    }

    @Transactional(readOnly = true)
    public Optional<UserActivitySession> findOne(Long id) {
        return repository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<UserActivitySession> findAll() {
        return repository.findAll();
    }

    @Transactional
    public boolean delete(Long id) {
        if (!repository.existsById(id)) return false;
        repository.deleteById(id);
        return true;
    }
}
