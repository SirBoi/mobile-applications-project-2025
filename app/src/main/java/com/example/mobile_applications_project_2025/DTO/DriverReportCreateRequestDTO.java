package com.example.mobile_applications_project_2025.DTO;

public class DriverReportCreateRequestDTO {
    public Long rideId;
    public Long driverId;
    public Long passengerId;
    public String text;

    public DriverReportCreateRequestDTO() {}

    public DriverReportCreateRequestDTO(Long rideId, Long driverId, Long passengerId, String text) {
        this.rideId = rideId;
        this.driverId = driverId;
        this.passengerId = passengerId;
        this.text = text;
    }
}