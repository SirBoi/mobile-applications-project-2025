package com.example.mobile_applications_project_2025.Model;

public class DriverRating {
    public Long id;
    public Driver driver;
    public Passenger passenger;
    public Integer rating;
    public String text;

    public DriverRating() {

    }

    public DriverRating(Long id, Driver driver, Passenger passenger, Integer rating, String text) {
        this.id = id;
        this.driver = driver;
        this.passenger = passenger;
        this.rating = rating;
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

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "DriverRating{" +
                "id=" + id +
                ", driver=" + driver +
                ", passenger=" + passenger +
                ", rating=" + rating +
                ", text='" + text + '\'' +
                '}';
    }
}
