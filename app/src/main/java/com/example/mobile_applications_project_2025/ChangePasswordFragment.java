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
import com.google.android.material.textfield.TextInputEditText;

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
        TextInputEditText etPassword = view.findViewById(R.id.etPassword);
        TextInputEditText etConfirmPassword = view.findViewById(R.id.etConfirmPassword);
        MaterialButton btnSave = view.findViewById(R.id.btnSavePassword);

        btnSave.setOnClickListener(v -> {
            String p1 = etPassword.getText() != null ? etPassword.getText().toString() : "";
            String p2 = etConfirmPassword.getText() != null ? etConfirmPassword.getText().toString() : "";

            /*
            if (!isPasswordValid(p1)) {
                Toast.makeText(requireContext(), "Invalid password", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!TextUtils.equals(p1, p2)) {
                Toast.makeText(requireContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }
             */

            User u = SessionManager.getUser(requireContext());
            if (u != null) {
                u.password = p1;
                SessionManager.setUser(requireContext(), u);
            }

            NavHostFragment.findNavController(this).popBackStack();
        });
    }

    private boolean isPasswordValid(String p) {
        if (p == null) return false;
        String s = p.trim();
        if (s.length() < 8) return false;
        boolean hasUpper = false;
        boolean hasLower = false;
        boolean hasDigit = false;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (Character.isUpperCase(c)) hasUpper = true;
            else if (Character.isLowerCase(c)) hasLower = true;
            else if (Character.isDigit(c)) hasDigit = true;
        }
        return hasUpper && hasLower && hasDigit;
    }
}