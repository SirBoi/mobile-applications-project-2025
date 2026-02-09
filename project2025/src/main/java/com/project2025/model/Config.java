package com.project2025.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Config {
	 
	@Id
	private Integer id = 1;
	
	@Column(name="standardPrice")
	private Float standardPrice;
	
	@Column(name="luxuryPrice")
	private Float luxuryPrice;
	
	@Column(name="vanPrice")
	private Float vanPrice;
	
	public Config() {
		super();
	}

	public Config(Float standardPrice, Float luxuryPrice, Float vanPrice) {
		super();
		this.id = 1;
		this.standardPrice = standardPrice;
		this.luxuryPrice = luxuryPrice;
		this.vanPrice = vanPrice;
	}

	public Float getStandardPrice() {
		return standardPrice;
	}

	public void setStandardPrice(Float standardPrice) {
		this.standardPrice = standardPrice;
	}

	public Float getLuxuryPrice() {
		return luxuryPrice;
	}

	public void setLuxuryPrice(Float luxuryPrice) {
		this.luxuryPrice = luxuryPrice;
	}

	public Float getVanPrice() {
		return vanPrice;
	}

	public void setVanPrice(Float vanPrice) {
		this.vanPrice = vanPrice;
	}

	@Override
	public String toString() {
		return "Config [standardPrice=" + standardPrice + ", luxuryPrice=" + luxuryPrice + ", vanPrice=" + vanPrice
				+ "]";
	}
}
