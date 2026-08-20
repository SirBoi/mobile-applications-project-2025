package com.project2025.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project2025.model.DriverReport;

@Repository
public interface DriverReportRepository extends JpaRepository<DriverReport, Long> {

    List<DriverReport> findByDriverId(Long driverId);

    List<DriverReport> findByPassengerId(Long passengerId);

    List<DriverReport> findByRideId(Long rideId);
}	