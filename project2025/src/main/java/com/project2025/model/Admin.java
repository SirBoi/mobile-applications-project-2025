package com.project2025.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("Admin")
public class Admin extends RegisteredUser {
	
	public Admin() {
		super();
	}
}
