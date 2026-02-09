package com.example.mobile_applications_project_2025.Model;

import com.example.mobile_applications_project_2025.Model.Enumerator.Role;
import com.example.mobile_applications_project_2025.Model.Enumerator.UserStatus;

public class RegisteredUser {
    public Long id;
    public Role role;
    public String mail;
    public String password;
    public String firstName;
    public String lastName;
    public String address;
    public String phoneNumber;
    public String picture;
    public Integer dailyActiveMinutes;
    public Boolean isBlocked;
    public String blockMessage;
    public UserStatus status;

    public RegisteredUser() {

    }

    public RegisteredUser(Long id, Role role, String mail, String password, String firstName, String lastName, String address, String phoneNumber, String picture, Integer dailyActiveMinutes, Boolean isBlocked, String blockMessage, UserStatus status) {
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
        this.isBlocked = isBlocked;
        this.blockMessage = blockMessage;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public Integer getDailyActiveMinutes() {
        return dailyActiveMinutes;
    }

    public void setDailyActiveMinutes(Integer dailyActiveMinutes) {
        this.dailyActiveMinutes = dailyActiveMinutes;
    }

    public Boolean getBlocked() {
        return isBlocked;
    }

    public void setBlocked(Boolean blocked) {
        isBlocked = blocked;
    }

    public String getBlockMessage() {
        return blockMessage;
    }

    public void setBlockMessage(String blockMessage) {
        this.blockMessage = blockMessage;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "RegisteredUser{" +
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
