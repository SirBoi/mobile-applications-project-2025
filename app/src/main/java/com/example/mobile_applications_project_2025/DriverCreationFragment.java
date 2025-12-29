package com.example.mobile_applications_project_2025;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;

import com.example.mobile_applications_project_2025.Model.Enumerator.CarType;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

public class DriverCreationFragment extends Fragment {
    public DriverCreationFragment() {
        // Required empty public constructor
    }

    public static DriverCreationFragment newInstance(String param1, String param2) {
        DriverCreationFragment fragment = new DriverCreationFragment();
        Bundle bundle = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_driver_creation, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        TextInputLayout tilMail = view.findViewById(R.id.tilMail);
        TextInputEditText etMail = view.findViewById(R.id.etMail);
        TextInputEditText etFirstName = view.findViewById(R.id.etFirstName);
        TextInputEditText etLastName = view.findViewById(R.id.etLastName);
        TextInputEditText etAddress = view.findViewById(R.id.etAddress);
        TextInputEditText etPhone = view.findViewById(R.id.etPhone);

        TextInputEditText etCarModel = view.findViewById(R.id.etCarModel);
        TextInputLayout tilCarType = view.findViewById(R.id.tilCarType);
        AutoCompleteTextView actCarType = view.findViewById(R.id.actCarType);
        TextInputEditText etPlate = view.findViewById(R.id.etPlateNumber);
        TextInputEditText etSeats = view.findViewById(R.id.etSeats);

        CheckBox cbBaby = view.findViewById(R.id.cbBabyFriendly);
        CheckBox cbAnimal = view.findViewById(R.id.cbAnimalFriendly);

        MaterialButton btnCreate = view.findViewById(R.id.btnCreate);

        List<String> carTypes = new ArrayList<>();
        for (CarType t : CarType.values()) carTypes.add(String.valueOf(t));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, carTypes);
        actCarType.setAdapter(adapter);

        btnCreate.setOnClickListener(v -> {
            String mail = text(etMail);
            boolean mailOk = !mail.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(mail).matches();
            tilMail.setError(mailOk ? null : "Invalid email");
            tilMail.setErrorEnabled(!mailOk);

            if (!mailOk) return;

            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Driver created")
                    .setMessage("The driver was created successfully.")
                    .setPositiveButton("OK", (d, w) -> {
                        d.dismiss();
                        NavHostFragment.findNavController(this).popBackStack();
                    })
                    .show();
        });
    }

    private String text(TextInputEditText et) {
        return et.getText() != null ? et.getText().toString().trim() : "";
    }
}