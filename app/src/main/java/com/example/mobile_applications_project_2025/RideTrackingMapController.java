package com.example.mobile_applications_project_2025;

import androidx.core.content.ContextCompat;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import java.util.Random;

/**
 * 2.6.2 - Prati vožnju na mapi (praćenje lokacije vozila + ETA).
 *
 * Backend ne čuva prave GPS koordinate ni za adrese ni za vozača (isto kao
 * kod 2.1.1 - ActiveDriversMapController), pa se polazište i odredište
 * generišu deterministički na osnovu ID-a vožnje (uvek ista tačka za istu
 * vožnju), a trenutna pozicija vozila se linearno interpoliše između njih u
 * zavisnosti od proteklog vremena vožnje. Ovo je u skladu sa nefunkcionalnim
 * zahtevom da se simulacija kretanja vozila može uraditi na proizvoljan način.
 */
public class RideTrackingMapController {

    private static final double CENTER_LAT = 45.267136;
    private static final double CENTER_LNG = 19.833549;
    private static final double SPREAD_DEGREES = 0.02; // ~2km oko centra

    private final MapView mapView;
    private final Marker vehicleMarker;
    private final GeoPoint origin;
    private final GeoPoint destination;

    public RideTrackingMapController(MapView mapView, long rideId) {
        this.mapView = mapView;
        this.origin = simulatePoint(rideId * 2L + 1L);
        this.destination = simulatePoint(rideId * 2L + 2L);

        Polyline route = new Polyline();
        route.addPoint(origin);
        route.addPoint(destination);
        route.getOutlinePaint().setStrokeWidth(6f);
        mapView.getOverlays().add(route);

        Marker originMarker = new Marker(mapView);
        originMarker.setPosition(origin);
        originMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        originMarker.setTitle("Pickup");
        mapView.getOverlays().add(originMarker);

        Marker destinationMarker = new Marker(mapView);
        destinationMarker.setPosition(destination);
        destinationMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        destinationMarker.setTitle("Destination");
        mapView.getOverlays().add(destinationMarker);

        vehicleMarker = new Marker(mapView);
        vehicleMarker.setIcon(ContextCompat.getDrawable(mapView.getContext(), R.drawable.marker_driver_busy));
        vehicleMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
        vehicleMarker.setPosition(origin);
        vehicleMarker.setTitle("Your ride");
        mapView.getOverlays().add(vehicleMarker);

        GeoPoint mid = new GeoPoint(
                (origin.getLatitude() + destination.getLatitude()) / 2,
                (origin.getLongitude() + destination.getLongitude()) / 2);
        mapView.getController().setZoom(15.0);
        mapView.getController().setCenter(mid);
    }

    /** progress u opsegu [0,1] - koliki deo puta je vozilo prešlo */
    public void updateProgress(double progress) {
        double p = Math.max(0, Math.min(1, progress));
        double lat = origin.getLatitude() + (destination.getLatitude() - origin.getLatitude()) * p;
        double lng = origin.getLongitude() + (destination.getLongitude() - origin.getLongitude()) * p;
        vehicleMarker.setPosition(new GeoPoint(lat, lng));
        mapView.invalidate();
    }

    private GeoPoint simulatePoint(long seed) {
        Random random = new Random(seed);
        double angle = random.nextDouble() * 2 * Math.PI;
        double distance = 0.5 * SPREAD_DEGREES + random.nextDouble() * 0.5 * SPREAD_DEGREES;
        double lat = CENTER_LAT + distance * Math.cos(angle);
        double lng = CENTER_LNG + distance * Math.sin(angle);
        return new GeoPoint(lat, lng);
    }
}