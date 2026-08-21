package com.project2025.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.project2025.enums.RideStatus;
import com.project2025.model.Ride;

@Repository
public interface RideRepository extends JpaRepository<Ride, Long>, JpaSpecificationExecutor<Ride> {

    List<Ride> findByPassengerId(Long passengerId);

    List<Ride> findByDriverId(Long driverId);

    List<Ride> findByStatus(RideStatus status);

    List<Ride> findByDriverIdAndStatus(Long driverId, RideStatus status);

    List<Ride> findByDriverIdAndStatusOrderByRideStartDatetimeAsc(Long driverId, RideStatus status);

    List<Ride> findByPassengerIdAndStatus(Long passengerId, RideStatus status);

    List<Ride> findByRideStartDatetimeBetween(LocalDateTime from, LocalDateTime to);

    // 2.6.2 - "current" ride for a user, whether they created it (r.passenger)
    // or were ulinkovan as one of the additional passengers (r.passengers).
    @Query("SELECT DISTINCT r FROM Ride r LEFT JOIN r.passengers p " +
            "WHERE r.status = :status AND (r.passenger.id = :userId OR p.id = :userId) " +
            "ORDER BY r.rideStartDatetime DESC")
    List<Ride> findCurrentForUser(@Param("userId") Long userId, @Param("status") RideStatus status);
}