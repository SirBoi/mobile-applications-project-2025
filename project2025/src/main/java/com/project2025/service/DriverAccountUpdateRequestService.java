package com.project2025.service;

import com.project2025.dto.UpdateDriver;
import com.project2025.model.Driver;
import com.project2025.model.DriverAccountUpdateRequest;
import com.project2025.model.RegisteredUser;
import com.project2025.repository.DriverAccountUpdateRequestRepository;
import com.project2025.repository.RegisteredUserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DriverAccountUpdateRequestService {

    private final DriverAccountUpdateRequestRepository requestRepository;
    private final RegisteredUserRepository registeredUserRepository;

    public DriverAccountUpdateRequestService(
            DriverAccountUpdateRequestRepository requestRepository,
            RegisteredUserRepository registeredUserRepository
    ) {
        this.requestRepository = requestRepository;
        this.registeredUserRepository = registeredUserRepository;
    }

    @Transactional
    public void saveOrReplace(Long driverId, UpdateDriver dto) {

    	Driver driver = (Driver)registeredUserRepository.findById(driverId).orElseThrow(() -> new RuntimeException("Driver not found"));

        DriverAccountUpdateRequest request = requestRepository.findById(driverId).orElseGet(DriverAccountUpdateRequest::new);

        request.setDriver(driver);

        request.setFirstName(dto.getFirstName());
        request.setLastName(dto.getLastName());
        request.setAddress(dto.getAddress());
        request.setPhoneNumber(dto.getPhoneNumber());
        request.setModel(dto.getModel());
        request.setType(dto.getType());
        request.setPlateNumber(dto.getPlateNumber());
        request.setNumberOfSeats(dto.getNumberOfSeats());
        request.setIsBabyFriendly(dto.getIsBabyFriendly());
        request.setIsAnimalFriendly(dto.getIsAnimalFriendly());

        requestRepository.save(request);
    }

    public boolean hasPendingRequest(Long driverId) {
        return requestRepository.existsById(driverId);
    }

    public List<DriverAccountUpdateRequest> findAll() {
        return requestRepository.findAllWithDriver();
    }

    @Transactional
    public void approve(Long driverId) {

        DriverAccountUpdateRequest request = requestRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Update request not found"));

        Driver driver = request.getDriver();

        if (request.getFirstName() != null)
            driver.setFirstName(request.getFirstName());

        if (request.getLastName() != null)
            driver.setLastName(request.getLastName());

        if (request.getAddress() != null)
            driver.setAddress(request.getAddress());

        if (request.getPhoneNumber() != null)
            driver.setPhoneNumber(request.getPhoneNumber());

        if (request.getModel() != null)
            driver.setModel(request.getModel());

        if (request.getType() != null)
            driver.setType(request.getType());

        if (request.getPlateNumber() != null)
            driver.setPlateNumber(request.getPlateNumber());

        if (request.getNumberOfSeats() != null)
            driver.setNumberOfSeats(request.getNumberOfSeats());

        if (request.getIsBabyFriendly() != null)
            driver.setIsBabyFriendly(request.getIsBabyFriendly());

        if (request.getIsAnimalFriendly() != null)
            driver.setIsAnimalFriendly(request.getIsAnimalFriendly());

        registeredUserRepository.save(driver);
        requestRepository.deleteById(driverId);
    }

    @Transactional
    public void reject(Long driverId) {
        requestRepository.deleteById(driverId);
    }
}
