package com.example.mobile_applications_project_2025;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

import java.util.Calendar;
import java.util.Locale;

public class RideCreationFragment extends Fragment {
    private MaterialTextView tvPassengerLabel;
    private MaterialTextView tvStopLabel;
    private TextInputEditText etPassenger;
    private TextInputEditText etStop;
    private MaterialButton btnSetPassenger;
    private MaterialButton btnSetStop;

    private MaterialButton btnPickDateTime;
    private MaterialTextView tvSelectedDateTime;

    private MaterialAutoCompleteTextView ddVehicleType;

    private MaterialCheckBox cbBabyFriendly;
    private MaterialCheckBox cbAnimalFriendly;

    private MaterialTextView tvDistanceValue;
    private MaterialTextView tvPriceValue;

    private final Calendar selected = Calendar.getInstance();

    public RideCreationFragment() {
        // Required empty public constructor
    }

    public static RideCreationFragment newInstance(String param1, String param2) {
        RideCreationFragment fragment = new RideCreationFragment();
        Bundle bundle = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ride_creation, container, false);

        tvPassengerLabel = view.findViewById(R.id.tvPassengerLabel);
        tvStopLabel = view.findViewById(R.id.tvStopLabel);
        etPassenger = view.findViewById(R.id.etPassenger);
        etStop = view.findViewById(R.id.etStop);
        btnSetPassenger = view.findViewById(R.id.btnSetPassenger);
        btnSetStop = view.findViewById(R.id.btnSetStop);

        btnPickDateTime = view.findViewById(R.id.btnPickDateTime);
        tvSelectedDateTime = view.findViewById(R.id.tvSelectedDateTime);

        ddVehicleType = view.findViewById(R.id.ddVehicleType);

        cbBabyFriendly = view.findViewById(R.id.cbBabyFriendly);
        cbAnimalFriendly = view.findViewById(R.id.cbAnimalFriendly);

        tvDistanceValue = view.findViewById(R.id.tvDistanceValue);
        tvPriceValue = view.findViewById(R.id.tvPriceValue);

        setupAddressCapture(btnSetPassenger, etPassenger, tvPassengerLabel);
        setupAddressCapture(btnSetStop, etStop, tvStopLabel);

        String[] vehicleTypes = new String[]{"Standard", "Luxury", "Van"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, vehicleTypes);
        ddVehicleType.setAdapter(adapter);

        btnPickDateTime.setOnClickListener(v -> openDateThenTimePicker());

        updateDateTimeText();

        MaterialButton btnCreateRide = view.findViewById(R.id.btnCreateRide);
        btnCreateRide.setOnClickListener(v -> {
            new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                    .setTitle("Ride created")
                    .setMessage("Your ride has been created successfully.")
                    .setCancelable(false)
                    .setPositiveButton("OK", (dialog, which) -> {
                        dialog.dismiss();
                        androidx.navigation.fragment.NavHostFragment
                                .findNavController(this)
                                .navigateUp();
                    })
                    .show();
        });


        return view;
    }

    private void setupAddressCapture(MaterialButton button, TextInputEditText field, MaterialTextView label) {
        button.setOnClickListener(v -> {
            String text = field.getText() == null ? "" : field.getText().toString().trim();
            if (TextUtils.isEmpty(text)) return;
            label.setText(label.getText() + "\n" + text);
            label.setVisibility(View.VISIBLE);
            field.setText("");
        });
    }

    private void openDateThenTimePicker() {
        Calendar now = Calendar.getInstance();

        DatePickerDialog dp = new DatePickerDialog(
                requireContext(),
                (datePicker, year, month, dayOfMonth) -> {
                    selected.set(Calendar.YEAR, year);
                    selected.set(Calendar.MONTH, month);
                    selected.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    TimePickerDialog tp = new TimePickerDialog(
                            requireContext(),
                            (timePicker, hourOfDay, minute) -> {
                                selected.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                selected.set(Calendar.MINUTE, minute);
                                selected.set(Calendar.SECOND, 0);
                                selected.set(Calendar.MILLISECOND, 0);
                                updateDateTimeText();
                            },
                            now.get(Calendar.HOUR_OF_DAY),
                            now.get(Calendar.MINUTE),
                            true
                    );
                    tp.show();
                },
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dp.show();
    }

    private void updateDateTimeText() {
        String s = String.format(
                Locale.getDefault(),
                "%04d-%02d-%02d %02d:%02d",
                selected.get(Calendar.YEAR),
                selected.get(Calendar.MONTH),
                selected.get(Calendar.DAY_OF_MONTH),
                selected.get(Calendar.HOUR_OF_DAY) + 1,
                selected.get(Calendar.MINUTE)
        );
        tvSelectedDateTime.setText(s);
    }
}