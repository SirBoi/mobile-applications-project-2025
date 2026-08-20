package com.example.mobile_applications_project_2025.DTO;

public class DriverRatingCreateRequestDTO {
    public Long rideId;
    public Integer driverRating;
    public Integer vehicleRating;
    public String text;

    public DriverRatingCreateRequestDTO() {}

    public DriverRatingCreateRequestDTO(Long rideId, Integer driverRating, Integer vehicleRating, String text) {
        this.rideId = rideId;
        this.driverRating = driverRating;
        this.vehicleRating = vehicleRating;
        this.text = text;
    }
}