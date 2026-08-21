package com.example.mobile_applications_project_2025;

import android.content.Context;
import android.os.Bundle;
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
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// 2.13 - Admin: pregled voznji koje trenutno traju, pretraga po imenu vozaca.
public class AdminRideOverviewFragment extends Fragment {

    private LinearLayout cardsContainer;
    private TextInputEditText etSearch;
    private RideAPI rideAPI;
    private int orange, white, black;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_ride_overview, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        cardsContainer = view.findViewById(R.id.cardsContainer);
        etSearch = view.findViewById(R.id.etSearch);
        MaterialButton btnSearch = view.findViewById(R.id.btnSearch);

        orange = requireContext().getColor(R.color.orange_action);
        white = requireContext().getColor(R.color.white);
        black = requireContext().getColor(R.color.black);

        rideAPI = ApiClient.getRetrofit().create(RideAPI.class);

        search(null);
        btnSearch.setOnClickListener(v -> search(etSearch.getText() == null ? "" : etSearch.getText().toString().trim()));
    }

    @Override
    public void onResume() {
        super.onResume();
        if (rideAPI != null) search(etSearch.getText() == null ? null : etSearch.getText().toString().trim());
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
        cardsContainer.removeAllViews();
        if (rides.isEmpty()) {
            TextView empty = new TextView(requireContext());
            empty.setText("No rides currently in progress.");
            cardsContainer.addView(empty);
            return;
        }
        for (OngoingRideResponseDTO r : rides) addRideCard(r);
    }

    private void addRideCard(OngoingRideResponseDTO r) {
        Context ctx = requireContext();

        MaterialCardView card = new MaterialCardView(ctx);
        LinearLayout.LayoutParams cardLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        cardLp.bottomMargin = dp(10);
        card.setLayoutParams(cardLp);
        card.setRadius(dp(18));
        card.setCardElevation(2f);
        card.setCardBackgroundColor(Boolean.TRUE.equals(r.isPanicPressed)
                ? android.graphics.Color.parseColor("#D32F2F") : white);

        LinearLayout content = new LinearLayout(ctx);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(dp(14), dp(14), dp(14), dp(14));

        int textColor = Boolean.TRUE.equals(r.isPanicPressed) ? white : black;

        content.addView(line(ctx, "Driver: " + safe(r.driverName), textColor, true));
        content.addView(line(ctx, "From: " + formatAddress(r.origin), textColor, false));
        content.addView(line(ctx, "To: " + formatAddress(r.destination), textColor, false));
        content.addView(line(ctx, "Departed: " + safe(r.rideStartDatetime), textColor, false));
        content.addView(line(ctx, "ETA: " + safe(r.estimatedFinishDatetime), textColor, false));
        content.addView(line(ctx, "Progress: " + (r.progressPercent == null ? 0 : r.progressPercent) + "%", textColor, false));
        if (r.passengerNames != null && !r.passengerNames.isEmpty()) {
            content.addView(line(ctx, "Passengers: " + String.join(", ", r.passengerNames), textColor, false));
        }
        if (Boolean.TRUE.equals(r.isPanicPressed)) {
            content.addView(line(ctx, "⚠ PANIC pressed!", textColor, true));
        }

        card.addView(content);
        cardsContainer.addView(card);
    }

    private TextView line(Context ctx, String text, int color, boolean bold) {
        TextView tv = new TextView(ctx);
        tv.setText(text);
        tv.setTextColor(color);
        tv.setPadding(0, dp(4), 0, 0);
        if (bold) {
            tv.setTextSize(16);
            tv.setTypeface(null, android.graphics.Typeface.BOLD);
        }
        return tv;
    }

    private String formatAddress(Address a) {
        if (a == null) return "-";
        String street = a.street != null ? a.street : "";
        String number = a.number != null ? a.number : "";
        String city = a.city != null ? a.city : "";
        return (street + " " + number + ", " + city).trim();
    }

    private String safe(String s) { return s == null ? "-" : s; }

    private int dp(int v) {
        float d = requireContext().getResources().getDisplayMetrics().density;
        return (int) (v * d);
    }
}