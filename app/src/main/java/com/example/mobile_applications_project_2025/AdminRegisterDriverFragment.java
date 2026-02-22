package com.example.mobile_applications_project_2025;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mobile_applications_project_2025.Model.Enumerator.CarType;
import com.example.mobile_applications_project_2025.Network.APIs.RegisteredUserAPI;
import com.example.mobile_applications_project_2025.Network.ApiClient;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.slider.Slider;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class AdminRegisterDriverFragment extends Fragment {

    private EditText etEmail, etFirstName, etLastName, etAddress, etPhone;
    private EditText etModel, etPlateNumber;

    private TextInputLayout tilEmail, tilFirstName, tilLastName,
            tilAddress, tilPhone, tilModel, tilPlate;

    private Spinner spinnerType;
    private CheckBox cbBabyFriendly, cbAnimalFriendly;
    private MaterialButton btnCreateDriver;
    private Slider sliderSeats;
    private TextView tvSeatCount;

    public AdminRegisterDriverFragment() { }

    public static AdminRegisterDriverFragment newInstance() {
        return new AdminRegisterDriverFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_register_driver, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupCarTypeSpinner();
        setupSeatSlider();
        setupCreateButton();
    }

    private void initViews(View view) {

        tilEmail = view.findViewById(R.id.tilAdminRegisterDriverEmail);
        tilFirstName = view.findViewById(R.id.tilAdminRegisterDriverFirstName);
        tilLastName = view.findViewById(R.id.tilAdminRegisterDriverLastName);
        tilAddress = view.findViewById(R.id.tilAdminRegisterDriverAddress);
        tilPhone = view.findViewById(R.id.tilAdminRegisterDriverPhone);
        tilModel = view.findViewById(R.id.tilAdminRegisterDriverModel);
        tilPlate = view.findViewById(R.id.tilAdminRegisterDriverPlateNumber);

        etEmail = view.findViewById(R.id.etAdminRegisterDriverEmail);
        etFirstName = view.findViewById(R.id.etAdminRegisterDriverFirstName);
        etLastName = view.findViewById(R.id.etAdminRegisterDriverLastName);
        etAddress = view.findViewById(R.id.etAdminRegisterDriverAddress);
        etPhone = view.findViewById(R.id.etAdminRegisterDriverPhone);
        etModel = view.findViewById(R.id.etAdminRegisterDriverModel);
        etPlateNumber = view.findViewById(R.id.etAdminRegisterDriverPlateNumber);

        spinnerType = view.findViewById(R.id.spinnerAdminRegisterDriverCarType);
        sliderSeats = view.findViewById(R.id.sliderAdminRegisterDriverSeats);
        tvSeatCount = view.findViewById(R.id.tvAdminRegisterDriverSeatCount);

        cbBabyFriendly = view.findViewById(R.id.cbAdminRegisterDriverBabyFriendly);
        cbAnimalFriendly = view.findViewById(R.id.cbAdminRegisterDriverAnimalFriendly);

        btnCreateDriver = view.findViewById(R.id.btnAdminRegisterDriverCreateDriver);
    }

    private void setupCarTypeSpinner() {
        List<String> carTypes = new ArrayList<>();
        for (CarType type : CarType.values()) {
            carTypes.add(type.name());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                carTypes
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(adapter);
        spinnerType.setSelection(0);
    }

    private void setupSeatSlider() {
        sliderSeats.addOnChangeListener((slider, value, fromUser) -> {
            int seats = (int) value;
            tvSeatCount.setText("Seats: " + seats);
        });
    }

    private void setupCreateButton() {
        btnCreateDriver.setOnClickListener(v -> {
            if (!validateInputs()) {return;}

            JsonObject userJson = new JsonObject();

            userJson.addProperty("mail", etEmail.getText().toString().trim());
            userJson.addProperty("firstName", etFirstName.getText().toString().trim());
            userJson.addProperty("lastName", etLastName.getText().toString().trim());
            userJson.addProperty("address", etAddress.getText().toString().trim());
            userJson.addProperty("phoneNumber", etPhone.getText().toString().trim());
            userJson.addProperty("password", "");
            userJson.addProperty("role", "Driver");
            userJson.addProperty("model", etModel.getText().toString().trim());
            userJson.addProperty("type", spinnerType.getSelectedItem().toString());
            userJson.addProperty("plateNumber", etPlateNumber.getText().toString().trim());
            userJson.addProperty("numberOfSeats", (int) sliderSeats.getValue());
            userJson.addProperty("isBabyFriendly", cbBabyFriendly.isChecked());
            userJson.addProperty("isAnimalFriendly", cbAnimalFriendly.isChecked());

            RegisteredUserAPI api = ApiClient.getRetrofit().create(RegisteredUserAPI.class);
            api.createUser(userJson).enqueue(new retrofit2.Callback<JsonObject>() {
                @Override
                public void onResponse(retrofit2.Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(requireContext(),
                                "Driver created successfully. Activation email sent.",
                                Toast.LENGTH_LONG).show();

                        requireActivity().getSupportFragmentManager().popBackStack();
                    } else {
                        Toast.makeText(requireContext(),
                                "Failed to create driver: " + response.body(),
                                Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(retrofit2.Call<JsonObject> call, Throwable t) {
                    Toast.makeText(requireContext(),
                            "Error: " + t.getMessage(),
                            Toast.LENGTH_LONG).show();
                }
            });
        });
    }

    private boolean validateInputs() {

        boolean isValid = true;

        String email = etEmail.getText().toString().trim();
        String firstName = etFirstName.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String model = etModel.getText().toString().trim();
        String plate = etPlateNumber.getText().toString().trim();

        // Clear previous errors
        tilEmail.setError(null);
        tilFirstName.setError(null);
        tilLastName.setError(null);
        tilAddress.setError(null);
        tilPhone.setError(null);
        tilModel.setError(null);
        tilPlate.setError(null);

        if (email.isEmpty()) {
            tilEmail.setError("Email is required");
            isValid = false;
        } else if (!email.contains("@") || !email.contains(".")) {
            tilEmail.setError("Email must contain @ and .");
            isValid = false;
        }

        if (firstName.isEmpty()) {
            tilFirstName.setError("First name is required");
            isValid = false;
        }

        if (lastName.isEmpty()) {
            tilLastName.setError("Last name is required");
            isValid = false;
        }

        if (address.isEmpty()) {
            tilAddress.setError("Address is required");
            isValid = false;
        }

        if (phone.isEmpty()) {
            tilPhone.setError("Phone number is required");
            isValid = false;
        } else if (!phone.startsWith("+")) {
            tilPhone.setError("Phone number must start with '+'");
            isValid = false;
        }

        if (model.isEmpty()) {
            tilModel.setError("Car model is required");
            isValid = false;
        }

        if (plate.isEmpty()) {
            tilPlate.setError("Plate number is required");
            isValid = false;
        }

        return isValid;
    }
}