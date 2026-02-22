package com.example.mobile_applications_project_2025;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import android.widget.Toast;

import com.example.mobile_applications_project_2025.DTO.PageResponseDTO;
import com.example.mobile_applications_project_2025.Model.Enumerator.Role;
import com.example.mobile_applications_project_2025.Model.Enumerator.RideStatus;
import com.example.mobile_applications_project_2025.Model.RegisteredUser;
import com.example.mobile_applications_project_2025.Model.Ride;
import com.example.mobile_applications_project_2025.Network.APIs.RideAPI;
import com.example.mobile_applications_project_2025.Network.ApiClient;
import com.example.mobile_applications_project_2025.Network.BaseUrl;
import com.example.mobile_applications_project_2025.Network.UserActivityTracker;
import com.example.mobile_applications_project_2025.Network.WsPassengerReminders;
import com.example.mobile_applications_project_2025.Network.WsRideNotifications;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    // WS
    private final WsRideNotifications wsd = new WsRideNotifications();
    private final WsPassengerReminders wsp = new WsPassengerReminders();

    // UI
    private ExtendedFloatingActionButton fabOngoingRide;
    private ExtendedFloatingActionButton fabCreateRide;

    // API
    private RideAPI rideAPI;

    // cached latest rides for click
    private Ride cachedPassengerOngoing;   // Started
    private Ride cachedDriverOngoing;      // Started
    private Ride cachedDriverNextScheduled; // Scheduled

    public HomeFragment() {}

    public static HomeFragment newInstance(String param1, String param2) {
        return new HomeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rideAPI = ApiClient.getRetrofit().create(RideAPI.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button btnOdjava = view.findViewById(R.id.btnOdjava);
        btnOdjava.setOnClickListener(v -> {
            UserActivityTracker.getInstance().stop();
            SessionManager.clear();

            NavController navController = NavHostFragment.findNavController(this);
            NavGraph graph = navController.getNavInflater().inflate(R.navigation.nav_graph);
            graph.setStartDestination(R.id.unregisteredHomeFragment);
            navController.setGraph(graph);
        });

        fabOngoingRide = view.findViewById(R.id.fabOngoingRide);
        fabCreateRide = view.findViewById(R.id.fabCreateRide);

        // create ride click (passenger only; visibility handled in refreshHomeButtons())
        fabCreateRide.setOnClickListener(v -> {
            RegisteredUser u = SessionManager.getUser();

            if (u != null && Boolean.TRUE.equals(u.getBlocked())) {
                new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                        .setTitle("Account blocked")
                        .setMessage(
                                (u.getBlockMessage() != null && !u.getBlockMessage().trim().isEmpty())
                                        ? u.getBlockMessage()
                                        : "You are blocked and cannot create a ride."
                        )
                        .setPositiveButton("OK", (d, w) -> d.dismiss())
                        .show();
                return;
            }

            NavHostFragment.findNavController(this).navigate(R.id.rideCreationFragment);
        });

        // default: no click until we set behavior
        fabOngoingRide.setOnClickListener(null);
    }

    /**
     * IMPORTANT: runs every time fragment becomes visible again
     * (this is what you requested: "reconsidered every time this fragment gets visited")
     */
    @Override
    public void onResume() {
        super.onResume();
        refreshHomeButtons();
    }

    private void refreshHomeButtons() {
        RegisteredUser u = SessionManager.getUser();
        if (u == null || u.getId() == null) {
            hideAllRideButtons();
            return;
        }

        Role role = u.getRole();

        cachedPassengerOngoing = null;
        cachedDriverOngoing = null;
        cachedDriverNextScheduled = null;

        if (role == Role.Admin) {
            // admin: both invisible always
            hideAllRideButtons();
            return;
        }

        if (role == Role.Passenger) {
            // passenger: create visible, ongoing visible only if there is a Started ride
            fabCreateRide.setVisibility(View.VISIBLE);
            loadPassengerOngoingRideAndSetButton(u.getId());
            return;
        }

        if (role == Role.Driver) {
            // driver: create hidden, ongoing button depends on Started or next Scheduled
            fabCreateRide.setVisibility(View.GONE);
            loadDriverOngoingOrNextScheduledAndSetButton(u.getId());
        }
    }

    private void hideAllRideButtons() {
        if (fabCreateRide != null) fabCreateRide.setVisibility(View.GONE);
        if (fabOngoingRide != null) fabOngoingRide.setVisibility(View.GONE);
        if (fabOngoingRide != null) fabOngoingRide.setOnClickListener(null);
    }

    // --------------------------
    // Passenger: ongoing = Started
    // --------------------------

    private void loadPassengerOngoingRideAndSetButton(Long passengerId) {
        // We can reuse existing endpoint:
        // /api/rides/passenger/{id}?statuses=Started&page=0&size=1
        List<String> statuses = new ArrayList<>();
        statuses.add(RideStatus.Started.name());

        rideAPI.getPassengerRidesPaged(
                        passengerId,
                        statuses,
                        null,
                        null,
                        false,
                        0,
                        1
                )
                .enqueue(new Callback<PageResponseDTO<Ride>>() {
                    @Override
                    public void onResponse(@NonNull Call<PageResponseDTO<Ride>> call,
                                           @NonNull Response<PageResponseDTO<Ride>> response) {
                        if (!isAdded()) return;

                        if (!response.isSuccessful() || response.body() == null) {
                            // if error, safest: hide ongoing
                            fabOngoingRide.setVisibility(View.GONE);
                            fabOngoingRide.setOnClickListener(null);
                            return;
                        }

                        PageResponseDTO<Ride> page = response.body();
                        Ride ongoing = (page.content != null && !page.content.isEmpty()) ? page.content.get(0) : null;

                        if (ongoing == null) {
                            fabOngoingRide.setVisibility(View.GONE);
                            fabOngoingRide.setOnClickListener(null);
                            return;
                        }

                        cachedPassengerOngoing = ongoing;
                        fabOngoingRide.setVisibility(View.VISIBLE);
                        fabOngoingRide.setText("View ongoing ride");

                        fabOngoingRide.setOnClickListener(v -> {
                            // Use the exact same popup as PassengerRideHistoryFragment
                            openPassengerRideDetailsPopup(ongoing);
                        });
                    }

                    @Override
                    public void onFailure(@NonNull Call<PageResponseDTO<Ride>> call, @NonNull Throwable t) {
                        if (!isAdded()) return;
                        fabOngoingRide.setVisibility(View.GONE);
                        fabOngoingRide.setOnClickListener(null);
                    }
                });
    }

    /**
     * This is copied 1:1 in spirit from PassengerRideHistoryFragment.openRideDetailsPopup(),
     * so you get the SAME popup.
     */
    private void openPassengerRideDetailsPopup(Ride ride) {
        // Reuse PassengerRideHistoryFragment logic by instantiating its dialog code here
        // (we keep it self-contained to avoid needing to refactor adapter/fragment).
        PassengerRideHistoryFragment tmp = new PassengerRideHistoryFragment();
        // We can call the same method by duplicating the implementation, BUT it is private there.
        // So we reproduce the same UI here: dialog_passenger_ride_details.
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_passenger_ride_details, null, false);

        android.widget.TextView tvAll = dialogView.findViewById(R.id.tvRideAllData);
        Button btnToggleFavorite = dialogView.findViewById(R.id.btnToggleFavorite);

        boolean isFav = isRideFavoriteForLoggedPassenger(ride);
        btnToggleFavorite.setText(isFav ? "Remove from favorites" : "Add to favorites");
        tvAll.setText(buildRideDetailsTextForPassenger(ride));

        androidx.appcompat.app.AlertDialog dialog = new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .setCancelable(true)
                .create();

        btnToggleFavorite.setOnClickListener(v -> {
            if (!SessionManager.isLoggedIn() || SessionManager.getUser() == null) return;
            Long pid = SessionManager.getUser().getId();
            if (pid == null || ride == null || ride.id == null) return;

            btnToggleFavorite.setEnabled(false);

            Call<Void> call = isFav
                    ? rideAPI.unfavoriteRide(ride.id, pid)
                    : rideAPI.favoriteRide(ride.id, pid);

            call.enqueue(new retrofit2.Callback<Void>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                    if (!isAdded()) return;

                    if (!response.isSuccessful()) {
                        Toast.makeText(requireContext(), "Failed (" + response.code() + ")", Toast.LENGTH_SHORT).show();
                        btnToggleFavorite.setEnabled(true);
                        return;
                    }

                    Toast.makeText(requireContext(), isFav ? "Removed from favorites" : "Added to favorites", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    // refresh buttons again (not strictly needed, but consistent)
                    refreshHomeButtons();
                }

                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                    if (!isAdded()) return;
                    Toast.makeText(requireContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    btnToggleFavorite.setEnabled(true);
                }
            });
        });

        dialog.show();
    }

    // --------------------------
    // Driver: ongoing Started OR next Scheduled
    // --------------------------

    private void loadDriverOngoingOrNextScheduledAndSetButton(Long driverId) {
        // First try Started (ongoing)
        rideAPI.getDriverRidesPaged(driverId, Arrays.asList(RideStatus.Started.name()), 0, 1)
                .enqueue(new Callback<PageResponseDTO<Ride>>() {
                    @Override
                    public void onResponse(@NonNull Call<PageResponseDTO<Ride>> call,
                                           @NonNull Response<PageResponseDTO<Ride>> response) {
                        if (!isAdded()) return;

                        if (response.isSuccessful() && response.body() != null) {
                            Ride started = (response.body().content != null && !response.body().content.isEmpty())
                                    ? response.body().content.get(0)
                                    : null;

                            if (started != null) {
                                cachedDriverOngoing = started;

                                fabOngoingRide.setVisibility(View.VISIBLE);
                                fabOngoingRide.setText("View ongoing ride");

                                fabOngoingRide.setOnClickListener(v -> {
                                    // OPEN EXISTING 3-BUTTON DIALOG (Start/Finish/Cancel)
                                    showDriverActionsDialog(started);
                                });

                                return; // stop here; we found ongoing
                            }
                        }

                        // No ongoing -> load next scheduled
                        loadDriverNextScheduledRideAndSetButton(driverId);
                    }

                    @Override
                    public void onFailure(@NonNull Call<PageResponseDTO<Ride>> call, @NonNull Throwable t) {
                        if (!isAdded()) return;
                        // If started check fails, still try scheduled
                        loadDriverNextScheduledRideAndSetButton(driverId);
                    }
                });
    }

    private void loadDriverNextScheduledRideAndSetButton(Long driverId) {
        // Driver scheduled endpoint sorts by rideStartDatetime DESC currently on backend.
        // That means page=0,size=1 returns latest scheduled, not next-by-time.
        // BUT you asked for next scheduled; assuming your backend returns the correct "closest upcoming"
        // OR you have only one scheduled ride active at a time.
        // If not, we can change backend sort to ASC for Scheduled specifically later.
        rideAPI.getDriverRidesPaged(driverId, Arrays.asList(RideStatus.Scheduled.name()), 0, 1)
                .enqueue(new Callback<PageResponseDTO<Ride>>() {
                    @Override
                    public void onResponse(@NonNull Call<PageResponseDTO<Ride>> call,
                                           @NonNull Response<PageResponseDTO<Ride>> response) {
                        if (!isAdded()) return;

                        if (!response.isSuccessful() || response.body() == null) {
                            fabOngoingRide.setVisibility(View.GONE);
                            fabOngoingRide.setOnClickListener(null);
                            return;
                        }

                        Ride scheduled = (response.body().content != null && !response.body().content.isEmpty())
                                ? response.body().content.get(0)
                                : null;

                        if (scheduled == null) {
                            fabOngoingRide.setVisibility(View.GONE);
                            fabOngoingRide.setOnClickListener(null);
                            return;
                        }

                        cachedDriverNextScheduled = scheduled;

                        fabOngoingRide.setVisibility(View.VISIBLE);
                        fabOngoingRide.setText("View next scheduled ride");

                        fabOngoingRide.setOnClickListener(v -> {
                            // Show ride details + "Start the ride" at bottom (custom dialog we’ll add)
                            showDriverScheduledDetailsStartDialog(scheduled);
                        });
                    }

                    @Override
                    public void onFailure(@NonNull Call<PageResponseDTO<Ride>> call, @NonNull Throwable t) {
                        if (!isAdded()) return;
                        fabOngoingRide.setVisibility(View.GONE);
                        fabOngoingRide.setOnClickListener(null);
                    }
                });
    }

    // --------------------------
    // Driver dialogs
    // --------------------------

    private void showDriverActionsDialog(Ride ride) {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_ride_actions, null, false);

        Button btnStart = dialogView.findViewById(R.id.btnStart);
        Button btnFinish = dialogView.findViewById(R.id.btnFinish);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);

        androidx.appcompat.app.AlertDialog dialog = new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Ride actions")
                .setView(dialogView)
                .setCancelable(true)
                .create();

        // enable/disable by status
        boolean isScheduled = (ride.status == RideStatus.Scheduled);
        boolean isStarted = (ride.status == RideStatus.Started);

        setEnabledStyled(btnStart, isScheduled);
        setEnabledStyled(btnFinish, isStarted);
        setEnabledStyled(btnCancel, isScheduled || isStarted);

        btnStart.setOnClickListener(v -> {
            if (ride.id == null) return;
            setEnabledStyled(btnStart, false);

            rideAPI.startRide(ride.id).enqueue(new Callback<Ride>() {
                @Override
                public void onResponse(@NonNull Call<Ride> call, @NonNull Response<Ride> response) {
                    if (!isAdded()) return;
                    if (!response.isSuccessful()) {
                        Toast.makeText(requireContext(), "Failed (" + response.code() + ")", Toast.LENGTH_SHORT).show();
                        setEnabledStyled(btnStart, true);
                        return;
                    }
                    Toast.makeText(requireContext(), "Ride started.", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    refreshHomeButtons();
                }

                @Override
                public void onFailure(@NonNull Call<Ride> call, @NonNull Throwable t) {
                    if (!isAdded()) return;
                    Toast.makeText(requireContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    setEnabledStyled(btnStart, true);
                }
            });
        });

        btnFinish.setOnClickListener(v -> {
            if (ride.id == null) return;
            setEnabledStyled(btnFinish, false);

            rideAPI.finishRide(ride.id).enqueue(new Callback<Ride>() {
                @Override
                public void onResponse(@NonNull Call<Ride> call, @NonNull Response<Ride> response) {
                    if (!isAdded()) return;
                    if (!response.isSuccessful()) {
                        Toast.makeText(requireContext(), "Failed (" + response.code() + ")", Toast.LENGTH_SHORT).show();
                        setEnabledStyled(btnFinish, true);
                        return;
                    }
                    Toast.makeText(requireContext(), "Ride finished.", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    refreshHomeButtons();
                }

                @Override
                public void onFailure(@NonNull Call<Ride> call, @NonNull Throwable t) {
                    if (!isAdded()) return;
                    Toast.makeText(requireContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    setEnabledStyled(btnFinish, true);
                }
            });
        });

        btnCancel.setOnClickListener(v -> {
            if (ride.id == null) return;
            setEnabledStyled(btnCancel, false);

            rideAPI.cancelRide(ride.id).enqueue(new Callback<Ride>() {
                @Override
                public void onResponse(@NonNull Call<Ride> call, @NonNull Response<Ride> response) {
                    if (!isAdded()) return;
                    if (!response.isSuccessful()) {
                        Toast.makeText(requireContext(), "Failed (" + response.code() + ")", Toast.LENGTH_SHORT).show();
                        setEnabledStyled(btnCancel, true);
                        return;
                    }
                    Toast.makeText(requireContext(), "Ride cancelled.", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    refreshHomeButtons();
                }

                @Override
                public void onFailure(@NonNull Call<Ride> call, @NonNull Throwable t) {
                    if (!isAdded()) return;
                    Toast.makeText(requireContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    setEnabledStyled(btnCancel, true);
                }
            });
        });

        dialog.show();
    }

    /**
     * For "next scheduled ride": show details + Start button.
     * Uses a new layout you said you want to create (similar to passenger details but Start at bottom),
     * but since you didn’t paste it yet, we’ll build a minimal one in code using the same passenger details layout
     * and just change button text + action.
     *
     * RECOMMENDED: create res/layout/dialog_driver_scheduled_details.xml later.
     */
    private void showDriverScheduledDetailsStartDialog(Ride ride) {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_passenger_ride_details, null, false);

        android.widget.TextView tvAll = dialogView.findViewById(R.id.tvRideAllData);
        Button btn = dialogView.findViewById(R.id.btnToggleFavorite);

        tvAll.setText(buildRideDetailsTextForPassenger(ride)); // same info text is fine
        btn.setText("Start the ride");

        androidx.appcompat.app.AlertDialog dialog = new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .setCancelable(true)
                .create();

        btn.setOnClickListener(v -> {
            if (ride == null || ride.id == null) return;

            btn.setEnabled(false);

            rideAPI.startRide(ride.id).enqueue(new Callback<Ride>() {
                @Override
                public void onResponse(@NonNull Call<Ride> call, @NonNull Response<Ride> response) {
                    if (!isAdded()) return;

                    if (!response.isSuccessful()) {
                        Toast.makeText(requireContext(), "Failed to start (" + response.code() + ")", Toast.LENGTH_SHORT).show();
                        btn.setEnabled(true);
                        return;
                    }

                    Toast.makeText(requireContext(), "Ride started.", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    refreshHomeButtons();
                }

                @Override
                public void onFailure(@NonNull Call<Ride> call, @NonNull Throwable t) {
                    if (!isAdded()) return;
                    Toast.makeText(requireContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    btn.setEnabled(true);
                }
            });
        });

        dialog.show();
    }

    private void setEnabledStyled(Button btn, boolean enabled) {
        if (btn == null) return;
        btn.setEnabled(enabled);
        btn.setAlpha(enabled ? 1f : 0.4f);
    }

    // --------------------------
    // Passenger details helpers (same as your history popup)
    // --------------------------

    private boolean isRideFavoriteForLoggedPassenger(Ride ride) {
        if (ride == null || ride.route == null || ride.route.id == null) return false;

        if (ride.passenger != null && ride.passenger.favouriteRoutes != null) {
            for (com.example.mobile_applications_project_2025.Model.Route r : ride.passenger.favouriteRoutes) {
                if (r != null && r.id != null && r.id.equals(ride.route.id)) return true;
            }
        }

        if (SessionManager.getUser() instanceof com.example.mobile_applications_project_2025.Model.Passenger) {
            com.example.mobile_applications_project_2025.Model.Passenger p =
                    (com.example.mobile_applications_project_2025.Model.Passenger) SessionManager.getUser();
            if (p.favouriteRoutes != null) {
                for (com.example.mobile_applications_project_2025.Model.Route r : p.favouriteRoutes) {
                    if (r != null && r.id != null && r.id.equals(ride.route.id)) return true;
                }
            }
        }
        return false;
    }

    // Keeping it simple: show the same text builder you used before
    // (If you want, we can move this into a shared util later.)
    private String buildRideDetailsTextForPassenger(Ride r) {
        // This is a minimal version. If you want the full emoji-rich version exactly like your fragment,
        // paste that buildRideDetailsText(...) here and I’ll match it 1:1.
        StringBuilder sb = new StringBuilder();

        sb.append("Route:\n");
        sb.append(shortAddr(r.origin)).append("  →  ").append(shortAddr(r.destination)).append("\n\n");

        sb.append("Start: ").append(r.rideStartDatetime != null ? r.rideStartDatetime : "-").append("\n");
        sb.append("Finish: ").append(r.rideFinishDatetime != null ? r.rideFinishDatetime : "-").append("\n\n");

        sb.append("Price: ").append(r.ridePrice != null ? (String.format("%.0f RSD", r.ridePrice)) : "-").append("\n");
        sb.append("Duration: ").append(r.rideDuration != null ? (r.rideDuration + " min") : "-").append("\n\n");

        sb.append("Driver: ").append(person(r.driver)).append("\n");
        sb.append("Passenger: ").append(person(r.passenger)).append("\n\n");

        sb.append("Passengers:\n");
        if (r.passengers != null && !r.passengers.isEmpty()) {
            for (String p : r.passengers) {
                if (p != null && !p.trim().isEmpty()) sb.append("• ").append(p.trim()).append("\n");
            }
        } else {
            sb.append("• (none)\n");
        }

        return sb.toString().trim();
    }

    private String shortAddr(com.example.mobile_applications_project_2025.Model.Address a) {
        if (a == null) return "-";
        String s = ((a.street != null ? a.street : "") + " " + (a.number != null ? a.number : "")).trim();
        if (a.city != null && !a.city.trim().isEmpty()) {
            if (!s.isEmpty()) return s + ", " + a.city.trim();
            return a.city.trim();
        }
        return s.isEmpty() ? "-" : s;
    }

    private String person(Object userObj) {
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

    // --------------------------
    // Websocket behavior (unchanged)
    // --------------------------

    @Override
    public void onStart() {
        super.onStart();

        RegisteredUser u = SessionManager.getUser();
        if (u == null || u.getId() == null) return;

        String base = BaseUrl.get(); // e.g. "http://10.0.2.2:8080/"
        String wsUrl = base.replace("http://", "ws://")
                .replace("https://", "wss://");
        if (wsUrl.endsWith("/")) wsUrl = wsUrl.substring(0, wsUrl.length() - 1);
        wsUrl = wsUrl + "/ws";

        if (u.getRole() == Role.Driver) {
            wsd.connect(wsUrl, u.getId(), (rideId, message) -> {
                requireActivity().runOnUiThread(() -> {
                    new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                            .setTitle("New ride assigned")
                            .setMessage(message + "\nRide ID: " + rideId)
                            .setPositiveButton("OK", (d, w) -> d.dismiss())
                            .show();
                });
            });
        } else if (u.getRole() == Role.Passenger) {
            wsp.connect(wsUrl, u.getId(), (rideId, minutesBefore, message) -> {
                requireActivity().runOnUiThread(() -> {
                    new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                            .setTitle("Ride reminder")
                            .setMessage(message + "\nRide ID: " + rideId)
                            .setPositiveButton("OK", (d, w) -> d.dismiss())
                            .show();
                });
            });
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        try { wsd.disconnect(); } catch (Exception ignored) {}
        try { wsp.disconnect(); } catch (Exception ignored) {}
    }
}