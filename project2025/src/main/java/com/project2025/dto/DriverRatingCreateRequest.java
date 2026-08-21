package com.project2025.dto;

// 2.8 - putnik salje samo rideId + ocene; driver/passenger se izvode
// sa servera na osnovu voznje (passenger = onaj ko je porucio voznju),
// da korisnik ne bi mogao da "podmetne" tudju ocenu.
public class DriverRatingCreateRequest {

    private Long rideId;
    private Integer driverRating;
    private Integer vehicleRating;
    private String text;

    public Long getRideId() {
        return rideId;
    }

    public void setRideId(Long rideId) {
        this.rideId = rideId;
    }

    public Integer getDriverRating() {
        return driverRating;
    }

    public void setDriverRating(Integer driverRating) {
        this.driverRating = driverRating;
    }

    public Integer getVehicleRating() {
        return vehicleRating;
    }

    public void setVehicleRating(Integer vehicleRating) {
        this.vehicleRating = vehicleRating;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}