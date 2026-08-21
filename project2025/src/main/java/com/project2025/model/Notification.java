package com.project2025.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

// Notifikacija koja se čuva u bazi (nefunkcionalni zahtev: "Notifikacije treba
// čuvati u bazi, kako bi korisnici mogli da ih vide naknadno i da reaguju na
// njih"). Koristi se za sve tipove notifikacija (2.4.2, 2.4.1, 2.7, itd), ne
// samo za ulinkovane putnike.
@Entity
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "recipient_id", nullable = false)
    private RegisteredUser recipient;

    @Column(nullable = false, length = 1000)
    private String message;

    @ManyToOne
    @JoinColumn(name = "ride_id")
    private Ride ride; // opciono - vezuje notifikaciju za konkretnu vožnju

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "is_read", nullable = false)
    private Boolean isRead = false;

    public Notification() {
    }

    public Notification(RegisteredUser recipient, String message, Ride ride) {
        this.recipient = recipient;
        this.message = message;
        this.ride = ride;
        this.createdAt = LocalDateTime.now();
        this.isRead = false;
    }

    public Long getId() {
        return id;
    }

    public RegisteredUser getRecipient() {
        return recipient;
    }

    public void setRecipient(RegisteredUser recipient) {
        this.recipient = recipient;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Ride getRide() {
        return ride;
    }

    public void setRide(Ride ride) {
        this.ride = ride;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }
}