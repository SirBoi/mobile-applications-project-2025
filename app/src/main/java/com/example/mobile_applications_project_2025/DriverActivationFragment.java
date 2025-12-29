package com.example.mobile_applications_project_2025;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.mobile_applications_project_2025.Model.User;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

public class DriverActivationFragment extends Fragment {
    public DriverActivationFragment() {
        // Required empty public constructor
    }

    public static DriverActivationFragment newInstance(String param1, String param2) {
        DriverActivationFragment fragment = new DriverActivationFragment();
        Bundle bundle = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_driver_activation, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        TextInputEditText etPassword = view.findViewById(R.id.etPassword);
        TextInputEditText etConfirm = view.findViewById(R.id.etConfirmPassword);
        MaterialButton btnActivate = view.findViewById(R.id.btnActivateAccount);

        btnActivate.setOnClickListener(v -> {
            String p1 = etPassword.getText() != null ? etPassword.getText().toString() : "";
            String p2 = etConfirm.getText() != null ? etConfirm.getText().toString() : "";

            /*
            if (TextUtils.isEmpty(p1) || p1.length() < 6) {
                Toast.makeText(requireContext(), "Invalid password", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!TextUtils.equals(p1, p2)) {
                Toast.makeText(requireContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }
             */

            User u = SessionManager.getUser(requireContext());
            if (u == null) u = new User();
            u.password = p1;
            SessionManager.setUser(requireContext(), u);
            SessionManager.setLoggedIn(requireContext(), true);

            new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Account activated")
                .setMessage("Your account has been activated successfully.")
                .setPositiveButton("OK", (d, w) -> {
                    d.dismiss();
                    NavHostFragment.findNavController(this).navigate(R.id.homeFragment);
                })
                .show();
        });
    }
}