package com.example.mobile_applications_project_2025;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.mobile_applications_project_2025.DTO.UpdateDriverDTO;
import com.example.mobile_applications_project_2025.Model.Admin;
import com.example.mobile_applications_project_2025.Model.Enumerator.CarType;
import com.example.mobile_applications_project_2025.Model.Enumerator.Role;
import com.example.mobile_applications_project_2025.Model.Passenger;
import com.example.mobile_applications_project_2025.Model.RegisteredUser;
import com.example.mobile_applications_project_2025.Model.Driver;
import com.example.mobile_applications_project_2025.Network.APIs.RegisteredUserAPI;
import com.example.mobile_applications_project_2025.Network.ApiClient;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateAccountFragment extends Fragment {

    private ActivityResultLauncher<String> imagePickerLauncher;
    private Uri selectedImageUri;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_update_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        imagePickerLauncher =
                registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                    if (uri != null) {
                        selectedImageUri = uri;
                    }
                });

        ImageView ivProfile = view.findViewById(R.id.ivProfile);

        TextInputEditText etFirstName = view.findViewById(R.id.etFirstName);
        TextInputEditText etLastName = view.findViewById(R.id.etLastName);
        TextInputEditText etAddress = view.findViewById(R.id.etAddress);
        TextInputEditText etPhone = view.findViewById(R.id.etPhone);

        LinearLayout driverFieldsContainer = view.findViewById(R.id.driverFieldsContainer);
        TextInputEditText etCarModel = view.findViewById(R.id.etCarModel);
        AutoCompleteTextView spCarType = view.findViewById(R.id.spCarType);
        TextInputEditText etPlateNumber = view.findViewById(R.id.etPlateNumber);
        TextInputEditText etCarSeats = view.findViewById(R.id.etCarSeats);
        MaterialSwitch swBabyFriendly = view.findViewById(R.id.swBabyFriendly);
        MaterialSwitch swPetFriendly = view.findViewById(R.id.swPetFriendly);

        String[] carTypes = new String[CarType.values().length];
        for (int i = 0; i < CarType.values().length; i++) {
            carTypes[i] = CarType.values()[i].name();
        }

        spCarType.setAdapter(new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                carTypes
        ));

        MaterialButton btnChangeImage = view.findViewById(R.id.btnChangeImage);
        btnChangeImage.setOnClickListener(v ->
                imagePickerLauncher.launch("image/*")
        );

        RegisteredUser user = SessionManager.getUser();
        if (user == null) return;

        // ===== PREFILL COMMON =====
        etFirstName.setText(user.firstName);
        etLastName.setText(user.lastName);
        etAddress.setText(user.address);
        etPhone.setText(user.phoneNumber);

        // ===== PREFILL DRIVER =====
        driverFieldsContainer.setVisibility(View.GONE);

        if (user instanceof Driver) {
            Driver d = (Driver) user;
            driverFieldsContainer.setVisibility(View.VISIBLE);

            etCarModel.setText(d.model);
            if (d.type != null) spCarType.setText(d.type.name(), false);
            etPlateNumber.setText(d.plateNumber);
            etCarSeats.setText(d.numberOfSeats != null ? String.valueOf(d.numberOfSeats) : "");
            swBabyFriendly.setChecked(Boolean.TRUE.equals(d.isBabyFriendly));
            swPetFriendly.setChecked(Boolean.TRUE.equals(d.isAnimalFriendly));
        }

        MaterialButton btnSaveChanges = view.findViewById(R.id.btnSaveChanges);

        btnSaveChanges.setOnClickListener(v -> {

            RegisteredUser current = SessionManager.getUser();
            if (current == null) return;

            Object payload;

            // ===== DRIVER =====
            if (current instanceof Driver) {

                String firstName = etFirstName.getText().toString();
                String lastName = etLastName.getText().toString();
                String address = etAddress.getText().toString();
                String phone = etPhone.getText().toString();

                String model = etCarModel.getText().toString();
                String plate = etPlateNumber.getText().toString();

                String seatsStr = etCarSeats.getText().toString();
                Integer seats = seatsStr.isEmpty() ? null : Integer.parseInt(seatsStr);

                String ct = spCarType.getText().toString();
                CarType type = ct.isEmpty() ? null : CarType.valueOf(ct);

                Boolean babyFriendly = swBabyFriendly.isChecked();
                Boolean petFriendly = swPetFriendly.isChecked();

                UpdateDriverDTO dto = new UpdateDriverDTO(
                        firstName, lastName, address, phone,
                        model, type, plate, seats,
                        babyFriendly, petFriendly
                );

                RegisteredUserAPI api = ApiClient.getRetrofit().create(RegisteredUserAPI.class);

                api.createDriverAccountUpdateRequest(current.id, dto)
                        .enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                if (!response.isSuccessful()) return;

                                Toast.makeText(requireContext(),
                                        "Update request sent to admin",
                                        Toast.LENGTH_SHORT).show();

                                NavHostFragment.findNavController(UpdateAccountFragment.this)
                                        .popBackStack();
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) { }
                        });

                return; // IMPORTANT: stop here so it doesn't fall through to updateUser
            // ===== PASSENGER / ADMIN =====
            } else {
                current.firstName = etFirstName.getText().toString();
                current.lastName = etLastName.getText().toString();
                current.address = etAddress.getText().toString();
                current.phoneNumber = etPhone.getText().toString();

                payload = current;
            }

            RegisteredUserAPI api =
                    ApiClient.getRetrofit().create(RegisteredUserAPI.class);

            api.updateUser(current.id, payload)
                    .enqueue(new Callback<JsonObject>() {
                        @Override
                        public void onResponse(Call<JsonObject> call,
                                               Response<JsonObject> response) {

                            if (!response.isSuccessful() || response.body() == null) return;

                            JsonObject json = response.body();
                            Gson gson = new Gson();

                            String role = json.get("role").getAsString();
                            RegisteredUser freshUser;

                            switch (role) {
                                case "Driver":
                                    freshUser = gson.fromJson(json, Driver.class);
                                    break;
                                case "Passenger":
                                    freshUser = gson.fromJson(json, Passenger.class);
                                    break;
                                case "Admin":
                                    freshUser = gson.fromJson(json, Admin.class);
                                    break;
                                default:
                                    return;
                            }

                            SessionManager.setUser(freshUser);

                            NavHostFragment.findNavController(UpdateAccountFragment.this)
                                    .popBackStack();
                        }

                        @Override
                        public void onFailure(Call<JsonObject> call, Throwable t) { }
                    });

        });
    }

}
