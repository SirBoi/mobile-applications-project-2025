package com.project2025.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project2025.enums.CarType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "driver_account_update_request")
public class DriverAccountUpdateRequest {
	
	@Id
    private Long id;

	@OneToOne
	@MapsId
	@JoinColumn(name = "driver_id", nullable = false)
    private Driver driver;
	
	@Column(name="firstName", nullable = false)
	private String firstName;
	
	@Column(name="lastName", nullable = false)
	private String lastName;
	
	@Column(name="address", nullable = false)
	private String address;
	
	@Column(name="phoneNumber", nullable = false)
	private String phoneNumber;
	
	@Column(name="model")
	private String model;
	
	@Enumerated(EnumType.STRING)
	@Column(name="type")
	private CarType type;
	
	@Column(name="plate_number")
	private String plateNumber;
	
	@Column(name="number_of_seats")
	private Integer numberOfSeats;
	
	@Column(name="is_baby_friendly")
	private Boolean isBabyFriendly;
	
	@Column(name="is_animal_friendly")
	private Boolean isAnimalFriendly;
	
	public DriverAccountUpdateRequest() {
		super();
	}
	
	public DriverAccountUpdateRequest(Long userId, String firstName, String lastName, String address,
			String phoneNumber, String model, CarType type, String plateNumber, Integer numberOfSeats,
			Boolean isBabyFriendly, Boolean isAnimalFriendly) {
		super();
		this.id = userId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.address = address;
		this.phoneNumber = phoneNumber;
		this.model = model;
		this.type = type;
		this.plateNumber = plateNumber;
		this.numberOfSeats = numberOfSeats;
		this.isBabyFriendly = isBabyFriendly;
		this.isAnimalFriendly = isAnimalFriendly;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long userId) {
		this.id = userId;
	}
	
	public Driver getDriver() {
		return driver;
	}
	
	public void setDriver(Driver driver) {
		this.driver = driver;
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

	@Override
	public String toString() {
		return "DriverAccountUpdateRequest [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName
				+ ", address=" + address + ", phoneNumber=" + phoneNumber + ", model=" + model + ", type=" + type
				+ ", plateNumber=" + plateNumber + ", numberOfSeats=" + numberOfSeats + ", isBabyFriendly="
				+ isBabyFriendly + ", isAnimalFriendly=" + isAnimalFriendly + "]";
	}
}
