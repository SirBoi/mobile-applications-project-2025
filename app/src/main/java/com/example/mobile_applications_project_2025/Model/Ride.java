package com.example.mobile_applications_project_2025.Model;

import com.example.mobile_applications_project_2025.Model.Enumerator.RideStatus;

import java.time.LocalDateTime;
import java.util.List;

public class Ride {
    public Long id;
    public Address origin;
    public Address destination;
    public Route route;
    public Integer rideDuration;
    public Float ridePrice;
    public Passenger passenger;
    public List<String> passengers;
    public Driver driver;
    public Boolean hasStarted;
    public String rideStartDatetime;
    public String rideFinishDatetime;
    public RideStatus status;
    public RegisteredUser cancelledBy;
    public Boolean isPanicPressed;

    public Ride() {

    }

    public Ride(Long id, Address origin, Address destination, Route route, Integer rideDuration, Float ridePrice, Passenger passenger, List<String> passengers, Driver driver, Boolean hasStarted, String rideStartDatetime, String rideFinishDatetime, RideStatus status, RegisteredUser cancelledBy, Boolean isPanicPressed) {
        this.id = id;
        this.origin = origin;
        this.destination = destination;
        this.route = route;
        this.rideDuration = rideDuration;
        this.ridePrice = ridePrice;
        this.passenger = passenger;
        this.passengers = passengers;
        this.driver = driver;
        this.hasStarted = hasStarted;
        this.rideStartDatetime = rideStartDatetime;
        this.rideFinishDatetime = rideFinishDatetime;
        this.status = status;
        this.cancelledBy = cancelledBy;
        this.isPanicPressed = isPanicPressed;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Address getOrigin() {
        return origin;
    }

    public void setOrigin(Address origin) {
        this.origin = origin;
    }

    public Address getDestination() {
        return destination;
    }

    public void setDestination(Address destination) {
        this.destination = destination;
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public Integer getRideDuration() {
        return rideDuration;
    }

    public void setRideDuration(Integer rideDuration) {
        this.rideDuration = rideDuration;
    }

    public Float getRidePrice() {
        return ridePrice;
    }

    public void setRidePrice(Float ridePrice) {
        this.ridePrice = ridePrice;
    }

    public Passenger getPassenger() {
        return passenger;
    }

    public void setPassenger(Passenger passenger) {
        this.passenger = passenger;
    }

    public List<String> getPassengers() {
        return passengers;
    }

    public void setPassengers(List<String> passengers) {
        this.passengers = passengers;
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public Boolean getHasStarted() {
        return hasStarted;
    }

    public void setHasStarted(Boolean hasStarted) {
        this.hasStarted = hasStarted;
    }

    public String getRideStartDatetime() {
        return rideStartDatetime;
    }

    public void setRideStartDatetime(String rideStartDatetime) {
        this.rideStartDatetime = rideStartDatetime;
    }

    public String getRideFinishDatetime() {
        return rideFinishDatetime;
    }

    public void setRideFinishDatetime(String rideFinishDatetime) {
        this.rideFinishDatetime = rideFinishDatetime;
    }

    public RideStatus getStatus() {
        return status;
    }

    public void setStatus(RideStatus status) {
        this.status = status;
    }

    public RegisteredUser getCancelledBy() {
        return cancelledBy;
    }

    public void setCancelledBy(RegisteredUser cancelledBy) {
        this.cancelledBy = cancelledBy;
    }

    public Boolean getPanicPressed() {
        return isPanicPressed;
    }

    public void setPanicPressed(Boolean panicPressed) {
        isPanicPressed = panicPressed;
    }

    @Override
    public String toString() {
        return "Ride{" +
                "id=" + id +
                ", origin=" + origin +
                ", destination=" + destination +
                ", route=" + route +
                ", rideDuration=" + rideDuration +
                ", ridePrice=" + ridePrice +
                ", passenger=" + passenger +
                ", passengers=" + passengers +
                ", driver=" + driver +
                ", hasStarted=" + hasStarted +
                ", rideStartDatetime=" + rideStartDatetime +
                ", rideFinishDatetime=" + rideFinishDatetime +
                ", status=" + status +
                ", cancelledBy=" + cancelledBy +
                ", isPanicPressed=" + isPanicPressed +
                '}';
    }
}
