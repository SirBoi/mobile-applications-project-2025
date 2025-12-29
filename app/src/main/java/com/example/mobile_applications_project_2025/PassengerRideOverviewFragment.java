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
import android.widget.TextView;
import android.widget.Toast;
import android.widget.NumberPicker;

import com.google.android.material.textfield.TextInputEditText;

public class PassengerRideOverviewFragment extends Fragment {
    private CountDownTimer timer;

    public PassengerRideOverviewFragment() {
        // Required empty public constructor
    }

    public static PassengerRideOverviewFragment newInstance(String param1, String param2) {
        PassengerRideOverviewFragment fragment = new PassengerRideOverviewFragment();
        Bundle bundle = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_passenger_ride_overview, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView tvEta = view.findViewById(R.id.tvEta);
        TextInputEditText etNote = view.findViewById(R.id.etNote);
        Button btnSend = view.findViewById(R.id.btnSend);
        Button btnReport = view.findViewById(R.id.btnReport);

        NumberPicker npDriver = view.findViewById(R.id.npDriver);
        NumberPicker npVehicle = view.findViewById(R.id.npVehicle);
        TextInputEditText etRatingComment = view.findViewById(R.id.etRatingComment);
        Button btnSubmitRating = view.findViewById(R.id.btnSubmitRating);



        setupPicker(npDriver);
        setupPicker(npVehicle);

        btnSubmitRating.setOnClickListener(v -> {
            int driverScore = npDriver.getValue();
            int vehicleScore = npVehicle.getValue();
            String comment = etRatingComment.getText() != null ? etRatingComment.getText().toString().trim() : "";

            Toast.makeText(requireContext(),
                    "Ocena vozača: " + driverScore + ", Ocena vozila: " + vehicleScore +
                            (comment.isEmpty() ? "" : (", Komentar: " + comment)),
                    Toast.LENGTH_SHORT).show();

            etRatingComment.setText("");
        });


        startMockEtaCountdown(tvEta, 4 * 60 + 35);

        btnSend.setOnClickListener(v -> {
            String text = etNote.getText() != null ? etNote.getText().toString().trim() : "";
            if (text.isEmpty()) {
                Toast.makeText(requireContext(), "Enter a notice before submitting.", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(requireContext(), "Report submitted " + text, Toast.LENGTH_SHORT).show();
            etNote.setText("");
        });
    }

    private void setupPicker(NumberPicker np) {
        np.setMinValue(1);
        np.setMaxValue(5);
        np.setValue(5);
        np.setWrapSelectorWheel(false);
    }


    private void startMockEtaCountdown(TextView tvEta, int totalSeconds) {
        if (timer != null) timer.cancel();

        timer = new CountDownTimer(totalSeconds * 1000L, 1000L) {
            int secondsLeft = totalSeconds;

            @Override
            public void onTick(long millisUntilFinished) {
                secondsLeft--;
                int m = Math.max(0, secondsLeft) / 60;
                int s = Math.max(0, secondsLeft) % 60;
                tvEta.setText(String.format("%02d:%02d", m, s));
            }

            @Override
            public void onFinish() {
                tvEta.setText("00:00");
            }
        }.start();
    }

    @Override
    public void onDestroyView() {
        if (timer != null) timer.cancel();
        super.onDestroyView();
    }
}