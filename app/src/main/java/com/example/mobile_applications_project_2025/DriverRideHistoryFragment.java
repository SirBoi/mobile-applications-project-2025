package com.example.mobile_applications_project_2025;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;
import java.util.Locale;

public class DriverRideHistoryFragment extends Fragment {
    private TextInputEditText etFromDate, etToDate;

    public DriverRideHistoryFragment() {
        // Required empty public constructor
    }

    public static DriverRideHistoryFragment newInstance(String param1, String param2) {
        DriverRideHistoryFragment fragment = new DriverRideHistoryFragment();
        Bundle bundle = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_driver_ride_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etFromDate = view.findViewById(R.id.etFromDate);
        etToDate = view.findViewById(R.id.etToDate);
        Button btnApply = view.findViewById(R.id.btnApply);

        etFromDate.setOnClickListener(v -> showDatePicker(etFromDate));
        etToDate.setOnClickListener(v -> showDatePicker(etToDate));

        btnApply.setOnClickListener(v -> {
            String from = etFromDate.getText() != null ? etFromDate.getText().toString() : "";
            String to = etToDate.getText() != null ? etToDate.getText().toString() : "";
            Toast.makeText(requireContext(), " " + from + " → " + to, Toast.LENGTH_SHORT).show();
        });
    }

    private void showDatePicker(TextInputEditText target) {
        Calendar c = Calendar.getInstance();
        int y = c.get(Calendar.YEAR);
        int m = c.get(Calendar.MONTH);
        int d = c.get(Calendar.DAY_OF_MONTH);

        new DatePickerDialog(requireContext(), (dp, year, month, dayOfMonth) -> {
            String dd = String.format(
                    Locale.getDefault(),
                    "%02d.%02d.%04d",
                    dayOfMonth,
                    (month + 1),
                    year
            );
            target.setText(dd);
        }, y, m, d).show();
    }
}