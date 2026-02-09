package com.example.mobile_applications_project_2025.Model;

public class Admin extends RegisteredUser {
    public Admin() {

    }

    @Override
    public String toString() {
        return "Admin{" +
                "id=" + id +
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
