package com.project2025.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project2025.dto.ActiveDriverResponse;
import com.project2025.repository.DriverRepository;

@Service
public class DriverService {

    private final DriverRepository driverRepository;

    public DriverService(DriverRepository driverRepository) {
        this.driverRepository = driverRepository;
    }

    // 2.1.1 - vehicles shown on the map to (un)registered users.
    @Transactional(readOnly = true)
    public List<ActiveDriverResponse> findActiveDrivers() {
        return driverRepository.findByIsProfileActivatedTrue()
                .stream()
                .map(ActiveDriverResponse::from)
                .collect(Collectors.toList());
    }
}