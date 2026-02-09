package com.project2025.repository;

import com.project2025.model.DriverAccountUpdateRequest;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DriverAccountUpdateRequestRepository extends JpaRepository<DriverAccountUpdateRequest, Long> {
	@Query("select r from DriverAccountUpdateRequest r join fetch r.driver")
    List<DriverAccountUpdateRequest> findAllWithDriver();
}
