package com.project2025.model;

import com.project2025.enums.CarStatus;
import com.project2025.enums.CarType;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("Driver")
public class Driver extends RegisteredUser {
	
	@Column(name="model")
	private String model;
	
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
	
	@Column(name="car_status")
	private CarStatus carStatus;
	
	@Column(name="is_profile_activated")
	private Boolean isProfileActivated;
	
	public Driver() {
		super();
	}

	public Driver(String model, CarType type, String plateNumber, Integer numberOfSeats, Boolean isBabyFriendly,
			Boolean isAnimalFriendly) {
		super();
		this.model = model;
		this.type = type;
		this.plateNumber = plateNumber;
		this.numberOfSeats = numberOfSeats;
		this.isBabyFriendly = isBabyFriendly;
		this.isAnimalFriendly = isAnimalFriendly;
		this.carStatus = CarStatus.Available;
		this.isProfileActivated = false;
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

	public CarStatus getCarStatus() {
		return carStatus;
	}

	public void setCarStatus(CarStatus carStatus) {
		this.carStatus = carStatus;
	}

	public Boolean getIsProfileActivated() {
		return isProfileActivated;
	}

	public void setIsProfileActivated(Boolean isProfileActivated) {
		this.isProfileActivated = isProfileActivated;
	}

	@Override
	public String toString() {
		return "Driver [model=" + model + ", type=" + type + ", plateNumber=" + plateNumber + ", numberOfSeats="
				+ numberOfSeats + ", isBabyFriendly=" + isBabyFriendly + ", isAnimalFriendly=" + isAnimalFriendly
				+ ", carStatus=" + carStatus + ", isProfileActivated=" + isProfileActivated + "]";
	}
}
