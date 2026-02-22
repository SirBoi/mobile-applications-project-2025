package com.example.mobile_applications_project_2025;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mobile_applications_project_2025.Network.APIs.RegisteredUserAPI;
import com.example.mobile_applications_project_2025.Network.ApiClient;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivateAccountActivity extends AppCompatActivity {

    private TextInputLayout tilPassword, tilConfirmPassword;
    private TextInputEditText etPassword, etConfirmPassword;
    private MaterialButton btnActivate;
    private CircularProgressIndicator progressIndicator;

    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activate_account);

        // Get references
        tilPassword = findViewById(R.id.tilPassword);
        tilConfirmPassword = findViewById(R.id.tilConfirmPassword);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnActivate = findViewById(R.id.btnActivate);

        // Optional: add a circular progress indicator
        progressIndicator = new CircularProgressIndicator(this);
        progressIndicator.setVisibility(View.GONE);

        // Extract token from deep link
        Uri data = getIntent().getData();
        if (data != null && "activate".equals(data.getHost())) {
            token = data.getQueryParameter("token");
        }

        btnActivate.setOnClickListener(v -> {
            // Clear previous errors
            tilPassword.setError(null);
            tilConfirmPassword.setError(null);

            String password = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";
            String confirmPassword = etConfirmPassword.getText() != null ? etConfirmPassword.getText().toString().trim() : "";

            // Validation
            if (password.isEmpty()) {
                tilPassword.setError("Password cannot be empty");
                return;
            }

            if (confirmPassword.isEmpty()) {
                tilConfirmPassword.setError("Confirm password cannot be empty");
                return;
            }

            if (!password.equals(confirmPassword)) {
                tilConfirmPassword.setError("Passwords do not match");
                return;
            }

            if (token == null || token.isEmpty()) {
                Toast.makeText(this, "Invalid activation link", Toast.LENGTH_LONG).show();
                return;
            }

            // Show progress indicator
            btnActivate.setEnabled(false);
            progressIndicator.setVisibility(View.VISIBLE);

            // Make API call
            RegisteredUserAPI api = ApiClient.getRetrofit().create(RegisteredUserAPI.class);
            api.activateAccount(token, password).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    btnActivate.setEnabled(true);
                    progressIndicator.setVisibility(View.GONE);

                    if (response.isSuccessful()) {
                        try {
                            String message = response.body() != null ? response.body().string() : "Account activated";
                            Toast.makeText(ActivateAccountActivity.this, message, Toast.LENGTH_LONG).show();
                            // go to login
                            startActivity(new Intent(ActivateAccountActivity.this, LoginActivity.class));
                            finish();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(ActivateAccountActivity.this, "Activation succeeded, but error reading message", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(ActivateAccountActivity.this, "Activation failed", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    btnActivate.setEnabled(true);
                    progressIndicator.setVisibility(View.GONE);
                    Toast.makeText(ActivateAccountActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        });
    }
}