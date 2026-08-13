package com.example.mobile_applications_project_2025;

import android.os.Handler;
import android.os.Looper;

import androidx.core.content.ContextCompat;

import com.example.mobile_applications_project_2025.DTO.ActiveDriverDTO;
import com.example.mobile_applications_project_2025.Network.APIs.DriverAPI;
import com.example.mobile_applications_project_2025.Network.ApiClient;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Prikazuje aktivna vozila na mapi (2.1.1 - Prikaz informacija).
 *
 * Backend ne čuva stvarnu GPS lokaciju vozača (nema tog polja u bazi), pa se
 * pozicija simulira na klijentu: svakom vozaču se dodeljuje stabilna tačka
 * oko centra grada (deterministički, na osnovu njegovog ID-a), koja blago
 * "luta" dok je vozač slobodan. Ovo je u skladu sa nefunkcionalnim zahtevom:
 * "Simulaciju kretanja vozila po mapi kada vozač nema vožnju je moguće
 * definisati na proizvoljan način."
 *
 * Korišćenje: napraviti instancu nakon što je MapView inicijalizovan, pa
 * pozvati start() u onResume() i stop() u onPause() fragmenta/aktivnosti.
 */
public class ActiveDriversMapController {

    private static final double CENTER_LAT = 45.267136;
    private static final double CENTER_LNG = 19.833549;
    private static final double SPREAD_DEGREES = 0.01; // ~1km oko centra (da stanu u početni zум)

    private static final long REFRESH_INTERVAL_MS = 5000; // koliko često pitamo server ko je aktivan
    private static final long DRIFT_INTERVAL_MS = 2000;   // koliko često pomeramo slobodne markere

    private final MapView mapView;
    private final DriverAPI driverAPI;
    private final Handler handler = new Handler(Looper.getMainLooper());

    private final Map<Long, Marker> markersByDriverId = new HashMap<>();
    private final Map<Long, GeoPoint> basePositionsByDriverId = new HashMap<>();
    private final Map<Long, Boolean> busyByDriverId = new HashMap<>();

    private boolean running = false;

    public ActiveDriversMapController(MapView mapView) {
        this.mapView = mapView;
        this.driverAPI = ApiClient.getRetrofit().create(DriverAPI.class);
    }

    public void start() {
        if (running) return;
        running = true;
        handler.post(refreshRunnable);
        handler.postDelayed(driftRunnable, DRIFT_INTERVAL_MS);
    }

    public void stop() {
        running = false;
        handler.removeCallbacks(refreshRunnable);
        handler.removeCallbacks(driftRunnable);
    }

    private final Runnable refreshRunnable = new Runnable() {
        @Override
        public void run() {
            fetchActiveDrivers();
            if (running) handler.postDelayed(this, REFRESH_INTERVAL_MS);
        }
    };

    private final Runnable driftRunnable = new Runnable() {
        @Override
        public void run() {
            driftFreeDrivers();
            if (running) handler.postDelayed(this, DRIFT_INTERVAL_MS);
        }
    };

    private void fetchActiveDrivers() {
        driverAPI.getActiveDrivers().enqueue(new Callback<List<ActiveDriverDTO>>() {
            @Override
            public void onResponse(Call<List<ActiveDriverDTO>> call, Response<List<ActiveDriverDTO>> response) {
                if (!running || !response.isSuccessful() || response.body() == null) return;
                applyDrivers(response.body());
            }

            @Override
            public void onFailure(Call<List<ActiveDriverDTO>> call, Throwable t) {
                // Tiho ignorišemo - pokušaćemo ponovo na sledećem refresh-u.
            }
        });
    }

    private void applyDrivers(List<ActiveDriverDTO> drivers) {
        Set<Long> stillPresent = new HashSet<>();

        for (ActiveDriverDTO driver : drivers) {
            stillPresent.add(driver.getId());
            busyByDriverId.put(driver.getId(), driver.isBusy());

            GeoPoint basePoint = basePositionsByDriverId.computeIfAbsent(
                    driver.getId(), this::simulateBasePosition);

            Marker marker = markersByDriverId.get(driver.getId());
            if (marker == null) {
                marker = new Marker(mapView);
                marker.setPosition(basePoint);
                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
                mapView.getOverlays().add(marker);
                markersByDriverId.put(driver.getId(), marker);
            }

            marker.setIcon(ContextCompat.getDrawable(
                    mapView.getContext(),
                    driver.isBusy() ? R.drawable.marker_driver_busy : R.drawable.marker_driver_free));

            marker.setTitle(safeName(driver));
            marker.setSnippet((driver.getModel() != null ? driver.getModel() : "")
                    + " • " + (driver.isBusy() ? "Zauzeto" : "Slobodno"));
        }

        // Ukloni markere vozača koji više nisu aktivni (npr. odjavili su se).
        Iterator<Map.Entry<Long, Marker>> it = markersByDriverId.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Long, Marker> entry = it.next();
            if (!stillPresent.contains(entry.getKey())) {
                mapView.getOverlays().remove(entry.getValue());
                basePositionsByDriverId.remove(entry.getKey());
                busyByDriverId.remove(entry.getKey());
                it.remove();
            }
        }

        mapView.invalidate();
    }

    private String safeName(ActiveDriverDTO driver) {
        String first = driver.getFirstName() != null ? driver.getFirstName() : "";
        String last = driver.getLastName() != null ? driver.getLastName() : "";
        return (first + " " + last).trim();
    }

    private GeoPoint simulateBasePosition(Long driverId) {
        // Seed-ovan random po ID-u vozača -> ista pozicija pri svakom osvežavanju/pokretanju.
        Random random = new Random(driverId);
        double angle = random.nextDouble() * 2 * Math.PI;
        double distance = random.nextDouble() * SPREAD_DEGREES;
        double lat = CENTER_LAT + distance * Math.cos(angle);
        double lng = CENTER_LNG + distance * Math.sin(angle);
        return new GeoPoint(lat, lng);
    }

    private void driftFreeDrivers() {
        if (markersByDriverId.isEmpty()) return;
        Random random = new Random();
        boolean changed = false;
        for (Map.Entry<Long, Marker> entry : markersByDriverId.entrySet()) {
            Boolean busy = busyByDriverId.get(entry.getKey());
            if (Boolean.TRUE.equals(busy)) continue; // busy vozila prate rutu vožnje, van obima 2.1.1

            Marker marker = entry.getValue();
            GeoPoint current = marker.getPosition();
            double deltaLat = (random.nextDouble() - 0.5) * 0.0015;
            double deltaLng = (random.nextDouble() - 0.5) * 0.0015;
            marker.setPosition(new GeoPoint(current.getLatitude() + deltaLat, current.getLongitude() + deltaLng));
            changed = true;
        }
        if (changed) mapView.invalidate();
    }
}