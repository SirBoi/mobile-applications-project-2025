package com.example.mobile_applications_project_2025.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobile_applications_project_2025.Model.Address;
import com.example.mobile_applications_project_2025.Model.Ride;
import com.example.mobile_applications_project_2025.R;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DriverRideCardAdapter extends RecyclerView.Adapter<DriverRideCardAdapter.VH> {

    public interface OnRideClickListener {
        void onRideClick(Ride ride);
    }

    private final List<Ride> items = new ArrayList<>();
    private final OnRideClickListener listener;

    private final DateTimeFormatter inIso = DateTimeFormatter.ISO_DATE_TIME;
    private final DateTimeFormatter outDate = DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.getDefault());
    private final DateTimeFormatter outTime = DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault());

    public DriverRideCardAdapter(OnRideClickListener listener) {
        this.listener = listener;
    }

    public void submit(List<Ride> rides) {
        items.clear();
        if (rides != null) items.addAll(rides);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_ride_card, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        Ride r = items.get(position);

        // Date + time formatting
        String dateText = "-";
        String timeRange = "--:-- - --:--";

        // Start datetime (Android Ride has String rideStartDatetime)
        if (r.rideStartDatetime != null) {
            try {
                LocalDateTime dt = LocalDateTime.parse(r.rideStartDatetime);
                dateText = dt.format(outDate);
                String startTime = dt.format(outTime);

                String endTime = "--:--";
                if (r.rideFinishDatetime != null && !r.rideFinishDatetime.isEmpty()) {
                    LocalDateTime endDt = LocalDateTime.parse(r.rideFinishDatetime);
                    endTime = endDt.format(outTime);
                }

                timeRange = startTime + " - " + endTime;
            } catch (Exception ignored) {
                dateText = r.rideStartDatetime;
            }
        }

        h.tvDate.setText(dateText);
        h.tvStatus.setText(r.status != null ? r.status.name() : "-");

        h.tvRoute.setText(formatRoute(r.origin, r.destination));
        h.tvTimeRange.setText(timeRange);

        // Price
        if (r.ridePrice != null) {
            h.tvPrice.setText(String.format(Locale.getDefault(), "%.0f RSD", r.ridePrice));
        } else {
            h.tvPrice.setText("- RSD");
        }

        // Cancelled + Panicked
        boolean cancelled = (r.status != null && r.status.name().equalsIgnoreCase("Cancelled"));
        String cancelledText = "Cancelled: " + (cancelled ? "Yes" : "No");
        if (cancelled && r.cancelledBy != null) {
            String fn = r.cancelledBy.firstName != null ? r.cancelledBy.firstName : "";
            String ln = r.cancelledBy.lastName != null ? r.cancelledBy.lastName : "";
            String name = (fn + " " + ln).trim();
            if (!name.isEmpty()) cancelledText += " (by " + name + ")";
        }
        h.tvCancelled.setText(cancelledText);
        h.tvPanicked.setText("Panicked: " + (Boolean.TRUE.equals(r.isPanicPressed) ? "Yes" : "No"));
        h.tvPassengers.setText("Passengers: " + formatPassengers(r));

        h.itemView.setOnClickListener(v -> listener.onRideClick(r));
    }

    private String formatPassengers(Ride r) {
        java.util.List<String> names = new ArrayList<>();
        if (r.passenger != null) {
            String fn = r.passenger.getFirstName() != null ? r.passenger.getFirstName() : "";
            String ln = r.passenger.getLastName() != null ? r.passenger.getLastName() : "";
            String name = (fn + " " + ln).trim();
            if (!name.isEmpty()) names.add(name);
        }
        if (r.passengers != null) {
            for (String n : r.passengers) {
                if (n != null && !n.trim().isEmpty() && !names.contains(n.trim())) names.add(n.trim());
            }
        }
        return names.isEmpty() ? "-" : String.join(", ", names);
    }

    private String formatRoute(Address o, Address d) {
        String from = formatShortAddress(o);
        String to = formatShortAddress(d);
        return from + " \u2192 " + to;
    }

    private String formatShortAddress(Address a) {
        if (a == null) return "?";
        // keep it simple and consistent with your data
        if (a.street != null && a.number != null) return a.street + " " + a.number;
        if (a.street != null) return a.street;
        if (a.city != null) return a.city;
        return "?";
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvDate, tvStatus, tvRoute, tvTimeRange, tvPrice, tvCancelled, tvPanicked, tvPassengers;

        VH(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvRoute = itemView.findViewById(R.id.tvRoute);
            tvTimeRange = itemView.findViewById(R.id.tvTimeRange);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvCancelled = itemView.findViewById(R.id.tvCancelled);
            tvPanicked = itemView.findViewById(R.id.tvPanicked);
            tvPassengers = itemView.findViewById(R.id.tvPassengers);
        }
    }
}