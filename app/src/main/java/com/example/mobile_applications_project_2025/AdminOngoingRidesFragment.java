package com.example.mobile_applications_project_2025;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mobile_applications_project_2025.DTO.OngoingRideResponseDTO;
import com.example.mobile_applications_project_2025.Model.Address;
import com.example.mobile_applications_project_2025.Network.APIs.RideAPI;
import com.example.mobile_applications_project_2025.Network.ApiClient;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// 2.13 - Admin: pregled stanja voznje koja trenutno traje, bilo kog vozaca,
// sa pretragom po imenu vozaca. Nema RecyclerView-a namerno, drzimo prosto.
public class AdminOngoingRidesFragment extends Fragment {

    private LinearLayout container;
    private TextInputEditText etSearch;
    private RideAPI rideAPI;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup parent,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_ongoing_rides, parent, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        container = view.findViewById(R.id.ongoingRidesContainer);
        etSearch = view.findViewById(R.id.etDriverSearch);
        rideAPI = ApiClient.getRetrofit().create(RideAPI.class);

        search(null);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int a, int b, int c) {}
            @Override public void onTextChanged(CharSequence s, int a, int b, int c) {}
            @Override public void afterTextChanged(Editable s) {
                search(s == null ? "" : s.toString().trim());
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (rideAPI != null) {
            search(etSearch.getText() == null ? null : etSearch.getText().toString().trim());
        }
    }

    private void search(String driverName) {
        rideAPI.getOngoingRides(driverName).enqueue(new Callback<List<OngoingRideResponseDTO>>() {
            @Override
            public void onResponse(@NonNull Call<List<OngoingRideResponseDTO>> call, @NonNull Response<List<OngoingRideResponseDTO>> response) {
                if (!isAdded()) return;
                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(requireContext(), "Could not load ongoing rides.", Toast.LENGTH_SHORT).show();
                    return;
                }
                render(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<List<OngoingRideResponseDTO>> call, @NonNull Throwable t) {
                if (!isAdded()) return;
                Toast.makeText(requireContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void render(List<OngoingRideResponseDTO> rides) {
        container.removeAllViews();
        if (rides.isEmpty()) {
            TextView empty = new TextView(requireContext());
            empty.setText("No rides currently in progress.");
            container.addView(empty);
            return;
        }
        for (OngoingRideResponseDTO r : rides) {
            container.addView(buildCard(r));
        }
    }

    private View buildCard(OngoingRideResponseDTO r) {
        MaterialCardView card = new MaterialCardView(requireContext());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.bottomMargin = dp(10);
        card.setLayoutParams(lp);
        card.setRadius(dp(16));
        card.setCardElevation(dp(2));
        card.setCardBackgroundColor(Boolean.TRUE.equals(r.isPanicPressed)
                ? android.graphics.Color.parseColor("#D32F2F")
                : requireContext().getColor(R.color.orange_action));

        TextView tv = new TextView(requireContext());
        tv.setPadding(dp(14), dp(14), dp(14), dp(14));
        tv.setTextColor(requireContext().getColor(R.color.white));
        tv.setTextSize(14f);
        tv.setText(buildText(r));

        card.addView(tv);
        return card;
    }

    private String buildText(OngoingRideResponseDTO r) {
        StringBuilder sb = new StringBuilder();
        sb.append("Driver: ").append(r.driverName == null ? "-" : r.driverName);
        if (r.carModel != null) sb.append(" (").append(r.carModel);
        if (r.plateNumber != null) sb.append(", ").append(r.plateNumber);
        if (r.carModel != null) sb.append(")");
        sb.append("\nFrom: ").append(formatAddress(r.origin));
        sb.append("\nTo: ").append(formatAddress(r.destination));
        sb.append("\nDeparted: ").append(r.rideStartDatetime == null ? "-" : r.rideStartDatetime);
        sb.append("\nETA: ").append(r.estimatedFinishDatetime == null ? "-" : r.estimatedFinishDatetime);
        sb.append("\nProgress: ").append(r.progressPercent == null ? 0 : r.progressPercent).append("%");
        if (r.passengerNames != null && !r.passengerNames.isEmpty()) {
            sb.append("\nPassengers: ").append(String.join(", ", r.passengerNames));
        }
        if (Boolean.TRUE.equals(r.isPanicPressed)) {
            sb.append("\n⚠ PANIC pressed!");
        }
        return sb.toString();
    }

    private String formatAddress(Address a) {
        if (a == null) return "-";
        String street = a.street != null ? a.street : "";
        String number = a.number != null ? a.number : "";
        String city = a.city != null ? a.city : "";
        return (street + " " + number + ", " + city).trim();
    }

    private int dp(int value) {
        float density = requireContext().getResources().getDisplayMetrics().density;
        return (int) (value * density);
    }
}