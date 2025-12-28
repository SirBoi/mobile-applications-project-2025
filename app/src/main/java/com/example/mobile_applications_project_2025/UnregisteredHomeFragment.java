package com.example.mobile_applications_project_2025;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

public class UnregisteredHomeFragment extends Fragment {
    public UnregisteredHomeFragment() {
        // Required empty public constructor
    }

    public static UnregisteredHomeFragment newInstance(String param1, String param2) {
        UnregisteredHomeFragment fragment = new UnregisteredHomeFragment();
        Bundle bundle = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_unregistered_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button btnPrijava = view.findViewById(R.id.btnPrijava);
        btnPrijava.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), DriverRideHistoryFragment.class);
            // Intent intent = new Intent(requireContext(), PassengerRideOverviewFragment.class);
            startActivity(intent);
        });

        View markerBusy1 = view.findViewById(R.id.markerBusy1);
        View markerFree1 = view.findViewById(R.id.markerFree1);
        View markerFree2 = view.findViewById(R.id.markerFree2);

        markerBusy1.setOnClickListener(v ->
                Toast.makeText(requireContext(), "Vozilo: Zauzeto", Toast.LENGTH_SHORT).show()
        );
        markerFree1.setOnClickListener(v ->
                Toast.makeText(requireContext(), "Vozilo: Slobodno", Toast.LENGTH_SHORT).show()
        );
        markerFree2.setOnClickListener(v ->
                Toast.makeText(requireContext(), "Vozilo: Slobodno", Toast.LENGTH_SHORT).show()
        );
    }
}
























