package com.project2025.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project2025.model.DriverReport;
import com.project2025.repository.DriverReportRepository;

@Service
public class DriverReportService {

    private final DriverReportRepository repository;

    public DriverReportService(DriverReportRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public DriverReport create(DriverReport entity) {
        return repository.save(entity);
    }

    @Transactional
    public Optional<DriverReport> update(Long id, DriverReport updated) {
        if (!repository.existsById(id)) return Optional.empty();
        updated.setId(id);
        return Optional.of(repository.save(updated));
    }

    @Transactional(readOnly = true)
    public Optional<DriverReport> findOne(Long id) {
        return repository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<DriverReport> findAll() {
        return repository.findAll();
    }

    @Transactional
    public boolean delete(Long id) {
        if (!repository.existsById(id)) return false;
        repository.deleteById(id);
        return true;
    }
}
