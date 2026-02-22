package com.example.mobile_applications_project_2025;

import android.app.DatePickerDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobile_applications_project_2025.DTO.DailyStatPointDTO;
import com.example.mobile_applications_project_2025.DTO.StatsResponseDTO;
import com.example.mobile_applications_project_2025.Model.Driver;
import com.example.mobile_applications_project_2025.Model.Enumerator.Role;
import com.example.mobile_applications_project_2025.Model.RegisteredUser;
import com.example.mobile_applications_project_2025.Network.APIs.RegisteredUserAPI;
import com.example.mobile_applications_project_2025.Network.APIs.StatsAPI;
import com.example.mobile_applications_project_2025.Network.ApiClient;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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

    private View statsContainer;
    private TextView tvStatsFromDate;
    private TextView tvStatsToDate;
    private MaterialButton btnStatsGenerate;

    private LocalDate statsFrom;
    private LocalDate statsTo;

    private BarChart chartRides;
    private BarChart chartKm;
    private BarChart chartMoney;

    private static final DateTimeFormatter UI_FMT = DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.getDefault());
    private static final DateTimeFormatter ISO_FMT = DateTimeFormatter.ISO_LOCAL_DATE;

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

        tvStatusLabel = view.findViewById(R.id.tvStatusLabel);
        tvActivatedLabel = view.findViewById(R.id.tvActivatedLabel);

        // Default placeholder
        ivProfile.setImageResource(R.drawable.ic_launcher_foreground);

        statsContainer = view.findViewById(R.id.statsContainer);
        tvStatsFromDate = view.findViewById(R.id.tvStatsFromDate);
        tvStatsToDate = view.findViewById(R.id.tvStatsToDate);
        btnStatsGenerate = view.findViewById(R.id.btnStatsGenerate);

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
                    boolean targetHasStats = loadedIsPassenger || loadedIsDriver;
                    setViewVisible(statsContainer, targetHasStats);

                    if (targetHasStats) {
                        setupStatsUiForTarget(roleStr);
                        loadTargetStats(roleStr);
                    }

                    renderBasicFromJson(loadedUserJson, loadedIsPassenger, loadedIsAdmin);

                    // Passenger-only
                    passengerFieldsContainer.setVisibility(loadedIsPassenger ? View.VISIBLE : View.GONE);
                    if (loadedIsPassenger) {
                        renderPassengerFromJson(loadedUserJson);
                    }

                    // Driver-only fields
                    driverFieldsContainer.setVisibility(loadedIsDriver ? View.VISIBLE : View.GONE);
                    if (loadedIsDriver) {
                        Driver d = gson.fromJson(loadedUserJson, Driver.class);
                        renderDriverFields(d);
                    }

                    // Block/unblock button: Admin viewer can block Driver or Passenger
                    RegisteredUser me = SessionManager.getUser();
                    boolean isAdminViewer = (me != null && me.role == Role.Admin);

                    boolean targetBlockable = roleStr != null && (roleStr.equalsIgnoreCase("Driver") || roleStr.equalsIgnoreCase("Passenger"));

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

    private void setupStatsUiForTarget(String roleStr) {
        YearMonth ym = YearMonth.now();
        statsFrom = ym.atDay(1);
        statsTo = ym.atEndOfMonth();

        tvStatsFromDate.setText(statsFrom.format(UI_FMT));
        tvStatsToDate.setText(statsTo.format(UI_FMT));

        tvStatsFromDate.setOnClickListener(v -> showDatePicker(statsFrom, picked -> {
            statsFrom = picked;
            tvStatsFromDate.setText(statsFrom.format(UI_FMT));
        }));

        tvStatsToDate.setOnClickListener(v -> showDatePicker(statsTo, picked -> {
            statsTo = picked;
            tvStatsToDate.setText(statsTo.format(UI_FMT));
        }));

        // Titles depending on viewed user's role
        TextView tvRidesTitle = getView().findViewById(R.id.tvRidesTitle);
        TextView tvKmTitle = getView().findViewById(R.id.tvKmTitle);
        TextView tvMoneyTitle = getView().findViewById(R.id.tvMoneyTitle);

        boolean isDriver = roleStr != null && roleStr.equalsIgnoreCase("Driver");
        boolean isPassenger = roleStr != null && roleStr.equalsIgnoreCase("Passenger");

        if (isDriver) {
            tvRidesTitle.setText("Number of rides given per day");
            tvKmTitle.setText("Kilometers driven per day");
            tvMoneyTitle.setText("Money earned per day");
        } else if (isPassenger) {
            tvRidesTitle.setText("Number of rides taken per day");
            tvKmTitle.setText("Kilometers traveled per day");
            tvMoneyTitle.setText("Money spent per day");
        }

        // Replace placeholders in FrameLayouts with BarCharts
        ViewGroup ridesContainer = getView().findViewById(R.id.graphRidesContainer);
        ViewGroup kmContainer = getView().findViewById(R.id.graphKmContainer);
        ViewGroup moneyContainer = getView().findViewById(R.id.graphMoneyContainer);

        chartRides = new BarChart(requireContext());
        chartKm = new BarChart(requireContext());
        chartMoney = new BarChart(requireContext());

        ridesContainer.removeAllViews();
        kmContainer.removeAllViews();
        moneyContainer.removeAllViews();

        ridesContainer.addView(chartRides, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        kmContainer.addView(chartKm, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        moneyContainer.addView(chartMoney, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        styleChart(chartRides, false);
        styleChart(chartKm, true);
        styleChart(chartMoney, true);

        btnStatsGenerate.setOnClickListener(v -> {
            if (!validateStatsRange()) return;
            loadTargetStats(roleStr);
        });

        // Reset sums
        ((TextView) getView().findViewById(R.id.tvRidesSum)).setText("0");
        ((TextView) getView().findViewById(R.id.tvRidesAvg)).setText("0");
        ((TextView) getView().findViewById(R.id.tvKmSum)).setText("0");
        ((TextView) getView().findViewById(R.id.tvKmAvg)).setText("0");
        ((TextView) getView().findViewById(R.id.tvMoneySum)).setText("0");
        ((TextView) getView().findViewById(R.id.tvMoneyAvg)).setText("0");
    }

    private void loadTargetStats(String roleStr) {
        if (roleStr == null) return;

        StatsAPI api = ApiClient.getRetrofit().create(StatsAPI.class);

        String fromIso = statsFrom.format(ISO_FMT);
        String toIso = statsTo.format(ISO_FMT);

        Call<StatsResponseDTO> call;

        if (roleStr.equalsIgnoreCase("Driver")) {
            call = api.driverStats(userId, fromIso, toIso);
        } else if (roleStr.equalsIgnoreCase("Passenger")) {
            call = api.passengerStats(userId, fromIso, toIso);
        } else {
            return;
        }

        call.enqueue(new Callback<StatsResponseDTO>() {
            @Override
            public void onResponse(@NonNull Call<StatsResponseDTO> call, @NonNull Response<StatsResponseDTO> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(requireContext(), "Failed to load statistics (" + response.code() + ")", Toast.LENGTH_SHORT).show();
                    return;
                }
                renderStatsPoints(response.body().getPoints());
            }

            @Override
            public void onFailure(@NonNull Call<StatsResponseDTO> call, @NonNull Throwable t) {
                Toast.makeText(requireContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void renderStatsPoints(List<DailyStatPointDTO> points) {
        if (points == null) points = new ArrayList<>();

        final List<String> xLabels = new ArrayList<>();
        List<BarEntry> ridesEntries = new ArrayList<>();
        List<BarEntry> kmEntries = new ArrayList<>();
        List<BarEntry> moneyEntries = new ArrayList<>();

        double ridesTotal = 0;
        double kmTotal = 0;
        double moneyTotal = 0;

        for (int i = 0; i < points.size(); i++) {
            DailyStatPointDTO p = points.get(i);

            LocalDate d = LocalDate.parse(p.getDate(), ISO_FMT);
            xLabels.add(String.format(Locale.getDefault(), "%02d", d.getDayOfMonth()));

            float rides = p.getRides();
            float km = (float) p.getKm();
            float money = (float) p.getMoney();

            ridesEntries.add(new BarEntry(i, rides));
            kmEntries.add(new BarEntry(i, km));
            moneyEntries.add(new BarEntry(i, money));

            ridesTotal += rides;
            kmTotal += km;
            moneyTotal += money;
        }

        int days = Math.max(points.size(), 1);

        ((TextView) getView().findViewById(R.id.tvRidesSum)).setText(formatNumber(ridesTotal));
        ((TextView) getView().findViewById(R.id.tvRidesAvg)).setText(formatNumber(ridesTotal / days));

        ((TextView) getView().findViewById(R.id.tvKmSum)).setText(formatNumber(kmTotal));
        ((TextView) getView().findViewById(R.id.tvKmAvg)).setText(formatNumber(kmTotal / days));

        ((TextView) getView().findViewById(R.id.tvMoneySum)).setText(formatNumber(moneyTotal));
        ((TextView) getView().findViewById(R.id.tvMoneyAvg)).setText(formatNumber(moneyTotal / days));

        setBarData(chartRides, ridesEntries, xLabels, false);
        setBarData(chartKm, kmEntries, xLabels, true);
        setBarData(chartMoney, moneyEntries, xLabels, true);
    }

    private void setBarData(BarChart chart, List<BarEntry> entries, List<String> xLabels, boolean allowDecimals) {
        BarDataSet set = new BarDataSet(entries, "");
        set.setDrawValues(false);

        int barColor = ContextCompat.getColor(requireContext(), R.color.orange_action);
        set.setColor(barColor);

        BarData data = new BarData(set);
        data.setBarWidth(0.9f);

        chart.setData(data);

        XAxis x = chart.getXAxis();
        x.setValueFormatter(new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                int idx = Math.round(value);
                if (idx < 0 || idx >= xLabels.size()) return "";
                return xLabels.get(idx);
            }
        });

        YAxis y = chart.getAxisLeft();
        y.setValueFormatter(new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                if (!allowDecimals) return String.valueOf((int) value);
                if (Math.abs(value - Math.round(value)) < 0.0001) return String.valueOf((int) Math.round(value));
                return String.format(Locale.getDefault(), "%.1f", value);
            }
        });

        chart.getAxisRight().setEnabled(false);
        chart.setFitBars(true);
        chart.invalidate();
    }

    private void styleChart(BarChart chart, boolean allowDecimals) {
        chart.setNoDataText("Press Generate to load.");
        chart.setDrawGridBackground(false);
        chart.setDrawBorders(false);
        chart.setPinchZoom(false);
        chart.setScaleEnabled(false);
        chart.setDoubleTapToZoomEnabled(false);
        chart.getLegend().setEnabled(false);

        Description d = new Description();
        d.setText("");
        chart.setDescription(d);

        XAxis x = chart.getXAxis();
        x.setPosition(XAxis.XAxisPosition.BOTTOM);
        x.setGranularity(1f);
        x.setDrawGridLines(false);
        x.setLabelCount(6, false);

        YAxis left = chart.getAxisLeft();
        left.setAxisMinimum(0f);
        left.setDrawGridLines(true);
    }

    private boolean validateStatsRange() {
        if (statsFrom == null || statsTo == null) return false;
        if (statsTo.isBefore(statsFrom)) {
            Toast.makeText(requireContext(), "'To' must be after 'From'.", Toast.LENGTH_SHORT).show();
            return false;
        }
        long days = ChronoUnit.DAYS.between(statsFrom, statsTo);
        if (days > 31) {
            Toast.makeText(requireContext(), "Date range cannot exceed 31 days.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private String formatNumber(double v) {
        if (Math.abs(v - Math.round(v)) < 0.0001) return String.valueOf((long) Math.round(v));
        return String.format(Locale.getDefault(), "%.2f", v);
    }

    private interface OnPickedDate { void onPicked(LocalDate d); }

    private void showDatePicker(LocalDate initial, OnPickedDate cb) {
        new DatePickerDialog(
                requireContext(),
                (dp, y, m, d) -> cb.onPicked(LocalDate.of(y, m + 1, d)),
                initial.getYear(),
                initial.getMonthValue() - 1,
                initial.getDayOfMonth()
        ).show();
    }
}