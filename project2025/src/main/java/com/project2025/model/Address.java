package com.project2025.model;

import jakarta.persistence.Embeddable;

@Embeddable
public class Address {

	private String country;
	
	private String city;
	
	private String street;
	
	private String number;
	
	public Address() {
		super();
	}

	public Address(String country, String city, String street, String number) {
		super();
		this.country = country;
		this.city = city;
		this.street = street;
		this.number = number;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	@Override
	public String toString() {
		return "Address [country=" + country + ", city=" + city + ", street=" + street + ", number=" + number + "]";
	}

	// Citljiv format za mejlove/notifikacije, npr. "Fruskogorska 1, Novi Sad".
	public String toDisplayString() {
		StringBuilder sb = new StringBuilder();
		if (street != null && !street.isBlank()) sb.append(street);
		if (number != null && !number.isBlank()) sb.append(sb.length() > 0 ? " " : "").append(number);
		if (city != null && !city.isBlank()) sb.append(sb.length() > 0 ? ", " : "").append(city);
		if (country != null && !country.isBlank()) sb.append(sb.length() > 0 ? ", " : "").append(country);
		return sb.length() > 0 ? sb.toString() : "-";
	}
}