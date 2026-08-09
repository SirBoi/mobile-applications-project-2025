package com.project2025.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project2025.enums.CarStatus;
import com.project2025.enums.CarType;
import com.project2025.model.Driver;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {

    List<Driver> findByTypeAndCarStatusAndIsProfileActivatedTrue(CarType type, CarStatus carStatus);
}