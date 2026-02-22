package com.example.mobile_applications_project_2025;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobile_applications_project_2025.Model.Driver;
import com.example.mobile_applications_project_2025.Model.Enumerator.Role;
import com.example.mobile_applications_project_2025.Model.RegisteredUser;
import com.example.mobile_applications_project_2025.Network.APIs.RegisteredUserAPI;
import com.example.mobile_applications_project_2025.Network.ApiClient;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Path;

public class UserAccountFragment extends Fragment {

    // ---- UI ----
    private ImageView ivProfile;
    private TextView tvFullName;
    private TextView tvEmail;
    private TextView tvRole;

    private MaterialButton btnBlockActions;

    private LinearLayout driverFieldsContainer;
    private TextView tvBlockedStatus;
    private TextView tvBlockReason;

    private TextView tvDailyActiveMinutes;
    private TextView tvCarModel;
    private TextView tvCarType;
    private TextView tvPlateNumber;
    private TextView tvCarSeats;
    private TextView tvBabyFriendly;
    private TextView tvPetFriendly;

    private TextView tvAddress;
    private TextView tvPhone;
    private TextView tvStatus;
    private TextView tvActivated;
    private TextView tvDailyMinutesCommon;

    private LinearLayout passengerFieldsContainer;
    private TextView tvFavouriteRoutesCount;

    private TextView tvStatusLabel;
    private TextView tvActivatedLabel;

    private View cardRides, cardKm, cardMoney;

    // ---- State ----
    private long userId = -1L;

    private final Gson gson = new Gson();

    private JsonObject loadedUserJson;
    private RegisteredUser loadedUser;
    private boolean loadedIsDriver = false;

    public UserAccountFragment() {}

    // Local “raw JSON” endpoint interface
    private interface RawUserAPI {
        @GET("api/users/{id}")
        Call<ResponseBody> getUserRaw(@Path("id") long id);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Bind
        ivProfile = view.findViewById(R.id.ivProfile);
        tvFullName = view.findViewById(R.id.tvFullName);
        tvEmail = view.findViewById(R.id.tvEmail);
        tvRole = view.findViewById(R.id.tvRole);

        btnBlockActions = view.findViewById(R.id.btnBlockActions);

        driverFieldsContainer = view.findViewById(R.id.driverFieldsContainer);
        tvBlockedStatus = view.findViewById(R.id.tvBlockedStatus);
        tvBlockReason = view.findViewById(R.id.tvBlockReason);

        tvDailyActiveMinutes = view.findViewById(R.id.tvDailyActiveMinutes);
        tvCarModel = view.findViewById(R.id.tvCarModel);
        tvCarType = view.findViewById(R.id.tvCarType);
        tvPlateNumber = view.findViewById(R.id.tvPlateNumber);
        tvCarSeats = view.findViewById(R.id.tvCarSeats);
        tvBabyFriendly = view.findViewById(R.id.tvBabyFriendly);
        tvPetFriendly = view.findViewById(R.id.tvPetFriendly);

        tvAddress = view.findViewById(R.id.tvAddress);
        tvPhone = view.findViewById(R.id.tvPhone);
        tvStatus = view.findViewById(R.id.tvStatus);
        tvActivated = view.findViewById(R.id.tvActivated);

        passengerFieldsContainer = view.findViewById(R.id.passengerFieldsContainer);
        tvFavouriteRoutesCount = view.findViewById(R.id.tvFavouriteRoutesCount);

        cardRides = view.findViewById(R.id.cardRides);
        cardKm = view.findViewById(R.id.cardKm);
        cardMoney = view.findViewById(R.id.cardMoney);

        tvStatusLabel = view.findViewById(R.id.tvStatusLabel);
        tvActivatedLabel = view.findViewById(R.id.tvActivatedLabel);

        // Default placeholder
        ivProfile.setImageResource(R.drawable.ic_launcher_foreground);

        // Read nav arg: userId
        if (getArguments() != null) {
            userId = getArguments().getLong("userId", -1L);
        }
        if (userId <= 0) {
            Toast.makeText(requireContext(), "Missing userId.", Toast.LENGTH_SHORT).show();
            return;
        }

        loadUserAndRender(userId);
    }

    private void loadUserAndRender(long id) {
        RawUserAPI rawApi = ApiClient.getRetrofit().create(RawUserAPI.class);
        rawApi.getUserRaw(id).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(requireContext(), "Failed to load user (" + response.code() + ")", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    String json = response.body().string();
                    loadedUserJson = gson.fromJson(json, JsonObject.class);
                    loadedUser = gson.fromJson(loadedUserJson, RegisteredUser.class);

                    String roleStr = getJsonStringAny(loadedUserJson, "role");
                    boolean loadedIsPassenger = roleStr != null && roleStr.equalsIgnoreCase("Passenger");
                    boolean loadedIsAdmin = roleStr != null && roleStr.equalsIgnoreCase("Admin");
                    loadedIsDriver = (roleStr != null && roleStr.equalsIgnoreCase("Driver"));

                    renderBasicFromJson(loadedUserJson, loadedIsPassenger, loadedIsAdmin);

                    // Passenger-only
                    passengerFieldsContainer.setVisibility(loadedIsPassenger ? View.VISIBLE : View.GONE);
                    if (loadedIsPassenger) {
                        renderPassengerFromJson(loadedUserJson);
                    }

                    // Driver-only graphs/cards
                    setViewVisible(cardRides, loadedIsDriver);
                    setViewVisible(cardKm, loadedIsDriver);
                    setViewVisible(cardMoney, loadedIsDriver);

                    // Driver-only fields
                    driverFieldsContainer.setVisibility(loadedIsDriver ? View.VISIBLE : View.GONE);
                    if (loadedIsDriver) {
                        Driver d = gson.fromJson(loadedUserJson, Driver.class);
                        renderDriverFields(d);
                    }

                    // Block/unblock button: Admin viewer can block Driver or Passenger
                    RegisteredUser me = SessionManager.getUser();
                    boolean isAdminViewer = (me != null && me.role == Role.Admin);

                    boolean targetBlockable = roleStr != null &&
                            (roleStr.equalsIgnoreCase("Driver") || roleStr.equalsIgnoreCase("Passenger"));

                    btnBlockActions.setVisibility(isAdminViewer && targetBlockable ? View.VISIBLE : View.GONE);
                    if (isAdminViewer && targetBlockable) {
                        btnBlockActions.setOnClickListener(v -> openBlockDialog());
                    } else {
                        btnBlockActions.setOnClickListener(null);
                    }

                    loadProfilePicture(id);

                } catch (IOException e) {
                    Toast.makeText(requireContext(), "Failed to parse user.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(requireContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Common fields for everyone, with requested exclusions:
     * - Admin: hide status, activated, daily active minutes
     * - Passenger: hide daily active minutes
     */
    private void renderBasicFromJson(JsonObject j, boolean isPassenger, boolean isAdmin) {
        if (j == null) return;

        String first = getJsonString(j, "firstName");
        String last  = getJsonString(j, "lastName");
        String mail  = getJsonString(j, "mail");
        String role  = getJsonString(j, "role");

        String full = (first + " " + last).trim();
        tvFullName.setText(full.isEmpty() ? "User" : full);

        tvEmail.setText(mail != null ? mail : "");
        tvRole.setText(role != null ? role : "—");

        boolean blocked = getJsonBoolean(j, "isBlocked", false);
        tvBlockedStatus.setText(blocked ? "Blocked" : "Active");

        String reason = getJsonString(j, "blockMessage");
        if (reason == null) reason = "";
        reason = reason.trim();
        tvBlockReason.setText(reason.isEmpty() ? "—" : reason);

        String address = getJsonStringAny(j, "address");
        tvAddress.setText(address != null && !address.trim().isEmpty() ? address : "—");

        String phone = getJsonStringAny(j, "phoneNumber", "phone");
        tvPhone.setText(phone != null && !phone.trim().isEmpty() ? phone : "—");

        boolean showStatusActivated = !isAdmin;
        setViewVisible(tvStatusLabel, showStatusActivated);
        setViewVisible(tvStatus, showStatusActivated);
        setViewVisible(tvActivatedLabel, showStatusActivated);
        setViewVisible(tvActivated, showStatusActivated);

        setViewVisible(tvStatus, !isAdmin);
        setViewVisible(tvActivated, !isAdmin);

        if (!isAdmin) {
            String status = getJsonStringAny(j, "status");
            tvStatus.setText(status != null ? status : "—");

            boolean activated = getJsonBooleanAny(j, false, "isActivated", "activated");
            tvActivated.setText(activated ? "Yes" : "No");
        }
    }

    private String getJsonString(JsonObject j, String key) {
        try {
            if (j.has(key) && !j.get(key).isJsonNull()) return j.get(key).getAsString();
        } catch (Exception ignored) {}
        return null;
    }

    private boolean getJsonBoolean(JsonObject j, String key, boolean def) {
        try {
            if (j.has(key) && !j.get(key).isJsonNull()) return j.get(key).getAsBoolean();
        } catch (Exception ignored) {}
        return def;
    }

    private void renderDriverFields(Driver d) {
        if (d == null) return;

        tvDailyActiveMinutes.setText(d.dailyActiveMinutes != null ? String.valueOf(d.dailyActiveMinutes) : "—");
        tvCarModel.setText(!TextUtils.isEmpty(d.model) ? d.model : "—");
        tvCarType.setText(d.type != null ? String.valueOf(d.type) : "—");
        tvPlateNumber.setText(!TextUtils.isEmpty(d.plateNumber) ? d.plateNumber : "—");
        tvCarSeats.setText(d.numberOfSeats != null ? String.valueOf(d.numberOfSeats) : "—");
        tvBabyFriendly.setText(Boolean.TRUE.equals(d.isBabyFriendly) ? "Yes" : "No");
        tvPetFriendly.setText(Boolean.TRUE.equals(d.isAnimalFriendly) ? "Yes" : "No");
    }

    private void loadProfilePicture(long id) {
        RegisteredUserAPI api = ApiClient.getRetrofit().create(RegisteredUserAPI.class);
        api.getProfilePicture(id).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (!response.isSuccessful() || response.body() == null) return;

                try {
                    byte[] bytes = response.body().bytes();
                    Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    if (bmp != null) ivProfile.setImageBitmap(bmp);
                } catch (IOException ignored) {}
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // ignore
            }
        });
    }

    // -------- Block / Unblock dialog --------

    private void openBlockDialog() {
        if (loadedUserJson == null) return;

        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_block_user, null, false);

        TextInputEditText etReason = dialogView.findViewById(R.id.etReason);
        MaterialButton btnBlock = dialogView.findViewById(R.id.btnBlock);
        MaterialButton btnUnblock = dialogView.findViewById(R.id.btnUnblock);

        boolean isBlocked = getJsonBooleanAny(loadedUserJson, false, "isBlocked");

        String existingReason = getJsonStringAny(loadedUserJson, "blockMessage");
        if (existingReason == null) existingReason = "";
        etReason.setText(existingReason);

        setEnabledStyled(btnBlock, !isBlocked);
        setEnabledStyled(btnUnblock, isBlocked);

        androidx.appcompat.app.AlertDialog dlg = new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Block user")
                .setView(dialogView)
                .setNegativeButton("Close", (d, which) -> d.dismiss())
                .create();

        btnBlock.setOnClickListener(v -> {
            String reason = etReason.getText() != null ? etReason.getText().toString().trim() : "";
            if (TextUtils.isEmpty(reason)) {
                Toast.makeText(requireContext(), "Reason cannot be empty.", Toast.LENGTH_SHORT).show();
                return;
            }
            applyBlockState(true, reason, dlg);
        });

        btnUnblock.setOnClickListener(v -> applyBlockState(false, "", dlg));

        dlg.show();
    }

    private void applyBlockState(boolean block, String reason, androidx.appcompat.app.AlertDialog dlgToClose) {
        RegisteredUserAPI api = ApiClient.getRetrofit().create(RegisteredUserAPI.class);

        Call<RegisteredUser> call;
        if (block) {
            Map<String, String> body = new HashMap<>();
            body.put("reason", reason);
            call = api.blockUser(userId, body);
        } else {
            call = api.unblockUser(userId);
        }

        call.enqueue(new Callback<RegisteredUser>() {
            @Override
            public void onResponse(Call<RegisteredUser> call, Response<RegisteredUser> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(requireContext(), "Failed (" + response.code() + ")", Toast.LENGTH_SHORT).show();
                    return;
                }

                String roleStr = (loadedUserJson != null) ? getJsonStringAny(loadedUserJson, "role") : null;
                String who = (roleStr != null && !roleStr.trim().isEmpty()) ? roleStr : "User";

                Toast.makeText(requireContext(),
                        block ? (who + " blocked.") : (who + " unblocked."),
                        Toast.LENGTH_SHORT).show();

                if (dlgToClose != null) dlgToClose.dismiss();

                loadUserAndRender(userId);
            }

            @Override
            public void onFailure(Call<RegisteredUser> call, Throwable t) {
                Toast.makeText(requireContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setEnabledStyled(MaterialButton btn, boolean enabled) {
        btn.setEnabled(enabled);
        btn.setAlpha(enabled ? 1f : 0.4f);
    }

    private void setViewVisible(View v, boolean visible) {
        if (v == null) return;
        v.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    private void renderPassengerFromJson(JsonObject j) {
        if (j == null) return;

        int count = 0;
        try {
            if (j.has("favouriteRoutes") && j.get("favouriteRoutes").isJsonArray()) {
                count = j.getAsJsonArray("favouriteRoutes").size();
            }
        } catch (Exception ignored) {}

        tvFavouriteRoutesCount.setText(String.valueOf(count));
    }

    private String getJsonStringAny(JsonObject j, String... keys) {
        if (j == null || keys == null) return null;

        for (String key : keys) {
            try {
                if (j.has(key) && !j.get(key).isJsonNull()) {
                    return j.get(key).getAsString();
                }
            } catch (Exception ignored) {}
        }

        return null;
    }

    private boolean getJsonBooleanAny(JsonObject j, boolean def, String... keys) {
        if (j == null || keys == null) return def;

        for (String key : keys) {
            try {
                if (j.has(key) && !j.get(key).isJsonNull()) {
                    return j.get(key).getAsBoolean();
                }
            } catch (Exception ignored) {}
        }

        return def;
    }
}