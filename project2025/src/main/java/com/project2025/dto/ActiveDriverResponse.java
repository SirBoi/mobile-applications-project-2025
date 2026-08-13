package com.project2025.dto;

import com.project2025.enums.CarType;
import com.project2025.model.Driver;

// Lightweight shape of an active driver for the "vehicles on the map" screen
// (2.1.1). Deliberately excludes password/mail/etc. that live on the entity.
public class ActiveDriverResponse {

    private Long id;
    private String firstName;
    private String lastName;
    private String model;
    private CarType type;
    private Boolean isBabyFriendly;
    private Boolean isAnimalFriendly;
    private boolean busy;

    public static ActiveDriverResponse from(Driver driver) {
        ActiveDriverResponse r = new ActiveDriverResponse();
        r.id = driver.getId();
        r.firstName = driver.getFirstName();
        r.lastName = driver.getLastName();
        r.model = driver.getModel();
        r.type = driver.getType();
        r.isBabyFriendly = driver.getIsBabyFriendly();
        r.isAnimalFriendly = driver.getIsAnimalFriendly();
        // Available == free for a new ride, Unavailable == currently on a ride (busy)
        r.busy = driver.getCarStatus() == com.project2025.enums.CarStatus.Unavailable;
        return r;
    }

    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getModel() {
        return model;
    }

    public CarType getType() {
        return type;
    }

    public Boolean getIsBabyFriendly() {
        return isBabyFriendly;
    }

    public Boolean getIsAnimalFriendly() {
        return isAnimalFriendly;
    }

    public boolean isBusy() {
        return busy;
    }
}