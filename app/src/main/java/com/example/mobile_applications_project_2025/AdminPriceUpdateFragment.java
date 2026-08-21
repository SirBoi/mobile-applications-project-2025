package com.example.mobile_applications_project_2025;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mobile_applications_project_2025.DTO.ConfigDTO;
import com.example.mobile_applications_project_2025.Network.APIs.ConfigAPI;
import com.example.mobile_applications_project_2025.Network.ApiClient;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// 2.14 - Admin definise/menja cenu voznje po tipu vozila.
public class AdminPriceUpdateFragment extends Fragment {

    private TextInputEditText etStandard, etLux, etVan;
    private ConfigAPI configAPI;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_price_update, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        etStandard = view.findViewById(R.id.etStandard);
        etLux = view.findViewById(R.id.etLux);
        etVan = view.findViewById(R.id.etVan);

        MaterialButton btnStandard = view.findViewById(R.id.btnUpdateStandard);
        MaterialButton btnLux = view.findViewById(R.id.btnUpdateLux);
        MaterialButton btnVan = view.findViewById(R.id.btnUpdateVan);

        configAPI = ApiClient.getRetrofit().create(ConfigAPI.class);
        loadCurrentPrices();

        btnStandard.setOnClickListener(v -> updatePrice(parse(etStandard), null, null, "Standard"));
        btnLux.setOnClickListener(v -> updatePrice(null, parse(etLux), null, "Luxury"));
        btnVan.setOnClickListener(v -> updatePrice(null, null, parse(etVan), "Van"));
    }

    private void loadCurrentPrices() {
        configAPI.get().enqueue(new Callback<ConfigDTO>() {
            @Override
            public void onResponse(@NonNull Call<ConfigDTO> call, @NonNull Response<ConfigDTO> response) {
                if (!isAdded() || !response.isSuccessful() || response.body() == null) return;
                ConfigDTO c = response.body();
                if (c.standardPrice != null) etStandard.setText(String.valueOf(c.standardPrice));
                if (c.luxuryPrice != null) etLux.setText(String.valueOf(c.luxuryPrice));
                if (c.vanPrice != null) etVan.setText(String.valueOf(c.vanPrice));
            }

            @Override
            public void onFailure(@NonNull Call<ConfigDTO> call, @NonNull Throwable t) { }
        });
    }

    // Salje samo izmenjeno polje - server cuva ostala dva kako jesu (upsert preko celog objekta,
    // pa prvo ucitamo trenutne vrednosti da ih ne bismo obrisali).
    private void updatePrice(Float standard, Float lux, Float van, String label) {
        Float finalStandard = standard != null ? standard : parse(etStandard);
        Float finalLux = lux != null ? lux : parse(etLux);
        Float finalVan = van != null ? van : parse(etVan);

        configAPI.update(new ConfigDTO(finalStandard, finalLux, finalVan)).enqueue(new Callback<ConfigDTO>() {
            @Override
            public void onResponse(@NonNull Call<ConfigDTO> call, @NonNull Response<ConfigDTO> response) {
                if (!isAdded()) return;
                if (response.isSuccessful()) {
                    Toast.makeText(requireContext(), label + " price updated.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), "Failed to update price (" + response.code() + ").", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ConfigDTO> call, @NonNull Throwable t) {
                if (!isAdded()) return;
                Toast.makeText(requireContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Float parse(TextInputEditText et) {
        String s = et.getText() == null ? "" : et.getText().toString().trim();
        if (s.isEmpty()) return null;
        try { return Float.parseFloat(s); } catch (NumberFormatException e) { return null; }
    }
}