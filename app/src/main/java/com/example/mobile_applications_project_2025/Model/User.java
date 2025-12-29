package com.example.mobile_applications_project_2025.Model;

import com.example.mobile_applications_project_2025.Model.Enumerator.Role;

public class User {
    public String id;
    public Role role;
    public String mail;
    public String password;
    public String firstName;
    public String lastName;
    public String address;
    public String phoneNumber;
    public String picture;
    public Integer dailyActiveMinutes;
    public Boolean isActive;
    public Boolean isBlocked;
    public String blockMessage;

    public User() {

    }

    public User(String id, Role role, String mail, String password, String firstName, String lastName, String address, String phoneNumber, String picture, Integer dailyActiveMinutes) {
        this.id = id;
        this.role = role;
        this.mail = mail;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.picture = picture;
        this.dailyActiveMinutes = dailyActiveMinutes;
        this.isActive = false;
        this.isBlocked = false;
        this.blockMessage = "";
    }
}
