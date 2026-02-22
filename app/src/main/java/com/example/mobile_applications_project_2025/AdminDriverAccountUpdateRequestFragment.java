package com.example.mobile_applications_project_2025;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mobile_applications_project_2025.Model.DriverAccountUpdateRequest;
import com.example.mobile_applications_project_2025.Model.Enumerator.Role;
import com.example.mobile_applications_project_2025.Network.APIs.RegisteredUserAPI;
import com.example.mobile_applications_project_2025.Network.ApiClient;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminDriverAccountUpdateRequestFragment extends Fragment {

    private LinearLayout requestsContainer;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_driver_account_update_requests, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        requestsContainer = view.findViewById(R.id.requestsContainer);

        if (SessionManager.getUser() == null || SessionManager.getUser().role != Role.Admin) {
            requireActivity().onBackPressed();
            return;
        }

        loadRequests();
    }

    private void loadRequests() {
        requestsContainer.removeAllViews();

        RegisteredUserAPI api = ApiClient.getRetrofit().create(RegisteredUserAPI.class);
        api.getAllDriverAccountUpdateRequests().enqueue(new Callback<List<DriverAccountUpdateRequest>>() {
            @Override
            public void onResponse(Call<List<DriverAccountUpdateRequest>> call, Response<List<DriverAccountUpdateRequest>> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(requireContext(), "Failed to load requests", Toast.LENGTH_SHORT).show();
                    return;
                }

                List<DriverAccountUpdateRequest> list = response.body();
                if (list.isEmpty()) {
                    addEmptyState("No pending requests.");
                    return;
                }

                for (DriverAccountUpdateRequest r : list) {
                    addRequestCard(r);
                }
            }

            @Override
            public void onFailure(Call<List<DriverAccountUpdateRequest>> call, Throwable t) {
                Toast.makeText(requireContext(), "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addEmptyState(String text) {
        TextView tv = new TextView(requireContext());
        tv.setText(text);
        tv.setPadding(10, 10, 10, 10);
        requestsContainer.addView(tv);
    }

    private void addRequestCard(DriverAccountUpdateRequest r) {

        View row = LayoutInflater.from(requireContext())
                .inflate(R.layout.item_driver_update_request, requestsContainer, false);

        MaterialCardView card = row.findViewById(R.id.cardRequest);
        TextView tvEmail = row.findViewById(R.id.tvEmail);
        TextView tvName = row.findViewById(R.id.tvName);

        String mail = (r.driver != null && r.driver.mail != null) ? r.driver.mail : "";
        String fn = (r.driver != null && r.driver.firstName != null) ? r.driver.firstName : "";
        String ln = (r.driver != null && r.driver.lastName != null) ? r.driver.lastName : "";

        tvEmail.setText(mail);
        tvName.setText((fn + " " + ln).trim());

        card.setOnClickListener(v -> showDetailsDialog(r));

        requestsContainer.addView(row);
    }

    private void showDetailsDialog(DriverAccountUpdateRequest r) {
        Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_driver_account_update_request);
        dialog.setCanceledOnTouchOutside(true);

        LinearLayout details = dialog.findViewById(R.id.detailsContainer);
        MaterialButton btnApprove = dialog.findViewById(R.id.btnApprove);
        MaterialButton btnReject = dialog.findViewById(R.id.btnReject);

        details.removeAllViews();

        if (r.driver.isBlocked) {
            addRow(details, "Account status", safe("Blocked"));
            addRow(details, "Block reason", safe(r.driver.blockMessage));
        }
        addRow(details, "Email", safe(r.driver != null ? r.driver.mail : null));
        addRow(details, "First name", safe(r.firstName));
        addRow(details, "Last name", safe(r.lastName));
        addRow(details, "Address", safe(r.address));
        addRow(details, "Phone", safe(r.phoneNumber));
        addRow(details, "Car model", safe(r.model));
        addRow(details, "Car type", r.type == null ? "" : r.type.name());
        addRow(details, "Plate number", safe(r.plateNumber));
        addRow(details, "Seats", r.numberOfSeats == null ? "" : String.valueOf(r.numberOfSeats));
        addRow(details, "Baby friendly", r.isBabyFriendly == null ? "" : String.valueOf(r.isBabyFriendly));
        addRow(details, "Animal friendly", r.isAnimalFriendly == null ? "" : String.valueOf(r.isAnimalFriendly));

        long driverId = (r.id != null) ? r.id : (r.driver != null && r.driver.id != null ? r.driver.id : -1L);
        if (driverId <= 0) {
            Toast.makeText(requireContext(), "Invalid request", Toast.LENGTH_SHORT).show();
            return;
        }

        RegisteredUserAPI api = ApiClient.getRetrofit().create(RegisteredUserAPI.class);

        btnApprove.setOnClickListener(v -> {
            api.approveDriverAccountUpdateRequest(driverId).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (!response.isSuccessful()) {
                        Toast.makeText(requireContext(), "Approve failed", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    dialog.dismiss();
                    loadRequests();
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(requireContext(), "Network error", Toast.LENGTH_SHORT).show();
                }
            });
        });

        btnReject.setOnClickListener(v -> {
            api.rejectDriverAccountUpdateRequest(driverId).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (!response.isSuccessful()) {
                        Toast.makeText(requireContext(), "Reject failed", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    dialog.dismiss();
                    loadRequests();
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(requireContext(), "Network error", Toast.LENGTH_SHORT).show();
                }
            });
        });

        dialog.show();
    }

    private void addRow(LinearLayout parent, String label, String value) {
        TextView tv = new TextView(requireContext());
        tv.setText(label + ": " + value);
        tv.setTextSize(14);
        tv.setPadding(0, 8, 0, 0);
        parent.addView(tv);
    }

    private String safe(String s) {
        return s == null ? "" : s;
    }
}
