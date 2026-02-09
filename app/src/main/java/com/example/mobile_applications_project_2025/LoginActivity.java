package com.example.mobile_applications_project_2025;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.mobile_applications_project_2025.DTO.LoginRequestDTO;
import com.example.mobile_applications_project_2025.Model.Admin;
import com.example.mobile_applications_project_2025.Model.Driver;
import com.example.mobile_applications_project_2025.Model.Enumerator.Role;
import com.example.mobile_applications_project_2025.Model.Passenger;
import com.example.mobile_applications_project_2025.Model.RegisteredUser;
import com.example.mobile_applications_project_2025.Network.API;
import com.example.mobile_applications_project_2025.Network.APIs.RegisteredUserAPI;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextInputEditText etEmail = findViewById(R.id.etEmail);
        TextInputEditText etPassword = findViewById(R.id.etPassword);
        MaterialButton btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(view -> {
            String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
            String password = etPassword.getText() != null ? etPassword.getText().toString() : "";

            boolean ok = true;

            if (email.isEmpty()) {
                etEmail.setError("Enter a valid email");
                ok = false;
            } else {
                etEmail.setError(null);
            }

            if (password.isEmpty()) {
                etPassword.setError("Password must be at least 6 characters");
                ok = false;
            } else {
                etPassword.setError(null);
            }

            if (!ok) return;

            RegisteredUserAPI api = API.of(RegisteredUserAPI.class);
            Context context = this;

            api.login(email, password).enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.isSuccessful()) {
                        JsonObject json = response.body();
                        Gson gson = new Gson();

                        String role = json.get("role").getAsString();
                        RegisteredUser user;

                        switch (role) {
                            case "Driver":
                                user = gson.fromJson(json, Driver.class);
                                break;
                            case "Passenger":
                                user = gson.fromJson(json, Passenger.class);
                                break;
                            case "Admin":
                                user = gson.fromJson(json, Admin.class);
                                break;
                            default:
                                Toast.makeText(context, "Login failed", Toast.LENGTH_SHORT).show();
                                return;
                        }

                        Log.i("i", response.body().toString());

                        SessionManager.setUser(user);
                        SessionManager.setRole(Role.valueOf(role));

                        Toast.makeText(context, "Login success", Toast.LENGTH_SHORT).show();

                        Intent i = new Intent(context, MainActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                        finish();
                    }
                    else {
                        Toast.makeText(context, "Login failed", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    Toast.makeText(context, "Login failed", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}