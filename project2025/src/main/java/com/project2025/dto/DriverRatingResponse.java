package com.project2025.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.project2025.model.DriverRating;

// Flat DTO (bez ugnjezdenih entiteta) - laksi za deserijalizaciju na mobilnoj strani.
public class DriverRatingResponse {

    private Long id;
    private Long rideId;
    private Long driverId;
    private Long passengerId;
    private Integer driverRating;
    private Integer vehicleRating;
    private String text;
    private LocalDateTime createdAt;

    public static DriverRatingResponse from(DriverRating r) {
        DriverRatingResponse dto = new DriverRatingResponse();
        dto.id = r.getId();
        dto.rideId = r.getRide() != null ? r.getRide().getId() : null;
        dto.driverId = r.getDriver() != null ? r.getDriver().getId() : null;
        dto.passengerId = r.getPassenger() != null ? r.getPassenger().getId() : null;
        dto.driverRating = r.getDriverRating();
        dto.vehicleRating = r.getVehicleRating();
        dto.text = r.getText();
        dto.createdAt = r.getCreatedAt();
        return dto;
    }

    public Long getId() { return id; }
    public Long getRideId() { return rideId; }
    public Long getDriverId() { return driverId; }
    public Long getPassengerId() { return passengerId; }
    public Integer getDriverRating() { return driverRating; }
    public Integer getVehicleRating() { return vehicleRating; }
    public String getText() { return text; }
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    public LocalDateTime getCreatedAt() { return createdAt; }
}