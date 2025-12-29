package com.example.mobile_applications_project_2025.Model;

import com.example.mobile_applications_project_2025.Model.Enumerator.CarStatus;
import com.example.mobile_applications_project_2025.Model.Enumerator.CarType;
import com.example.mobile_applications_project_2025.Model.Enumerator.Role;

public class Driver extends User {
    public String carModel;
    public CarType carType;
    public String plateNumber;
    public Integer numberOfSeats;
    public Boolean isBabyFriendly;
    public Boolean isAnimalFriendly;
    public CarStatus carStatus;
    public Boolean isProfileActivated;

    public Driver(String id, Role role, String mail, String password, String firstName, String lastName, String address, String phoneNumber, String picture, Integer dailyActiveMinutes, String carModel, CarType carType, String plateNumber, Integer numberOfSeats, Boolean isBabyFriendly, Boolean isAnimalFriendly) {
        super(id, role, mail, password, firstName, lastName, address, phoneNumber, picture, dailyActiveMinutes);
        this.carModel = carModel;
        this.carType = carType;
        this.plateNumber = plateNumber;
        this.numberOfSeats = numberOfSeats;
        this.isBabyFriendly = isBabyFriendly;
        this.isAnimalFriendly = isAnimalFriendly;
        this.carStatus = CarStatus.available;
        this.isProfileActivated = false;
    }
}
