package com.example.mobile_applications_project_2025.DTO;

public class ConfigDTO {
    public Float standardPrice;
    public Float luxuryPrice;
    public Float vanPrice;

    public ConfigDTO() {}

    public ConfigDTO(Float standardPrice, Float luxuryPrice, Float vanPrice) {
        this.standardPrice = standardPrice;
        this.luxuryPrice = luxuryPrice;
        this.vanPrice = vanPrice;
    }
}