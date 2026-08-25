package com.project2025.service;

import org.springframework.stereotype.Service;

import com.project2025.model.Passenger;
import com.project2025.model.Ride;

/**
 * 2.4.2 - Notifikacije ulinkovanih putnika.
 *
 * Ulinkovani putnici (ride.getPassengers(), različiti od putnika koji je
 * poručio vožnju) dobijaju mejl i in-app notifikaciju kada je vožnja
 * prihvaćena (sistem pronašao vozača), i ponovo kada se vožnja završi.
 */
@Service
public class RidePassengerNotificationService {

    private final NotificationService notificationService;
    private final MailService mailService;

    public RidePassengerNotificationService(NotificationService notificationService, MailService mailService) {
        this.notificationService = notificationService;
        this.mailService = mailService;
    }

    public void notifyRideAccepted(Ride ride) {
        if (ride.getPassengers() == null || ride.getPassengers().isEmpty()) return;

        String routeDescription = describeRoute(ride);
        String message = "Dodati ste kao putnik na vožnju " + routeDescription + ". Vožnja je prihvaćena.";

        for (Passenger passenger : ride.getPassengers()) {
            notificationService.create(passenger, message, ride);
            mailService.send(
                    passenger.getMail(),
                    "Dodati ste na vožnju",
                    message
            );
        }
    }

    // Poziva se iz 2.7 (Završetak vožnje) kada vozač označi da je vožnja gotova.
    public void notifyRideFinished(Ride ride) {
        if (ride.getPassengers() == null || ride.getPassengers().isEmpty()) return;

        String routeDescription = describeRoute(ride);
        String message = "Vožnja " + routeDescription + " je uspešno završena.";

        for (Passenger passenger : ride.getPassengers()) {
            notificationService.create(passenger, message, ride);
            mailService.send(
                    passenger.getMail(),
                    "Vožnja je završena",
                    message
            );
        }
    }

    private String describeRoute(Ride ride) {
        String origin = ride.getOrigin() != null ? ride.getOrigin().toDisplayString() : "-";
        String destination = ride.getDestination() != null ? ride.getDestination().toDisplayString() : "-";
        return "od " + origin + " do " + destination;
    }
}