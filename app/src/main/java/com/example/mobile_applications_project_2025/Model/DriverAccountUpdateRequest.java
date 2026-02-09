package com.example.mobile_applications_project_2025.Model;

import com.example.mobile_applications_project_2025.Model.Enumerator.CarType;

public class DriverAccountUpdateRequest {
    public Long id;
    public Driver driver;

    public String firstName;
    public String lastName;
    public String address;
    public String phoneNumber;

    public String model;
    public CarType type;
    public String plateNumber;
    public Integer numberOfSeats;

    public Boolean isBabyFriendly;
    public Boolean isAnimalFriendly;

    public DriverAccountUpdateRequest() {}
}