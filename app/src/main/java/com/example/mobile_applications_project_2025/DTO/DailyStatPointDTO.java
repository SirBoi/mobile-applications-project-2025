package com.example.mobile_applications_project_2025.DTO;

public class DailyStatPointDTO {
    private String date;
    private int rides;
    private double km;
    private double money;

    public DailyStatPointDTO() {}

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public int getRides() { return rides; }
    public void setRides(int rides) { this.rides = rides; }

    public double getKm() { return km; }
    public void setKm(double km) { this.km = km; }

    public double getMoney() { return money; }
    public void setMoney(double money) { this.money = money; }
}