package com.project2025.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project2025.dto.RideCreateRequest;
import com.project2025.dto.RideCreateWithCriteriaRequest;
import com.project2025.enums.CarStatus;
import com.project2025.enums.RideStatus;
import com.project2025.model.Driver;
import com.project2025.model.Passenger;
import com.project2025.model.RegisteredUser;
import com.project2025.model.Ride;
import com.project2025.repository.DriverRepository;
import com.project2025.repository.RegisteredUserRepository;
import com.project2025.repository.RideRepository;
import com.project2025.repository.RideSpecifications;
import com.project2025.repository.RouteRepository;

@Service
public class RideService {

    private final RideRepository repository;
    private final RouteRepository routeRepository;
    private final DriverRepository driverRepository;
    private final RegisteredUserRepository registeredUserRepository;
    private final RidePassengerNotificationService passengerNotificationService;

    public RideService(
            RideRepository repository,
            RouteRepository routeRepository,
            DriverRepository driverRepository,
            RegisteredUserRepository registeredUserRepository,
            RidePassengerNotificationService passengerNotificationService
    ) {
        this.repository = repository;
        this.routeRepository = routeRepository;
        this.driverRepository = driverRepository;
        this.registeredUserRepository = registeredUserRepository;
        this.passengerNotificationService = passengerNotificationService;
    }

    @Transactional
    public Ride create(Ride entity) {
        return repository.save(entity);
    }

    @Transactional
    public Optional<Ride> update(Long id, Ride updated) {
        if (!repository.existsById(id)) return Optional.empty();
        updated.setId(id);
        return Optional.of(repository.save(updated));
    }

    @Transactional(readOnly = true)
    public Optional<Ride> findOne(Long id) {
        return repository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Ride> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Page<Ride> findPassengerRidesPaged(
            Long passengerId,
            List<RideStatus> statuses,
            LocalDateTime from,
            LocalDateTime to,
            boolean favoritesOnly,
            Pageable pageable
    ) {
        return repository.findAll(
                RideSpecifications.forPassengerHistory(passengerId, statuses, from, to, favoritesOnly),
                pageable
        );
    }

    @Transactional(readOnly = true)
    public Page<Ride> findDriverRidesPaged(
            Long driverId,
            List<RideStatus> statuses,
            LocalDateTime from,
            LocalDateTime to,
            Pageable pageable
    ) {
        return repository.findAll(RideSpecifications.forDriverHistory(driverId, statuses, from, to), pageable);
    }

    @Transactional
    public boolean delete(Long id) {
        if (!repository.existsById(id)) return false;
        repository.deleteById(id);
        return true;
    }

    /**
     * Creates a ride and assigns the first available driver matching the
     * requested car type / baby-friendly / animal-friendly criteria.
     *
     * @return the saved Ride, or Optional.empty() if no eligible driver was found
     *         (caller should translate that into HTTP 409).
     */
    @Transactional
    public Optional<Ride> createWithDriverMatch(RideCreateWithCriteriaRequest request) {
        List<Driver> candidates = driverRepository.findByTypeAndCarStatusAndIsProfileActivatedTrue(
                request.getCarType(), CarStatus.Available);

        if (Boolean.TRUE.equals(request.getBabyFriendly())) {
            candidates.removeIf(d -> !Boolean.TRUE.equals(d.getIsBabyFriendly()));
        }
        if (Boolean.TRUE.equals(request.getAnimalFriendly())) {
            candidates.removeIf(d -> !Boolean.TRUE.equals(d.getIsAnimalFriendly()));
        }

        if (candidates.isEmpty()) {
            return Optional.empty();
        }

        Driver chosenDriver = candidates.get(0);

        RideCreateRequest reqRide = request.getRide();

        // Route is brand new (no id) coming from the client — must be
        // persisted first since Ride.route has no cascade configured.
        Ride ride = new Ride();
        if (reqRide.getRoute() != null) {
            ride.setRoute(routeRepository.save(reqRide.getRoute()));
        }

        ride.setOrigin(reqRide.getOrigin());
        ride.setDestination(reqRide.getDestination());
        ride.setRideDuration(reqRide.getRideDuration());
        ride.setRidePrice(reqRide.getRidePrice());
        ride.setPassenger(reqRide.getPassenger());

        List<Passenger> resolvedPassengers = new ArrayList<>();
        if (reqRide.getPassengers() != null) {
            for (String mail : reqRide.getPassengers()) {
                Optional<RegisteredUser> user = registeredUserRepository.findByMail(mail);
                if (user.isPresent() && user.get() instanceof Passenger) {
                    resolvedPassengers.add((Passenger) user.get());
                }
            }
        }
        ride.setPassengers(resolvedPassengers);

        ride.setDriver(chosenDriver);
        ride.setHasStarted(Boolean.FALSE);
        ride.setRideStartDatetime(
                reqRide.getRideStartDatetime() != null ? LocalDateTime.parse(reqRide.getRideStartDatetime()) : null
        );
        ride.setRideFinishDatetime(null);
        ride.setStatus(RideStatus.Scheduled);
        ride.setIsPanicPressed(Boolean.FALSE);

        Ride saved = repository.save(ride);

        chosenDriver.setCarStatus(CarStatus.Unavailable);
        driverRepository.save(chosenDriver);

        // 2.4.2 - obavesti ulinkovane putnike (mejl + in-app notifikacija)
        passengerNotificationService.notifyRideAccepted(saved);

        return Optional.of(saved);
    }
}