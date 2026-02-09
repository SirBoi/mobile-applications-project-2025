package com.project2025.model;

import java.time.LocalDateTime;
import java.util.List;

import com.project2025.enums.RideStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;

@Entity
public class Ride {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Embedded
	@AttributeOverrides({
	    @AttributeOverride(name="country", column=@Column(name="origin_country")),
	    @AttributeOverride(name="city", column=@Column(name="origin_city")),
	    @AttributeOverride(name="street", column=@Column(name="origin_street")),
	    @AttributeOverride(name="number", column=@Column(name="origin_number"))
	})
	private Address origin;
	
	@Embedded
	@AttributeOverrides({
	    @AttributeOverride(name="country", column=@Column(name="destination_country")),
	    @AttributeOverride(name="city", column=@Column(name="destination_city")),
	    @AttributeOverride(name="street", column=@Column(name="destination_street")),
	    @AttributeOverride(name="number", column=@Column(name="destination_number"))
	})
	private Address destination;
	
	@ManyToOne
	@JoinColumn(name = "route_id")
	private Route route;
	
	@Column(name="rideDuration")
	private Integer rideDuration;
	
	@Column(name="ridePrice")
	private Float ridePrice;
	
	@ManyToOne
	@JoinColumn(name = "passenger_id")
	private Passenger passenger;
	
	@ManyToMany
	@JoinTable(
	    name = "ride_passengers",
	    joinColumns = @JoinColumn(name = "ride_id"),
	    inverseJoinColumns = @JoinColumn(name = "passenger_id")
	)
	private List<Passenger> passengers;
	
	@ManyToOne
	@JoinColumn(name = "driver_id")
	private Driver driver;
	
	@Column(name="hasStarted")
	private Boolean hasStarted;
	
	@Column(name="rideStartDatetime")
	private LocalDateTime rideStartDatetime;
	
	@Column(name="rideFinishDatetime")
	private LocalDateTime rideFinishDatetime;
	
	@Enumerated(EnumType.STRING)
	@Column(name="status")
	private RideStatus status;
	
	@ManyToOne
	@JoinColumn(name = "cancelled_by_id")
	private RegisteredUser cancelledBy;
	
	@Column(name="isPanicPressed")
	private Boolean isPanicPressed;
	
	public Ride() {
		super();
	}
	
	public Ride(Address origin, Address destination, Route route, Integer rideDuration, Float ridePrice,
			Passenger passenger, List<Passenger> passengers, Driver driver) {
		super();
		this.origin = origin;
		this.destination = destination;
		this.route = route;
		this.rideDuration = rideDuration;
		this.ridePrice = ridePrice;
		this.passenger = passenger;
		this.passengers = passengers;
		this.driver = driver;
		this.hasStarted = false;
		this.rideStartDatetime = null;
		this.rideFinishDatetime = null;
		this.status = RideStatus.Scheduled;
		this.cancelledBy = null;
		this.isPanicPressed = false;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Address getOrigin() {
		return origin;
	}

	public void setOrigin(Address origin) {
		this.origin = origin;
	}

	public Address getDestination() {
		return destination;
	}

	public void setDestination(Address destination) {
		this.destination = destination;
	}

	public Route getRoute() {
		return route;
	}

	public void setRoute(Route route) {
		this.route = route;
	}

	public Integer getRideDuration() {
		return rideDuration;
	}

	public void setRideDuration(Integer rideDuration) {
		this.rideDuration = rideDuration;
	}

	public Float getRidePrice() {
		return ridePrice;
	}

	public void setRidePrice(Float ridePrice) {
		this.ridePrice = ridePrice;
	}

	public Passenger getPassenger() {
		return passenger;
	}

	public void setPassenger(Passenger passenger) {
		this.passenger = passenger;
	}

	public List<Passenger> getPassengers() {
		return passengers;
	}

	public void setPassengers(List<Passenger> passengers) {
		this.passengers = passengers;
	}

	public Driver getDriver() {
		return driver;
	}

	public void setDriver(Driver driver) {
		this.driver = driver;
	}

	public Boolean getHasStarted() {
		return hasStarted;
	}

	public void setHasStarted(Boolean hasStarted) {
		this.hasStarted = hasStarted;
	}

	public LocalDateTime getRideStartDatetime() {
		return rideStartDatetime;
	}

	public void setRideStartDatetime(LocalDateTime rideStartDatetime) {
		this.rideStartDatetime = rideStartDatetime;
	}

	public LocalDateTime getRideFinishDatetime() {
		return rideFinishDatetime;
	}

	public void setRideFinishDatetime(LocalDateTime rideFinishDatetime) {
		this.rideFinishDatetime = rideFinishDatetime;
	}

	public RideStatus getStatus() {
		return status;
	}

	public void setStatus(RideStatus status) {
		this.status = status;
	}

	public RegisteredUser getCancelledBy() {
		return cancelledBy;
	}

	public void setCancelledBy(RegisteredUser cancelledBy) {
		this.cancelledBy = cancelledBy;
	}

	public Boolean getIsPanicPressed() {
		return isPanicPressed;
	}

	public void setIsPanicPressed(Boolean isPanicPressed) {
		this.isPanicPressed = isPanicPressed;
	}

	@Override
	public String toString() {
		return "Ride [id=" + id + ", origin=" + origin + ", destination=" + destination + ", route=" + route
				+ ", rideDuration=" + rideDuration + ", ridePrice=" + ridePrice + ", passenger=" + passenger
				+ ", passengers=" + passengers + ", driver=" + driver + ", hasStarted=" + hasStarted
				+ ", rideStartDatetime=" + rideStartDatetime + ", rideFinishDatetime=" + rideFinishDatetime
				+ ", status=" + status + ", cancelledBy=" + cancelledBy + ", isPanicPressed=" + isPanicPressed + "]";
	}
}
