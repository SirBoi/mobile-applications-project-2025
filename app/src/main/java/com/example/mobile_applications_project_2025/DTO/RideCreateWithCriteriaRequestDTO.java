package com.example.mobile_applications_project_2025.DTO;

import com.example.mobile_applications_project_2025.Model.Enumerator.CarType;
import com.example.mobile_applications_project_2025.Model.Ride;

public class RideCreateWithCriteriaRequestDTO {

    private Ride ride;

    private CarType carType;
    private Boolean babyFriendly = false;
    private Boolean animalFriendly = false;

    public RideCreateWithCriteriaRequestDTO() {}

    public Ride getRide() { return ride; }
    public void setRide(Ride ride) { this.ride = ride; }

    public CarType getCarType() { return carType; }
    public void setCarType(CarType carType) { this.carType = carType; }

    public Boolean getBabyFriendly() { return babyFriendly; }
    public void setBabyFriendly(Boolean babyFriendly) { this.babyFriendly = babyFriendly; }

    public Boolean getAnimalFriendly() { return animalFriendly; }
    public void setAnimalFriendly(Boolean animalFriendly) { this.animalFriendly = animalFriendly; }
}