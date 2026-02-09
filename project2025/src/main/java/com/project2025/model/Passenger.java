package com.project2025.model;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;

@Entity
@DiscriminatorValue("Passenger")
public class Passenger extends RegisteredUser {
	
	@OneToMany(cascade = CascadeType.ALL)
	private List<Route> favouriteRoutes;
	
	public Passenger() {
		super();
	}

	public Passenger(List<Route> favouriteRoutes) {
        super();
        this.favouriteRoutes = favouriteRoutes;
    }

	public List<Route> getFavouriteRoutes() {
		return favouriteRoutes;
	}

	public void setFavouriteRoutes(List<Route> favouriteRoutes) {
		this.favouriteRoutes = favouriteRoutes;
	}

	@Override
	public String toString() {
		return "Passenger [favouriteRoutes=" + favouriteRoutes + "]";
	}
}
