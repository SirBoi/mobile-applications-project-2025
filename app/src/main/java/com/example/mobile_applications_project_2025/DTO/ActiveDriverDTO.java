package com.example.mobile_applications_project_2025.DTO;

import com.example.mobile_applications_project_2025.Model.Enumerator.CarType;

// Mirrors backend's ActiveDriverResponse (GET api/drivers/active) - used for
// showing active vehicles on the map (2.1.1).
public class ActiveDriverDTO {
    public Long id;
    public String firstName;
    public String lastName;
    public String model;
    public CarType type;
    public Boolean isBabyFriendly;
    public Boolean isAnimalFriendly;
    public boolean busy;

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