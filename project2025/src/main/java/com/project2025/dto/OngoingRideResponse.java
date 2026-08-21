package com.project2025.dto;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.project2025.model.Address;
import com.project2025.model.Passenger;
import com.project2025.model.Ride;

// 2.13 - Admin pregled vožnje koja trenutno traje (status = Started).
// Backend ne čuva GPS poziciju vozača (nema tog polja u bazi - pozicija se
// simulira samo na klijentu), pa umesto "trenutne tačke na mapi" dajemo
// procenat napredovanja i procenjeno vreme dolaska, izračunate iz
// rideStartDatetime + rideDuration (u sekundama, skraćeno radi demonstracije).
public class OngoingRideResponse {

    private Long rideId;
    private Long driverId;
    private String driverName;
    private String carModel;
    private String plateNumber;
    private Address origin;
    private Address destination;
    private List<String> passengerNames;
    private Float ridePrice;
    private LocalDateTime rideStartDatetime;
    private LocalDateTime estimatedFinishDatetime;
    private Long secondsElapsed;
    private Long secondsRemaining;
    private Integer progressPercent;
    private Boolean isPanicPressed;

    public static OngoingRideResponse from(Ride ride) {
        OngoingRideResponse dto = new OngoingRideResponse();
        dto.rideId = ride.getId();
        if (ride.getDriver() != null) {
            dto.driverId = ride.getDriver().getId();
            String fn = ride.getDriver().getFirstName() != null ? ride.getDriver().getFirstName() : "";
            String ln = ride.getDriver().getLastName() != null ? ride.getDriver().getLastName() : "";
            dto.driverName = (fn + " " + ln).trim();
            dto.carModel = ride.getDriver().getModel();
            dto.plateNumber = ride.getDriver().getPlateNumber();
        }
        dto.origin = ride.getOrigin();
        dto.destination = ride.getDestination();
        dto.passengerNames = buildPassengerNames(ride);
        dto.ridePrice = ride.getRidePrice();
        dto.rideStartDatetime = ride.getRideStartDatetime();

        int durationSeconds = ride.getRideDuration() != null ? ride.getRideDuration() : 0;
        if (ride.getRideStartDatetime() != null) {
            dto.estimatedFinishDatetime = ride.getRideStartDatetime().plusSeconds(durationSeconds);

            long elapsed = java.time.Duration.between(ride.getRideStartDatetime(), LocalDateTime.now()).getSeconds();
            elapsed = Math.max(0, elapsed);
            long remaining = Math.max(0, durationSeconds - elapsed);

            dto.secondsElapsed = elapsed;
            dto.secondsRemaining = remaining;
            dto.progressPercent = durationSeconds > 0
                    ? (int) Math.min(100, Math.round((elapsed * 100.0) / durationSeconds))
                    : 0;
        }

        dto.isPanicPressed = ride.getIsPanicPressed();
        return dto;
    }

    private static List<String> buildPassengerNames(Ride ride) {
        if (ride.getPassenger() == null) return Collections.emptyList();
        List<String> names = ride.getPassengers() == null ? Collections.emptyList()
                : ride.getPassengers().stream().map(OngoingRideResponse::fullName).collect(Collectors.toList());
        List<String> all = new java.util.ArrayList<>();
        all.add(fullName(ride.getPassenger()));
        all.addAll(names);
        return all;
    }

    private static String fullName(Passenger p) {
        String fn = p.getFirstName() != null ? p.getFirstName() : "";
        String ln = p.getLastName() != null ? p.getLastName() : "";
        return (fn + " " + ln).trim();
    }

    public Long getRideId() { return rideId; }
    public Long getDriverId() { return driverId; }
    public String getDriverName() { return driverName; }
    public String getCarModel() { return carModel; }
    public String getPlateNumber() { return plateNumber; }
    public Address getOrigin() { return origin; }
    public Address getDestination() { return destination; }
    public List<String> getPassengerNames() { return passengerNames; }
    public Float getRidePrice() { return ridePrice; }
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    public LocalDateTime getRideStartDatetime() { return rideStartDatetime; }
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    public LocalDateTime getEstimatedFinishDatetime() { return estimatedFinishDatetime; }
    public Long getSecondsElapsed() { return secondsElapsed; }
    public Long getSecondsRemaining() { return secondsRemaining; }
    public Integer getProgressPercent() { return progressPercent; }
    public Boolean getIsPanicPressed() { return isPanicPressed; }
}