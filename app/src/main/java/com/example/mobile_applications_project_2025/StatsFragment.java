package com.example.mobile_applications_project_2025;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Locale;

public class StatsFragment extends Fragment {
    public StatsFragment() {
        // Required empty public constructor
    }

    public static StatsFragment newInstance(String param1, String param2) {
        StatsFragment fragment = new StatsFragment();
        Bundle bundle = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_stats, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        TextView tvFrom = view.findViewById(R.id.tvFromDate);
        TextView tvTo = view.findViewById(R.id.tvToDate);

        Calendar now = Calendar.getInstance();
        Calendar from = (Calendar) now.clone();
        Calendar to = (Calendar) now.clone();

        tvFrom.setText(format(from));
        tvTo.setText(format(to));

        tvFrom.setOnClickListener(v -> showPicker(from, picked -> tvFrom.setText(format(picked))));
        tvTo.setOnClickListener(v -> showPicker(to, picked -> tvTo.setText(format(picked))));

        view.findViewById(R.id.btnShowStatistics);

        TextView ridesSum = view.findViewById(R.id.tvRidesSum);
        TextView ridesAvg = view.findViewById(R.id.tvRidesAvg);
        TextView kmSum = view.findViewById(R.id.tvKmSum);
        TextView kmAvg = view.findViewById(R.id.tvKmAvg);
        TextView moneySum = view.findViewById(R.id.tvMoneySum);
        TextView moneyAvg = view.findViewById(R.id.tvMoneyAvg);

        ridesSum.setText("314");
        ridesAvg.setText("10");
        kmSum.setText("301");
        kmAvg.setText("10");
        moneySum.setText("279");
        moneyAvg.setText("9");

        ImageView imgRides = view.findViewById(R.id.imgRidesGraph);
        ImageView imgKm = view.findViewById(R.id.imgKmGraph);
        ImageView imgMoney = view.findViewById(R.id.imgMoneyGraph);

        //imgRides.setImageDrawable(null);
        //imgKm.setImageDrawable(null);
        //imgMoney.setImageDrawable(null);
        imgRides.setImageResource(R.drawable.graph_placeholder);
        imgKm.setImageResource(R.drawable.graph_placeholder);
        imgMoney.setImageResource(R.drawable.graph_placeholder);
    }

    private interface OnPicked {
        void onPicked(Calendar c);
    }

    private void showPicker(Calendar initial, OnPicked cb) {
        new DatePickerDialog(
                requireContext(),
                (dp, y, m, d) -> {
                    Calendar c = Calendar.getInstance();
                    c.set(y, m, d, 0, 0, 0);
                    c.set(Calendar.MILLISECOND, 0);
                    cb.onPicked(c);
                },
                initial.get(Calendar.YEAR),
                initial.get(Calendar.MONTH),
                initial.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    private String format(Calendar c) {
        return String.format(
                Locale.getDefault(),
                "%02d.%02d.%04d",
                c.get(Calendar.DAY_OF_MONTH),
                c.get(Calendar.MONTH) + 1,
                c.get(Calendar.YEAR)
        );
    }
}