package com.example.mobile_applications_project_2025.UI;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.mobile_applications_project_2025.DTO.DriverRatingCreateRequestDTO;
import com.example.mobile_applications_project_2025.DTO.DriverRatingResponseDTO;
import com.example.mobile_applications_project_2025.Model.Enumerator.RideStatus;
import com.example.mobile_applications_project_2025.Model.Passenger;
import com.example.mobile_applications_project_2025.Model.Ride;
import com.example.mobile_applications_project_2025.Model.Route;
import com.example.mobile_applications_project_2025.Network.APIs.DriverRatingAPI;
import com.example.mobile_applications_project_2025.Network.APIs.RideAPI;
import com.example.mobile_applications_project_2025.R;
import com.example.mobile_applications_project_2025.SessionManager;
import com.google.android.material.textfield.TextInputEditText;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public final class RideDetailsDialogs {

    private RideDetailsDialogs() {}

    public interface AfterAction {
        void run();
    }

    public static void showPassengerRideDetails(
            @NonNull Context ctx,
            @NonNull RideAPI rideAPI,
            @NonNull Ride ride,
            AfterAction after
    ) {
        showPassengerRideDetails(ctx, rideAPI, null, ride, after);
    }

    // 2.8 - overload sa DriverRatingAPI: omogucava naknadno ocenjivanje zavrsene
    // voznje direktno iz istorije (Ride History -> detalji voznje).
    public static void showPassengerRideDetails(
            @NonNull Context ctx,
            @NonNull RideAPI rideAPI,
            DriverRatingAPI driverRatingAPI,
            @NonNull Ride ride,
            AfterAction after
    ) {
        View dialogView = LayoutInflater.from(ctx).inflate(R.layout.dialog_passenger_ride_details, null, false);

        TextView tvAll = dialogView.findViewById(R.id.tvRideAllData);
        Button btnToggleFavorite = dialogView.findViewById(R.id.btnToggleFavorite);

        boolean isFav = isRideFavoriteForLoggedPassenger(ride);
        btnToggleFavorite.setText(isFav ? "Remove from favorites" : "Add to favorites");
        tvAll.setText(buildRideDetailsText(ride));

        AlertDialog dialog = new AlertDialog.Builder(ctx)
                .setView(dialogView)
                .setCancelable(true)
                .create();

        setupRatingSection(ctx, dialogView, driverRatingAPI, ride);

        btnToggleFavorite.setOnClickListener(v -> {
            if (!SessionManager.isLoggedIn() || SessionManager.getUser() == null) return;
            Long passengerId = SessionManager.getUser().getId();

            btnToggleFavorite.setEnabled(false);

            Call<Void> call = isFav
                    ? rideAPI.unfavoriteRide(ride.id, passengerId)
                    : rideAPI.favoriteRide(ride.id, passengerId);

            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                    if (!response.isSuccessful()) {
                        Toast.makeText(ctx, "Failed (" + response.code() + ")", Toast.LENGTH_SHORT).show();
                        btnToggleFavorite.setEnabled(true);
                        return;
                    }
                    Toast.makeText(ctx, isFav ? "Removed from favorites" : "Added to favorites", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    if (after != null) after.run();
                }

                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                    Toast.makeText(ctx, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    btnToggleFavorite.setEnabled(true);
                }
            });
        });

        dialog.show();
    }

    // --------- DRIVER dialog (new) ----------
    public static void showDriverRideDetailsStartable(
            @NonNull Context ctx,
            @NonNull RideAPI rideAPI,
            @NonNull Ride ride,
            AfterAction after
    ) {
        View dialogView = LayoutInflater.from(ctx).inflate(R.layout.dialog_driver_ride_details, null, false);

        TextView tvAll = dialogView.findViewById(R.id.tvRideAllData);
        Button btnStart = dialogView.findViewById(R.id.btnStartRide);

        tvAll.setText(buildRideDetailsText(ride));

        AlertDialog dialog = new AlertDialog.Builder(ctx)
                .setView(dialogView)
                .setCancelable(true)
                .create();

        btnStart.setOnClickListener(v -> {
            if (ride.id == null) return;
            btnStart.setEnabled(false);

            rideAPI.startRide(ride.id).enqueue(new Callback<Ride>() {
                @Override
                public void onResponse(@NonNull Call<Ride> call, @NonNull Response<Ride> response) {
                    if (!response.isSuccessful()) {
                        Toast.makeText(ctx, "Failed to start (" + response.code() + ")", Toast.LENGTH_SHORT).show();
                        btnStart.setEnabled(true);
                        return;
                    }
                    Toast.makeText(ctx, "Ride started.", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    if (after != null) after.run();
                }

                @Override
                public void onFailure(@NonNull Call<Ride> call, @NonNull Throwable t) {
                    Toast.makeText(ctx, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    btnStart.setEnabled(true);
                }
            });
        });

        dialog.show();
    }

    // 2.8 - prikazuje sekciju za ocenjivanje samo za zavrsene voznje, i proverava
    // da li vec postoji ocena za tu voznju (GET /api/driverratings/ride/{id}).
    private static void setupRatingSection(Context ctx, View dialogView, DriverRatingAPI driverRatingAPI, Ride ride) {
        View section = dialogView.findViewById(R.id.ratingSectionHistory);
        if (section == null || driverRatingAPI == null) return;
        if (ride.status != RideStatus.Finished || ride.id == null) return;

        section.setVisibility(View.VISIBLE);

        NumberPicker npDriver = dialogView.findViewById(R.id.npDriverHistory);
        NumberPicker npVehicle = dialogView.findViewById(R.id.npVehicleHistory);
        TextInputEditText etComment = dialogView.findViewById(R.id.etRatingCommentHistory);
        Button btnSubmit = dialogView.findViewById(R.id.btnSubmitRatingHistory);

        npDriver.setMinValue(1);
        npDriver.setMaxValue(5);
        npDriver.setValue(5);
        npVehicle.setMinValue(1);
        npVehicle.setMaxValue(5);
        npVehicle.setValue(5);

        Runnable markRatedEmpty = () -> markRated(section, npDriver, npVehicle, etComment, btnSubmit, null);

        // proveri da li je vec ocenjeno
        driverRatingAPI.getByRide(ride.id).enqueue(new Callback<DriverRatingResponseDTO>() {
            @Override
            public void onResponse(@NonNull Call<DriverRatingResponseDTO> call, @NonNull Response<DriverRatingResponseDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    markRated(section, npDriver, npVehicle, etComment, btnSubmit, response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<DriverRatingResponseDTO> call, @NonNull Throwable t) { /* 404 = jos nije ocenjeno */ }
        });

        btnSubmit.setOnClickListener(v -> {
            btnSubmit.setEnabled(false);
            String comment = etComment.getText() != null ? etComment.getText().toString().trim() : "";

            DriverRatingCreateRequestDTO body = new DriverRatingCreateRequestDTO(
                    ride.id, npDriver.getValue(), npVehicle.getValue(), comment.isEmpty() ? null : comment);

            driverRatingAPI.createRating(body).enqueue(new Callback<DriverRatingResponseDTO>() {
                @Override
                public void onResponse(@NonNull Call<DriverRatingResponseDTO> call, @NonNull Response<DriverRatingResponseDTO> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Toast.makeText(ctx, "Thanks for rating your ride!", Toast.LENGTH_SHORT).show();
                        markRated(section, npDriver, npVehicle, etComment, btnSubmit, response.body());
                    } else {
                        btnSubmit.setEnabled(true);
                        Toast.makeText(ctx, describeError(response), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<DriverRatingResponseDTO> call, @NonNull Throwable t) {
                    btnSubmit.setEnabled(true);
                    Toast.makeText(ctx, "Network error while submitting rating.", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    // 2.8 - disable-uje formu i prikazuje ostavljenu ocenu/komentar umesto praznog "Already rated".
    private static void markRated(View section, NumberPicker npDriver, NumberPicker npVehicle,
                                  TextInputEditText etComment, Button btnSubmit, DriverRatingResponseDTO rating) {
        btnSubmit.setEnabled(false);
        btnSubmit.setText("Already rated");
        npDriver.setEnabled(false);
        npVehicle.setEnabled(false);
        etComment.setEnabled(false);

        if (rating != null) {
            if (rating.driverRating != null) npDriver.setValue(rating.driverRating);
            if (rating.vehicleRating != null) npVehicle.setValue(rating.vehicleRating);
            etComment.setText(rating.text != null ? rating.text : "");
        }
    }

    private static String describeError(Response<?> response) {
        try {
            if (response.errorBody() != null) {
                String msg = response.errorBody().string();
                if (msg != null && !msg.trim().isEmpty()) return msg;
            }
        } catch (Exception ignored) { }
        switch (response.code()) {
            case 409: return "This ride has already been rated.";
            case 410: return "Rating deadline (3 days) has expired.";
            case 400: return "Ratings must be between 1 and 5.";
            case 404: return "Ride not found.";
            default: return "Could not submit rating (" + response.code() + ").";
        }
    }

    // --------- helpers (copied from your fragment, unchanged logic) ----------
    private static boolean isRideFavoriteForLoggedPassenger(Ride ride) {
        if (ride == null || ride.route == null || ride.route.id == null) return false;

        if (ride.passenger != null && ride.passenger.favouriteRoutes != null) {
            for (Route r : ride.passenger.favouriteRoutes) {
                if (r != null && r.id != null && r.id.equals(ride.route.id)) return true;
            }
        }

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

    private static String buildRideDetailsText(Ride r) {
        StringBuilder sb = new StringBuilder();

        sb.append("🗺️  Route\n");
        sb.append(formatShortAddress(r.origin)).append("  →  ").append(formatShortAddress(r.destination)).append("\n\n");

        sb.append("🕒  Time\n");
        sb.append("Start: ").append(formatDateTimePretty(r.rideStartDatetime)).append("\n");
        sb.append("Finish: ").append(formatDateTimePretty(r.rideFinishDatetime)).append("\n\n");

        sb.append("💳  Price & duration\n");
        sb.append("Price: ").append(formatPriceRsd(r.ridePrice)).append("\n");
        sb.append("Duration: ").append(r.rideDuration != null ? (r.rideDuration + " min") : "-").append("\n\n");

        sb.append("👤  People\n");
        sb.append("Driver: ").append(formatPerson(r.driver)).append("\n");
        sb.append("Passenger: ").append(formatPerson(r.passenger)).append("\n\n");

        sb.append("📧  Passengers\n");
        sb.append(formatPassengerList(r.passengers)).append("\n\n");

        sb.append("📍  Addresses in route\n");
        sb.append(formatRouteAddresses(r)).append("\n\n");

        sb.append("⚠️  Safety\n");
        sb.append("Panic pressed: ").append(Boolean.TRUE.equals(r.isPanicPressed) ? "Yes" : "No").append("\n");
        sb.append("Cancelled by: ").append(formatPerson(r.cancelledBy)).append("\n");

        return sb.toString().trim();
    }

    private static String formatDateTimePretty(String iso) {
        if (iso == null || iso.trim().isEmpty()) return "-";
        try {
            LocalDateTime dt = LocalDateTime.parse(iso);
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd.MM.yyyy  HH:mm");
            return dt.format(fmt);
        } catch (Exception ignored) {
            return iso;
        }
    }

    private static String formatPriceRsd(Float price) {
        if (price == null) return "-";
        return String.format(Locale.getDefault(), "%.0f RSD", price);
    }

    private static String formatPerson(Object userObj) {
        if (userObj == null) return "-";
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
        return userObj.toString();
    }

    private static String formatPassengerList(List<String> passengers) {
        if (passengers == null || passengers.isEmpty()) return "• (none)";
        StringBuilder sb = new StringBuilder();
        for (String p : passengers) {
            if (p == null || p.trim().isEmpty()) continue;
            sb.append("• ").append(p.trim()).append("\n");
        }
        String out = sb.toString().trim();
        return out.isEmpty() ? "• (none)" : out;
    }

    private static String formatRouteAddresses(Ride r) {
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

    private static String formatShortAddress(com.example.mobile_applications_project_2025.Model.Address a) {
        if (a == null) return "-";
        String street = a.street != null ? a.street : "";
        String number = a.number != null ? a.number : "";
        String city = a.city != null ? a.city : "";

        String left = (street + " " + number).trim();
        if (left.isEmpty() && city.isEmpty()) return "-";
        if (city.isEmpty()) return left;
        if (left.isEmpty()) return city;
        return left + ", " + city;
    }

    private static String formatFullAddress(com.example.mobile_applications_project_2025.Model.Address a) {
        if (a == null) return "-";
        String country = a.country != null ? a.country : "";
        String city = a.city != null ? a.city : "";
        String street = a.street != null ? a.street : "";
        String number = a.number != null ? a.number : "";

        String s = (street + " " + number).trim();
        List<String> parts = new ArrayList<>();
        if (!s.isEmpty()) parts.add(s);
        if (!city.isEmpty()) parts.add(city);
        if (!country.isEmpty()) parts.add(country);

        if (parts.isEmpty()) return "-";
        return String.join(", ", parts);
    }

    public static void showDriverOngoingActionsDialog(
            @NonNull Context ctx,
            @NonNull RideAPI rideAPI,
            @NonNull Ride ride,
            AfterAction after
    ) {
        View dialogView = LayoutInflater.from(ctx).inflate(R.layout.dialog_ride_actions, null, false);

        Button btnStart  = dialogView.findViewById(R.id.btnStart);
        Button btnFinish = dialogView.findViewById(R.id.btnFinish);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);

        androidx.appcompat.app.AlertDialog dialog = new androidx.appcompat.app.AlertDialog.Builder(ctx)
                .setTitle("Ride actions")
                .setView(dialogView)
                .setCancelable(true)
                .create();

        String status = (ride.status != null) ? ride.status.toString() : "";

        boolean isScheduled = "Scheduled".equalsIgnoreCase(status);
        boolean isStarted   = "Started".equalsIgnoreCase(status);

        setBtnEnabled(btnStart,  isScheduled);
        setBtnEnabled(btnFinish, isStarted);
        setBtnEnabled(btnCancel, isScheduled || isStarted);

        btnStart.setOnClickListener(v -> {
            if (ride.id == null) return;
            setBtnEnabled(btnStart, false);

            rideAPI.startRide(ride.id).enqueue(new retrofit2.Callback<Ride>() {
                @Override
                public void onResponse(@NonNull retrofit2.Call<Ride> call, @NonNull retrofit2.Response<Ride> response) {
                    if (!response.isSuccessful()) {
                        Toast.makeText(ctx, "Failed to start (" + response.code() + ")", Toast.LENGTH_SHORT).show();
                        setBtnEnabled(btnStart, true);
                        return;
                    }
                    Toast.makeText(ctx, "Ride started.", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    if (after != null) after.run();
                }

                @Override
                public void onFailure(@NonNull retrofit2.Call<Ride> call, @NonNull Throwable t) {
                    Toast.makeText(ctx, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    setBtnEnabled(btnStart, true);
                }
            });
        });

        btnFinish.setOnClickListener(v -> {
            if (ride.id == null) return;
            setBtnEnabled(btnFinish, false);

            rideAPI.finishRide(ride.id).enqueue(new retrofit2.Callback<Ride>() {
                @Override
                public void onResponse(@NonNull retrofit2.Call<Ride> call, @NonNull retrofit2.Response<Ride> response) {
                    if (!response.isSuccessful()) {
                        Toast.makeText(ctx, "Failed to finish (" + response.code() + ")", Toast.LENGTH_SHORT).show();
                        setBtnEnabled(btnFinish, true);
                        return;
                    }
                    Toast.makeText(ctx, "Ride finished.", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    if (after != null) after.run();
                }

                @Override
                public void onFailure(@NonNull retrofit2.Call<Ride> call, @NonNull Throwable t) {
                    Toast.makeText(ctx, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    setBtnEnabled(btnFinish, true);
                }
            });
        });

        btnCancel.setOnClickListener(v -> {
            if (ride.id == null) return;
            setBtnEnabled(btnCancel, false);

            rideAPI.cancelRide(ride.id).enqueue(new retrofit2.Callback<Ride>() {
                @Override
                public void onResponse(@NonNull retrofit2.Call<Ride> call, @NonNull retrofit2.Response<Ride> response) {
                    if (!response.isSuccessful()) {
                        Toast.makeText(ctx, "Failed to cancel (" + response.code() + ")", Toast.LENGTH_SHORT).show();
                        setBtnEnabled(btnCancel, true);
                        return;
                    }
                    Toast.makeText(ctx, "Ride cancelled.", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    if (after != null) after.run();
                }

                @Override
                public void onFailure(@NonNull retrofit2.Call<Ride> call, @NonNull Throwable t) {
                    Toast.makeText(ctx, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    setBtnEnabled(btnCancel, true);
                }
            });
        });

        dialog.show();
    }

    private static void setBtnEnabled(Button b, boolean enabled) {
        if (b == null) return;
        b.setEnabled(enabled);
        b.setAlpha(enabled ? 1f : 0.4f);
    }
}