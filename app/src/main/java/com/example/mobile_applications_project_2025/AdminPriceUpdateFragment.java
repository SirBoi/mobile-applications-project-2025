package com.example.mobile_applications_project_2025;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class AdminPriceUpdateFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_price_update, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        TextInputEditText etStandard = view.findViewById(R.id.etStandard);
        TextInputEditText etLux = view.findViewById(R.id.etLux);
        TextInputEditText etVan = view.findViewById(R.id.etVan);

        MaterialButton btnStandard = view.findViewById(R.id.btnUpdateStandard);
        MaterialButton btnLux = view.findViewById(R.id.btnUpdateLux);
        MaterialButton btnVan = view.findViewById(R.id.btnUpdateVan);

        btnStandard.setOnClickListener(v -> toast("Standard updated to: " + safe(etStandard)));
        btnLux.setOnClickListener(v -> toast("Luxury updated to: " + safe(etLux)));
        btnVan.setOnClickListener(v -> toast("Van updated to: " + safe(etVan)));
    }

    private String safe(TextInputEditText et) {
        return et.getText() == null ? "" : et.getText().toString().trim();
    }

    private void toast(String msg) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
    }
}
