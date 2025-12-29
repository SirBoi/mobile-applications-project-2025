package com.example.mobile_applications_project_2025;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

public class DriverRideOverviewFragment extends Fragment {

    private CountDownTimer timer; // ostavljeno radi iste strukture

    public DriverRideOverviewFragment() {
        // Required empty public constructor
    }

    public static DriverRideOverviewFragment newInstance(String param1, String param2) {
        DriverRideOverviewFragment fragment = new DriverRideOverviewFragment();
        Bundle bundle = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_driver_ride_overview, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView tvEta = view.findViewById(R.id.tvEta);
        CheckBox cbFinished = view.findViewById(R.id.cbFinished);
        CheckBox cbPaid = view.findViewById(R.id.cbPaid);
        Button btnSubmit = view.findViewById(R.id.btnSubmit);

        // ETA odmah 00:00 (nema odbrojavanja)
        tvEta.setText("00:00");

        btnSubmit.setOnClickListener(v -> {
            boolean finished = cbFinished.isChecked();
            boolean paid = cbPaid.isChecked();

            if (!finished || !paid) {
                Toast.makeText(requireContext(),
                        "No rides available",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(requireContext(),
                    "No rides available",
                    Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onDestroyView() {
        if (timer != null) timer.cancel();
        super.onDestroyView();
    }
}
