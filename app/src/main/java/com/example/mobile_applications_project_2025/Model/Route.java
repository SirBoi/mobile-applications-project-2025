package com.example.mobile_applications_project_2025.Model;

import java.util.List;

public class Route {
    public Long id;
    public List<Address> addresses;
    public Double distanceKm;

    public Route() {}

    public Route(Long id, List<Address> addresses) {
        this.id = id;
        this.addresses = addresses;
    }

    public Route(Long id, List<Address> addresses, Double distanceKm) {
        this.id = id;
        this.addresses = addresses;
        this.distanceKm = distanceKm;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public List<Address> getAddresses() { return addresses; }
    public void setAddresses(List<Address> addresses) { this.addresses = addresses; }

    public Double getDistanceKm() { return distanceKm; }
    public void setDistanceKm(Double distanceKm) { this.distanceKm = distanceKm; }

    @Override
    public String toString() {
        return "Route{" +
                "id=" + id +
                ", addresses=" + addresses +
                ", distanceKm=" + distanceKm +
                '}';
    }
}