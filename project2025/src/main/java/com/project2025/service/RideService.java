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
import com.project2025.model.Route;
import com.project2025.repository.DriverRepository;
import com.project2025.repository.RegisteredUserRepository;
import com.project2025.repository.RideRepository;
import com.project2025.repository.RideSpecifications;
import com.project2025.repository.RouteRepository;

@Service
public class RideService {

    // Kad klijent ne pošalje trajanje vožnje (mobilna app to trenutno ne
    // radi), koristimo kratko podrazumevano trajanje od 1 minuta. U skladu je
    // sa nefunkcionalnim zahtevom da se vreme radi demonstracije svede na
    // minute/sekunde, a usput omogućava lako end-to-end testiranje 2.6.2/2.7
    // bez čekanja na "pravu" procenu trajanja (2.1.2, Student3).
    private static final int DEFAULT_TEST_DURATION_SECONDS = 60;

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

        Integer duration = reqRide.getRideDuration();
        if (duration == null || duration <= 0) {
            duration = DEFAULT_TEST_DURATION_SECONDS;
        }

        ride.setOrigin(reqRide.getOrigin());
        ride.setDestination(reqRide.getDestination());
        ride.setRideDuration(duration);
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

    // ---------------------------------------------------------------
    // 2.6.2 / 2.7 support - start, finish, cancel and "current ride" lookup.
    // start/cancel su formalno tuđi zadaci (2.6.1 / 2.5), ali mobilna app već
    // poziva ove endpoint-e (dialog_ride_actions), pa su ovde implementirani
    // u osnovnoj verziji da bi ceo tok vožnje mogao da se testira. Kada
    // 2.6.1/2.5 budu rađeni kako treba (npr. provera da li su svi putnici
    // ušli, obavezan razlog otkazivanja), ovo je mesto za doradu.
    // ---------------------------------------------------------------

    @Transactional
    public Optional<Ride> startRide(Long id) {
        return repository.findById(id).map(ride -> {
            ride.setStatus(RideStatus.Started);
            ride.setHasStarted(Boolean.TRUE);
            ride.setRideStartDatetime(LocalDateTime.now());
            if (ride.getRideDuration() == null || ride.getRideDuration() <= 0) {
                ride.setRideDuration(DEFAULT_TEST_DURATION_SECONDS);
            }
            return repository.save(ride);
        });
    }

    @Transactional
    public Optional<Ride> finishRide(Long id) {
        return repository.findById(id).map(ride -> {
            ride.setStatus(RideStatus.Finished);
            ride.setRideFinishDatetime(LocalDateTime.now());
            Ride saved = repository.save(ride);

            freeDriverIfNoUpcomingRide(ride.getDriver());
            // 2.4.2 - obavesti ulinkovane putnike da je vožnja završena.
            passengerNotificationService.notifyRideFinished(saved);

            return saved;
        });
    }

    @Transactional
    public Optional<Ride> cancelRide(Long id) {
        return repository.findById(id).map(ride -> {
            ride.setStatus(RideStatus.Cancelled);
            Ride saved = repository.save(ride);
            freeDriverIfNoUpcomingRide(ride.getDriver());
            return saved;
        });
    }

    private void freeDriverIfNoUpcomingRide(Driver driver) {
        if (driver == null) return;

        boolean hasUpcoming =
                !repository.findByDriverIdAndStatus(driver.getId(), RideStatus.Started).isEmpty()
                || !repository.findByDriverIdAndStatus(driver.getId(), RideStatus.Scheduled).isEmpty();

        driver.setCarStatus(hasUpcoming ? CarStatus.Unavailable : CarStatus.Available);
        driverRepository.save(driver);
    }

    @Transactional(readOnly = true)
    public Optional<Ride> findCurrentForPassenger(Long passengerId) {
        List<Ride> rides = repository.findCurrentForUser(passengerId, RideStatus.Started);
        return rides.isEmpty() ? Optional.empty() : Optional.of(rides.get(0));
    }

    @Transactional(readOnly = true)
    public Optional<Ride> findCurrentForDriver(Long driverId) {
        List<Ride> rides = repository.findByDriverIdAndStatus(driverId, RideStatus.Started);
        return rides.isEmpty() ? Optional.empty() : Optional.of(rides.get(0));
    }

    @Transactional(readOnly = true)
    public Optional<Ride> findNextScheduledForDriver(Long driverId) {
        List<Ride> rides = repository.findByDriverIdAndStatusOrderByRideStartDatetimeAsc(driverId, RideStatus.Scheduled);
        return rides.isEmpty() ? Optional.empty() : Optional.of(rides.get(0));
    }

    // 2.4.3 - dodavanje/uklanjanje rute vožnje iz omiljenih ruta putnika.
    @Transactional
    public boolean addFavorite(Long rideId, Long passengerId) {
        return updateFavorite(rideId, passengerId, true);
    }

    @Transactional
    public boolean removeFavorite(Long rideId, Long passengerId) {
        return updateFavorite(rideId, passengerId, false);
    }

    private boolean updateFavorite(Long rideId, Long passengerId, boolean add) {
        Optional<Ride> rideOpt = repository.findById(rideId);
        Optional<RegisteredUser> userOpt = registeredUserRepository.findById(passengerId);

        if (rideOpt.isEmpty() || userOpt.isEmpty()) return false;
        if (!(userOpt.get() instanceof Passenger)) return false;

        Ride ride = rideOpt.get();
        Route route = ride.getRoute();
        if (route == null) return false;

        Passenger passenger = (Passenger) userOpt.get();
        List<Route> favorites = passenger.getFavouriteRoutes();
        if (favorites == null) {
            favorites = new ArrayList<>();
            passenger.setFavouriteRoutes(favorites);
        }

        boolean alreadyFavorite = favorites.stream()
                .anyMatch(r -> r.getId() != null && r.getId().equals(route.getId()));

        if (add && !alreadyFavorite) {
            favorites.add(route);
        } else if (!add) {
            favorites.removeIf(r -> r.getId() != null && r.getId().equals(route.getId()));
        }

        registeredUserRepository.save(passenger);
        return true;
    }
}