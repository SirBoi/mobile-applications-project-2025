package com.project2025.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.project2025.enums.CarType;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RideCreateWithCriteriaRequest {

    private RideCreateRequest ride;
    private CarType carType;
    private Boolean babyFriendly = false;
    private Boolean animalFriendly = false;

    public RideCreateRequest getRide() { return ride; }
    public void setRide(RideCreateRequest ride) { this.ride = ride; }

    public CarType getCarType() { return carType; }
    public void setCarType(CarType carType) { this.carType = carType; }

    public Boolean getBabyFriendly() { return babyFriendly; }
    public void setBabyFriendly(Boolean babyFriendly) { this.babyFriendly = babyFriendly; }

    public Boolean getAnimalFriendly() { return animalFriendly; }
    public void setAnimalFriendly(Boolean animalFriendly) { this.animalFriendly = animalFriendly; }
}