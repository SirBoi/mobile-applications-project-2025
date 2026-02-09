package com.example.mobile_applications_project_2025;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mobile_applications_project_2025.Model.Admin;
import com.example.mobile_applications_project_2025.Model.Driver;
import com.example.mobile_applications_project_2025.Model.Passenger;
import com.example.mobile_applications_project_2025.Model.RegisteredUser;
import com.example.mobile_applications_project_2025.Network.APIs.RegisteredUserAPI;
import com.example.mobile_applications_project_2025.Network.ApiClient;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordFragment extends Fragment {
    public ChangePasswordFragment() {
        // Required empty public constructor
    }

    public static ChangePasswordFragment newInstance(String param1, String param2) {
        ChangePasswordFragment fragment = new ChangePasswordFragment();
        Bundle bundle = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_change_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        TextInputEditText etNewPassword = view.findViewById(R.id.etPassword);
        TextInputEditText etConfirmPassword = view.findViewById(R.id.etConfirmPassword);
        MaterialButton btnSavePassword = view.findViewById(R.id.btnSavePassword);

        btnSavePassword.setOnClickListener(v -> {

            RegisteredUser user = SessionManager.getUser();
            if (user == null) return;

            String newPass = etNewPassword.getText().toString();
            String confirmPass = etConfirmPassword.getText().toString();

            // ---- VALIDATION ----
            if (newPass.isEmpty() || confirmPass.isEmpty()) {
                if (newPass.isEmpty()) etNewPassword.setError("Required");
                if (confirmPass.isEmpty()) etConfirmPassword.setError("Required");
                return;
            }

            if (!newPass.equals(confirmPass)) {
                etConfirmPassword.setError("Passwords do not match");
                return;
            }

            if (newPass.equals(user.password)) {
                etNewPassword.setError("New password must be different");
                return;
            }

            // ---- UPDATE PASSWORD ----
            user.password = newPass;

            RegisteredUserAPI api =
                    ApiClient.getRetrofit().create(RegisteredUserAPI.class);

            api.updateUser(user.id, user).enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call,
                                       Response<JsonObject> response) {

                    if (!response.isSuccessful() || response.body() == null) return;

                    JsonObject json = response.body();
                    Gson gson = new Gson();

                    String role = json.get("role").getAsString();
                    RegisteredUser updated;

                    switch (role) {
                        case "Driver":
                            updated = gson.fromJson(json, Driver.class);
                            break;
                        case "Passenger":
                            updated = gson.fromJson(json, Passenger.class);
                            break;
                        case "Admin":
                            updated = gson.fromJson(json, Admin.class);
                            break;
                        default:
                            return;
                    }

                    SessionManager.setUser(updated);

                    NavHostFragment.findNavController(ChangePasswordFragment.this)
                            .popBackStack();
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) { }
            });
        });
    }
}