package com.project2025.dto;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.project2025.enums.RideStatus;
import com.project2025.model.Address;
import com.project2025.model.Driver;
import com.project2025.model.Passenger;
import com.project2025.model.RegisteredUser;
import com.project2025.model.Ride;
import com.project2025.model.Route;

// Shapes a Ride entity into what the Android client expects.
// Main difference vs the entity: "passengers" is a list of e-mails,
// not full Passenger objects (client's Ride.passengers is List<String>).
public class RideResponse {

    private Long id;
    private Address origin;
    private Address destination;
    private Route route;
    private Integer rideDuration;
    private Float ridePrice;
    private Passenger passenger;
    private List<String> passengers;
    private Driver driver;
    private Boolean hasStarted;
    private LocalDateTime rideStartDatetime;
    private LocalDateTime rideFinishDatetime;
    private RideStatus status;
    private RegisteredUser cancelledBy;
    private Boolean isPanicPressed;

    public static RideResponse from(Ride ride) {
        RideResponse r = new RideResponse();
        r.id = ride.getId();
        r.origin = ride.getOrigin();
        r.destination = ride.getDestination();
        r.route = ride.getRoute();
        r.rideDuration = ride.getRideDuration();
        r.ridePrice = ride.getRidePrice();
        r.passenger = ride.getPassenger();
        r.passengers = ride.getPassengers() == null
                ? Collections.emptyList()
                : ride.getPassengers().stream().map(Passenger::getMail).collect(Collectors.toList());
        r.driver = ride.getDriver();
        r.hasStarted = ride.getHasStarted();
        r.rideStartDatetime = ride.getRideStartDatetime();
        r.rideFinishDatetime = ride.getRideFinishDatetime();
        r.status = ride.getStatus();
        r.cancelledBy = ride.getCancelledBy();
        r.isPanicPressed = ride.getIsPanicPressed();
        return r;
    }

    public Long getId() { return id; }
    public Address getOrigin() { return origin; }
    public Address getDestination() { return destination; }
    public Route getRoute() { return route; }
    public Integer getRideDuration() { return rideDuration; }
    public Float getRidePrice() { return ridePrice; }
    public Passenger getPassenger() { return passenger; }
    public List<String> getPassengers() { return passengers; }
    public Driver getDriver() { return driver; }
    public Boolean getHasStarted() { return hasStarted; }
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    public LocalDateTime getRideStartDatetime() { return rideStartDatetime; }
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    public LocalDateTime getRideFinishDatetime() { return rideFinishDatetime; }
    public RideStatus getStatus() { return status; }
    public RegisteredUser getCancelledBy() { return cancelledBy; }
    public Boolean getIsPanicPressed() { return isPanicPressed; }
}