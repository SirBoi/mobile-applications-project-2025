package com.example.mobile_applications_project_2025.Model;

public class Config {
    public Float standardPrice;
    public Float luxuryPrice;
    public Float vanPrice;

    public Config() {

    }

    public Config(Float standardPrice, Float luxuryPrice, Float vanPrice) {
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
        return "Config{" +
                "standardPrice=" + standardPrice +
                ", luxuryPrice=" + luxuryPrice +
                ", vanPrice=" + vanPrice +
                '}';
    }
}
