package com.example.mobile_applications_project_2025;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobile_applications_project_2025.Adapters.DriverRideCardAdapter;
import com.example.mobile_applications_project_2025.DTO.PageResponseDTO;
import com.example.mobile_applications_project_2025.Model.Address;
import com.example.mobile_applications_project_2025.Model.Passenger;
import com.example.mobile_applications_project_2025.Model.Ride;
import com.example.mobile_applications_project_2025.Model.Route;
import com.example.mobile_applications_project_2025.Model.Enumerator.RideStatus;
import com.example.mobile_applications_project_2025.Network.ApiClient;
import com.example.mobile_applications_project_2025.Network.APIs.RideAPI;
import com.google.android.material.textfield.TextInputEditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PassengerRideHistoryFragment extends Fragment {

    private static final int PAGE_SIZE = 8;

    private TextInputEditText etFromDate, etToDate;
    private CheckBox cbScheduled, cbStarted, cbFinished, cbCancelled, cbFavoritesOnly;
    private Button btnApply;
    private RecyclerView rvRides;
    private ImageButton btnPrev, btnNext;
    private TextView tvPageInfo;

    private DriverRideCardAdapter adapter;
    private RideAPI rideAPI;

    private int currentPage = 0; // 0-based
    private int totalPages = 1;

    private final SimpleDateFormat uiFmt = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
    private final SimpleDateFormat isoDateTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_passenger_ride_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etFromDate = view.findViewById(R.id.etFromDate);
        etToDate = view.findViewById(R.id.etToDate);

        cbScheduled = view.findViewById(R.id.cbScheduled);
        cbStarted = view.findViewById(R.id.cbStarted);
        cbFinished = view.findViewById(R.id.cbFinished);
        cbCancelled = view.findViewById(R.id.cbCancelled);
        cbFavoritesOnly = view.findViewById(R.id.cbFavoritesOnly);

        btnApply = view.findViewById(R.id.btnApply);

        rvRides = view.findViewById(R.id.rvRides);
        btnPrev = view.findViewById(R.id.btnPrev);
        btnNext = view.findViewById(R.id.btnNext);
        tvPageInfo = view.findViewById(R.id.tvPageInfo);

        rideAPI = ApiClient.getRetrofit().create(RideAPI.class);

        adapter = new DriverRideCardAdapter(this::openRideDetailsPopup);
        rvRides.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvRides.setAdapter(adapter);

        // defaults
        cbScheduled.setChecked(true);

        etFromDate.setOnClickListener(v -> showDatePicker(etFromDate));
        etToDate.setOnClickListener(v -> showDatePicker(etToDate));

        btnApply.setOnClickListener(v -> {
            currentPage = 0;
            fetchPage();
        });

        btnPrev.setOnClickListener(v -> {
            if (currentPage > 0) {
                currentPage--;
                fetchPage();
            }
        });

        btnNext.setOnClickListener(v -> {
            if (currentPage < totalPages - 1) {
                currentPage++;
                fetchPage();
            }
        });

        fetchPage();
    }

    private void fetchPage() {
        if (!SessionManager.isLoggedIn() || SessionManager.getUser() == null) {
            Toast.makeText(requireContext(), "Not logged in.", Toast.LENGTH_SHORT).show();
            return;
        }

        Long passengerId = SessionManager.getUser().getId();

        List<String> statuses = getSelectedStatuses();
        if (statuses.isEmpty()) {
            adapter.submit(new ArrayList<>());
            totalPages = 1;
            currentPage = 0;
            updatePagerUi();
            Toast.makeText(requireContext(), "Select at least one status.", Toast.LENGTH_SHORT).show();
            return;
        }

        String fromIso = toIsoStartOrNull(etFromDate);
        String toIso = toIsoEndOrNull(etToDate);

        boolean favoritesOnly = cbFavoritesOnly.isChecked();

        rideAPI.getPassengerRidesPaged(passengerId, statuses, fromIso, toIso, favoritesOnly, currentPage, PAGE_SIZE)
                .enqueue(new Callback<PageResponseDTO<Ride>>() {
                    @Override
                    public void onResponse(@NonNull Call<PageResponseDTO<Ride>> call,
                                           @NonNull Response<PageResponseDTO<Ride>> response) {
                        if (!response.isSuccessful() || response.body() == null) {
                            Toast.makeText(requireContext(),
                                    "Failed to load rides (" + response.code() + ")",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }

                        PageResponseDTO<Ride> page = response.body();

                        adapter.submit(page.content);

                        currentPage = page.number;
                        totalPages = Math.max(page.totalPages, 1);
                        updatePagerUi();
                    }

                    @Override
                    public void onFailure(@NonNull Call<PageResponseDTO<Ride>> call, @NonNull Throwable t) {
                        Toast.makeText(requireContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updatePagerUi() {
        tvPageInfo.setText((currentPage + 1) + " / " + totalPages);
        btnPrev.setEnabled(currentPage > 0);
        btnNext.setEnabled(currentPage < totalPages - 1);
    }

    private List<String> getSelectedStatuses() {
        List<String> list = new ArrayList<>();
        if (cbScheduled.isChecked()) list.add(RideStatus.Scheduled.name());
        if (cbStarted.isChecked()) list.add(RideStatus.Started.name());
        if (cbFinished.isChecked()) list.add(RideStatus.Finished.name());
        if (cbCancelled.isChecked()) list.add(RideStatus.Cancelled.name());
        if (!cbScheduled.isChecked() && !cbStarted.isChecked() && !cbFinished.isChecked() && !cbCancelled.isChecked()) {
            list.clear();
            list.add(RideStatus.Scheduled.name());
            list.add(RideStatus.Started.name());
            list.add(RideStatus.Finished.name());
            list.add(RideStatus.Cancelled.name());
        }

        return list;
    }

    private void showDatePicker(TextInputEditText target) {
        Calendar c = Calendar.getInstance();
        int y = c.get(Calendar.YEAR);
        int m = c.get(Calendar.MONTH);
        int d = c.get(Calendar.DAY_OF_MONTH);

        new DatePickerDialog(requireContext(), (dp, year, month, dayOfMonth) -> {
            String dd = String.format(Locale.getDefault(), "%02d.%02d.%04d", dayOfMonth, (month + 1), year);
            target.setText(dd);
        }, y, m, d).show();
    }

    private String toIsoStartOrNull(TextInputEditText et) {
        String s = et.getText() != null ? et.getText().toString().trim() : "";
        if (s.isEmpty()) return null;
        try {
            java.util.Date d = uiFmt.parse(s);
            Calendar cal = Calendar.getInstance();
            cal.setTime(d);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            return isoDateTime.format(cal.getTime());
        } catch (ParseException e) {
            return null;
        }
    }

    private String toIsoEndOrNull(TextInputEditText et) {
        String s = et.getText() != null ? et.getText().toString().trim() : "";
        if (s.isEmpty()) return null;
        try {
            java.util.Date d = uiFmt.parse(s);
            Calendar cal = Calendar.getInstance();
            cal.setTime(d);
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
            cal.set(Calendar.MILLISECOND, 0);
            return isoDateTime.format(cal.getTime());
        } catch (ParseException e) {
            return null;
        }
    }

    private void openRideDetailsPopup(Ride ride) {
        com.example.mobile_applications_project_2025.UI.RideDetailsDialogs.showPassengerRideDetails(requireContext(), rideAPI, ride, this::fetchPage);
    }

    private boolean isRideFavoriteForLoggedPassenger(Ride ride) {
        if (ride == null || ride.route == null || ride.route.id == null) return false;

        // If backend includes passenger.favouriteRoutes in ride JSON (it does in your sample),
        // we can check it directly without extra requests.
        if (ride.passenger != null && ride.passenger.favouriteRoutes != null) {
            for (Route r : ride.passenger.favouriteRoutes) {
                if (r != null && r.id != null && r.id.equals(ride.route.id)) return true;
            }
        }

        // Fallback: if SessionManager user is Passenger and has favouriteRoutes loaded
        if (SessionManager.getUser() instanceof Passenger) {
            Passenger p = (Passenger) SessionManager.getUser();
            if (p.favouriteRoutes != null) {
                for (Route r : p.favouriteRoutes) {
                    if (r != null && r.id != null && r.id.equals(ride.route.id)) return true;
                }
            }
        }
        return false;
    }

    private String buildRideDetailsText(Ride r, boolean isFav) {
        StringBuilder sb = new StringBuilder();

        // Header: route
        sb.append("🗺️  Route\n");
        sb.append(formatShortAddress(r.origin))
                .append("  →  ")
                .append(formatShortAddress(r.destination))
                .append("\n\n");

        // Dates / times
        sb.append("🕒  Time\n");
        sb.append("Start: ").append(formatDateTimePretty(r.rideStartDatetime)).append("\n");
        sb.append("Finish: ").append(formatDateTimePretty(r.rideFinishDatetime)).append("\n\n");

        // Price / duration
        sb.append("💳  Price & duration\n");
        sb.append("Price: ").append(formatPriceRsd(r.ridePrice)).append("\n");
        sb.append("Duration: ").append(r.rideDuration != null ? (r.rideDuration + " min") : "-").append("\n\n");

        // People
        sb.append("👤  People\n");
        sb.append("Driver: ").append(formatPerson(r.driver)).append("\n");
        sb.append("Passenger: ").append(formatPerson(r.passenger)).append("\n\n");

        // Passenger list (emails)
        sb.append("📧  Passengers\n");
        sb.append(formatPassengerList(r.passengers)).append("\n\n");

        // Addresses list from route
        sb.append("📍  Addresses in route\n");
        sb.append(formatRouteAddresses(r)).append("\n\n");

        // Other flags
        sb.append("⚠️  Safety\n");
        sb.append("Panic pressed: ").append(Boolean.TRUE.equals(r.isPanicPressed) ? "Yes" : "No").append("\n");
        sb.append("Cancelled by: ").append(formatPerson(r.cancelledBy)).append("\n");

        return sb.toString().trim();
    }

    private String formatDateTimePretty(String iso) {
        if (iso == null || iso.trim().isEmpty()) return "-";

        // Expecting ISO_LOCAL_DATE_TIME like "2026-02-22T14:31:10"
        try {
            LocalDateTime dt = LocalDateTime.parse(iso);
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd.MM.yyyy  HH:mm");
            return dt.format(fmt);
        } catch (Exception ignored) {
            // If backend sends something else, just show raw string
            return iso;
        }
    }

    private String formatPriceRsd(Float price) {
        if (price == null) return "-";
        return String.format(Locale.getDefault(), "%.0f RSD", price);
    }

    private String formatPerson(Object userObj) {
        if (userObj == null) return "-";

        // Ride.driver / Ride.passenger / cancelledBy are RegisteredUser subclasses with same fields in your model
        if (userObj instanceof com.example.mobile_applications_project_2025.Model.RegisteredUser) {
            com.example.mobile_applications_project_2025.Model.RegisteredUser u =
                    (com.example.mobile_applications_project_2025.Model.RegisteredUser) userObj;

            String name = ((u.firstName != null ? u.firstName : "") + " " + (u.lastName != null ? u.lastName : "")).trim();
            String mail = u.mail != null ? u.mail : "";

            if (!name.isEmpty() && !mail.isEmpty()) return name + " (" + mail + ")";
            if (!name.isEmpty()) return name;
            if (!mail.isEmpty()) return mail;
            return "-";
        }

        // Fallback
        return userObj.toString();
    }

    private String formatPassengerList(List<String> passengers) {
        if (passengers == null || passengers.isEmpty()) return "• (none)";
        StringBuilder sb = new StringBuilder();
        for (String p : passengers) {
            if (p == null || p.trim().isEmpty()) continue;
            sb.append("• ").append(p.trim()).append("\n");
        }
        String out = sb.toString().trim();
        return out.isEmpty() ? "• (none)" : out;
    }

    private String formatRouteAddresses(Ride r) {
        if (r == null || r.route == null || r.route.addresses == null || r.route.addresses.isEmpty()) {
            return "• (no addresses)";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < r.route.addresses.size(); i++) {
            com.example.mobile_applications_project_2025.Model.Address a = r.route.addresses.get(i);
            sb.append(i + 1).append(". ").append(formatFullAddress(a)).append("\n");
        }
        return sb.toString().trim();
    }

    private String formatShortAddress(Address a) {
        if (a == null) return "-";
        // short: "Street 1, City"
        String street = a.street != null ? a.street : "";
        String number = a.number != null ? a.number : "";
        String city = a.city != null ? a.city : "";

        String left = (street + " " + number).trim();
        if (left.isEmpty() && city.isEmpty()) return "-";
        if (city.isEmpty()) return left;
        if (left.isEmpty()) return city;
        return left + ", " + city;
    }

    private String formatFullAddress(Address a) {
        if (a == null) return "-";
        String country = a.country != null ? a.country : "";
        String city = a.city != null ? a.city : "";
        String street = a.street != null ? a.street : "";
        String number = a.number != null ? a.number : "";

        // "Street 1, City, Country"
        String s = (street + " " + number).trim();
        List<String> parts = new ArrayList<>();
        if (!s.isEmpty()) parts.add(s);
        if (!city.isEmpty()) parts.add(city);
        if (!country.isEmpty()) parts.add(country);

        if (parts.isEmpty()) return "-";
        return String.join(", ", parts);
    }
}