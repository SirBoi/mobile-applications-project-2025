package com.example.mobile_applications_project_2025.DTO;

public class LoginRequestDTO {
    public String mail;
    public String password;

    public LoginRequestDTO() {

    }

    public LoginRequestDTO(String mail, String password) {
        this.mail = mail;
        this.password = password;
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
}