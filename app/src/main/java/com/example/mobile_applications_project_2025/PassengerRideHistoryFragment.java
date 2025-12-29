package com.example.mobile_applications_project_2025;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

public class PassengerRideHistoryFragment extends Fragment {

    public PassengerRideHistoryFragment() {
        // Required empty public constructor
    }

    public static PassengerRideHistoryFragment newInstance(String param1, String param2) {
        PassengerRideHistoryFragment fragment = new PassengerRideHistoryFragment();
        Bundle bundle = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_passenger_ride_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        View card1 = view.findViewById(R.id.card1);
        View card2 = view.findViewById(R.id.card2);
        View card3 = view.findViewById(R.id.card3);
        Button btnApply = view.findViewById(R.id.btnApply);

        View.OnClickListener goToOverview = v -> {
            NavController nav = NavHostFragment.findNavController(this);
            nav.navigate(R.id.action_passengerRideHistoryFragment_to_passengerRideOverviewFragment);

        };

        card1.setOnClickListener(goToOverview);
        card2.setOnClickListener(goToOverview);
        card3.setOnClickListener(goToOverview);

        btnApply.setOnClickListener(v ->
                Toast.makeText(requireContext(), "UI-only filter.", Toast.LENGTH_SHORT).show()
        );
    }
}
