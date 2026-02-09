package com.example.mobile_applications_project_2025.DTO;

import com.example.mobile_applications_project_2025.Model.Enumerator.CarType;

public class UpdateDriverDTO {

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

    public UpdateDriverDTO() { }

    public UpdateDriverDTO(String firstName, String lastName, String address, String phoneNumber,
                           String model, CarType type, String plateNumber, Integer numberOfSeats,
                           Boolean isBabyFriendly, Boolean isAnimalFriendly) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.model = model;
        this.type = type;
        this.plateNumber = plateNumber;
        this.numberOfSeats = numberOfSeats;
        this.isBabyFriendly = isBabyFriendly;
        this.isAnimalFriendly = isAnimalFriendly;
    }
}
