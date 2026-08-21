package com.example.mobile_applications_project_2025.DTO;

import com.example.mobile_applications_project_2025.Model.Address;

import java.util.List;

public class OngoingRideResponseDTO {
    public Long rideId;
    public Long driverId;
    public String driverName;
    public String carModel;
    public String plateNumber;
    public Address origin;
    public Address destination;
    public List<String> passengerNames;
    public Float ridePrice;
    public String rideStartDatetime;
    public String estimatedFinishDatetime;
    public Long secondsElapsed;
    public Long secondsRemaining;
    public Integer progressPercent;
    public Boolean isPanicPressed;
}