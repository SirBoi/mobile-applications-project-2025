package com.project2025.dto;

public class LoginRequest {
    public String mail;
    public String password;
    
    public LoginRequest() {
	
    }
    
    public LoginRequest(String mail, String password) {
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