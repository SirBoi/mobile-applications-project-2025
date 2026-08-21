package com.example.mobile_applications_project_2025;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobile_applications_project_2025.Model.Enumerator.RideStatus;
import com.example.mobile_applications_project_2025.Model.Ride;
import com.example.mobile_applications_project_2025.Network.APIs.RideAPI;
import com.example.mobile_applications_project_2025.Network.ApiClient;

import org.osmdroid.views.MapView;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 2.7 - Vozacev ekran voznje u toku: prati poziciju (ista simulacija kao
 * 2.6.2), a klikom na "Ride finished" + "Paid in vehicle" + Submit voznja se
 * zavrsava (backend: /rides/{id}/finish -> notifikacije putnicima iz 2.4.2).
 *
 * Nakon zavrsetka: ako vozac ima sledecu zakazanu voznju, nudi mu se da je
 * odmah pokrene ("krece ka novom polazistu"); ako nema, vraca se na Home
 * (odakle postoji stranica istorije/filtriranja po statusu "Scheduled" da
 * vidi buduce zakazane voznje - 2.9.2 DriverRideHistoryFragment).
 */
public class DriverRideOverviewFragment extends Fragment {

    private static final String ARG_RIDE_ID = "rideId";
    private static final long POLL_INTERVAL_MS = 5000;
    private static final long TICK_INTERVAL_MS = 1000;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private RideAPI rideAPI;

    private MapView mapView;
    private RideTrackingMapController trackingController;

    private TextView tvEta;
    private TextView tvRideInfo;
    private CheckBox cbFinished;
    private CheckBox cbPaid;
    private Button btnSubmit;

    private long rideId = -1;
    private Ride ride;
    private boolean running = false;

    public DriverRideOverviewFragment() {
        // Required empty public constructor
    }

    public static DriverRideOverviewFragment newInstance(long rideId) {
        DriverRideOverviewFragment fragment = new DriverRideOverviewFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(ARG_RIDE_ID, rideId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rideAPI = ApiClient.getRetrofit().create(RideAPI.class);
        if (getArguments() != null) {
            rideId = getArguments().getLong(ARG_RIDE_ID, -1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_driver_ride_overview, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvEta = view.findViewById(R.id.tvEta);
        tvRideInfo = view.findViewById(R.id.tvRideInfo);
        mapView = view.findViewById(R.id.mapView);
        cbFinished = view.findViewById(R.id.cbFinished);
        cbPaid = view.findViewById(R.id.cbPaid);
        btnSubmit = view.findViewById(R.id.btnSubmit);

        if (mapView != null) {
            mapView.setTileSource(MapTileSourceProvider.MAPTILER_STREETS);
            mapView.setMultiTouchControls(true);
        }

        btnSubmit.setOnClickListener(v -> {
            if (!cbFinished.isChecked() || !cbPaid.isChecked()) {
                Toast.makeText(requireContext(),
                        "Confirm both that the ride is finished and paid before submitting.",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            if (ride == null) return;
            finishRide();
        });

        if (rideId <= 0) {
            // Nije prosledjen konkretan ID - ucitaj trenutno aktivnu voznju vozaca.
            loadCurrentDriverRide();
        } else {
            running = true;
            loadRide();
        }
    }

    private void loadCurrentDriverRide() {
        if (SessionManager.getUser() == null) {
            Toast.makeText(requireContext(), "Not logged in.", Toast.LENGTH_SHORT).show();
            return;
        }
        Long driverId = SessionManager.getUser().getId();

        rideAPI.getDriverCurrentRide(driverId).enqueue(new Callback<Ride>() {
            @Override
            public void onResponse(@NonNull Call<Ride> call, @NonNull Response<Ride> response) {
                if (!isAdded()) return;
                if (!response.isSuccessful() || response.body() == null || response.body().id == null) {
                    Toast.makeText(requireContext(), "No active ride.", Toast.LENGTH_SHORT).show();
                    return;
                }
                rideId = response.body().id;
                running = true;
                ride = response.body();
                onRideUpdated();
                handler.postDelayed(pollRunnable, POLL_INTERVAL_MS);
                handler.post(tickRunnable);
            }

            @Override
            public void onFailure(@NonNull Call<Ride> call, @NonNull Throwable t) {
                if (!isAdded()) return;
                Toast.makeText(requireContext(), "Network error while loading ride.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadRide() {
        rideAPI.getRide(rideId).enqueue(new Callback<Ride>() {
            @Override
            public void onResponse(@NonNull Call<Ride> call, @NonNull Response<Ride> response) {
                if (!running) return;
                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(requireContext(), "Could not load ride.", Toast.LENGTH_SHORT).show();
                    return;
                }
                ride = response.body();
                onRideUpdated();
                handler.postDelayed(pollRunnable, POLL_INTERVAL_MS);
                handler.post(tickRunnable);
            }

            @Override
            public void onFailure(@NonNull Call<Ride> call, @NonNull Throwable t) {
                if (!running) return;
                Toast.makeText(requireContext(), "Network error while loading ride.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private final Runnable pollRunnable = new Runnable() {
        @Override
        public void run() {
            if (!running) return;
            rideAPI.getRide(rideId).enqueue(new Callback<Ride>() {
                @Override
                public void onResponse(@NonNull Call<Ride> call, @NonNull Response<Ride> response) {
                    if (!running) return;
                    if (response.isSuccessful() && response.body() != null) {
                        ride = response.body();
                        onRideUpdated();
                    }
                    if (running) handler.postDelayed(pollRunnable, POLL_INTERVAL_MS);
                }

                @Override
                public void onFailure(@NonNull Call<Ride> call, @NonNull Throwable t) {
                    if (running) handler.postDelayed(pollRunnable, POLL_INTERVAL_MS);
                }
            });
        }
    };

    private final Runnable tickRunnable = new Runnable() {
        @Override
        public void run() {
            if (!running) return;
            updateEtaAndPosition();
            handler.postDelayed(this, TICK_INTERVAL_MS);
        }
    };

    private void onRideUpdated() {
        if (ride == null || mapView == null) return;

        if (trackingController == null) {
            trackingController = new RideTrackingMapController(mapView, ride.id);
        }

        StringBuilder sb = new StringBuilder();
        sb.append(ride.origin != null ? (ride.origin.street + " " + ride.origin.number) : "?")
                .append(" -> ")
                .append(ride.destination != null ? (ride.destination.street + " " + ride.destination.number) : "?")
                .append("\n");
        sb.append("Price: ").append(ride.ridePrice != null ? ride.ridePrice : "-").append(" RSD\n");

        StringBuilder passengers = new StringBuilder();
        if (ride.passenger != null && ride.passenger.mail != null) {
            passengers.append(ride.passenger.mail);
        }
        if (ride.passengers != null) {
            for (String mail : ride.passengers) {
                if (passengers.length() > 0) passengers.append(", ");
                passengers.append(mail);
            }
        }
        sb.append("Passengers: ").append(passengers.length() > 0 ? passengers.toString() : "-");
        tvRideInfo.setText(sb.toString());

        boolean started = ride.status == RideStatus.Started;
        boolean finished = ride.status == RideStatus.Finished;

        cbFinished.setEnabled(started);
        cbPaid.setEnabled(started);
        btnSubmit.setEnabled(started);

        if (finished) {
            tvEta.setText("Finished");
            running = false;
        }

        updateEtaAndPosition();
    }

    private void updateEtaAndPosition() {
        if (ride == null || ride.status != RideStatus.Started) return;
        if (ride.rideStartDatetime == null || ride.rideDuration == null) return;

        LocalDateTime start;
        try {
            start = LocalDateTime.parse(ride.rideStartDatetime);
        } catch (Exception e) {
            return;
        }

        long elapsedSeconds = ChronoUnit.SECONDS.between(start, LocalDateTime.now());
        int totalSeconds = ride.rideDuration;
        long remaining = totalSeconds - elapsedSeconds;
        if (remaining < 0) remaining = 0;

        int m = (int) (remaining / 60);
        int s = (int) (remaining % 60);
        tvEta.setText(String.format("%02d:%02d", m, s));

        double progress = totalSeconds <= 0 ? 1 : (double) elapsedSeconds / totalSeconds;
        if (trackingController != null) trackingController.updateProgress(progress);
    }

    private void finishRide() {
        btnSubmit.setEnabled(false);
        rideAPI.finishRide(ride.id).enqueue(new Callback<Ride>() {
            @Override
            public void onResponse(@NonNull Call<Ride> call, @NonNull Response<Ride> response) {
                if (!isAdded()) return;
                if (!response.isSuccessful()) {
                    Toast.makeText(requireContext(), "Could not finish ride (" + response.code() + ")", Toast.LENGTH_SHORT).show();
                    btnSubmit.setEnabled(true);
                    return;
                }
                Toast.makeText(requireContext(), "Ride finished.", Toast.LENGTH_SHORT).show();
                running = false;
                checkForNextScheduledRide();
            }

            @Override
            public void onFailure(@NonNull Call<Ride> call, @NonNull Throwable t) {
                if (!isAdded()) return;
                Toast.makeText(requireContext(), "Network error while finishing ride.", Toast.LENGTH_SHORT).show();
                btnSubmit.setEnabled(true);
            }
        });
    }

    private void checkForNextScheduledRide() {
        if (SessionManager.getUser() == null) return;
        Long driverId = SessionManager.getUser().getId();

        rideAPI.getDriverNextScheduledRide(driverId).enqueue(new Callback<Ride>() {
            @Override
            public void onResponse(@NonNull Call<Ride> call, @NonNull Response<Ride> response) {
                if (!isAdded()) return;

                if (!response.isSuccessful() || response.body() == null || response.body().id == null) {
                    // 2.7 - nema dodeljenu voznju -> vozac se vraca na Home, odakle
                    // moze otvoriti istoriju voznji filtriranu po "Scheduled" da vidi
                    // buduce zakazane voznje (2.9.2, DriverRideHistoryFragment).
                    Toast.makeText(requireContext(), "No upcoming rides.", Toast.LENGTH_SHORT).show();
                    NavHostFragment.findNavController(DriverRideOverviewFragment.this).popBackStack();
                    return;
                }

                Ride next = response.body();
                String pickup = next.origin != null ? (next.origin.street + " " + next.origin.number) : "your next pickup point";

                new AlertDialog.Builder(requireContext())
                        .setTitle("Next ride")
                        .setMessage("You have another scheduled ride. Head to " + pickup + "?")
                        .setCancelable(false)
                        .setPositiveButton("Start", (d, w) -> startNextRide(next.id))
                        .setNegativeButton("Later", (d, w) ->
                                NavHostFragment.findNavController(DriverRideOverviewFragment.this).popBackStack())
                        .show();
            }

            @Override
            public void onFailure(@NonNull Call<Ride> call, @NonNull Throwable t) {
                if (!isAdded()) return;
                NavHostFragment.findNavController(DriverRideOverviewFragment.this).popBackStack();
            }
        });
    }

    private void startNextRide(Long nextRideId) {
        rideAPI.startRide(nextRideId).enqueue(new Callback<Ride>() {
            @Override
            public void onResponse(@NonNull Call<Ride> call, @NonNull Response<Ride> response) {
                if (!isAdded()) return;
                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(requireContext(), "Could not start next ride.", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Resetuj ekran na novu voznju umesto da se vracamo na Home.
                rideId = response.body().id;
                ride = null;
                trackingController = null;
                running = true;
                cbFinished.setChecked(false);
                cbPaid.setChecked(false);
                loadRide();
            }

            @Override
            public void onFailure(@NonNull Call<Ride> call, @NonNull Throwable t) {
                if (!isAdded()) return;
                Toast.makeText(requireContext(), "Network error while starting next ride.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mapView != null) mapView.onResume();
    }

    @Override
    public void onPause() {
        if (mapView != null) mapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        running = false;
        handler.removeCallbacksAndMessages(null);
        super.onDestroyView();
    }
}