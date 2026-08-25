package com.example.mobile_applications_project_2025;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.NumberPicker;

import com.example.mobile_applications_project_2025.DTO.DriverRatingCreateRequestDTO;
import com.example.mobile_applications_project_2025.DTO.DriverRatingResponseDTO;
import com.example.mobile_applications_project_2025.DTO.DriverReportCreateRequestDTO;
import com.example.mobile_applications_project_2025.Model.Enumerator.RideStatus;
import com.example.mobile_applications_project_2025.Model.Ride;
import com.example.mobile_applications_project_2025.Network.APIs.DriverRatingAPI;
import com.example.mobile_applications_project_2025.Network.APIs.DriverReportAPI;
import com.example.mobile_applications_project_2025.Network.APIs.RideAPI;
import com.example.mobile_applications_project_2025.Network.ApiClient;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.card.MaterialCardView;

import org.osmdroid.views.MapView;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 2.6.2 - U toku trajanja vožnje: putnik prati poziciju vozila na mapi,
 * ETA se ažurira kako se vozilo približava odredištu, i putnik može da
 * prijavi nekonzistentnost vozača (napomena koja ide u DriverReport).
 */
public class PassengerRideOverviewFragment extends Fragment {

    private static final String ARG_RIDE_ID = "rideId";
    private static final long POLL_INTERVAL_MS = 5000; // osvežavanje statusa vožnje sa servera
    private static final long TICK_INTERVAL_MS = 1000;  // lokalno ažuriranje ETA/pozicije vozila

    private final Handler handler = new Handler(Looper.getMainLooper());
    private RideAPI rideAPI;
    private DriverReportAPI driverReportAPI;
    private DriverRatingAPI driverRatingAPI;

    private MapView mapView;
    private RideTrackingMapController trackingController;

    private TextView tvEta;
    private MaterialCardView etaCard;
    private View ratingSection;
    private View reportSection;

    // 2.8
    private NumberPicker npDriverField;
    private NumberPicker npVehicleField;
    private TextInputEditText etRatingCommentField;
    private Button btnSubmitRatingField;
    private boolean alreadyRated = false;

    private long rideId = -1;
    private Ride ride;
    private boolean running = false;

    public PassengerRideOverviewFragment() {
        // Required empty public constructor
    }

    public static PassengerRideOverviewFragment newInstance(long rideId) {
        PassengerRideOverviewFragment fragment = new PassengerRideOverviewFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(ARG_RIDE_ID, rideId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rideAPI = ApiClient.getRetrofit().create(RideAPI.class);
        driverReportAPI = ApiClient.getRetrofit().create(DriverReportAPI.class);
        driverRatingAPI = ApiClient.getRetrofit().create(DriverRatingAPI.class);
        if (getArguments() != null) {
            rideId = getArguments().getLong(ARG_RIDE_ID, -1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_passenger_ride_overview, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvEta = view.findViewById(R.id.tvEta);
        etaCard = view.findViewById(R.id.etaCard);
        mapView = view.findViewById(R.id.mapView);
        ratingSection = view.findViewById(R.id.ratingSection);
        reportSection = view.findViewById(R.id.reportSection);

        TextInputEditText etNote = view.findViewById(R.id.etNote);
        Button btnSend = view.findViewById(R.id.btnSend);

        NumberPicker npDriver = view.findViewById(R.id.npDriver);
        NumberPicker npVehicle = view.findViewById(R.id.npVehicle);
        TextInputEditText etRatingComment = view.findViewById(R.id.etRatingComment);
        Button btnSubmitRating = view.findViewById(R.id.btnSubmitRating);

        setupPicker(npDriver);
        setupPicker(npVehicle);

        npDriverField = npDriver;
        npVehicleField = npVehicle;
        etRatingCommentField = etRatingComment;
        btnSubmitRatingField = btnSubmitRating;

        if (mapView != null) {
            mapView.setTileSource(MapTileSourceProvider.MAPTILER_STREETS);
            mapView.setMultiTouchControls(true);
        }

        // 2.8 - Ocenjivanje vozila i vozaca (u roku od 3 dana od zavrsetka voznje).
        btnSubmitRating.setOnClickListener(v -> submitRating());

        btnSend.setOnClickListener(v -> {
            String text = etNote.getText() != null ? etNote.getText().toString().trim() : "";
            if (text.isEmpty()) {
                Toast.makeText(requireContext(), "Enter a notice before submitting.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (ride == null || ride.getDriver() == null || ride.getPassenger() == null) {
                Toast.makeText(requireContext(), "Ride not loaded yet.", Toast.LENGTH_SHORT).show();
                return;
            }
            sendReport(text, etNote);
        });

        if (rideId <= 0) {
            Toast.makeText(requireContext(), "Ride not found.", Toast.LENGTH_SHORT).show();
            return;
        }

        running = true;
        loadRide();
    }

    private void loadRide() {
        rideAPI.getRide(rideId).enqueue(new Callback<Ride>() {
            @Override
            public void onResponse(Call<Ride> call, Response<Ride> response) {
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
            public void onFailure(Call<Ride> call, Throwable t) {
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
                public void onResponse(Call<Ride> call, Response<Ride> response) {
                    if (!running) return;
                    if (response.isSuccessful() && response.body() != null) {
                        ride = response.body();
                        onRideUpdated();
                    }
                    if (running) handler.postDelayed(pollRunnable, POLL_INTERVAL_MS);
                }

                @Override
                public void onFailure(Call<Ride> call, Throwable t) {
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
            trackingController = new RideTrackingMapController(mapView, ride.getId());
        }

        boolean ongoing = ride.getStatus() == RideStatus.Started;
        boolean finished = ride.getStatus() == RideStatus.Finished;

        if (reportSection != null) reportSection.setVisibility(ongoing ? View.VISIBLE : View.GONE);
        if (ratingSection != null) ratingSection.setVisibility(finished ? View.VISIBLE : View.GONE);
        if (etaCard != null) etaCard.setVisibility(ride.getStatus() == RideStatus.Cancelled ? View.GONE : View.VISIBLE);

        if (finished) {
            tvEta.setText("Ride finished");
            if (!alreadyRated) checkExistingRating();
        } else if (ride.getStatus() == RideStatus.Cancelled) {
            tvEta.setText("Ride cancelled");
            running = false;
        }

        updateEtaAndPosition();
    }

    private void updateEtaAndPosition() {
        if (ride == null || ride.getStatus() != RideStatus.Started) return;
        if (ride.getRideStartDatetime() == null || ride.getRideDuration() == null) return;

        LocalDateTime start;
        try {
            start = LocalDateTime.parse(ride.getRideStartDatetime());
        } catch (Exception e) {
            return;
        }

        long elapsedSeconds = ChronoUnit.SECONDS.between(start, LocalDateTime.now());
        int totalSeconds = ride.getRideDuration();
        long remaining = totalSeconds - elapsedSeconds;
        if (remaining < 0) remaining = 0;

        int m = (int) (remaining / 60);
        int s = (int) (remaining % 60);
        tvEta.setText(String.format("%02d:%02d", m, s));

        double progress = totalSeconds <= 0 ? 1 : (double) elapsedSeconds / totalSeconds;
        if (trackingController != null) trackingController.updateProgress(progress);
    }

    // 2.8 - proverava da li je voznja vec ocenjena (npr. korisnik se vratio na ekran).
    private void checkExistingRating() {
        if (ride == null || ride.getId() == null) return;
        driverRatingAPI.getByRide(ride.getId()).enqueue(new Callback<DriverRatingResponseDTO>() {
            @Override
            public void onResponse(Call<DriverRatingResponseDTO> call, Response<DriverRatingResponseDTO> response) {
                if (!isAdded()) return;
                if (response.isSuccessful() && response.body() != null) {
                    markAlreadyRated(response.body());
                }
            }

            @Override
            public void onFailure(Call<DriverRatingResponseDTO> call, Throwable t) {
                // 404 (nije ocenjeno) stize kao neuspesan odgovor - nista ne radimo.
            }
        });
    }

    private void markAlreadyRated(DriverRatingResponseDTO rating) {
        alreadyRated = true;
        if (btnSubmitRatingField != null) btnSubmitRatingField.setEnabled(false);
        if (npDriverField != null) npDriverField.setEnabled(false);
        if (npVehicleField != null) npVehicleField.setEnabled(false);
        if (etRatingCommentField != null) etRatingCommentField.setEnabled(false);
        if (btnSubmitRatingField != null) btnSubmitRatingField.setText("Already rated");
        if (rating != null) {
            if (rating.driverRating != null && npDriverField != null) npDriverField.setValue(rating.driverRating);
            if (rating.vehicleRating != null && npVehicleField != null) npVehicleField.setValue(rating.vehicleRating);
            if (etRatingCommentField != null) etRatingCommentField.setText(rating.text != null ? rating.text : "");
        }
    }

    private void submitRating() {
        if (alreadyRated || ride == null || ride.getId() == null) return;

        int driverScore = npDriverField.getValue();
        int vehicleScore = npVehicleField.getValue();
        String comment = etRatingCommentField.getText() != null
                ? etRatingCommentField.getText().toString().trim() : "";

        btnSubmitRatingField.setEnabled(false);

        DriverRatingCreateRequestDTO body = new DriverRatingCreateRequestDTO(
                ride.getId(), driverScore, vehicleScore, comment.isEmpty() ? null : comment);

        driverRatingAPI.createRating(body).enqueue(new Callback<DriverRatingResponseDTO>() {
            @Override
            public void onResponse(Call<DriverRatingResponseDTO> call, Response<DriverRatingResponseDTO> response) {
                if (!isAdded()) return;
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(requireContext(), "Thanks for rating your ride!", Toast.LENGTH_SHORT).show();
                    markAlreadyRated(response.body());
                } else {
                    btnSubmitRatingField.setEnabled(true);
                    Toast.makeText(requireContext(), describeError(response), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<DriverRatingResponseDTO> call, Throwable t) {
                if (!isAdded()) return;
                btnSubmitRatingField.setEnabled(true);
                Toast.makeText(requireContext(), "Network error while submitting rating.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String describeError(Response<?> response) {
        try {
            if (response.errorBody() != null) {
                String msg = response.errorBody().string();
                if (msg != null && !msg.trim().isEmpty()) return msg;
            }
        } catch (Exception ignored) { }
        switch (response.code()) {
            case 409: return "This ride has already been rated.";
            case 410: return "Rating deadline (3 days) has expired.";
            case 400: return "Ratings must be between 1 and 5.";
            case 404: return "Ride not found.";
            default: return "Could not submit rating (" + response.code() + ").";
        }
    }

    private void sendReport(String text, TextInputEditText etNote) {
        DriverReportCreateRequestDTO body = new DriverReportCreateRequestDTO(
                ride.getId(), ride.getDriver().getId(), ride.getPassenger().getId(), text);

        driverReportAPI.createReport(body).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (!isAdded()) return;
                if (response.isSuccessful()) {
                    Toast.makeText(requireContext(), "Report submitted.", Toast.LENGTH_SHORT).show();
                    etNote.setText("");
                } else {
                    Toast.makeText(requireContext(), "Could not submit report.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (!isAdded()) return;
                Toast.makeText(requireContext(), "Network error while sending report.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupPicker(NumberPicker np) {
        np.setMinValue(1);
        np.setMaxValue(5);
        np.setValue(5);
        np.setWrapSelectorWheel(false);
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