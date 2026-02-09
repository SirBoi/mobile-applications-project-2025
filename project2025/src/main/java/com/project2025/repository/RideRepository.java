package com.project2025.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project2025.enums.RideStatus;
import com.project2025.model.Ride;

@Repository
public interface RideRepository extends JpaRepository<Ride, Long> {

    List<Ride> findByPassengerId(Long passengerId);

    List<Ride> findByDriverId(Long driverId);

    List<Ride> findByStatus(RideStatus status);

    List<Ride> findByDriverIdAndStatus(Long driverId, RideStatus status);

    List<Ride> findByPassengerIdAndStatus(Long passengerId, RideStatus status);

    List<Ride> findByRideStartDatetimeBetween(LocalDateTime from, LocalDateTime to);
}
