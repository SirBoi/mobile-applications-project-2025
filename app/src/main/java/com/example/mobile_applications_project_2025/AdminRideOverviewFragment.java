package com.example.mobile_applications_project_2025;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;

public class AdminRideOverviewFragment extends Fragment {

    private LinearLayout cardsContainer;
    private int orange;
    private int white;
    private int black;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_admin_ride_overview, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        cardsContainer = view.findViewById(R.id.cardsContainer);
        TextInputEditText etSearch = view.findViewById(R.id.etSearch);
        MaterialButton btnSearch = view.findViewById(R.id.btnSearch);

        orange = requireContext().getColor(R.color.orange_action);
        white = requireContext().getColor(R.color.white);
        black = requireContext().getColor(R.color.black);

        // Hardcoded cards (UI mock)
        addRideCard("Marko Marković", "10:21", "10:48");
        addRideCard("Ivana Ivanović", "11:05", "11:37");
        addRideCard("Petar Petrović", "12:10", "12:44");

        btnSearch.setOnClickListener(v -> {
            String q = etSearch.getText() == null ? "" : etSearch.getText().toString();
            Toast.makeText(requireContext(),
                    "Search clicked: " + q,
                    Toast.LENGTH_SHORT).show();
        });
    }

    /* =========================
       Card helper
       ========================= */

    private void addRideCard(String driver, String dep, String arr) {
        Context ctx = requireContext();

        MaterialCardView card = new MaterialCardView(ctx);
        LinearLayout.LayoutParams cardLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        cardLp.bottomMargin = dp(10);
        card.setLayoutParams(cardLp);
        card.setRadius(dp(18));
        card.setCardElevation(2f);
        card.setCardBackgroundColor(white);

        LinearLayout content = new LinearLayout(ctx);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(dp(14), dp(14), dp(14), dp(14));

        TextView tvDriver = new TextView(ctx);
        tvDriver.setText("Driver: " + driver);
        tvDriver.setTextColor(black);
        tvDriver.setTextSize(16);
        tvDriver.setTypeface(null, android.graphics.Typeface.BOLD);

        TextView tvDep = new TextView(ctx);
        tvDep.setText("Departure: " + dep);
        tvDep.setTextColor(black);
        tvDep.setPadding(0, dp(6), 0, 0);

        TextView tvArr = new TextView(ctx);
        tvArr.setText("Arrival: " + arr);
        tvArr.setTextColor(black);
        tvArr.setPadding(0, dp(4), 0, 0);

        content.addView(tvDriver);
        content.addView(tvDep);
        content.addView(tvArr);

        card.addView(content);
        cardsContainer.addView(card);
    }

    private int dp(int v) {
        float d = requireContext().getResources().getDisplayMetrics().density;
        return (int) (v * d);
    }
}
