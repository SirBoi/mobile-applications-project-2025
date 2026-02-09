package com.example.mobile_applications_project_2025.Model;

public class DriverReport {
    public Long id;
    public Driver driver;
    public Passenger passenger;
    public String text;

    public DriverReport() {

    }

    public DriverReport(Long id, Driver driver, Passenger passenger, String text) {
        this.id = id;
        this.driver = driver;
        this.passenger = passenger;
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

    @Override
    public String toString() {
        return "DriverReport{" +
                "id=" + id +
                ", driver=" + driver +
                ", passenger=" + passenger +
                ", text='" + text + '\'' +
                '}';
    }
}
