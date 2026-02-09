package com.project2025.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project2025.model.Route;

@Repository
public interface RouteRepository extends JpaRepository<Route, Long> {
}
