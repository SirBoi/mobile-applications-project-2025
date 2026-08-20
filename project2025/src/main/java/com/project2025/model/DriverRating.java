package com.project2025.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

// 2.8 - Ocenjivanje vozila i vozaca.
// Jedna ocena je uvek vezana za konkretnu (zavrsenu) voznju, ostavlja je
// putnik koji je poruzio voznju (Ride.passenger), u roku od 3 dana od
// zavrsetka. Vozac i vozilo se ocenjuju odvojeno (driverRating / vehicleRating),
// uz opcioni komentar.
@Entity
public class DriverRating {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "ride_id", nullable = false, unique = true)
	private Ride ride;

	@ManyToOne
	@JoinColumn(name = "driver_id", nullable = false)
	private Driver driver;

	@ManyToOne
	@JoinColumn(name = "passenger_id", nullable = false)
	private Passenger passenger;

	@Column(name = "driver_rating")
	private Integer driverRating;

	@Column(name = "vehicle_rating")
	private Integer vehicleRating;

	@Column(name = "text")
	private String text;

	@Column(name = "created_at")
	private LocalDateTime createdAt;

	public DriverRating() {
		super();
	}

	public DriverRating(Ride ride, Driver driver, Passenger passenger, Integer driverRating, Integer vehicleRating,
			String text) {
		super();
		this.ride = ride;
		this.driver = driver;
		this.passenger = passenger;
		this.driverRating = driverRating;
		this.vehicleRating = vehicleRating;
		this.text = text;
		this.createdAt = LocalDateTime.now();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Ride getRide() {
		return ride;
	}

	public void setRide(Ride ride) {
		this.ride = ride;
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

	public Integer getDriverRating() {
		return driverRating;
	}

	public void setDriverRating(Integer driverRating) {
		this.driverRating = driverRating;
	}

	public Integer getVehicleRating() {
		return vehicleRating;
	}

	public void setVehicleRating(Integer vehicleRating) {
		this.vehicleRating = vehicleRating;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	@Override
	public String toString() {
		return "DriverRating [id=" + id + ", ride=" + (ride != null ? ride.getId() : null) + ", driver=" + driver
				+ ", passenger=" + passenger + ", driverRating=" + driverRating + ", vehicleRating=" + vehicleRating
				+ ", text=" + text + ", createdAt=" + createdAt + "]";
	}
}