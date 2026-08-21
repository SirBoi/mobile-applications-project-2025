package com.project2025.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project2025.dto.DriverReportCreateRequest;
import com.project2025.model.Driver;
import com.project2025.model.DriverReport;
import com.project2025.model.Passenger;
import com.project2025.model.RegisteredUser;
import com.project2025.model.Ride;
import com.project2025.repository.DriverRepository;
import com.project2025.repository.DriverReportRepository;
import com.project2025.repository.RegisteredUserRepository;
import com.project2025.repository.RideRepository;

@Service
public class DriverReportService {

    private final DriverReportRepository repository;
    private final RideRepository rideRepository;
    private final DriverRepository driverRepository;
    private final RegisteredUserRepository registeredUserRepository;

    public DriverReportService(
            DriverReportRepository repository,
            RideRepository rideRepository,
            DriverRepository driverRepository,
            RegisteredUserRepository registeredUserRepository
    ) {
        this.repository = repository;
        this.rideRepository = rideRepository;
        this.driverRepository = driverRepository;
        this.registeredUserRepository = registeredUserRepository;
    }

    // 2.6.2 - putnik prijavljuje nekonzistentnost vozača tokom vožnje.
    @Transactional
    public Optional<DriverReport> createFromRequest(DriverReportCreateRequest request) {
        Optional<Ride> rideOpt = rideRepository.findById(request.getRideId());
        Optional<Driver> driverOpt = driverRepository.findById(request.getDriverId());
        Optional<RegisteredUser> userOpt = registeredUserRepository.findById(request.getPassengerId());

        if (rideOpt.isEmpty() || driverOpt.isEmpty() || userOpt.isEmpty()) return Optional.empty();
        if (!(userOpt.get() instanceof Passenger)) return Optional.empty();

        DriverReport report = new DriverReport(driverOpt.get(), (Passenger) userOpt.get(), rideOpt.get(), request.getText());
        return Optional.of(repository.save(report));
    }

    @Transactional(readOnly = true)
    public List<DriverReport> findByRide(Long rideId) {
        return repository.findByRideId(rideId);
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