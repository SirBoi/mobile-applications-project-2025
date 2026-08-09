package com.project2025.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.project2025.enums.RideStatus;
import com.project2025.model.Passenger;
import com.project2025.model.Ride;
import com.project2025.model.Route;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;

public final class RideSpecifications {

    private RideSpecifications() {}

    public static Specification<Ride> forPassengerHistory(
            Long passengerId,
            List<RideStatus> statuses,
            LocalDateTime from,
            LocalDateTime to,
            boolean favoritesOnly
    ) {
        return (root, query, cb) -> {
            query.distinct(true);

            Predicate predicate = cb.equal(root.get("passenger").get("id"), passengerId);

            if (statuses != null && !statuses.isEmpty()) {
                predicate = cb.and(predicate, root.get("status").in(statuses));
            }

            if (from != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("rideStartDatetime"), from));
            }

            if (to != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("rideStartDatetime"), to));
            }

            if (favoritesOnly) {
                Join<Ride, Passenger> passengerJoin = root.join("passenger", JoinType.INNER);
                Join<Passenger, Route> favJoin = passengerJoin.join("favouriteRoutes", JoinType.INNER);
                predicate = cb.and(predicate, cb.equal(favJoin.get("id"), root.get("route").get("id")));
            }

            return predicate;
        };
    }

    public static Specification<Ride> forDriverHistory(
            Long driverId,
            List<RideStatus> statuses,
            LocalDateTime from,
            LocalDateTime to
    ) {
        return (root, query, cb) -> {
            Predicate predicate = cb.equal(root.get("driver").get("id"), driverId);

            if (statuses != null && !statuses.isEmpty()) {
                predicate = cb.and(predicate, root.get("status").in(statuses));
            }

            if (from != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("rideStartDatetime"), from));
            }

            if (to != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("rideStartDatetime"), to));
            }

            return predicate;
        };
    }
}