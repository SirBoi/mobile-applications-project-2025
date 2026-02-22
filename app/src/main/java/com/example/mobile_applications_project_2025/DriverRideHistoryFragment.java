package com.example.mobile_applications_project_2025;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobile_applications_project_2025.Adapters.DriverRideCardAdapter;
import com.example.mobile_applications_project_2025.DTO.PageResponseDTO;
import com.example.mobile_applications_project_2025.Model.Ride;
import com.example.mobile_applications_project_2025.Model.Enumerator.RideStatus;
import com.example.mobile_applications_project_2025.Network.ApiClient;
import com.example.mobile_applications_project_2025.Network.APIs.RideAPI;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DriverRideHistoryFragment extends Fragment {

    private static final int PAGE_SIZE = 8;

    private CheckBox cbScheduled, cbStarted, cbFinished, cbCanceled;
    private Button btnApply;
    private RecyclerView rvRides;
    private ImageButton btnPrev, btnNext;
    private TextView tvPageInfo;

    private DriverRideCardAdapter adapter;

    private int currentPage = 0; // 0-based
    private int totalPages = 1;

    private RideAPI rideAPI;

    public DriverRideHistoryFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_driver_ride_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        cbScheduled = view.findViewById(R.id.cbScheduled);
        cbStarted = view.findViewById(R.id.cbStarted);
        cbFinished = view.findViewById(R.id.cbFinished);
        cbCanceled = view.findViewById(R.id.cbCanceled);

        btnApply = view.findViewById(R.id.btnApplyStatusFilter);

        rvRides = view.findViewById(R.id.rvRides);
        btnPrev = view.findViewById(R.id.btnPrev);
        btnNext = view.findViewById(R.id.btnNext);
        tvPageInfo = view.findViewById(R.id.tvPageInfo);

        adapter = new DriverRideCardAdapter(this::openRidePopup);
        rvRides.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvRides.setAdapter(adapter);

        rideAPI = ApiClient.getRetrofit().create(RideAPI.class);

        // default selection
        cbScheduled.setChecked(true);

        btnApply.setOnClickListener(v -> {
            currentPage = 0;
            fetchPage();
        });

        btnPrev.setOnClickListener(v -> {
            if (currentPage > 0) {
                currentPage--;
                fetchPage();
            }
        });

        btnNext.setOnClickListener(v -> {
            if (currentPage < totalPages - 1) {
                currentPage++;
                fetchPage();
            }
        });

        fetchPage();
    }

    private void fetchPage() {
        if (!SessionManager.isLoggedIn() || SessionManager.getUser() == null) {
            Toast.makeText(requireContext(), "Not logged in.", Toast.LENGTH_SHORT).show();
            return;
        }

        Long driverId = SessionManager.getUser().getId();

        List<String> statuses = getSelectedStatuses();
        if (statuses.isEmpty()) {
            adapter.submit(new ArrayList<>());
            totalPages = 1;
            currentPage = 0;
            updatePagerUi();
            Toast.makeText(requireContext(), "Select at least one status.", Toast.LENGTH_SHORT).show();
            return;
        }

        rideAPI.getDriverRidesPaged(driverId, statuses, currentPage, PAGE_SIZE)
                .enqueue(new Callback<PageResponseDTO<Ride>>() {
                    @Override
                    public void onResponse(@NonNull Call<PageResponseDTO<Ride>> call,
                                           @NonNull Response<PageResponseDTO<Ride>> response) {
                        if (!response.isSuccessful() || response.body() == null) {
                            Toast.makeText(requireContext(),
                                    "Failed to load rides (" + response.code() + ")",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }

                        PageResponseDTO<Ride> page = response.body();

                        adapter.submit(page.content);

                        currentPage = page.number;
                        totalPages = Math.max(page.totalPages, 1);

                        updatePagerUi();
                    }

                    @Override
                    public void onFailure(@NonNull Call<PageResponseDTO<Ride>> call, @NonNull Throwable t) {
                        Toast.makeText(requireContext(),
                                "Network error: " + t.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updatePagerUi() {
        tvPageInfo.setText((currentPage + 1) + " / " + totalPages);
        btnPrev.setEnabled(currentPage > 0);
        btnNext.setEnabled(currentPage < totalPages - 1);
    }

    private List<String> getSelectedStatuses() {
        List<String> list = new ArrayList<>();
        if (cbScheduled.isChecked()) list.add(RideStatus.Scheduled.name());
        if (cbStarted.isChecked()) list.add(RideStatus.Started.name());
        if (cbFinished.isChecked()) list.add(RideStatus.Finished.name());
        if (cbCanceled.isChecked()) list.add(RideStatus.Cancelled.name());
        if (!cbScheduled.isChecked() && !cbStarted.isChecked() && !cbFinished.isChecked() && !cbCanceled.isChecked()) {
            list.clear();
            list.add(RideStatus.Scheduled.name());
            list.add(RideStatus.Started.name());
            list.add(RideStatus.Finished.name());
            list.add(RideStatus.Cancelled.name());
        }
        return list;
    }

    private void openRidePopup(Ride ride) {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_ride_actions, null, false);

        Button btnStart = dialogView.findViewById(R.id.btnStart);
        Button btnFinish = dialogView.findViewById(R.id.btnFinish);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);

        String from = ride.origin != null ? (ride.origin.street + " " + ride.origin.number) : "?";
        String to = ride.destination != null ? (ride.destination.street + " " + ride.destination.number) : "?";

        // Button enable rules:
        // Scheduled: start+finish+cancel enabled
        // Started: only finish enabled
        // Finished: none enabled
        // Cancelled: none enabled
        boolean startEnabled = false, finishEnabled = false, cancelEnabled = false;

        if (ride.status == RideStatus.Scheduled) {
            startEnabled = true;
            finishEnabled = true;
            cancelEnabled = true;
        } else if (ride.status == RideStatus.Started) {
            finishEnabled = true;
        }

        setActionButtonEnabled(btnStart, startEnabled);
        setActionButtonEnabled(btnFinish, finishEnabled);
        setActionButtonEnabled(btnCancel, cancelEnabled);

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .setCancelable(true)
                .create();

        btnStart.setOnClickListener(v -> {
            setActionButtonEnabled(btnStart, false);
            rideAPI.startRide(ride.id).enqueue(new RideActionCallback(dialog, "Ride started"));
        });

        btnFinish.setOnClickListener(v -> {
            setActionButtonEnabled(btnFinish, false);
            rideAPI.finishRide(ride.id).enqueue(new RideActionCallback(dialog, "Ride finished"));
        });

        btnCancel.setOnClickListener(v -> {
            setActionButtonEnabled(btnCancel, false);
            rideAPI.cancelRide(ride.id).enqueue(new RideActionCallback(dialog, "Ride cancelled"));
        });

        dialog.show();
    }

    private class RideActionCallback implements Callback<Ride> {
        private final AlertDialog dialog;
        private final String successMessage;

        RideActionCallback(AlertDialog dialog, String successMessage) {
            this.dialog = dialog;
            this.successMessage = successMessage;
        }

        @Override
        public void onResponse(@NonNull Call<Ride> call, @NonNull Response<Ride> response) {
            if (!response.isSuccessful()) {
                Toast.makeText(requireContext(), "Action failed (" + response.code() + ")", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(requireContext(), successMessage, Toast.LENGTH_SHORT).show();
            dialog.dismiss();
            fetchPage();
        }

        @Override
        public void onFailure(@NonNull Call<Ride> call, @NonNull Throwable t) {
            Toast.makeText(requireContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void setActionButtonEnabled(Button btn, boolean enabled) {
        btn.setEnabled(enabled);

        int enabledBg = ContextCompat.getColor(requireContext(), R.color.orange_action);
        int enabledText = ContextCompat.getColor(requireContext(), R.color.white);

        int disabledBg = ContextCompat.getColor(requireContext(), R.color.button_disabled_bg);
        int disabledText = ContextCompat.getColor(requireContext(), R.color.button_disabled_text);

        btn.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(enabled ? enabledBg : disabledBg)
        );
        btn.setTextColor(enabled ? enabledText : disabledText);

        btn.setAlpha(enabled ? 1.0f : 0.75f);
    }
}