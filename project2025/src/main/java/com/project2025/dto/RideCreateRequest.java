package com.project2025.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.project2025.model.Address;
import com.project2025.model.Passenger;
import com.project2025.model.Route;

// Mirrors the "ride" object sent by the Android client on ride creation.
// NOTE: "passengers" here is a list of e-mails (as the client sends it),
// not full Passenger entities like the Ride JPA entity expects — the
// service layer resolves these to actual Passenger entities.
@JsonIgnoreProperties(ignoreUnknown = true)
public class RideCreateRequest {

    private Address origin;
    private Address destination;
    private Route route;
    private Integer rideDuration;
    private Float ridePrice;
    private Passenger passenger;
    private List<String> passengers;
    private String rideStartDatetime;

    public Address getOrigin() { return origin; }
    public void setOrigin(Address origin) { this.origin = origin; }

    public Address getDestination() { return destination; }
    public void setDestination(Address destination) { this.destination = destination; }

    public Route getRoute() { return route; }
    public void setRoute(Route route) { this.route = route; }

    public Integer getRideDuration() { return rideDuration; }
    public void setRideDuration(Integer rideDuration) { this.rideDuration = rideDuration; }

    public Float getRidePrice() { return ridePrice; }
    public void setRidePrice(Float ridePrice) { this.ridePrice = ridePrice; }

    public Passenger getPassenger() { return passenger; }
    public void setPassenger(Passenger passenger) { this.passenger = passenger; }

    public List<String> getPassengers() { return passengers; }
    public void setPassengers(List<String> passengers) { this.passengers = passengers; }

    public String getRideStartDatetime() { return rideStartDatetime; }
    public void setRideStartDatetime(String rideStartDatetime) { this.rideStartDatetime = rideStartDatetime; }
}