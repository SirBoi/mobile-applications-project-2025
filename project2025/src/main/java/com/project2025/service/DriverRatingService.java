package com.project2025.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project2025.model.DriverRating;
import com.project2025.repository.DriverRatingRepository;

@Service
public class DriverRatingService {

    private final DriverRatingRepository repository;

    public DriverRatingService(DriverRatingRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public DriverRating create(DriverRating entity) {
        return repository.save(entity);
    }

    @Transactional
    public Optional<DriverRating> update(Long id, DriverRating updated) {
        if (!repository.existsById(id)) return Optional.empty();
        updated.setId(id);
        return Optional.of(repository.save(updated));
    }

    @Transactional(readOnly = true)
    public Optional<DriverRating> findOne(Long id) {
        return repository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<DriverRating> findAll() {
        return repository.findAll();
    }

    @Transactional
    public boolean delete(Long id) {
        if (!repository.existsById(id)) return false;
        repository.deleteById(id);
        return true;
    }
}
