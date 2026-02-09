package com.project2025.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project2025.model.DriverRating;

@Repository
public interface DriverRatingRepository extends JpaRepository<DriverRating, Long> {

    List<DriverRating> findByDriverId(Long driverId);

    List<DriverRating> findByPassengerId(Long passengerId);

    Optional<DriverRating> findByDriverIdAndPassengerId(Long driverId, Long passengerId);
}
