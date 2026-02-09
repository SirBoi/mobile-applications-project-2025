package com.project2025.dto;

import com.project2025.enums.CarType;

public class UpdateDriver {

    private String firstName;
    private String lastName;
    private String address;
    private String phoneNumber;

    private String model;
    private CarType type;
    private String plateNumber;
    private Integer numberOfSeats;

    private Boolean isBabyFriendly;
    private Boolean isAnimalFriendly;

    public UpdateDriver() {
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
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

    public Boolean getIsBabyFriendly() {
        return isBabyFriendly;
    }

    public void setIsBabyFriendly(Boolean isBabyFriendly) {
        this.isBabyFriendly = isBabyFriendly;
    }

    public Boolean getIsAnimalFriendly() {
        return isAnimalFriendly;
    }

    public void setIsAnimalFriendly(Boolean isAnimalFriendly) {
        this.isAnimalFriendly = isAnimalFriendly;
    }
}
