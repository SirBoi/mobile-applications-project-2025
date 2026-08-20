package com.project2025.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class DriverReport {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne
	@JoinColumn(name = "driver_id", nullable = false)
	private Driver driver;

	@ManyToOne
	@JoinColumn(name = "passenger_id", nullable = false)
	private Passenger passenger;
	
	// 2.6.2 - na koju konkretnu vožnju se prijava nekonzistentnosti odnosi
	// (bitno za prikaz u istoriji vožnji, 2.9.x).
	@ManyToOne
	@JoinColumn(name = "ride_id")
	private Ride ride;
	
	@Column(name="text")
	private String text;
	
	public DriverReport() {
		super();
	}
	
	public DriverReport(Driver driver, Passenger passenger, String text) {
		super();
		this.driver = driver;
		this.passenger = passenger;
		this.text = text;
	}
	
	public DriverReport(Driver driver, Passenger passenger, Ride ride, String text) {
		super();
		this.driver = driver;
		this.passenger = passenger;
		this.ride = ride;
		this.text = text;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Driver getDriver() {
		return driver;
	}

	public void setDriver(Driver driver) {
		this.driver = driver;
	}

	public Passenger getPassenger() {
		return passenger;
	}

	public void setPassenger(Passenger passenger) {
		this.passenger = passenger;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Ride getRide() {
		return ride;
	}

	public void setRide(Ride ride) {
		this.ride = ride;
	}

	@Override
	public String toString() {
		return "DriverReport [id=" + id + ", driver=" + driver + ", passenger=" + passenger + ", ride="
				+ (ride != null ? ride.getId() : null) + ", text=" + text + "]";
	}
}