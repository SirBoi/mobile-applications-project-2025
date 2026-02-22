package com.example.mobile_applications_project_2025.Model;

import com.example.mobile_applications_project_2025.Model.Enumerator.CarStatus;
import com.example.mobile_applications_project_2025.Model.Enumerator.CarType;
import com.example.mobile_applications_project_2025.Model.Enumerator.Role;
import com.example.mobile_applications_project_2025.Model.Enumerator.UserStatus;

public class Driver extends RegisteredUser {
    public String model;
    public CarType type;
    public String plateNumber;
    public Integer numberOfSeats;
    public Boolean isBabyFriendly;
    public Boolean isAnimalFriendly;
    public CarStatus carStatus;
    public Boolean isProfileActivated;

    public Driver() {

    }

    public Driver(String model, CarType type, String plateNumber, Integer numberOfSeats, Boolean isBabyFriendly, Boolean isAnimalFriendly, CarStatus carStatus, Boolean isProfileActivated) {
        this.model = model;
        this.type = type;
        this.plateNumber = plateNumber;
        this.numberOfSeats = numberOfSeats;
        this.isBabyFriendly = isBabyFriendly;
        this.isAnimalFriendly = isAnimalFriendly;
        this.carStatus = carStatus;
        this.isProfileActivated = isProfileActivated;
    }

    public Driver(Long id, Role role, String mail, String password, String firstName, String lastName, String address, String phoneNumber, String picture, Integer dailyActiveMinutes, Boolean isBlocked, String blockMessage, UserStatus status, String model, CarType type, String plateNumber, Integer numberOfSeats, Boolean isBabyFriendly, Boolean isAnimalFriendly, CarStatus carStatus, Boolean isProfileActivated) {
        super(id, role, mail, password, firstName, lastName, address, phoneNumber, picture, dailyActiveMinutes, isBlocked, blockMessage, status, null, null, null);
        this.model = model;
        this.type = type;
        this.plateNumber = plateNumber;
        this.numberOfSeats = numberOfSeats;
        this.isBabyFriendly = isBabyFriendly;
        this.isAnimalFriendly = isAnimalFriendly;
        this.carStatus = carStatus;
        this.isProfileActivated = isProfileActivated;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public CarType getType() {
        return type;
    }

    public void setType(CarType type) {
        this.type = type;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public Integer getNumberOfSeats() {
        return numberOfSeats;
    }

    public void setNumberOfSeats(Integer numberOfSeats) {
        this.numberOfSeats = numberOfSeats;
    }

    public Boolean getBabyFriendly() {
        return isBabyFriendly;
    }

    public void setBabyFriendly(Boolean babyFriendly) {
        isBabyFriendly = babyFriendly;
    }

    public Boolean getAnimalFriendly() {
        return isAnimalFriendly;
    }

    public void setAnimalFriendly(Boolean animalFriendly) {
        isAnimalFriendly = animalFriendly;
    }

    public CarStatus getCarStatus() {
        return carStatus;
    }

    public void setCarStatus(CarStatus carStatus) {
        this.carStatus = carStatus;
    }

    public Boolean getProfileActivated() {
        return isProfileActivated;
    }

    public void setProfileActivated(Boolean profileActivated) {
        isProfileActivated = profileActivated;
    }

    @Override
    public String toString() {
        return "Driver{" +
                "model='" + model + '\'' +
                ", type=" + type +
                ", plateNumber='" + plateNumber + '\'' +
                ", numberOfSeats=" + numberOfSeats +
                ", isBabyFriendly=" + isBabyFriendly +
                ", isAnimalFriendly=" + isAnimalFriendly +
                ", carStatus=" + carStatus +
                ", isProfileActivated=" + isProfileActivated +
                ", id=" + id +
                ", role=" + role +
                ", mail='" + mail + '\'' +
                ", password='" + password + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", address='" + address + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", picture='" + picture + '\'' +
                ", dailyActiveMinutes=" + dailyActiveMinutes +
                ", isBlocked=" + isBlocked +
                ", blockMessage='" + blockMessage + '\'' +
                ", status=" + status +
                '}';
    }
}
