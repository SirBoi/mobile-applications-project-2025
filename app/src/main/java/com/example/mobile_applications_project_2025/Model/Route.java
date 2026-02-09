package com.example.mobile_applications_project_2025.Model;

import java.util.List;

public class Route {
    public Long id;
    public List<Address> addresses;

    public Route() {

    }

    public Route(Long id, List<Address> addresses) {
        this.id = id;
        this.addresses = addresses;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Address> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<Address> addresses) {
        this.addresses = addresses;
    }

    @Override
    public String toString() {
        return "Route{" +
                "id=" + id +
                ", addresses=" + addresses +
                '}';
    }
}
