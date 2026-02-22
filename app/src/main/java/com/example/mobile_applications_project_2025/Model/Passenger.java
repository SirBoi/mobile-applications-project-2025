package com.example.mobile_applications_project_2025.Model;

import com.example.mobile_applications_project_2025.Model.Enumerator.Role;
import com.example.mobile_applications_project_2025.Model.Enumerator.UserStatus;

import java.util.List;

public class Passenger extends RegisteredUser {
    public List<Route> favouriteRoutes;

    public Passenger() {

    }

    public Passenger(List<Route> favouriteRoutes) {
        this.favouriteRoutes = favouriteRoutes;
    }

    public Passenger(Long id, Role role, String mail, String password, String firstName, String lastName, String address, String phoneNumber, String picture, Integer dailyActiveMinutes, Boolean isBlocked, String blockMessage, UserStatus status, List<Route> favouriteRoutes) {
        super(id, role, mail, password, firstName, lastName, address, phoneNumber, picture, dailyActiveMinutes, isBlocked, blockMessage, status, null, null, null);
        this.favouriteRoutes = favouriteRoutes;
    }

    public List<Route> getFavouriteRoutes() {
        return favouriteRoutes;
    }

    public void setFavouriteRoutes(List<Route> favouriteRoutes) {
        this.favouriteRoutes = favouriteRoutes;
    }

    @Override
    public String toString() {
        return "Passenger{" +
                "favouriteRoutes=" + favouriteRoutes +
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
