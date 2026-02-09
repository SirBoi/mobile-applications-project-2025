package com.project2025.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project2025.model.Route;
import com.project2025.repository.RouteRepository;

@Service
public class RouteService {

    private final RouteRepository repository;

    public RouteService(RouteRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Route create(Route entity) {
        return repository.save(entity);
    }

    @Transactional
    public Optional<Route> update(Long id, Route updated) {
        if (!repository.existsById(id)) return Optional.empty();
        updated.setId(id);
        return Optional.of(repository.save(updated));
    }

    @Transactional(readOnly = true)
    public Optional<Route> findOne(Long id) {
        return repository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Route> findAll() {
        return repository.findAll();
    }

    @Transactional
    public boolean delete(Long id) {
        if (!repository.existsById(id)) return false;
        repository.deleteById(id);
        return true;
    }
}
