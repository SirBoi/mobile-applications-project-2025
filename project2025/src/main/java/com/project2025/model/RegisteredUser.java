package com.project2025.model;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.project2025.enums.Role;
import com.project2025.enums.UserStatus;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "role",
    visible = true
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = Driver.class, name = "Driver"),
    @JsonSubTypes.Type(value = Passenger.class, name = "Passenger"),
    @JsonSubTypes.Type(value = Admin.class, name = "Admin")
})
@Entity
@Table(name = "registered_user")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "role", discriminatorType = DiscriminatorType.STRING)
public class RegisteredUser {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "role", insertable = false, updatable = false)
	private Role role;
	
	@Column(name="mail", nullable = false, unique = true)
	private String mail;
	
	@Column(name="password", nullable = false)
	private String password;
	
	@Column(name="firstName", nullable = false)
	private String firstName;
	
	@Column(name="lastName", nullable = false)
	private String lastName;
	
	@Column(name="address", nullable = false)
	private String address;
	
	@Column(name="phoneNumber", nullable = false)
	private String phoneNumber;
	
	@Column(name="picture")
	private String picture = "default.png";
	
	@Column(name="dailyActiveMinutes")
	private Integer dailyActiveMinutes = 0;
	
	@Column(name="isBlocked")
	private Boolean isBlocked = false;
	
	@Column(name="blockMessage")
	private String blockMessage = "";
	
	@Enumerated(EnumType.STRING)
	@Column(name="status")
	private UserStatus status = UserStatus.Free;
	
	public RegisteredUser() {
		
	}

	public RegisteredUser(Long id, Role role, String mail, String password, String firstName, String lastName, String address,
			String phoneNumber) {
		super();
		this.id = id;
		this.role = role;
		this.mail = mail;
		this.password = password;
		this.firstName = firstName;
		this.lastName = lastName;
		this.address = address;
		this.phoneNumber = phoneNumber;
		this.picture = "default.png";
		this.dailyActiveMinutes = 0;
		this.isBlocked = false;
		this.blockMessage = "";
		this.status = UserStatus.Free;
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

	public Boolean getIsBlocked() {
		return isBlocked;
	}

	public void setIsBlocked(Boolean isBlocked) {
		this.isBlocked = isBlocked;
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
		return "User [id=" + id + ", role=" + role + ", mail=" + mail + ", password=" + password + ", firstName="
				+ firstName + ", lastName=" + lastName + ", address=" + address + ", phoneNumber=" + phoneNumber
				+ ", picture=" + picture + ", dailyActiveMinutes=" + dailyActiveMinutes + ", isBlocked=" + isBlocked
				+ ", blockMessage=" + blockMessage + ", status=" + status + "]";
	}
}
