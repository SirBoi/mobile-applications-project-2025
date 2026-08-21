package com.project2025.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project2025.dto.DriverRatingCreateRequest;
import com.project2025.dto.DriverRatingResponse;
import com.project2025.enums.RideStatus;
import com.project2025.model.DriverRating;
import com.project2025.model.Passenger;
import com.project2025.model.Ride;
import com.project2025.repository.DriverRatingRepository;
import com.project2025.repository.RideRepository;

@Service
public class DriverRatingService {

    private static final int RATING_DEADLINE_DAYS = 3;

    private final DriverRatingRepository repository;
    private final RideRepository rideRepository;

    public DriverRatingService(DriverRatingRepository repository, RideRepository rideRepository) {
        this.repository = repository;
        this.rideRepository = rideRepository;
    }

    public enum Status {
        OK, RIDE_NOT_FOUND, RIDE_NOT_FINISHED, ALREADY_RATED, DEADLINE_EXPIRED, INVALID_RATING
    }

    public static class CreateResult {
        public final Status status;
        public final DriverRatingResponse response;

        private CreateResult(Status status, DriverRatingResponse response) {
            this.status = status;
            this.response = response;
        }

        static CreateResult error(Status status) {
            return new CreateResult(status, null);
        }

        static CreateResult ok(DriverRatingResponse response) {
            return new CreateResult(Status.OK, response);
        }
    }

    // 2.8 - Nakon zavrsetka voznje, osoba koja je poruzila voznju (ride.passenger)
    // moze da oceni vozaca i vozilo (1-5) uz opcioni komentar, odmah ili
    // naknadno iz istorije, ali najkasnije 3 dana od zavrsetka voznje.
    @Transactional
    public CreateResult createFromRequest(DriverRatingCreateRequest request) {
        if (request.getDriverRating() == null || request.getVehicleRating() == null
                || !isValidScore(request.getDriverRating()) || !isValidScore(request.getVehicleRating())) {
            return CreateResult.error(Status.INVALID_RATING);
        }

        Optional<Ride> rideOpt = rideRepository.findById(request.getRideId());
        if (rideOpt.isEmpty()) {
            return CreateResult.error(Status.RIDE_NOT_FOUND);
        }
        Ride ride = rideOpt.get();

        if (ride.getStatus() != RideStatus.Finished || ride.getRideFinishDatetime() == null) {
            return CreateResult.error(Status.RIDE_NOT_FINISHED);
        }

        if (repository.existsByRideId(ride.getId())) {
            return CreateResult.error(Status.ALREADY_RATED);
        }

        LocalDateTime deadline = ride.getRideFinishDatetime().plusDays(RATING_DEADLINE_DAYS);
        if (LocalDateTime.now().isAfter(deadline)) {
            return CreateResult.error(Status.DEADLINE_EXPIRED);
        }

        Passenger passenger = ride.getPassenger();
        if (passenger == null || ride.getDriver() == null) {
            return CreateResult.error(Status.RIDE_NOT_FOUND);
        }

        DriverRating rating = new DriverRating(ride, ride.getDriver(), passenger,
                request.getDriverRating(), request.getVehicleRating(), request.getText());

        DriverRating saved = repository.save(rating);
        return CreateResult.ok(DriverRatingResponse.from(saved));
    }

    private boolean isValidScore(Integer score) {
        return score >= 1 && score <= 5;
    }

    @Transactional(readOnly = true)
    public Optional<DriverRatingResponse> findByRide(Long rideId) {
        return repository.findByRideId(rideId).map(DriverRatingResponse::from);
    }

    @Transactional(readOnly = true)
    public List<DriverRatingResponse> findByDriver(Long driverId) {
        return repository.findByDriverId(driverId).stream()
                .map(DriverRatingResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DriverRatingResponse> findAll() {
        return repository.findAll().stream()
                .map(DriverRatingResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public boolean delete(Long id) {
        if (!repository.existsById(id)) return false;
        repository.deleteById(id);
        return true;
    }
}