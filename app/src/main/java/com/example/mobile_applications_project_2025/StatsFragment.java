package com.example.mobile_applications_project_2025;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.mobile_applications_project_2025.DTO.DailyStatPointDTO;
import com.example.mobile_applications_project_2025.DTO.StatsResponseDTO;
import com.example.mobile_applications_project_2025.Model.Enumerator.Role;
import com.example.mobile_applications_project_2025.Network.ApiClient;
import com.example.mobile_applications_project_2025.Network.APIs.StatsAPI;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StatsFragment extends Fragment {

    private static final DateTimeFormatter UI_FMT = DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.getDefault());
    private static final DateTimeFormatter ISO_FMT = DateTimeFormatter.ISO_LOCAL_DATE;

    private LocalDate fromDate;
    private LocalDate toDate;

    private TextView tvFrom;
    private TextView tvTo;

    private TextView ridesSum, ridesAvg, kmSum, kmAvg, moneySum, moneyAvg;

    private BarChart chartRides;
    private BarChart chartKm;
    private BarChart chartMoney;

    public StatsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_stats, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        tvFrom = view.findViewById(R.id.tvFromDate);
        tvTo = view.findViewById(R.id.tvToDate);

        ridesSum = view.findViewById(R.id.tvRidesSum);
        ridesAvg = view.findViewById(R.id.tvRidesAvg);
        kmSum = view.findViewById(R.id.tvKmSum);
        kmAvg = view.findViewById(R.id.tvKmAvg);
        moneySum = view.findViewById(R.id.tvMoneySum);
        moneyAvg = view.findViewById(R.id.tvMoneyAvg);

        // Default: first/last day of current month
        YearMonth ym = YearMonth.now();
        fromDate = ym.atDay(1);
        toDate = ym.atEndOfMonth();

        tvFrom.setText(fromDate.format(UI_FMT));
        tvTo.setText(toDate.format(UI_FMT));

        tvFrom.setOnClickListener(v -> showPicker(fromDate, picked -> {
            fromDate = picked;
            tvFrom.setText(fromDate.format(UI_FMT));
        }));

        tvTo.setOnClickListener(v -> showPicker(toDate, picked -> {
            toDate = picked;
            tvTo.setText(toDate.format(UI_FMT));
        }));

        Role role = SessionManager.getRole();
        if (role == null) role = Role.Passenger;

        TextView tvRidesTitle = view.findViewById(R.id.tvRidesTitle);
        TextView tvKmTitle = view.findViewById(R.id.tvKmTitle);
        TextView tvMoneyTitle = view.findViewById(R.id.tvMoneyTitle);

        if (role == Role.Driver) {
            tvRidesTitle.setText("Number of rides given per day");
            tvKmTitle.setText("Kilometers driven per day");
            tvMoneyTitle.setText("Money earned per day");
        } else if (role == Role.Admin) {
            tvRidesTitle.setText("Total number of rides per day");
            tvKmTitle.setText("Total kilometers driven per day");
            tvMoneyTitle.setText("Total money earned per day");
        } else {
            tvRidesTitle.setText("Number of rides taken per day");
            tvKmTitle.setText("Kilometers traveled per day");
            tvMoneyTitle.setText("Money spent per day");
        }

        // Create charts inside FrameLayouts
        ViewGroup ridesContainer = view.findViewById(R.id.graphRidesContainer);
        ViewGroup kmContainer = view.findViewById(R.id.graphKmContainer);
        ViewGroup moneyContainer = view.findViewById(R.id.graphMoneyContainer);

        chartRides = new BarChart(requireContext());
        chartKm = new BarChart(requireContext());
        chartMoney = new BarChart(requireContext());

        ridesContainer.removeAllViews();
        kmContainer.removeAllViews();
        moneyContainer.removeAllViews();

        ridesContainer.addView(chartRides, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        kmContainer.addView(chartKm, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        moneyContainer.addView(chartMoney, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        styleChart(chartRides, "Rides");
        styleChart(chartKm, "km");
        styleChart(chartMoney, "Money");

        // Initial values
        setSummary(ridesSum, ridesAvg, 0, 0);
        setSummary(kmSum, kmAvg, 0, 0);
        setSummary(moneySum, moneyAvg, 0, 0);

        View btn = view.findViewById(R.id.btnShowStatistics);
        Role finalRole = role;
        btn.setOnClickListener(v -> {
            if (!validateRange()) return;
            loadStats(finalRole);
        });

        // Optionally: auto-generate on open
        // loadStats(role);
    }

    private void loadStats(Role role) {
        StatsAPI api = ApiClient.getRetrofit().create(StatsAPI.class);

        String fromIso = fromDate.format(ISO_FMT);
        String toIso = toDate.format(ISO_FMT);

        Call<StatsResponseDTO> call;

        if (role == Role.Admin) {
            call = api.adminStats(fromIso, toIso);
        } else if (role == Role.Driver) {
            if (SessionManager.getUser() == null) {
                Toast.makeText(requireContext(), "Not logged in.", Toast.LENGTH_SHORT).show();
                return;
            }
            long id = SessionManager.getUser().getId();
            call = api.driverStats(id, fromIso, toIso);
        } else {
            if (SessionManager.getUser() == null) {
                Toast.makeText(requireContext(), "Not logged in.", Toast.LENGTH_SHORT).show();
                return;
            }
            long id = SessionManager.getUser().getId();
            call = api.passengerStats(id, fromIso, toIso);
        }

        call.enqueue(new Callback<StatsResponseDTO>() {
            @Override
            public void onResponse(@NonNull Call<StatsResponseDTO> call, @NonNull Response<StatsResponseDTO> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(requireContext(), "Failed to load statistics (" + response.code() + ")", Toast.LENGTH_SHORT).show();
                    return;
                }
                render(response.body().getPoints());
            }

            @Override
            public void onFailure(@NonNull Call<StatsResponseDTO> call, @NonNull Throwable t) {
                Toast.makeText(requireContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void render(List<DailyStatPointDTO> points) {
        if (points == null) points = new ArrayList<>();

        // X labels = day of month (01, 02, ...)
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

        setSummary(ridesSum, ridesAvg, ridesTotal, ridesTotal / days);
        setSummary(kmSum, kmAvg, kmTotal, kmTotal / days);
        setSummary(moneySum, moneyAvg, moneyTotal, moneyTotal / days);

        setBarData(chartRides, ridesEntries, xLabels, false);
        setBarData(chartKm, kmEntries, xLabels, true);
        setBarData(chartMoney, moneyEntries, xLabels, true);
    }

    private void setBarData(BarChart chart, List<BarEntry> entries, List<String> xLabels, boolean allowDecimals) {
        BarDataSet set = new BarDataSet(entries, "");
        set.setDrawValues(false); // cleaner; you can enable if you want numbers above bars

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

    private void styleChart(BarChart chart, String unit) {
        chart.setNoDataText("Press 'Show statistics' to generate.");
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
        x.setLabelCount(6, false); // avoid too many labels

        YAxis left = chart.getAxisLeft();
        left.setAxisMinimum(0f);
        left.setDrawGridLines(true);
    }

    private boolean validateRange() {
        if (fromDate == null || toDate == null) return false;
        if (toDate.isBefore(fromDate)) {
            Toast.makeText(requireContext(), "'To' must be after 'From'.", Toast.LENGTH_SHORT).show();
            return false;
        }
        long days = ChronoUnit.DAYS.between(fromDate, toDate);
        if (days > 31) {
            Toast.makeText(requireContext(), "Date range cannot exceed 31 days.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void setSummary(TextView sumTv, TextView avgTv, double sum, double avg) {
        // Rides should show integers; km/money can show decimals.
        // We’ll format as: if near int => int, else 2 decimals.
        sumTv.setText(formatNumber(sum));
        avgTv.setText(formatNumber(avg));
    }

    private String formatNumber(double v) {
        if (Math.abs(v - Math.round(v)) < 0.0001) {
            return String.valueOf((long) Math.round(v));
        }
        return String.format(Locale.getDefault(), "%.2f", v);
    }

    private interface OnPicked {
        void onPicked(LocalDate d);
    }

    private void showPicker(LocalDate initial, OnPicked cb) {
        new DatePickerDialog(
                requireContext(),
                (dp, y, m, d) -> cb.onPicked(LocalDate.of(y, m + 1, d)),
                initial.getYear(),
                initial.getMonthValue() - 1,
                initial.getDayOfMonth()
        ).show();
    }
}